package com.tradingjournal.service;

import com.tradingjournal.model.Trade;
import com.tradingjournal.model.TradeStatistics;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for analyzing trades and calculating statistics
 */
public class TradeAnalyzer {

    /**
     * Calculate comprehensive statistics from a list of trades
     */
    public TradeStatistics calculateStatistics(List<Trade> trades) {
        TradeStatistics stats = new TradeStatistics();

        if (trades == null || trades.isEmpty()) {
            return stats;
        }

        // Filter only closed trades for statistics
        List<Trade> closedTrades = trades.stream()
                .filter(Trade::isClosed)
                .collect(Collectors.toList());

        stats.setTotalTrades(closedTrades.size());

        if (closedTrades.isEmpty()) {
            return stats;
        }

        // Separate winning and losing trades
        List<Trade> winners = closedTrades.stream()
                .filter(Trade::isWinner)
                .collect(Collectors.toList());

        List<Trade> losers = closedTrades.stream()
                .filter(Trade::isLoser)
                .collect(Collectors.toList());

        stats.setWinningTrades(winners.size());
        stats.setLosingTrades(losers.size());

        // Calculate profit metrics
        double totalProfit = winners.stream()
                .mapToDouble(Trade::getNetProfit)
                .sum();

        double totalLoss = Math.abs(losers.stream()
                .mapToDouble(Trade::getNetProfit)
                .sum());

        stats.setTotalProfit(totalProfit);
        stats.setTotalLoss(totalLoss);
        stats.setNetProfit(totalProfit - totalLoss);

        // Calculate win rate
        double winRate = closedTrades.isEmpty() ? 0 : (double) winners.size() / closedTrades.size();
        stats.setWinRate(winRate);

        // Calculate profit factor
        double profitFactor = totalLoss == 0 ? (totalProfit > 0 ? Double.POSITIVE_INFINITY : 0)
                : totalProfit / totalLoss;
        stats.setProfitFactor(profitFactor);

        // Calculate average win/loss
        double avgWin = winners.isEmpty() ? 0 : winners.stream().mapToDouble(Trade::getNetProfit).average().orElse(0);

        double avgLoss = losers.isEmpty() ? 0 : losers.stream().mapToDouble(Trade::getNetProfit).average().orElse(0);

        stats.setAverageWin(avgWin);
        stats.setAverageLoss(avgLoss);

        // Find largest win/loss
        double largestWin = winners.stream()
                .mapToDouble(Trade::getNetProfit)
                .max()
                .orElse(0);

        double largestLoss = losers.stream()
                .mapToDouble(Trade::getNetProfit)
                .min()
                .orElse(0);

        stats.setLargestWin(largestWin);
        stats.setLargestLoss(largestLoss);

        return stats;
    }

    /**
     * Calculate statistics for a specific symbol
     */
    public TradeStatistics calculateStatisticsBySymbol(List<Trade> trades, String symbol) {
        if (symbol == null) {
            return calculateStatistics(trades);
        }

        List<Trade> filteredTrades = trades.stream()
                .filter(t -> symbol.equalsIgnoreCase(t.getSymbol()))
                .collect(Collectors.toList());

        return calculateStatistics(filteredTrades);
    }
}
