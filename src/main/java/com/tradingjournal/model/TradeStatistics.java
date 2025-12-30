package com.tradingjournal.model;

/**
 * Statistics aggregation for trade analysis
 */
public class TradeStatistics {
    private int totalTrades;
    private int winningTrades;
    private int losingTrades;
    private double totalProfit;
    private double totalLoss;
    private double netProfit;
    private double largestWin;
    private double largestLoss;
    private double averageWin;
    private double averageLoss;
    private double winRate;
    private double profitFactor;

    public TradeStatistics() {
    }

    // Getters and Setters
    public int getTotalTrades() {
        return totalTrades;
    }

    public void setTotalTrades(int totalTrades) {
        this.totalTrades = totalTrades;
    }

    public int getWinningTrades() {
        return winningTrades;
    }

    public void setWinningTrades(int winningTrades) {
        this.winningTrades = winningTrades;
    }

    public int getLosingTrades() {
        return losingTrades;
    }

    public void setLosingTrades(int losingTrades) {
        this.losingTrades = losingTrades;
    }

    public double getTotalProfit() {
        return totalProfit;
    }

    public void setTotalProfit(double totalProfit) {
        this.totalProfit = totalProfit;
    }

    public double getTotalLoss() {
        return totalLoss;
    }

    public void setTotalLoss(double totalLoss) {
        this.totalLoss = totalLoss;
    }

    public double getNetProfit() {
        return netProfit;
    }

    public void setNetProfit(double netProfit) {
        this.netProfit = netProfit;
    }

    public double getLargestWin() {
        return largestWin;
    }

    public void setLargestWin(double largestWin) {
        this.largestWin = largestWin;
    }

    public double getLargestLoss() {
        return largestLoss;
    }

    public void setLargestLoss(double largestLoss) {
        this.largestLoss = largestLoss;
    }

    public double getAverageWin() {
        return averageWin;
    }

    public void setAverageWin(double averageWin) {
        this.averageWin = averageWin;
    }

    public double getAverageLoss() {
        return averageLoss;
    }

    public void setAverageLoss(double averageLoss) {
        this.averageLoss = averageLoss;
    }

    public double getWinRate() {
        return winRate;
    }

    public void setWinRate(double winRate) {
        this.winRate = winRate;
    }

    public double getProfitFactor() {
        return profitFactor;
    }

    public void setProfitFactor(double profitFactor) {
        this.profitFactor = profitFactor;
    }

    @Override
    public String toString() {
        return String.format(
                "Statistics: %d trades, %.2f%% win rate, $%.2f net profit, %.2f profit factor",
                totalTrades, winRate * 100, netProfit, profitFactor);
    }
}
