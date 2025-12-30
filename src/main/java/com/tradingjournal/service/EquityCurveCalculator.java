package com.tradingjournal.service;

import com.tradingjournal.model.EquityPoint;
import com.tradingjournal.model.Trade;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.time.LocalDateTime;

/**
 * Service for calculating equity curve data
 */
public class EquityCurveCalculator {

    /**
     * Calculate equity curve from list of trades with starting balance
     * 
     * @param trades          The list of trades to analyze
     * @param startingBalance The initial account balance (default 0)
     * @return List of equity points
     */
    public List<EquityPoint> calculateEquityCurve(List<Trade> trades, double startingBalance) {
        List<EquityPoint> equityCurve = new ArrayList<>();

        if (trades == null || trades.isEmpty()) {
            return equityCurve;
        }

        // Filter and sort closed trades by close time
        List<Trade> sortedTrades = trades.stream()
                .filter(Trade::isClosed)
                .sorted(Comparator.comparing(Trade::getCloseTime))
                .collect(Collectors.toList());

        if (sortedTrades.isEmpty()) {
            return equityCurve;
        }

        double currentEquity = startingBalance;

        // Add starting point just before the first trade
        if (!sortedTrades.isEmpty()) {
            LocalDateTime startTime = sortedTrades.get(0).getCloseTime().minusSeconds(1);
            equityCurve.add(new EquityPoint(startTime, startingBalance, 0, "Start"));
        }

        for (int i = 0; i < sortedTrades.size(); i++) {
            Trade trade = sortedTrades.get(i);
            currentEquity += trade.getNetProfit();

            EquityPoint point = new EquityPoint(
                    trade.getCloseTime(),
                    currentEquity,
                    i + 1,
                    trade.getTicket());

            equityCurve.add(point);
        }

        return equityCurve;
    }

    /**
     * Calculate equity curve from list of trades (default 0 balance)
     */
    public List<EquityPoint> calculateEquityCurve(List<Trade> trades) {
        return calculateEquityCurve(trades, 0.0);
    }

    /**
     * Calculate equity curve filtered by strategy
     */
    public List<EquityPoint> calculateEquityCurveByStrategy(List<Trade> trades, String strategy) {
        if (strategy == null || strategy.trim().isEmpty()) {
            return calculateEquityCurve(trades);
        }

        List<Trade> filteredTrades = trades.stream()
                .filter(t -> strategy.equalsIgnoreCase(t.getStrategy()))
                .collect(Collectors.toList());

        return calculateEquityCurve(filteredTrades);
    }

    /**
     * Calculate equity curve filtered by account
     */
    public List<EquityPoint> calculateEquityCurveByAccount(List<Trade> trades, String account) {
        if (account == null || account.trim().isEmpty()) {
            return calculateEquityCurve(trades);
        }

        List<Trade> filteredTrades = trades.stream()
                .filter(t -> account.equalsIgnoreCase(t.getAccount()))
                .collect(Collectors.toList());

        return calculateEquityCurve(filteredTrades);
    }

    /**
     * Get list of unique strategies from trades
     */
    public List<String> getUniqueStrategies(List<Trade> trades) {
        if (trades == null)
            return new ArrayList<>();
        return trades.stream()
                .map(Trade::getStrategy)
                .filter(s -> s != null && !s.trim().isEmpty())
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }

    /**
     * Get list of unique accounts from trades
     */
    public List<String> getUniqueAccounts(List<Trade> trades) {
        if (trades == null)
            return new ArrayList<>();
        return trades.stream()
                .map(Trade::getAccount)
                .filter(a -> a != null && !a.trim().isEmpty())
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }
}
