package com.tradingjournal.model;

/**
 * Enum representing different types of trading operations
 */
public enum TradeType {
    BUY("Buy"),
    SELL("Sell"),
    BUY_LIMIT("Buy Limit"),
    SELL_LIMIT("Sell Limit"),
    BUY_STOP("Buy Stop"),
    SELL_STOP("Sell Stop"),
    BALANCE("Balance"),
    CREDIT("Credit");

    private final String displayName;

    TradeType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    /**
     * Parse trade type from MT5 string representation
     */
    public static TradeType fromString(String type) {
        if (type == null) return null;
        
        String normalized = type.trim().toLowerCase();
        
        if (normalized.contains("buy limit")) return BUY_LIMIT;
        if (normalized.contains("sell limit")) return SELL_LIMIT;
        if (normalized.contains("buy stop")) return BUY_STOP;
        if (normalized.contains("sell stop")) return SELL_STOP;
        if (normalized.contains("buy")) return BUY;
        if (normalized.contains("sell")) return SELL;
        if (normalized.contains("balance")) return BALANCE;
        if (normalized.contains("credit")) return CREDIT;
        
        return BUY; // Default
    }

    public boolean isBuy() {
        return this == BUY || this == BUY_LIMIT || this == BUY_STOP;
    }

    public boolean isSell() {
        return this == SELL || this == SELL_LIMIT || this == SELL_STOP;
    }
}
