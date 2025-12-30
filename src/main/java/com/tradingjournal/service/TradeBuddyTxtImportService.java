package com.tradingjournal.service;

import com.tradingjournal.model.Trade;
import com.tradingjournal.model.TradeType;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Service for importing trades from TradeBuddy TXT files
 */
public class TradeBuddyTxtImportService {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm");

    /**
     * Import trades from TradeBuddy TXT file
     * Format:
     * Ticket;Symbol;Size;Type;OpenPrice;OpenTime;ClosePrice;CloseTime;Commission;Swap;Profit;StopLoss;TakeProfit;???;Strategy
     */
    public List<Trade> importFromTxt(File file) throws IOException {
        List<Trade> trades = new ArrayList<Trade>();

        if (file == null || !file.exists()) {
            throw new IllegalArgumentException("File does not exist");
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            int lineNumber = 0;

            while ((line = reader.readLine()) != null) {
                lineNumber++;
                line = line.trim();

                // Skip empty lines
                if (line.isEmpty()) {
                    continue;
                }

                try {
                    Trade trade = parseTradeLine(line);
                    if (trade != null && trade.getTicket() != null) {
                        trades.add(trade);
                    }
                } catch (Exception e) {
                    System.err.println("Error parsing line " + lineNumber + ": " + e.getMessage());
                    System.err.println("Line content: " + line);
                }
            }
        }

        System.out.println("TradeBuddy TXT import: " + trades.size() + " trades imported");
        return trades;
    }

    /**
     * Parse a single line into a Trade object
     * Format:
     * Ticket;Symbol;Size;Type;OpenPrice;OpenTime;ClosePrice;CloseTime;Commission;Swap;Profit;StopLoss;TakeProfit;???;Strategy
     */
    private Trade parseTradeLine(String line) {
        String[] parts = line.split(";");

        if (parts.length < 11) {
            throw new IllegalArgumentException("Invalid line format - expected at least 11 fields");
        }

        Trade trade = new Trade();

        // Field 0: Ticket
        trade.setTicket(parts[0].trim());

        // Field 1: Symbol
        trade.setSymbol(parts[1].trim());

        // Field 2: Size
        trade.setSize(parseDouble(parts[2]));

        // Field 3: Type (Long/Short)
        String typeStr = parts[3].trim();
        if (typeStr.equalsIgnoreCase("Long")) {
            trade.setType(TradeType.BUY);
        } else if (typeStr.equalsIgnoreCase("Short")) {
            trade.setType(TradeType.SELL);
        }

        // Field 4: Open Price
        trade.setOpenPrice(parseDouble(parts[4]));

        // Field 5: Open Time
        trade.setOpenTime(parseDateTime(parts[5]));

        // Field 6: Close Price
        trade.setClosePrice(parseDouble(parts[6]));

        // Field 7: Close Time
        trade.setCloseTime(parseDateTime(parts[7]));

        // Field 8: Commission
        trade.setCommission(parseDouble(parts[8]));

        // Field 9: Swap
        trade.setSwap(parseDouble(parts[9]));

        // Field 10: Profit
        trade.setProfit(parseDouble(parts[10]));

        // Field 11: Stop Loss (if present)
        if (parts.length > 11) {
            trade.setStopLoss(parseDouble(parts[11]));
        }

        // Field 12: Take Profit (if present)
        if (parts.length > 12) {
            trade.setTakeProfit(parseDouble(parts[12]));
        }

        // Field 13: Magic Number (if present)
        if (parts.length > 13) {
            trade.setMagicNumber(parseLong(parts[13]));
        }

        // Field 14: Strategy - leave empty for manual assignment
        // User will assign strategies later through the Strategy Manager
        /*
         * if (parts.length > 14 && !parts[14].trim().isEmpty()) {
         * trade.setStrategy(parts[14].trim());
         * }
         */

        return trade;
    }

    /**
     * Parse double value
     */
    private double parseDouble(String value) {
        if (value == null || value.trim().isEmpty()) {
            return 0.0;
        }
        try {
            return Double.parseDouble(value.trim());
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }

    /**
     * Parse long value
     */
    private long parseLong(String value) {
        if (value == null || value.trim().isEmpty()) {
            return 0L;
        }
        try {
            return Long.parseLong(value.trim());
        } catch (NumberFormatException e) {
            return 0L;
        }
    }

    /**
     * Parse datetime in format: yyyy.MM.dd HH:mm
     */
    private LocalDateTime parseDateTime(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        try {
            return LocalDateTime.parse(value.trim(), DATE_FORMATTER);
        } catch (Exception e) {
            System.err.println("Error parsing date: " + value + " - " + e.getMessage());
            return null;
        }
    }
}
