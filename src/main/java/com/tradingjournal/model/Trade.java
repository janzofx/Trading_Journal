package com.tradingjournal.model;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Represents a single trade from MT5
 */
public class Trade {
    private String ticket;
    private LocalDateTime openTime;
    private LocalDateTime closeTime;
    private TradeType type;
    private String symbol;
    private double size;
    private double openPrice;
    private double closePrice;
    private double stopLoss;
    private double takeProfit;
    private double profit;
    private double commission;
    private double swap;
    private String comment;
    private String strategy; // Strategy tagging
    private String account; // Account identifier for multi-account support
    private long magicNumber; // Magic number for EA identification

    public Trade() {
    }

    public Trade(String ticket, LocalDateTime openTime, TradeType type, String symbol,
            double size, double openPrice) {
        this.ticket = ticket;
        this.openTime = openTime;
        this.type = type;
        this.symbol = symbol;
        this.size = size;
        this.openPrice = openPrice;
    }

    // Getters and Setters
    public String getTicket() {
        return ticket;
    }

    public void setTicket(String ticket) {
        this.ticket = ticket;
    }

    public LocalDateTime getOpenTime() {
        return openTime;
    }

    public void setOpenTime(LocalDateTime openTime) {
        this.openTime = openTime;
    }

    public LocalDateTime getCloseTime() {
        return closeTime;
    }

    public void setCloseTime(LocalDateTime closeTime) {
        this.closeTime = closeTime;
    }

    public TradeType getType() {
        return type;
    }

    public void setType(TradeType type) {
        this.type = type;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public double getSize() {
        return size;
    }

    public void setSize(double size) {
        this.size = size;
    }

    public double getOpenPrice() {
        return openPrice;
    }

    public void setOpenPrice(double openPrice) {
        this.openPrice = openPrice;
    }

    public double getClosePrice() {
        return closePrice;
    }

    public void setClosePrice(double closePrice) {
        this.closePrice = closePrice;
    }

    public double getStopLoss() {
        return stopLoss;
    }

    public void setStopLoss(double stopLoss) {
        this.stopLoss = stopLoss;
    }

    public double getTakeProfit() {
        return takeProfit;
    }

    public void setTakeProfit(double takeProfit) {
        this.takeProfit = takeProfit;
    }

    public double getProfit() {
        return profit;
    }

    public void setProfit(double profit) {
        this.profit = profit;
    }

    public double getCommission() {
        return commission;
    }

    public void setCommission(double commission) {
        this.commission = commission;
    }

    public double getSwap() {
        return swap;
    }

    public void setSwap(double swap) {
        this.swap = swap;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getStrategy() {
        return strategy;
    }

    public void setStrategy(String strategy) {
        this.strategy = strategy;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public long getMagicNumber() {
        return magicNumber;
    }

    public void setMagicNumber(long magicNumber) {
        this.magicNumber = magicNumber;
    }

    /**
     * Calculate net profit including commission and swap
     */
    public double getNetProfit() {
        return profit + commission + swap;
    }

    /**
     * Check if this is a winning trade
     */
    public boolean isWinner() {
        return getNetProfit() > 0;
    }

    /**
     * Check if this is a losing trade
     */
    public boolean isLoser() {
        return getNetProfit() < 0;
    }

    /**
     * Check if trade is closed
     */
    public boolean isClosed() {
        return closeTime != null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Trade trade = (Trade) o;
        return Objects.equals(ticket, trade.ticket);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ticket);
    }

    @Override
    public String toString() {
        return "Trade{" +
                "ticket='" + ticket + '\'' +
                ", symbol='" + symbol + '\'' +
                ", type=" + type +
                ", profit=" + getNetProfit() +
                '}';
    }
}
