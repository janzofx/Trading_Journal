package com.tradingjournal.ui;

import com.tradingjournal.model.Trade;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.time.DayOfWeek;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Panel for displaying trade entries by weekday as a bar chart
 */
public class EntriesByWeekdayChartPanel extends JPanel {

    private Map<DayOfWeek, Integer> entriesByDay = new HashMap<>();
    private static final Color BAR_COLOR = new Color(34, 197, 94); // Green
    private static final int PADDING = 60;

    private static final DayOfWeek[] DAYS = {
            DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY,
            DayOfWeek.THURSDAY, DayOfWeek.FRIDAY, DayOfWeek.SATURDAY, DayOfWeek.SUNDAY
    };

    private static final String[] DAY_LABELS = { "Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat" };

    public EntriesByWeekdayChartPanel() {
        setBackground(Color.WHITE);
        setPreferredSize(new Dimension(500, 250));
        initializeDays();
    }

    private void initializeDays() {
        for (DayOfWeek day : DAYS) {
            entriesByDay.put(day, 0);
        }
    }

    public void setTrades(List<Trade> trades) {
        initializeDays();

        if (trades != null) {
            for (Trade trade : trades) {
                DayOfWeek day = trade.getOpenTime().getDayOfWeek();
                entriesByDay.put(day, entriesByDay.get(day) + 1);
            }
        }

        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int chartWidth = getWidth() - 2 * PADDING;
        int chartHeight = getHeight() - 2 * PADDING;
        int chartX = PADDING;
        int chartY = PADDING;

        int maxEntries = entriesByDay.values().stream().max(Integer::compare).orElse(1);
        if (maxEntries == 0)
            maxEntries = 1;

        // Draw X-axis only (bottom line)
        g2.setColor(Color.BLACK);
        g2.setStroke(new BasicStroke(2));
        int zeroY = chartY + chartHeight;
        g2.drawLine(chartX, zeroY, chartX + chartWidth, zeroY);

        // Draw bars
        int barWidth = chartWidth / 10;
        int spacing = chartWidth / 7;

        for (int i = 0; i < DAYS.length; i++) {
            DayOfWeek day = DAYS[i];
            int count = entriesByDay.get(day);

            if (count > 0) {
                int barHeight = (int) ((count / (double) maxEntries) * chartHeight);
                int barX = chartX + (i * spacing) + spacing / 2 - barWidth / 2;
                int barY = zeroY - barHeight;

                g2.setColor(BAR_COLOR);
                Rectangle2D bar = new Rectangle2D.Double(barX, barY, barWidth, barHeight);
                g2.fill(bar);
                g2.setColor(Color.BLACK);
                g2.setStroke(new BasicStroke(1));
                g2.draw(bar);

                // Draw count on top of bar
                g2.setFont(new Font("Arial", Font.BOLD, 11));
                String valueText = String.valueOf(count);
                FontMetrics fm = g2.getFontMetrics();
                int textWidth = fm.stringWidth(valueText);
                int textX = barX + (barWidth - textWidth) / 2;
                int textY = barY - 5;
                g2.setColor(Color.BLACK);
                g2.drawString(valueText, textX, textY);
            }

            // Draw day label
            g2.setFont(new Font("Arial", Font.PLAIN, 11));
            String label = DAY_LABELS[i];
            FontMetrics fm = g2.getFontMetrics();
            int textWidth = fm.stringWidth(label);
            int textX = chartX + (i * spacing) + spacing / 2 - textWidth / 2;
            g2.drawString(label, textX, zeroY + 20);
        }

        // Title
        g2.setColor(Color.BLACK);
        g2.setFont(new Font("Arial", Font.BOLD, 14));
        String title = "Entries by weekdays";
        FontMetrics fm = g2.getFontMetrics();
        g2.drawString(title, (getWidth() - fm.stringWidth(title)) / 2, 20);
    }
}
