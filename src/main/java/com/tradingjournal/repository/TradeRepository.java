package com.tradingjournal.repository;

import com.tradingjournal.model.Trade;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Trade data access
 */
public interface TradeRepository {

    /**
     * Save a new trade or update existing one
     */
    void save(Trade trade);

    /**
     * Save multiple trades at once
     */
    void saveAll(List<Trade> trades);

    /**
     * Find trade by ticket number
     */
    Optional<Trade> findByTicket(String ticket);

    /**
     * Get all trades
     */
    List<Trade> findAll();

    /**
     * Find trades by symbol
     */
    List<Trade> findBySymbol(String symbol);

    /**
     * Delete a trade by ticket
     */
    boolean delete(String ticket);

    /**
     * Delete all trades
     */
    void deleteAll();

    /**
     * Check if a trade exists by ticket
     */
    boolean exists(String ticket);

    /**
     * Get count of all trades
     */
    int count();
}
