package com.tradingjournal.repository;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.tradingjournal.model.Trade;
import com.tradingjournal.util.LocalDateTimeAdapter;

import java.io.*;
import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * JSON-based implementation of TradeRepository
 * Stores trades in a trades.json file
 */
public class JsonTradeRepository implements TradeRepository {

    private static final String DEFAULT_FILE = "trades.json";
    private final String filePath;
    private final Gson gson;
    private Map<String, Trade> trades;

    public JsonTradeRepository() {
        this(DEFAULT_FILE);
    }

    public JsonTradeRepository(String filePath) {
        this.filePath = filePath;
        this.gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .setPrettyPrinting()
                .create();
        this.trades = new HashMap<>();
        loadTrades();
    }

    @Override
    public void save(Trade trade) {
        if (trade == null || trade.getTicket() == null) {
            throw new IllegalArgumentException("Trade and ticket cannot be null");
        }
        trades.put(trade.getTicket(), trade);
        saveTrades();
    }

    @Override
    public void saveAll(List<Trade> tradeList) {
        if (tradeList == null)
            return;

        for (Trade trade : tradeList) {
            if (trade != null && trade.getTicket() != null) {
                trades.put(trade.getTicket(), trade);
            }
        }
        saveTrades();
    }

    @Override
    public Optional<Trade> findByTicket(String ticket) {
        return Optional.ofNullable(trades.get(ticket));
    }

    @Override
    public List<Trade> findAll() {
        return new ArrayList<>(trades.values());
    }

    @Override
    public List<Trade> findBySymbol(String symbol) {
        if (symbol == null)
            return new ArrayList<>();

        return trades.values().stream()
                .filter(t -> symbol.equalsIgnoreCase(t.getSymbol()))
                .collect(Collectors.toList());
    }

    @Override
    public boolean delete(String ticket) {
        boolean removed = trades.remove(ticket) != null;
        if (removed) {
            saveTrades();
        }
        return removed;
    }

    @Override
    public void deleteAll() {
        trades.clear();
        saveTrades();
    }

    @Override
    public boolean exists(String ticket) {
        return trades.containsKey(ticket);
    }

    @Override
    public int count() {
        return trades.size();
    }

    /**
     * Load trades from JSON file
     */
    private void loadTrades() {
        File file = new File(filePath);
        if (!file.exists()) {
            trades = new HashMap<>();
            return;
        }

        try (Reader reader = new FileReader(file)) {
            Type type = new TypeToken<List<Trade>>() {
            }.getType();
            List<Trade> tradeList = gson.fromJson(reader, type);

            if (tradeList != null) {
                trades = tradeList.stream()
                        .filter(t -> t != null && t.getTicket() != null)
                        .collect(Collectors.toMap(Trade::getTicket, t -> t, (a, b) -> b));
            }
        } catch (IOException e) {
            System.err.println("Error loading trades: " + e.getMessage());
            trades = new HashMap<>();
        }
    }

    /**
     * Save trades to JSON file
     */
    private void saveTrades() {
        try (Writer writer = new FileWriter(filePath)) {
            List<Trade> tradeList = new ArrayList<>(trades.values());
            gson.toJson(tradeList, writer);
        } catch (IOException e) {
            System.err.println("Error saving trades: " + e.getMessage());
        }
    }
}
