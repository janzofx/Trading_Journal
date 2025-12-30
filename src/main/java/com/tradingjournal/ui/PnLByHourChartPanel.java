package com.tradingjournal.ui;

import com.tradingjournal.model.Trade;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Panel for displaying P/L by hour as a bar chart (similar to weekday chart)
 */
public class PnLByHourChartPanel extends JPanel {

    private Map<Integer, Double> pnlByHour = new HashMap<>();
    private static final DecimalFormat MONEY_FORMAT = new DecimalFormat("$#,##0");
    private static final Color PROFIT_COLOR = new Color(34, 197, 94); // Green
    private static final Color LOSS_COLOR = new Color(239, 68, 68); // Red
    private static final int PADDING = 60;

    public PnLByHourChartPanel() {
        setBackground(Color.WHITE);
        setPreferredSize(new Dimension(700, 300));
        initializeHours();
    }

    private void initializeHours() {
        for (int hour = 0; hour < 24; hour++) {
            pnlByHour.put(hour, 0.0);
        }
    }

    public void setTrades(List<Trade> trades) {
        initializeHours();

        if (trades != null) {
            for (Trade trade : trades) {
                int hour = trade.getCloseTime().getHour();
                double currentPnL = pnlByHour.getOrDefault(hour, 0.0);
                pnlByHour.put(hour, currentPnL + trade.getNetProfit());
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

        // Find max value for scaling
        double maxValue = 0;
        double minValue = 0;
        for (double pnl : pnlByHour.values()) {
            if (pnl > maxValue)
                maxValue = pnl;
            if (pnl < minValue)
                minValue = pnl;
        }

        // Add padding to range
        double range = Math.max(Math.abs(maxValue), Math.abs(minValue));
        if (range == 0)
            range = 100;
        range *= 1.1;
        maxValue = range;
        minValue = -range;

        // Draw axes
        g2.setColor(Color.BLACK);
        g2.setStroke(new BasicStroke(2));
        int zeroY = chartY + (int) (chartHeight * maxValue / (maxValue - minValue));
        g2.drawLine(chartX, chartY, chartX, chartY + chartHeight); // Y-axis
        g2.drawLine(chartX, zeroY, chartX + chartWidth, zeroY); // X-axis (zero line)

        // Draw Y-axis labels and grid lines
        g2.setFont(new Font("Arial", Font.PLAIN, 9));
        int numGridLines = 6;
        for (int i = 0; i <= numGridLines; i++) {
            double value = maxValue - (i * (maxValue - minValue) / numGridLines);
            int y = chartY + (i * chartHeight / numGridLines);

            // Grid line
            g2.setColor(Color.LIGHT_GRAY);
            g2.setStroke(new BasicStroke(1));
            g2.drawLine(chartX, y, chartX + chartWidth, y);

            // Label
            g2.setColor(Color.BLACK);
            String label = MONEY_FORMAT.format(value);
            g2.drawString(label, 5, y + 3);
        }

        // Draw bars
        int barWidth = chartWidth / 24;
        for (int hour = 0; hour < 24; hour++) {
            double pnl = pnlByHour.get(hour);

            int barX = chartX + (hour * barWidth);
            drawBar(g2, barX, zeroY, barWidth - 2, pnl, maxValue, minValue, chartHeight, String.valueOf(hour));
        }

        // Draw ALL hour labels (0-23) on the X-axis - larger and more visible
        g2.setColor(Color.BLACK);
        g2.setFont(new Font("Arial", Font.PLAIN, 10));
        FontMetrics fm = g2.getFontMetrics();
        for (int hour = 0; hour < 24; hour++) {
            int barX = chartX + (hour * barWidth);
            String hourLabel = String.valueOf(hour);
            int labelWidth = fm.stringWidth(hourLabel);
            // Center the label under the bar
            g2.drawString(hourLabel, barX + (barWidth - labelWidth) / 2, zeroY + 20);
        }

        // Title
        g2.setColor(Color.BLACK);
        g2.setFont(new Font("Arial", Font.BOLD, 14));
        String title = "Profits and losses by hours";
        fm = g2.getFontMetrics();
        g2.drawString(title, (getWidth() - fm.stringWidth(title)) / 2, 25);
    }

    private void drawBar(Graphics2D g2, int x, int zeroY, int width, double value,
            double maxValue, double minValue, int chartHeight, String label) {
        // Remove the hour-checking logic - it's now drawn separately

        if (value == 0) {
            return;
        }

        // Calculate bar height proportional to value
        int barHeight = (int) ((Math.abs(value) / (maxValue - minValue)) * chartHeight);

        // Determine color
        Color barColor = value >= 0 ? PROFIT_COLOR : LOSS_COLOR;
        g2.setColor(barColor);

        // Draw bar (upward for positive, downward for negative)
        int barY;
        if (value >= 0) {
            barY = zeroY - barHeight;
        } else {
            barY = zeroY;
        }

        Rectangle2D bar = new Rectangle2D.Double(x, barY, width, barHeight);
        g2.fill(bar);

        // Draw bar outline
        g2.setColor(Color.BLACK);
        g2.setStroke(new BasicStroke(1));
        g2.draw(bar);

        // Draw dollar value outside the bar (clearly visible)
        g2.setFont(new Font("Arial", Font.BOLD, 11));
        String valueText = MONEY_FORMAT.format(value);
        FontMetrics fm = g2.getFontMetrics();
        int textWidth = fm.stringWidth(valueText);
        int textX = x + (width - textWidth) / 2;
        int textY = value >= 0 ? barY - 8 : barY + barHeight + 15;
        g2.setColor(Color.BLACK);
        g2.drawString(valueText, textX, textY);
    }
}
