package com.tradingjournal.ui;

import com.tradingjournal.model.Trade;
import com.tradingjournal.model.TradeType;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Arc2D;
import java.awt.geom.Rectangle2D;
import java.util.List;

/**
 * Panel for displaying Long vs Short trades distribution as a pie chart
 */
public class LongShortTradesChartPanel extends JPanel {

    private int longCount = 0;
    private int shortCount = 0;
    private static final Color LONG_COLOR = new Color(34, 197, 94); // Green
    private static final Color SHORT_COLOR = new Color(239, 68, 68); // Red

    public LongShortTradesChartPanel() {
        setBackground(Color.WHITE);
        setPreferredSize(new Dimension(400, 300));
    }

    /**
     * Set trade data and calculate Long/Short counts
     */
    public void setTrades(List<Trade> trades) {
        longCount = 0;
        shortCount = 0;

        if (trades != null) {
            for (Trade trade : trades) {
                if (trade.getType() == TradeType.BUY) {
                    longCount++;
                } else if (trade.getType() == TradeType.SELL) {
                    shortCount++;
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

        int totalTrades = longCount + shortCount;

        if (totalTrades == 0) {
            // Draw "No data" message
            g2.setColor(Color.GRAY);
            g2.setFont(new Font("Arial", Font.PLAIN, 14));
            String msg = "No trades to display";
            FontMetrics fm = g2.getFontMetrics();
            int msgWidth = fm.stringWidth(msg);
            g2.drawString(msg, (getWidth() - msgWidth) / 2, getHeight() / 2);
            return;
        }

        // Calculate percentages
        double longPercentage = (longCount * 100.0) / totalTrades;
        double shortPercentage = (shortCount * 100.0) / totalTrades;

        // Calculate angles for pie chart (360 degrees total)
        double longAngle = (longCount * 360.0) / totalTrades;
        double shortAngle = (shortCount * 360.0) / totalTrades;

        // Define pie chart dimensions
        int diameter = Math.min(getWidth(), getHeight()) - 100;
        int x = (getWidth() - diameter) / 2;
        int y = (getHeight() - diameter) / 2 + 20; // Add offset for title

        // Draw Long trades slice
        g2.setColor(LONG_COLOR);
        Arc2D.Double longArc = new Arc2D.Double(x, y, diameter, diameter, 0, longAngle, Arc2D.PIE);
        g2.fill(longArc);
        g2.setColor(Color.BLACK);
        g2.setStroke(new BasicStroke(2));
        g2.draw(longArc);

        // Draw Short trades slice
        g2.setColor(SHORT_COLOR);
        Arc2D.Double shortArc = new Arc2D.Double(x, y, diameter, diameter, longAngle, shortAngle, Arc2D.PIE);
        g2.fill(shortArc);
        g2.setColor(Color.BLACK);
        g2.draw(shortArc);

        // Draw title
        g2.setColor(Color.BLACK);
        g2.setFont(new Font("Arial", Font.BOLD, 16));
        String title = "Long vs Short Trades";
        FontMetrics fm = g2.getFontMetrics();
        int titleWidth = fm.stringWidth(title);
        g2.drawString(title, (getWidth() - titleWidth) / 2, 25);

        // Draw legend for Long trades (on the right)
        int legendX = x + diameter + 20;
        int legendY = y + diameter / 2 - 30;

        drawLegend(g2, legendX, legendY, LONG_COLOR,
                "Long trades", String.format("(%d%%)", Math.round(longPercentage)));

        // Draw legend for Short trades (on the left)
        int shortLegendX = x - 120;
        int shortLegendY = y + diameter / 2 - 30;

        drawLegend(g2, shortLegendX, shortLegendY, SHORT_COLOR,
                "Short trades", String.format("(%d%%)", Math.round(shortPercentage)));
    }

    private void drawLegend(Graphics2D g2, int x, int y, Color color, String label, String percentage) {
        // Draw color box
        g2.setColor(color);
        Rectangle2D box = new Rectangle2D.Double(x, y, 20, 20);
        g2.fill(box);
        g2.setColor(Color.BLACK);
        g2.setStroke(new BasicStroke(1));
        g2.draw(box);

        // Draw label
        g2.setFont(new Font("Arial", Font.PLAIN, 12));
        g2.drawString(label, x + 25, y + 15);

        // Draw percentage below
        g2.setFont(new Font("Arial", Font.BOLD, 12));
        g2.drawString(percentage, x + 25, y + 30);
    }
}
