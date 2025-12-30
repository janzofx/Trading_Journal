package com.tradingjournal.model;

import java.time.LocalDateTime;

/**
 * Represents a point in the equity curve
 */
public class EquityPoint {
    private LocalDateTime timestamp;
    private double cumulativeProfit;
    private int tradeNumber;
    private String tradeTicket;

    public EquityPoint(LocalDateTime timestamp, double cumulativeProfit, int tradeNumber, String tradeTicket) {
        this.timestamp = timestamp;
        this.cumulativeProfit = cumulativeProfit;
        this.tradeNumber = tradeNumber;
        this.tradeTicket = tradeTicket;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public double getCumulativeProfit() {
        return cumulativeProfit;
    }

    public int getTradeNumber() {
        return tradeNumber;
    }

    public String getTradeTicket() {
        return tradeTicket;
    }
}
