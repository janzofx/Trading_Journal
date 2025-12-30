package com.tradingjournal.ui;

import com.tradingjournal.model.Trade;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.time.Month;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Panel for displaying trade entries by month as a bar chart
 */
public class EntriesByMonthChartPanel extends JPanel {

    private Map<Month, Integer> entriesByMonth = new HashMap<>();
    private static final Color BAR_COLOR = new Color(56, 189, 248); // Blue
    private static final int PADDING = 60;

    private static final Month[] MONTHS = Month.values();
    private static final String[] MONTH_LABELS = {
            "Jan", "Feb", "Mar", "Apr", "May", "Jun",
            "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"
    };

    public EntriesByMonthChartPanel() {
        setBackground(Color.WHITE);
        setPreferredSize(new Dimension(700, 250));
        initializeMonths();
    }

    private void initializeMonths() {
        for (Month month : MONTHS) {
            entriesByMonth.put(month, 0);
        }
    }

    public void setTrades(List<Trade> trades) {
        initializeMonths();

        if (trades != null) {
            for (Trade trade : trades) {
                Month month = trade.getOpenTime().getMonth();
                entriesByMonth.put(month, entriesByMonth.get(month) + 1);
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

        int maxEntries = entriesByMonth.values().stream().max(Integer::compare).orElse(1);
        if (maxEntries == 0)
            maxEntries = 1;

        // Draw X-axis only (bottom line)
        g2.setColor(Color.BLACK);
        g2.setStroke(new BasicStroke(2));
        int zeroY = chartY + chartHeight;
        g2.drawLine(chartX, zeroY, chartX + chartWidth, zeroY);

        // Draw bars
        int barWidth = chartWidth / 15;
        int spacing = chartWidth / 12;

        for (int i = 0; i < MONTHS.length; i++) {
            Month month = MONTHS[i];
            int count = entriesByMonth.get(month);

            if (count > 0) {
                int barHeight = (int) ((count / (double) maxEntries) * chartHeight);
                int barX = chartX + (i * spacing) + spacing / 2 - barWidth / 2;
                int barY = chartY + chartHeight - barHeight;

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

            // Month label
            g2.setFont(new Font("Arial", Font.PLAIN, 10));
            String label = MONTH_LABELS[i];
            FontMetrics fm = g2.getFontMetrics();
            int textWidth = fm.stringWidth(label);
            int textX = chartX + (i * spacing) + spacing / 2 - textWidth / 2; // Adjusted to center under the bar's
                                                                              // general position
            g2.drawString(label, textX, chartY + chartHeight + 20); // Using chartY + chartHeight for the base of the
                                                                    // axis
        }

        // Title
        g2.setColor(Color.BLACK);
        g2.setFont(new Font("Arial", Font.BOLD, 14));
        String title = "Entries by months";
        FontMetrics fm = g2.getFontMetrics();
        g2.drawString(title, (getWidth() - fm.stringWidth(title)) / 2, 20);
    }
}
