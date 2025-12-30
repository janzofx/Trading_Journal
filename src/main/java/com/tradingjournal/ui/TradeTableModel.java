package com.tradingjournal.ui;

import com.tradingjournal.model.Trade;

import javax.swing.table.AbstractTableModel;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Table model for displaying trades in a JTable
 */
public class TradeTableModel extends AbstractTableModel {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private final String[] columnNames = {
            "Ticket", "Symbol", "Type", "Open Time", "Close Time",
            "Size", "Open Price", "Close Price", "Profit", "Strategy", "Account", "Magic Number", "Status"
    };

    private List<Trade> trades;

    public TradeTableModel() {
        this.trades = new ArrayList<>();
    }

    public TradeTableModel(List<Trade> trades) {
        this.trades = trades != null ? new ArrayList<>(trades) : new ArrayList<>();
    }

    @Override
    public int getRowCount() {
        return trades.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    public List<Trade> getTrades() {
        return trades;
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        // Strategy (9) and Account (10) columns are editable
        return column == 9 || column == 10;
    }

    @Override
    public void setValueAt(Object value, int row, int column) {
        if (row >= 0 && row < trades.size()) {
            Trade trade = trades.get(row);
            if (column == 9) {
                trade.setStrategy((String) value);
            } else if (column == 10) {
                trade.setAccount((String) value);
            }
            fireTableCellUpdated(row, column);
        }
    }

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        if (rowIndex < 0 || rowIndex >= trades.size()) {
            return null;
        }

        Trade trade = trades.get(rowIndex);

        switch (columnIndex) {
            case 0:
                return trade.getTicket();
            case 1:
                return trade.getSymbol();
            case 2:
                return trade.getType() != null ? trade.getType().getDisplayName() : "";
            case 3:
                return trade.getOpenTime() != null ? trade.getOpenTime().format(DATE_FORMATTER) : "";
            case 4:
                return trade.getCloseTime() != null ? trade.getCloseTime().format(DATE_FORMATTER) : "";
            case 5:
                return String.format("%.2f", trade.getSize());
            case 6:
                return String.format("%.5f", trade.getOpenPrice());
            case 7:
                return trade.isClosed() ? String.format("%.5f", trade.getClosePrice()) : "";
            case 8:
                return String.format("%.2f", trade.getNetProfit());
            case 9:
                return trade.getStrategy() != null ? trade.getStrategy() : "";
            case 10:
                return trade.getAccount() != null ? trade.getAccount() : "";
            case 11:
                return trade.getMagicNumber();
            case 12:
                return trade.isClosed() ? "Closed" : "Open";
            default:
                return null;
        }
    }

    /**
     * Set new trade data
     */
    public void setTrades(List<Trade> trades) {
        this.trades = trades != null ? new ArrayList<>(trades) : new ArrayList<>();
        fireTableDataChanged();
    }

    /**
     * Add a trade to the table
     */
    public void addTrade(Trade trade) {
        if (trade != null) {
            trades.add(trade);
            fireTableRowsInserted(trades.size() - 1, trades.size() - 1);
        }
    }

    /**
     * Get trade at specific row
     */
    public Trade getTradeAt(int row) {
        if (row >= 0 && row < trades.size()) {
            return trades.get(row);
        }
        return null;
    }

    /**
     * Clear all trades
     */
    public void clear() {
        trades.clear();
        fireTableDataChanged();
    }
}
