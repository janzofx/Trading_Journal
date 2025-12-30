package com.tradingjournal.service;

import com.tradingjournal.model.Trade;
import com.tradingjournal.model.TradeType;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Service for importing trades from MT5 Excel files
 */
public class ExcelImportService {

    /**
     * Import trades from an Excel file exported from MT5
     */
    public List<Trade> importFromExcel(File file) throws IOException {
        if (file == null || !file.exists()) {
            throw new IllegalArgumentException("File does not exist");
        }

        List<Trade> trades = new ArrayList<>();

        try (FileInputStream fis = new FileInputStream(file);
                Workbook workbook = createWorkbook(file, fis)) {

            Sheet sheet = workbook.getSheetAt(0);

            // Find header row (usually first row, but might be after some metadata)
            int headerRowIndex = findHeaderRow(sheet);
            if (headerRowIndex == -1) {
                // Build debug info showing first few rows
                StringBuilder debugInfo = new StringBuilder("Could not find header row in Excel file.\n");
                debugInfo.append("First 5 rows found:\n");
                for (int i = 0; i <= Math.min(4, sheet.getLastRowNum()); i++) {
                    Row row = sheet.getRow(i);
                    if (row != null) {
                        debugInfo.append("Row ").append(i).append(": ");
                        for (int j = 0; j < Math.min(10, row.getLastCellNum()); j++) {
                            String cellValue = getCellValueAsString(row.getCell(j));
                            if (cellValue != null && !cellValue.trim().isEmpty()) {
                                debugInfo.append("[").append(cellValue.trim()).append("] ");
                            }
                        }
                        debugInfo.append("\n");
                    }
                }
                debugInfo.append("\nExpected headers like: Ticket, Symbol, Type, Time, Profit, etc.");
                throw new IOException(debugInfo.toString());
            }

            Row headerRow = sheet.getRow(headerRowIndex);

            // Debug: print actual headers found
            System.out.println("Found header at row " + headerRowIndex);
            System.out.print("Actual headers in Excel: ");
            for (int i = 0; i < headerRow.getLastCellNum(); i++) {
                String header = getCellValueAsString(headerRow.getCell(i));
                if (header != null && !header.trim().isEmpty()) {
                    System.out.print("[" + header.trim() + "] ");
                }
            }
            System.out.println();

            ColumnMapping mapping = mapColumns(headerRow);

            // Debug: print column mapping
            System.out.println("Column mapping - Ticket: " + mapping.ticket + ", Symbol: " + mapping.symbol +
                    ", Type: " + mapping.type + ", Profit: " + mapping.profit);

            // Read data rows
            int rowsProcessed = 0;
            int rowsSkipped = 0;
            for (int i = headerRowIndex + 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null || isEmptyRow(row)) {
                    rowsSkipped++;
                    continue;
                }

                rowsProcessed++;
                try {
                    Trade trade = parseTradeFromRow(row, mapping);
                    if (trade != null && trade.getTicket() != null) {
                        trades.add(trade);
                    } else {
                        System.out.println("Row " + i + " skipped - no ticket ID found");
                    }
                } catch (Exception e) {
                    System.err.println("Error parsing row " + i + ": " + e.getMessage());
                    e.printStackTrace();
                }
            }

            System.out.println("Total rows processed: " + rowsProcessed + ", skipped: " + rowsSkipped
                    + ", trades found: " + trades.size());
        }

