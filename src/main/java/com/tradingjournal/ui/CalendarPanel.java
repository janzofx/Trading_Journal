package com.tradingjournal.ui;

import com.tradingjournal.model.Trade;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;

/**
 * Calendar panel showing monthly view with daily P&L and weekly summary
 */
public class CalendarPanel extends JPanel {

    private static final String[] DAY_NAMES = { "Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat" };
    private static final Color PROFIT_COLOR = new Color(200, 230, 200);
    private static final Color LOSS_COLOR = new Color(245, 200, 200);
    private static final Color HEADER_COLOR = new Color(245, 245, 245);
    private static final Color BORDER_COLOR = new Color(220, 220, 220);
    private static final DateTimeFormatter MONTH_FORMATTER = DateTimeFormatter.ofPattern("MMMM yyyy");

    private YearMonth currentMonth;
    private List<Trade> trades;
    private Map<LocalDate, Double> dailyPnL;
    private Map<LocalDate, Integer> dailyTradeCounts;

    private JLabel monthLabel;
    private JPanel calendarGridPanel;
    private JPanel weeklyPnLPanel;

    public CalendarPanel() {
        this.currentMonth = YearMonth.now();
        this.trades = new ArrayList<>();
        this.dailyPnL = new HashMap<>();
        this.dailyTradeCounts = new HashMap<>();

        initializeUI();
        updateCalendar();
    }

    private void initializeUI() {
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(15, 15, 15, 15));
        setBackground(Color.WHITE);

