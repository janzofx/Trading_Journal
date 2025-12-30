package com.tradingjournal.ui;

import com.tradingjournal.model.Trade;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Panel for displaying trade entries by hour of day as a bar chart
 */
public class EntriesByHourChartPanel extends JPanel {

    private Map<Integer, Integer> entriesByHour = new HashMap<>();
    private static final Color BAR_COLOR = new Color(56, 189, 248); // Blue
    private static final int PADDING = 60;

    public EntriesByHourChartPanel() {
        setBackground(Color.WHITE);
        setPreferredSize(new Dimension(600, 250));
        initializeHours();
    }

    private void initializeHours() {
        for (int hour = 0; hour < 24; hour++) {
            entriesByHour.put(hour, 0);
        }
    }

    public void setTrades(List<Trade> trades) {
        initializeHours();

        if (trades != null) {
            for (Trade trade : trades) {
                int hour = trade.getOpenTime().getHour();
                entriesByHour.put(hour, entriesByHour.get(hour) + 1);
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

        // Find max value
        int maxEntries = entriesByHour.values().stream().max(Integer::compare).orElse(1);
        if (maxEntries == 0)
            maxEntries = 1;

        // Draw X-axis only (bottom line)
        g2.setColor(Color.BLACK);
        g2.setStroke(new BasicStroke(2));
        int zeroY = chartY + chartHeight;
        g2.drawLine(chartX, zeroY, chartX + chartWidth, zeroY);

        // Draw bars
        int barWidth = chartWidth / 24;
        for (int hour = 0; hour < 24; hour++) {
            int count = entriesByHour.get(hour);
            int barX = chartX + (hour * barWidth);
            drawBar(g2, barX, zeroY, barWidth - 2, count, maxEntries, chartHeight);
        }

        // Draw ALL hour labels (0-23) on the X-axis
        g2.setColor(Color.BLACK);
        g2.setFont(new Font("Arial", Font.PLAIN, 10));
        FontMetrics fm = g2.getFontMetrics();
        for (int h = 0; h < 24; h++) {
            int barX = chartX + (h * barWidth);
            String hourLabel = String.valueOf(h);
            int labelWidth = fm.stringWidth(hourLabel);
            // Center the label under the bar
            g2.drawString(hourLabel, barX + (barWidth - labelWidth) / 2, zeroY + 20);
        }

        // Draw title
        g2.setColor(Color.BLACK);
        g2.setFont(new Font("Arial", Font.BOLD, 16));
        String title = "Entries by hours";
        fm = g2.getFontMetrics();
        int titleWidth = fm.stringWidth(title);
        g2.drawString(title, (getWidth() - titleWidth) / 2, 25);
    }

    private void drawBar(Graphics2D g2, int x, int zeroY, int width, int count, int maxValue, int chartHeight) {
        if (count > 0) {
            int barHeight = (int) ((count / (double) maxValue) * chartHeight);
            int barY = zeroY - barHeight; // zeroY is the bottom of the chart
            g2.setColor(BAR_COLOR);
            Rectangle2D bar = new Rectangle2D.Double(x, barY, width, barHeight);
            g2.fill(bar);
            g2.setColor(Color.BLACK);
            g2.draw(bar);

            // Draw count on top of bar
            g2.setFont(new Font("Arial", Font.BOLD, 11));
            String valueText = String.valueOf(count);
            FontMetrics fm = g2.getFontMetrics();
            int textWidth = fm.stringWidth(valueText);
            int textX = x + (width - textWidth) / 2;
            int textY = barY - 5;
            g2.setColor(Color.BLACK);
            g2.drawString(valueText, textX, textY);
        }
    }
}
