package com.tradingjournal.ui;

import com.tradingjournal.model.Trade;
import com.tradingjournal.model.TradeType;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.text.DecimalFormat;
import java.util.List;

/**
 * Panel for displaying Long vs Short profit bar chart
 */
public class LongShortProfitChartPanel extends JPanel {

    private double longProfit = 0.0;
    private double shortProfit = 0.0;
    private static final DecimalFormat MONEY_FORMAT = new DecimalFormat("$#,##0.00");
    private static final Color PROFIT_COLOR = new Color(34, 197, 94); // Green
    private static final Color LOSS_COLOR = new Color(239, 68, 68); // Red
    private static final int PADDING = 60;

    public LongShortProfitChartPanel() {
        setBackground(Color.WHITE);
        setPreferredSize(new Dimension(400, 300));
    }

    /**
     * Set trade data and calculate Long/Short profit
     */
    public void setTrades(List<Trade> trades) {
        longProfit = 0.0;
        shortProfit = 0.0;

        if (trades != null) {
            for (Trade trade : trades) {
                double profit = trade.getNetProfit();
                if (trade.getType() == TradeType.BUY) {
                    longProfit += profit;
                } else if (trade.getType() == TradeType.SELL) {
                    shortProfit += profit;
                }
            }
        }

        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Define chart area
        int chartWidth = getWidth() - 2 * PADDING;
        int chartHeight = getHeight() - 2 * PADDING;
        int chartX = PADDING;
        int chartY = PADDING;

        // Find max value for scaling
        double maxValue = Math.max(Math.abs(longProfit), Math.abs(shortProfit));
        if (maxValue == 0) {
            maxValue = 100; // Prevent division by zero
        }
        // Add 10% padding to max value
        maxValue *= 1.1;

        // Draw axes
        g2.setColor(Color.BLACK);
        g2.setStroke(new BasicStroke(2));
        int zeroY = chartY + chartHeight / 2; // Y-axis at center for positive/negative values
        g2.drawLine(chartX, chartY, chartX, chartY + chartHeight); // Y-axis
        g2.drawLine(chartX, zeroY, chartX + chartWidth, zeroY); // X-axis (at zero line)

        // Draw Y-axis labels and grid lines
        g2.setFont(new Font("Arial", Font.PLAIN, 10));
        g2.setColor(Color.LIGHT_GRAY);
        g2.setStroke(new BasicStroke(1));

        int numGridLines = 8;
        for (int i = 0; i <= numGridLines; i++) {
            double value = maxValue - (i * 2 * maxValue / numGridLines);
            int y = chartY + (i * chartHeight / numGridLines);

            // Grid line
            g2.drawLine(chartX, y, chartX + chartWidth, y);

            // Label
            g2.setColor(Color.BLACK);
            String label = MONEY_FORMAT.format(value);
            g2.drawString(label, 5, y + 5);
            g2.setColor(Color.LIGHT_GRAY);
        }

        // Bar width and positions
        int barWidth = chartWidth / 5; // Leave space between bars
        int longBarX = chartX + chartWidth / 4 - barWidth / 2;
        int shortBarX = chartX + 3 * chartWidth / 4 - barWidth / 2;

        // Draw Long P/L bar
        drawBar(g2, longBarX, zeroY, barWidth, longProfit, maxValue, chartHeight, "Long P/L");

        // Draw Short P/L bar
        drawBar(g2, shortBarX, zeroY, barWidth, shortProfit, maxValue, chartHeight, "Short P/L");

        // Draw title
        g2.setColor(Color.BLACK);
        g2.setFont(new Font("Arial", Font.BOLD, 16));
        String title = "P/L / Order Type";
        FontMetrics fm = g2.getFontMetrics();
        int titleWidth = fm.stringWidth(title);
        g2.drawString(title, (getWidth() - titleWidth) / 2, 30);
    }

    private void drawBar(Graphics2D g2, int x, int zeroY, int width, double value, double maxValue, int chartHeight,
            String label) {
        // Calculate bar height (proportional to value)
        int barHeight = (int) ((Math.abs(value) / maxValue) * (chartHeight / 2));

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

        // Draw value on top of bar
        g2.setFont(new Font("Arial", Font.BOLD, 12));
        String valueText = MONEY_FORMAT.format(value);
        FontMetrics fm = g2.getFontMetrics();
        int textWidth = fm.stringWidth(valueText);
        int textX = x + (width - textWidth) / 2;
        int textY = value >= 0 ? barY - 5 : barY + barHeight + 15;
        g2.setColor(Color.BLACK);
        g2.drawString(valueText, textX, textY);

        // Draw label below X-axis
        g2.setFont(new Font("Arial", Font.PLAIN, 11));
        fm = g2.getFontMetrics();
        textWidth = fm.stringWidth(label);
        textX = x + (width - textWidth) / 2;
        g2.drawString(label, textX, zeroY + 20);
    }
}
