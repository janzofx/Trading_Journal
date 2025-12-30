package com.tradingjournal.ui;

import com.tradingjournal.model.EquityPoint;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Line2D;
import java.text.DecimalFormat;
import java.util.List;

/**
 * Panel for displaying equity curve chart
 */
public class EquityCurvePanel extends JPanel {

    private List<EquityPoint> equityCurve;
    private static final DecimalFormat MONEY_FORMAT = new DecimalFormat("$#,##0.00");
    private static final int PADDING = 60;

    public EquityCurvePanel() {
        setBackground(Color.WHITE);
        setPreferredSize(new Dimension(600, 300));
    }

    public void setEquityCurve(List<EquityPoint> equityCurve) {
        this.equityCurve = equityCurve;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (equityCurve == null || equityCurve.isEmpty()) {
            g.setColor(Color.GRAY);
            g.drawString("No data to display", getWidth() / 2 - 50, getHeight() / 2);
            return;
        }

        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int width = getWidth() - 2 * PADDING;
        int height = getHeight() - 2 * PADDING;

        // Find min and max profit for scaling
        double minProfit = equityCurve.stream()
                .mapToDouble(EquityPoint::getCumulativeProfit)
                .min().orElse(0);
        double maxProfit = equityCurve.stream()
                .mapToDouble(EquityPoint::getCumulativeProfit)
                .max().orElse(0);

        // Add some padding to the range
        double range = maxProfit - minProfit;
        if (range == 0)
            range = 100;
        minProfit -= range * 0.1;
        maxProfit += range * 0.1;

        // Draw axes
        g2.setColor(Color.BLACK);
        g2.drawLine(PADDING, PADDING, PADDING, getHeight() - PADDING); // Y-axis
        g2.drawLine(PADDING, getHeight() - PADDING, getWidth() - PADDING, getHeight() - PADDING); // X-axis

        // Draw zero line if in range
        if (minProfit < 0 && maxProfit > 0) {
            int zeroY = getHeight() - PADDING - (int) ((0 - minProfit) / (maxProfit - minProfit) * height);
            g2.setColor(new Color(200, 200, 200));
            g2.drawLine(PADDING, zeroY, getWidth() - PADDING, zeroY);
        }

        // Draw Y-axis labels
        g2.setColor(Color.BLACK);
        g2.setFont(new Font("Arial", Font.PLAIN, 10));
        for (int i = 0; i <= 5; i++) {
            double value = minProfit + (maxProfit - minProfit) * i / 5.0;
            int y = getHeight() - PADDING - (i * height / 5);
            String label = MONEY_FORMAT.format(value);
            g2.drawString(label, 5, y + 5);
            g2.setColor(new Color(230, 230, 230));
            g2.drawLine(PADDING, y, getWidth() - PADDING, y);
            g2.setColor(Color.BLACK);
        }

        // Draw equity curve
        g2.setColor(new Color(0, 120, 215));
        g2.setStroke(new BasicStroke(2.0f));

        for (int i = 0; i < equityCurve.size() - 1; i++) {
            EquityPoint p1 = equityCurve.get(i);
            EquityPoint p2 = equityCurve.get(i + 1);

            int x1 = PADDING + (i * width / (equityCurve.size() - 1));
            int y1 = getHeight() - PADDING
                    - (int) ((p1.getCumulativeProfit() - minProfit) / (maxProfit - minProfit) * height);

            int x2 = PADDING + ((i + 1) * width / (equityCurve.size() - 1));
            int y2 = getHeight() - PADDING
                    - (int) ((p2.getCumulativeProfit() - minProfit) / (maxProfit - minProfit) * height);

            g2.draw(new Line2D.Double(x1, y1, x2, y2));
        }

        // Draw title
        g2.setColor(Color.BLACK);
        g2.setFont(new Font("Arial", Font.BOLD, 14));
        g2.drawString("Equity Curve", getWidth() / 2 - 40, 30);

        // Draw trade count (equity curve has +1 point for starting balance, so subtract
        // 1)
        g2.setFont(new Font("Arial", Font.PLAIN, 11));
        int actualTradeCount = equityCurve.size() > 0 ? equityCurve.size() - 1 : 0;
        g2.drawString("Trades: " + actualTradeCount, PADDING, getHeight() - 10);

        // Draw final P&L
        EquityPoint lastPoint = equityCurve.get(equityCurve.size() - 1);
        String finalPL = "Final P&L: " + MONEY_FORMAT.format(lastPoint.getCumulativeProfit());
        Color plColor = lastPoint.getCumulativeProfit() >= 0 ? new Color(0, 150, 0) : new Color(200, 0, 0);
        g2.setColor(plColor);
        g2.setFont(new Font("Arial", Font.BOLD, 12));
        g2.drawString(finalPL, getWidth() - PADDING - 150, getHeight() - 10);
    }
}