        // Header with navigation
        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);

        // Main content: Calendar grid + Weekly P&L summary
        JPanel contentPanel = new JPanel(new BorderLayout(20, 0));
        contentPanel.setBackground(Color.WHITE);

        // Calendar grid
        calendarGridPanel = new JPanel();
        calendarGridPanel.setBackground(Color.WHITE);
        contentPanel.add(calendarGridPanel, BorderLayout.CENTER);

        // Weekly P&L summary on the right
        weeklyPnLPanel = new JPanel();
        weeklyPnLPanel.setBackground(Color.WHITE);
        weeklyPnLPanel.setPreferredSize(new Dimension(150, 0));
        contentPanel.add(weeklyPnLPanel, BorderLayout.EAST);

        add(contentPanel, BorderLayout.CENTER);
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setBorder(new EmptyBorder(0, 0, 15, 0));

        // Navigation panel
        JPanel navPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        navPanel.setBackground(Color.WHITE);

        JButton prevButton = new JButton("< Prev");
        prevButton.setFont(new Font("SansSerif", Font.PLAIN, 12));
        prevButton.addActionListener(e -> {
            currentMonth = currentMonth.minusMonths(1);
            updateCalendar();
        });

        JButton nextButton = new JButton("Next >");
        nextButton.setFont(new Font("SansSerif", Font.PLAIN, 12));
        nextButton.addActionListener(e -> {
            currentMonth = currentMonth.plusMonths(1);
            updateCalendar();
        });

        monthLabel = new JLabel();
        monthLabel.setFont(new Font("Arial", Font.BOLD, 16));

        navPanel.add(prevButton);
        navPanel.add(nextButton);
        navPanel.add(Box.createHorizontalStrut(10));
        navPanel.add(monthLabel);

        headerPanel.add(navPanel, BorderLayout.WEST);

        return headerPanel;
    }

    /**
     * Set trades data and refresh calendar
     */
    public void setTrades(List<Trade> trades) {
        this.trades = trades != null ? trades : new ArrayList<>();
        calculateDailyData();
        updateCalendar();
    }

    /**
     * Calculate daily P&L and trade counts from trades
     */
    private void calculateDailyData() {
        dailyPnL.clear();
        dailyTradeCounts.clear();

        for (Trade trade : trades) {
            if (trade.getCloseTime() != null) {
                LocalDate closeDate = trade.getCloseTime().toLocalDate();
                double pnl = trade.getNetProfit();

                dailyPnL.merge(closeDate, pnl, Double::sum);
                dailyTradeCounts.merge(closeDate, 1, Integer::sum);
            }
        }
    }

    /**
     * Update the calendar display for current month
     */
    private void updateCalendar() {
        // Update month label
        monthLabel.setText(currentMonth.format(MONTH_FORMATTER));

        // Rebuild calendar grid
        calendarGridPanel.removeAll();
        calendarGridPanel.setLayout(new GridLayout(0, 7, 2, 2));

        // Add day headers
        for (String dayName : DAY_NAMES) {
            JLabel headerLabel = new JLabel(dayName, SwingConstants.CENTER);
            headerLabel.setFont(new Font("Arial", Font.BOLD, 12));
            headerLabel.setOpaque(true);
            headerLabel.setBackground(HEADER_COLOR);
            headerLabel.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(BORDER_COLOR),
                    new EmptyBorder(8, 5, 8, 5)));
            calendarGridPanel.add(headerLabel);
        }

        // Get first day of month and calculate offset
        LocalDate firstOfMonth = currentMonth.atDay(1);
        int firstDayOfWeek = firstOfMonth.getDayOfWeek().getValue() % 7; // Sunday = 0
        int daysInMonth = currentMonth.lengthOfMonth();

        // Add empty cells for days before first of month
        for (int i = 0; i < firstDayOfWeek; i++) {
            calendarGridPanel.add(createEmptyDayCell());
        }

        // Add day cells
        for (int day = 1; day <= daysInMonth; day++) {
            LocalDate date = currentMonth.atDay(day);
            calendarGridPanel.add(createDayCell(date));
        }

        // Add empty cells to complete last row
        int totalCells = firstDayOfWeek + daysInMonth;
        int remainingCells = (7 - (totalCells % 7)) % 7;
        for (int i = 0; i < remainingCells; i++) {
            calendarGridPanel.add(createEmptyDayCell());
        }

        // Update weekly P&L panel
        updateWeeklyPnLPanel();

        calendarGridPanel.revalidate();
        calendarGridPanel.repaint();
    }

    private JPanel createEmptyDayCell() {
        JPanel cell = new JPanel();
        cell.setBackground(Color.WHITE);
        cell.setBorder(BorderFactory.createLineBorder(BORDER_COLOR));
        cell.setPreferredSize(new Dimension(80, 70));
        return cell;
    }

    private JPanel createDayCell(LocalDate date) {
        JPanel cell = new JPanel();
        cell.setLayout(new BorderLayout(2, 2));
        cell.setBorder(BorderFactory.createLineBorder(BORDER_COLOR));
        cell.setPreferredSize(new Dimension(80, 70));

        Double pnl = dailyPnL.get(date);
        Integer tradeCount = dailyTradeCounts.get(date);

        // Set background color based on P&L
        if (pnl != null) {
            cell.setBackground(pnl >= 0 ? PROFIT_COLOR : LOSS_COLOR);
        } else {
            cell.setBackground(Color.WHITE);
        }

        // Day number in top-left corner
        JLabel dayLabel = new JLabel(String.valueOf(date.getDayOfMonth()));
        dayLabel.setFont(new Font("Arial", Font.PLAIN, 10));
        dayLabel.setForeground(Color.GRAY);
        dayLabel.setBorder(new EmptyBorder(3, 5, 0, 0));
        cell.add(dayLabel, BorderLayout.NORTH);

        // P&L and trade count in center
        if (pnl != null) {
            JPanel dataPanel = new JPanel();
            dataPanel.setLayout(new BoxLayout(dataPanel, BoxLayout.Y_AXIS));
            dataPanel.setOpaque(false);

            // P&L label
            JLabel pnlLabel = new JLabel(formatPnL(pnl));
            pnlLabel.setFont(new Font("Arial", Font.BOLD, 12));
            pnlLabel.setForeground(pnl >= 0 ? new Color(0, 128, 0) : new Color(200, 0, 0));
            pnlLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

            // Trade count label
            JLabel countLabel = new JLabel(tradeCount + (tradeCount == 1 ? " trade" : " trades"));
            countLabel.setFont(new Font("Arial", Font.PLAIN, 9));
            countLabel.setForeground(pnl >= 0 ? new Color(0, 100, 0) : new Color(150, 0, 0));
            countLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

            dataPanel.add(Box.createVerticalGlue());
            dataPanel.add(pnlLabel);
            dataPanel.add(countLabel);
            dataPanel.add(Box.createVerticalGlue());

            cell.add(dataPanel, BorderLayout.CENTER);
        }

        return cell;
    }

    /**
     * Update the weekly P&L summary panel
     */
    private void updateWeeklyPnLPanel() {
        weeklyPnLPanel.removeAll();
        weeklyPnLPanel.setLayout(new BoxLayout(weeklyPnLPanel, BoxLayout.Y_AXIS));
        weeklyPnLPanel.setBorder(new EmptyBorder(0, 10, 0, 0));

        // Header
        JLabel headerLabel = new JLabel("P&L PER WEEK");
        headerLabel.setFont(new Font("Arial", Font.BOLD, 12));
        headerLabel.setForeground(Color.DARK_GRAY);
        headerLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        weeklyPnLPanel.add(headerLabel);
        weeklyPnLPanel.add(Box.createVerticalStrut(20));

        // Calculate weekly P&L for current month
        Map<Integer, Double> weeklyTotals = calculateWeeklyPnL();

        int weekNum = 1;
        for (Map.Entry<Integer, Double> entry : weeklyTotals.entrySet()) {
            double weekPnL = entry.getValue();

            JPanel weekPanel = new JPanel();
            weekPanel.setLayout(new BoxLayout(weekPanel, BoxLayout.Y_AXIS));
            weekPanel.setOpaque(false);
            weekPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

            // P&L value
            JLabel valueLabel = new JLabel(formatCurrency(weekPnL));
            valueLabel.setFont(new Font("Arial", Font.BOLD, 14));
            valueLabel.setForeground(weekPnL >= 0 ? new Color(0, 128, 0) : new Color(200, 0, 0));
            valueLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

            // Week label
            JLabel weekLabel = new JLabel("WEEK " + weekNum);
            weekLabel.setFont(new Font("Arial", Font.PLAIN, 10));
            weekLabel.setForeground(Color.GRAY);
            weekLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

            weekPanel.add(valueLabel);
            weekPanel.add(weekLabel);

            weeklyPnLPanel.add(weekPanel);
            weeklyPnLPanel.add(Box.createVerticalStrut(25));

            weekNum++;
        }

        weeklyPnLPanel.add(Box.createVerticalGlue());
        weeklyPnLPanel.revalidate();
        weeklyPnLPanel.repaint();
    }

    /**
     * Calculate P&L for each week of the current month
     */
    private Map<Integer, Double> calculateWeeklyPnL() {
        Map<Integer, Double> weeklyTotals = new LinkedHashMap<>();

        int daysInMonth = currentMonth.lengthOfMonth();

        int currentWeek = 1;
        double weekTotal = 0;

        for (int day = 1; day <= daysInMonth; day++) {
            LocalDate date = currentMonth.atDay(day);
            int dayOfWeek = date.getDayOfWeek().getValue() % 7;

            Double pnl = dailyPnL.get(date);
            if (pnl != null) {
                weekTotal += pnl;
            }

            // End of week (Saturday) or end of month
            if (dayOfWeek == 6 || day == daysInMonth) {
                weeklyTotals.put(currentWeek, weekTotal);
                currentWeek++;
                weekTotal = 0;
            }
        }

        return weeklyTotals;
    }

    /**
     * Format P&L value for display (e.g., $380, -$1.49K)
     */
    private String formatPnL(double value) {
        String sign = value >= 0 ? "" : "-";
        double absValue = Math.abs(value);

        if (absValue >= 1000) {
            return sign + "$" + String.format("%.2fK", absValue / 1000);
        } else {
            return sign + "$" + String.format("%.0f", absValue);
        }
    }

    /**
     * Format currency value (e.g., $1,234.56 or -$567.89)
     */
    private String formatCurrency(double value) {
        String sign = value >= 0 ? "" : "-";
        double absValue = Math.abs(value);
        return sign + "$" + String.format("%,.2f", absValue);
    }
}
