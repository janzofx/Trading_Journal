package com.tradingjournal.ui;

import com.tradingjournal.model.Trade;
import com.tradingjournal.model.TradeType;
import com.tradingjournal.repository.TradeRepository;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Dialog to view and edit trade details
 */
public class TradeDetailDialog extends JDialog {
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private final Trade trade;
    private final TradeRepository repository;
    private boolean saved = false;

    // Editable fields
    private JTextField ticketField;
    private JTextField symbolField;
    private JTextField typeField;
    private JTextField sizeField;
    private JTextField openTimeField;
    private JTextField openPriceField;
    private JTextField closeTimeField;
    private JTextField closePriceField;
    private JTextField profitField;
    private JTextField commissionField;
    private JTextField swapField;
    private JTextField netProfitField;
    private JComboBox<String> strategyField;
    private JTextField accountField;
    private JTextField magicField;
    private JTextArea commentField;

    public TradeDetailDialog(Frame parent, Trade trade, TradeRepository repository, List<String> strategies) {
        super(parent, "Trade Details - " + trade.getTicket(), true);
        this.trade = trade;
        this.repository = repository;

        initComponents(strategies);
        setLocationRelativeTo(parent);
    }

    private void initComponents(List<String> strategies) {
        setLayout(new BorderLayout(10, 10));

        // Main panel with trade info
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        int row = 0;

        // All fields are now editable
        ticketField = addField(mainPanel, gbc, row++, "Ticket:", trade.getTicket(), true);
        symbolField = addField(mainPanel, gbc, row++, "Symbol:", trade.getSymbol(), true);
        typeField = addField(mainPanel, gbc, row++, "Type:", trade.getType().toString(), true);
        sizeField = addField(mainPanel, gbc, row++, "Size:", String.format("%.2f", trade.getSize()), true);
        openTimeField = addField(mainPanel, gbc, row++, "Open Time:",
                trade.getOpenTime() != null ? trade.getOpenTime().format(DATE_FORMATTER) : "N/A", true);
        openPriceField = addField(mainPanel, gbc, row++, "Open Price:", String.format("%.5f", trade.getOpenPrice()),
                true);
        closeTimeField = addField(mainPanel, gbc, row++, "Close Time:",
                trade.isClosed() ? trade.getCloseTime().format(DATE_FORMATTER) : "Open", true);
        closePriceField = addField(mainPanel, gbc, row++, "Close Price:",
                trade.isClosed() ? String.format("%.5f", trade.getClosePrice()) : "N/A", true);
        profitField = addField(mainPanel, gbc, row++, "Profit:", String.format("$%.2f", trade.getProfit()), true);
        commissionField = addField(mainPanel, gbc, row++, "Commission:", String.format("$%.2f", trade.getCommission()),
                true);
        swapField = addField(mainPanel, gbc, row++, "Swap:", String.format("$%.2f", trade.getSwap()), true);
        netProfitField = addField(mainPanel, gbc, row++, "Net Profit:", String.format("$%.2f", trade.getNetProfit()),
                true);

        // Editable fields - Strategy dropdown
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.fill = GridBagConstraints.NONE;
        mainPanel.add(new JLabel("Strategy:"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Create combo box with strategies
        strategyField = new JComboBox<String>();
        strategyField.addItem(""); // Empty option
        for (String strategy : strategies) {
            if (strategy != null && !strategy.isEmpty()) {
                strategyField.addItem(strategy);
            }
        }
        // Set current strategy
        if (trade.getStrategy() != null && !trade.getStrategy().isEmpty()) {
            strategyField.setSelectedItem(trade.getStrategy());
        }
        mainPanel.add(strategyField, gbc);
        row++;

        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.fill = GridBagConstraints.NONE;
        mainPanel.add(new JLabel("Account:"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        accountField = new JTextField(trade.getAccount() != null ? trade.getAccount() : "", 20);
        mainPanel.add(accountField, gbc);
        row++;

        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.fill = GridBagConstraints.NONE;
        mainPanel.add(new JLabel("Magic Number:"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        magicField = new JTextField(String.valueOf(trade.getMagicNumber()), 20);
        mainPanel.add(magicField, gbc);
        row++;

        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.fill = GridBagConstraints.NONE;
        mainPanel.add(new JLabel("Comment:"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.BOTH;
        commentField = new JTextArea(trade.getComment() != null ? trade.getComment() : "", 3, 20);
        commentField.setLineWrap(true);
        commentField.setWrapStyleWord(true);
        JScrollPane commentScroll = new JScrollPane(commentField);
        mainPanel.add(commentScroll, gbc);

        add(mainPanel, BorderLayout.CENTER);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        JButton saveButton = new JButton("Save");
        saveButton.addActionListener(e -> saveTrade());
        buttonPanel.add(saveButton);

        JButton deleteButton = new JButton("Delete");
        deleteButton.setBackground(new Color(255, 100, 100)); // Light red
        deleteButton.addActionListener(e -> deleteTrade());
        buttonPanel.add(deleteButton);

        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> dispose());
        buttonPanel.add(cancelButton);

        add(buttonPanel, BorderLayout.SOUTH);

        pack();
        setMinimumSize(new Dimension(500, 600));
    }

    private JTextField addField(JPanel panel, GridBagConstraints gbc, int row, String label, String value,
            boolean editable) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.fill = GridBagConstraints.NONE;
        panel.add(new JLabel(label), gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        JTextField field = new JTextField(value, 20);
        field.setEditable(editable);
        if (!editable) {
            field.setBackground(Color.LIGHT_GRAY);
        }
        panel.add(field, gbc);
        return field;
    }

    private void saveTrade() {
        try {
            // Update all fields from the form
            trade.setTicket(ticketField.getText().trim());
            trade.setSymbol(symbolField.getText().trim());

            // Parse type (BUY or SELL)
            String typeText = typeField.getText().trim().toUpperCase();
            trade.setType(TradeType.valueOf(typeText));

            // Parse numeric fields
            trade.setSize(Double.parseDouble(sizeField.getText().trim()));

            // Parse dates
            String openTimeText = openTimeField.getText().trim();
            if (!openTimeText.isEmpty() && !openTimeText.equals("N/A")) {
                trade.setOpenTime(LocalDateTime.parse(openTimeText, DATE_FORMATTER));
            }

            String closeTimeText = closeTimeField.getText().trim();
            if (!closeTimeText.isEmpty() && !closeTimeText.equals("Open") && !closeTimeText.equals("N/A")) {
                trade.setCloseTime(LocalDateTime.parse(closeTimeText, DATE_FORMATTER));
            }

            // Parse prices
            trade.setOpenPrice(Double.parseDouble(openPriceField.getText().trim()));

            String closePriceText = closePriceField.getText().trim();
            if (!closePriceText.isEmpty() && !closePriceText.equals("N/A")) {
                trade.setClosePrice(Double.parseDouble(closePriceText));
            }

            // Parse profit, commission, swap (remove $ sign if present)
            String profitText = profitField.getText().trim().replace("$", "");
            trade.setProfit(Double.parseDouble(profitText));

            String commissionText = commissionField.getText().trim().replace("$", "");
            trade.setCommission(Double.parseDouble(commissionText));

            String swapText = swapField.getText().trim().replace("$", "");
            trade.setSwap(Double.parseDouble(swapText));

            // Update editable fields - get strategy from combo box
            String selectedStrategy = (String) strategyField.getSelectedItem();
            trade.setStrategy(selectedStrategy != null ? selectedStrategy : "");
            trade.setAccount(accountField.getText().trim());
            trade.setMagicNumber(Long.parseLong(magicField.getText().trim()));
            trade.setComment(commentField.getText().trim());

            // Save to repository
            repository.save(trade);

            saved = true;
            JOptionPane.showMessageDialog(this,
                    "Trade details saved successfully!",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                    "Invalid number format. Please check your inputs:\n" +
                            "Size, prices, profit, commission, swap, and magic number must be valid numbers.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(this,
                    "Invalid trade type. Please enter BUY or SELL.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error saving trade: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    public boolean isSaved() {
        return saved;
    }

    private boolean deleted = false;

    private void deleteTrade() {
        int result = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete this trade?\nThis action cannot be undone.",
                "Confirm Deletion",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (result == JOptionPane.YES_OPTION) {
            try {
                repository.delete(trade.getTicket());
                deleted = true;
                dispose();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this,
                        "Error deleting trade: " + e.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public boolean isDeleted() {
        return deleted;
    }
}