        return trades;
    }

    /**
     * Create appropriate workbook based on file extension
     */
    private Workbook createWorkbook(File file, FileInputStream fis) throws IOException {
        String fileName = file.getName().toLowerCase();
        if (fileName.endsWith(".xlsx")) {
            return new XSSFWorkbook(fis);
        } else if (fileName.endsWith(".xls")) {
            return new HSSFWorkbook(fis);
        } else {
            throw new IOException("Unsupported file format. Only .xls and .xlsx are supported.");
        }
    }

    /**
     * Find the header row containing column names
     */
    private int findHeaderRow(Sheet sheet) {
        // Search up to row 20 (or last row if less)
        int maxRow = Math.min(20, sheet.getLastRowNum());

        for (int i = 0; i <= maxRow; i++) {
            Row row = sheet.getRow(i);
            if (row == null)
                continue;

            // Count how many cells match typical MT5 header keywords
            int matchCount = 0;
            int cellCount = 0;

            for (int j = 0; j < Math.min(20, row.getLastCellNum()); j++) {
                String cellValue = getCellValueAsString(row.getCell(j));
                if (cellValue != null && !cellValue.trim().isEmpty()) {
                    cellCount++;
                    String normalized = cellValue.toLowerCase().trim();

                    // Check for common MT5 column headers
                    if (normalized.contains("ticket") || normalized.contains("order") ||
                            normalized.contains("deal") || normalized.contains("time") ||
                            normalized.contains("type") || normalized.contains("symbol") ||
                            normalized.contains("volume") || normalized.contains("size") ||
                            normalized.contains("price") || normalized.contains("profit") ||
                            normalized.contains("commission") || normalized.contains("swap")) {
                        matchCount++;
                    }
                }
            }

            // If at least 3 cells match MT5 headers, consider this the header row
            if (matchCount >= 3 && cellCount >= 5) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Map column names to indices
     */
    private ColumnMapping mapColumns(Row headerRow) {
        ColumnMapping mapping = new ColumnMapping();

        for (int i = 0; i < headerRow.getLastCellNum(); i++) {
            String header = getCellValueAsString(headerRow.getCell(i));
            if (header == null)
                continue;

            String normalized = header.toLowerCase().trim();

            if (normalized.contains("ticket") || normalized.contains("order") ||
                    normalized.contains("deal") || normalized.equals("position")) {
                mapping.ticket = i;
            } else if (normalized.contains("time")) {
                // Handle duplicate Time columns: first is open, second is close
                if (mapping.openTime == -1) {
                    mapping.openTime = i;
                } else if (mapping.closeTime == -1) {
                    mapping.closeTime = i;
                }
            } else if (normalized.contains("type")) {
                mapping.type = i;
            } else if (normalized.contains("size") || normalized.contains("volume") || normalized.contains("lots")) {
                mapping.size = i;
            } else if (normalized.contains("symbol") || normalized.contains("item")) {
                mapping.symbol = i;
            } else if (normalized.contains("price")) {
                // Handle duplicate Price columns: first is open, second is close
                if (mapping.openPrice == -1) {
                    mapping.openPrice = i;
                } else if (mapping.closePrice == -1) {
                    mapping.closePrice = i;
                }
            } else if (normalized.contains("s") && normalized.contains("l")) {
                mapping.stopLoss = i;
            } else if (normalized.contains("t") && normalized.contains("p")) {
                mapping.takeProfit = i;
            } else if (normalized.contains("commission")) {
                mapping.commission = i;
            } else if (normalized.contains("swap")) {
                mapping.swap = i;
            } else if (normalized.equals("profit") || normalized.contains("profit")) {
                mapping.profit = i;
            } else if (normalized.contains("comment")) {
                mapping.comment = i;
            } else if (normalized.contains("strategy") || normalized.contains("tag")) {
                mapping.strategy = i;
            } else if (normalized.contains("account") || normalized.contains("acct")) {
                mapping.account = i;
            }
        }

        return mapping;
    }

    /**
     * Parse a Trade object from a row
     */
    private Trade parseTradeFromRow(Row row, ColumnMapping mapping) {
        Trade trade = new Trade();

        // Required fields
        if (mapping.ticket != -1) {
            trade.setTicket(getCellValueAsString(row.getCell(mapping.ticket)));
        } else {
            // Generate a ticket ID if none exists (use row number + timestamp)
            trade.setTicket("GEN-" + row.getRowNum() + "-" + System.currentTimeMillis());
        }

        if (mapping.openTime != -1) {
            trade.setOpenTime(getCellValueAsDateTime(row.getCell(mapping.openTime)));
        }

        if (mapping.type != -1) {
            String typeStr = getCellValueAsString(row.getCell(mapping.type));
            trade.setType(TradeType.fromString(typeStr));
        }

        if (mapping.symbol != -1) {
            trade.setSymbol(getCellValueAsString(row.getCell(mapping.symbol)));
        }

        if (mapping.size != -1) {
            trade.setSize(getCellValueAsDouble(row.getCell(mapping.size)));
        }

        if (mapping.openPrice != -1) {
            trade.setOpenPrice(getCellValueAsDouble(row.getCell(mapping.openPrice)));
        }

        // Optional fields
        if (mapping.closeTime != -1) {
            trade.setCloseTime(getCellValueAsDateTime(row.getCell(mapping.closeTime)));
        }

        if (mapping.closePrice != -1) {
            trade.setClosePrice(getCellValueAsDouble(row.getCell(mapping.closePrice)));
        }

        if (mapping.stopLoss != -1) {
            trade.setStopLoss(getCellValueAsDouble(row.getCell(mapping.stopLoss)));
        }

        if (mapping.takeProfit != -1) {
            trade.setTakeProfit(getCellValueAsDouble(row.getCell(mapping.takeProfit)));
        }

        if (mapping.profit != -1) {
            trade.setProfit(getCellValueAsDouble(row.getCell(mapping.profit)));
        }

        if (mapping.commission != -1) {
            trade.setCommission(getCellValueAsDouble(row.getCell(mapping.commission)));
        }

        if (mapping.swap != -1) {
            trade.setSwap(getCellValueAsDouble(row.getCell(mapping.swap)));
        }

        if (mapping.comment != -1) {
            trade.setComment(getCellValueAsString(row.getCell(mapping.comment)));
        }

        if (mapping.strategy != -1) {
            trade.setStrategy(getCellValueAsString(row.getCell(mapping.strategy)));
        }

        if (mapping.account != -1) {
            trade.setAccount(getCellValueAsString(row.getCell(mapping.account)));
        }

        return trade;
    }

    /**
     * Get cell value as string
     */
    private String getCellValueAsString(Cell cell) {
        if (cell == null)
            return null;

        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue().toString();
                }
                return String.valueOf((long) cell.getNumericCellValue());
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                return cell.getCellFormula();
            default:
                return null;
        }
    }

    /**
     * Get cell value as double
     */
    private double getCellValueAsDouble(Cell cell) {
        if (cell == null)
            return 0.0;

        try {
            switch (cell.getCellType()) {
                case NUMERIC:
                    return cell.getNumericCellValue();
                case STRING:
                    String str = cell.getStringCellValue().trim();
                    if (str.isEmpty())
                        return 0.0;
                    return Double.parseDouble(str);
                default:
                    return 0.0;
            }
        } catch (Exception e) {
            return 0.0;
        }
    }

    /**
     * Get cell value as LocalDateTime
     */
    private LocalDateTime getCellValueAsDateTime(Cell cell) {
        if (cell == null)
            return null;

        try {
            if (cell.getCellType() == CellType.NUMERIC && DateUtil.isCellDateFormatted(cell)) {
                Date date = cell.getDateCellValue();
                return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
            } else if (cell.getCellType() == CellType.STRING) {
                // Try to parse string date - this would need more robust parsing
                return null;
            }
        } catch (Exception e) {
            System.err.println("Error parsing date: " + e.getMessage());
        }

        return null;
    }

    /**
     * Check if row is empty
     */
    private boolean isEmptyRow(Row row) {
        for (int i = 0; i < row.getLastCellNum(); i++) {
            Cell cell = row.getCell(i);
            if (cell != null && cell.getCellType() != CellType.BLANK) {
                return false;
            }
        }
        return true;
    }

    /**
     * Internal class to store column mappings
     */
    private static class ColumnMapping {
        int ticket = -1;
        int openTime = -1;
        int type = -1;
        int size = -1;
        int symbol = -1;
        int openPrice = -1;
        int closeTime = -1;
        int closePrice = -1;
        int stopLoss = -1;
        int takeProfit = -1;
        int profit = -1;
        int commission = -1;
        int swap = -1;
        int comment = -1;
        int strategy = -1;
        int account = -1;
    }
}
