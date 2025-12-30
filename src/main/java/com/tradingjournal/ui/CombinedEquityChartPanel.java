package com.tradingjournal.ui;

import com.tradingjournal.model.EquityPoint;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Line2D;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.util.*;
import java.util.List;

/**
 * Panel for displaying multiple strategy equity curves on a single chart
 */
public class CombinedEquityChartPanel extends JPanel {

    private Map<String, List<EquityPoint>> strategyCurves;
    private Map<String, Color> strategyColors;
    private static final Color[] DEFAULT_COLORS = {
            new Color(50, 100, 200), // Blue
            new Color(255, 150, 50), // Orange
            new Color(200, 50, 50), // Red
            new Color(50, 150, 50), // Green
            new Color(150, 50, 200), // Purple
            new Color(200, 150, 50), // Gold
            new Color(50, 200, 200), // Cyan
            new Color(200, 100, 150) // Pink
    };

    public CombinedEquityChartPanel() {
        this.strategyCurves = new HashMap<>();
        this.strategyColors = new HashMap<>();
        setPreferredSize(new Dimension(800, 400));
        setBackground(Color.WHITE);
    }

    /**
     * Set the equity curves for multiple strategies
     */
    public void setStrategyCurves(Map<String, List<EquityPoint>> curves) {
        this.strategyCurves = curves;

        // Assign colors to strategies
        strategyColors.clear();
        int colorIndex = 0;
        for (String strategy : curves.keySet()) {
            strategyColors.put(strategy, DEFAULT_COLORS[colorIndex % DEFAULT_COLORS.length]);
            colorIndex++;
        }

        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        if (strategyCurves == null || strategyCurves.isEmpty()) {
            // Draw message
            g2.setColor(Color.GRAY);
            String message = "No strategy data available";
            FontMetrics fm = g2.getFontMetrics();
            int x = (getWidth() - fm.stringWidth(message)) / 2;
            int y = getHeight() / 2;
            g2.drawString(message, x, y);
            return;
        }

        // Define chart area
        int padding = 60;
        int chartWidth = getWidth() - 2 * padding;
        int chartHeight = getHeight() - 2 * padding;
        int chartX = padding;
        int chartY = padding;

        // Find global min/max values across all strategies
        double minEquity = Double.MAX_VALUE;
        double maxEquity = Double.MIN_VALUE;
        long minTime = Long.MAX_VALUE;
        long maxTime = Long.MIN_VALUE;

        for (List<EquityPoint> curve : strategyCurves.values()) {
            if (curve.isEmpty())
                continue;

            for (EquityPoint point : curve) {
                double equity = point.getCumulativeProfit();
                minEquity = Math.min(minEquity, equity);
                maxEquity = Math.max(maxEquity, equity);

                long time = point.getTimestamp().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
                minTime = Math.min(minTime, time);
                maxTime = Math.max(maxTime, time);
            }
        }

        // Handle edge cases
        if (minEquity == maxEquity) {
            maxEquity = minEquity + 100;
        }
        if (minTime == maxTime) {
            maxTime = minTime + 86400000; // Add 1 day
        }

        // Add some padding to the equity range
        double equityRange = maxEquity - minEquity;
        minEquity -= equityRange * 0.1;
        maxEquity += equityRange * 0.1;

        // Draw axes
        g2.setColor(Color.BLACK);
        g2.setStroke(new BasicStroke(2));
        g2.drawLine(chartX, chartY + chartHeight, chartX + chartWidth, chartY + chartHeight); // X-axis
        g2.drawLine(chartX, chartY, chartX, chartY + chartHeight); // Y-axis

        // Draw grid lines and Y-axis labels
        g2.setColor(Color.LIGHT_GRAY);
        g2.setStroke(new BasicStroke(1));
        int numYGridLines = 8;
        for (int i = 0; i <= numYGridLines; i++) {
            int y = chartY + chartHeight - (i * chartHeight / numYGridLines);
            g2.drawLine(chartX, y, chartX + chartWidth, y);

            // Y-axis label
            double value = minEquity + (i * (maxEquity - minEquity) / numYGridLines);
            String label = String.format("$%.0f", value);
            g2.setColor(Color.BLACK);
            g2.drawString(label, chartX - 50, y + 5);
            g2.setColor(Color.LIGHT_GRAY);
        }

        // Draw X-axis labels (time)
        g2.setColor(Color.BLACK);
        SimpleDateFormat sdf = new SimpleDateFormat("MMM yyyy");
        int numXLabels = 6;
        for (int i = 0; i <= numXLabels; i++) {
            long time = minTime + (i * (maxTime - minTime) / numXLabels);
            int x = chartX + (i * chartWidth / numXLabels);

            Date date = new Date(time);
            String label = sdf.format(date);
            FontMetrics fm = g2.getFontMetrics();
            int labelWidth = fm.stringWidth(label);
            g2.drawString(label, x - labelWidth / 2, chartY + chartHeight + 20);
        }

        // Draw equity curves for each strategy
        g2.setStroke(new BasicStroke(2));
        for (Map.Entry<String, List<EquityPoint>> entry : strategyCurves.entrySet()) {
            String strategy = entry.getKey();
            List<EquityPoint> curve = entry.getValue();

            if (curve.isEmpty())
                continue;

            Color color = strategyColors.get(strategy);
            g2.setColor(color);

            // Draw line
            for (int i = 0; i < curve.size() - 1; i++) {
                EquityPoint p1 = curve.get(i);
                EquityPoint p2 = curve.get(i + 1);

                long t1 = p1.getTimestamp().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
                long t2 = p2.getTimestamp().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();

                double x1 = chartX + ((t1 - minTime) / (double) (maxTime - minTime)) * chartWidth;
                double y1 = chartY + chartHeight
                        - ((p1.getCumulativeProfit() - minEquity) / (maxEquity - minEquity)) * chartHeight;
                double x2 = chartX + ((t2 - minTime) / (double) (maxTime - minTime)) * chartWidth;
                double y2 = chartY + chartHeight
                        - ((p2.getCumulativeProfit() - minEquity) / (maxEquity - minEquity)) * chartHeight;

                g2.draw(new Line2D.Double(x1, y1, x2, y2));
            }
        }

        // Draw legend
        int legendX = chartX + chartWidth - 150;
        int legendY = chartY + 20;
        int legendItemHeight = 20;

        g2.setColor(new Color(255, 255, 255, 200));
        g2.fillRect(legendX - 5, legendY - 15, 145, strategyCurves.size() * legendItemHeight + 10);
        g2.setColor(Color.BLACK);
        g2.drawRect(legendX - 5, legendY - 15, 145, strategyCurves.size() * legendItemHeight + 10);

        int index = 0;
        for (String strategy : strategyCurves.keySet()) {
            Color color = strategyColors.get(strategy);

            // Draw color box
            g2.setColor(color);
            g2.fillRect(legendX, legendY + index * legendItemHeight, 15, 15);
            g2.setColor(Color.BLACK);
            g2.drawRect(legendX, legendY + index * legendItemHeight, 15, 15);

            // Draw strategy name
            g2.drawString(strategy, legendX + 20, legendY + index * legendItemHeight + 12);

            index++;
        }

        // Draw title
        g2.setFont(new Font("Arial", Font.BOLD, 16));
        String title = "Strategy Equity Comparison";
        FontMetrics fm = g2.getFontMetrics();
        int titleWidth = fm.stringWidth(title);
        g2.drawString(title, (getWidth() - titleWidth) / 2, 30);
    }
}
