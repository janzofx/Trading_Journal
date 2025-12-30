package com.tradingjournal.ui;

import com.tradingjournal.model.Trade;
import com.tradingjournal.model.TradeType;
import com.tradingjournal.repository.TradeRepository;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.UUID;

/**
 * Dialog to manually add a new trade
 */
public class AddManualTradeDialog extends JDialog {
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private final TradeRepository repository;
    private boolean tradeAdded = false;

    // Fields
    private JTextField ticketField;
    private JTextField symbolField;
    private JComboBox<TradeType> typeField;
    private JTextField sizeField;
    private JTextField openTimeField;
    private JTextField openPriceField;
    private JTextField closeTimeField;
    private JTextField closePriceField;
    private JTextField profitField; // Auto-calculated usually, but editable for manual entry
    private JTextField commissionField;
    private JTextField swapField;
    private JComboBox<String> strategyField;
    private JComboBox<String> accountField;
    private JTextField magicField;
    private JTextArea commentField;

    public AddManualTradeDialog(Frame parent, TradeRepository repository, List<String> strategies,
            List<String> accounts) {
        super(parent, "Add Manual Trade", true);
        this.repository = repository;

        initComponents(strategies, accounts);
        setLocationRelativeTo(parent);
    }

    private void initComponents(List<String> strategies, List<String> accounts) {
        setLayout(new BorderLayout(10, 10));

        // Main panel with trade info
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        int row = 0;

        // Ticket (can be auto-generated if empty)
        gbc.gridx = 0;
        gbc.gridy = row;
        mainPanel.add(new JLabel("Ticket:"), gbc);
        gbc.gridx = 1;
        ticketField = new JTextField(20);
        ticketField.setToolTipText("Leave empty to auto-generate");
        mainPanel.add(ticketField, gbc);
        row++;

        // Symbol
        gbc.gridx = 0;
        gbc.gridy = row;
        mainPanel.add(new JLabel("Symbol:"), gbc);
        gbc.gridx = 1;
        symbolField = new JTextField(20);
        mainPanel.add(symbolField, gbc);
        row++;

        // Type
        gbc.gridx = 0;
        gbc.gridy = row;
        mainPanel.add(new JLabel("Type:"), gbc);
        gbc.gridx = 1;
        typeField = new JComboBox<>(TradeType.values());
        mainPanel.add(typeField, gbc);
        row++;

        // Size
        gbc.gridx = 0;
        gbc.gridy = row;
        mainPanel.add(new JLabel("Size (Lots):"), gbc);
        gbc.gridx = 1;
        sizeField = new JTextField("0.01", 20);
        mainPanel.add(sizeField, gbc);
        row++;

        // Open Time
        gbc.gridx = 0;
        gbc.gridy = row;
        mainPanel.add(new JLabel("Open Time (yyyy-MM-dd HH:mm):"), gbc);
        gbc.gridx = 1;
        openTimeField = new JTextField(LocalDateTime.now().format(DATE_FORMATTER), 20);
        mainPanel.add(openTimeField, gbc);
        row++;

        // Open Price
        gbc.gridx = 0;
        gbc.gridy = row;
        mainPanel.add(new JLabel("Open Price:"), gbc);
        gbc.gridx = 1;
        openPriceField = new JTextField("0.00000", 20);
        mainPanel.add(openPriceField, gbc);
        row++;

        // Close Time
        gbc.gridx = 0;
        gbc.gridy = row;
        mainPanel.add(new JLabel("Close Time (yyyy-MM-dd HH:mm):"), gbc);
        gbc.gridx = 1;
        closeTimeField = new JTextField(LocalDateTime.now().format(DATE_FORMATTER), 20);
        mainPanel.add(closeTimeField, gbc);
        row++;

        // Close Price
        gbc.gridx = 0;
        gbc.gridy = row;
        mainPanel.add(new JLabel("Close Price:"), gbc);
        gbc.gridx = 1;
        closePriceField = new JTextField("0.00000", 20);
        mainPanel.add(closePriceField, gbc);
        row++;

        // Profit
        gbc.gridx = 0;
        gbc.gridy = row;
        mainPanel.add(new JLabel("Profit (Gross):"), gbc);
        gbc.gridx = 1;
        profitField = new JTextField("0.00", 20);
        mainPanel.add(profitField, gbc);
        row++;

        // Commission
        gbc.gridx = 0;
        gbc.gridy = row;
        mainPanel.add(new JLabel("Commission:"), gbc);
        gbc.gridx = 1;
        commissionField = new JTextField("0.00", 20);
        mainPanel.add(commissionField, gbc);
        row++;

        // Swap
        gbc.gridx = 0;
        gbc.gridy = row;
        mainPanel.add(new JLabel("Swap:"), gbc);
        gbc.gridx = 1;
        swapField = new JTextField("0.00", 20);
        mainPanel.add(swapField, gbc);
        row++;

        // Strategy
        gbc.gridx = 0;
        gbc.gridy = row;
        mainPanel.add(new JLabel("Strategy:"), gbc);
        gbc.gridx = 1;
        strategyField = new JComboBox<>();
        strategyField.addItem("");
        for (String s : strategies)
            if (s != null && !s.isEmpty())
                strategyField.addItem(s);
        strategyField.setEditable(true); // Allow custom strategy entry
        mainPanel.add(strategyField, gbc);
        row++;

        // Account
        gbc.gridx = 0;
        gbc.gridy = row;
        mainPanel.add(new JLabel("Account:"), gbc);
        gbc.gridx = 1;
        accountField = new JComboBox<>();
        accountField.addItem("");
        for (String a : accounts)
            if (a != null && !a.isEmpty())
                accountField.addItem(a);
        accountField.setEditable(true); // Allow custom account entry
        mainPanel.add(accountField, gbc);
        row++;

        // Magic Number
        gbc.gridx = 0;
        gbc.gridy = row;
        mainPanel.add(new JLabel("Magic Number:"), gbc);
        gbc.gridx = 1;
        magicField = new JTextField("0", 20);
        mainPanel.add(magicField, gbc);
        row++;

        // Comment
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        mainPanel.add(new JLabel("Comment:"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        commentField = new JTextArea(3, 20);
        commentField.setLineWrap(true);
        commentField.setWrapStyleWord(true);
        mainPanel.add(new JScrollPane(commentField), gbc);

        JScrollPane mainScroll = new JScrollPane(mainPanel);
        mainScroll.setBorder(null);
        add(mainScroll, BorderLayout.CENTER);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveButton = new JButton("Add Trade");
        saveButton.addActionListener(e -> saveTrade());
        buttonPanel.add(saveButton);

        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> dispose());
        buttonPanel.add(cancelButton);

        add(buttonPanel, BorderLayout.SOUTH);

        pack();
        setSize(new Dimension(550, 700)); // Fixed size to ensure visibility
    }

    private void saveTrade() {
        try {
            // Validation
            if (symbolField.getText().trim().isEmpty()) {
                throw new IllegalArgumentException("Symbol is required.");
            }

            Trade trade = new Trade();

            // Ticket
            String ticket = ticketField.getText().trim();
            if (ticket.isEmpty()) {
                ticket = "MAN-" + UUID.randomUUID().toString().substring(0, 8);
            }
            trade.setTicket(ticket);

            trade.setSymbol(symbolField.getText().trim());
            trade.setType((TradeType) typeField.getSelectedItem());

            // Numeric fields
            trade.setSize(Double.parseDouble(sizeField.getText().trim()));
            trade.setOpenPrice(Double.parseDouble(openPriceField.getText().trim()));
            trade.setClosePrice(Double.parseDouble(closePriceField.getText().trim()));
            trade.setProfit(Double.parseDouble(profitField.getText().trim()));
            trade.setCommission(Double.parseDouble(commissionField.getText().trim()));
            trade.setSwap(Double.parseDouble(swapField.getText().trim()));
            trade.setMagicNumber(Long.parseLong(magicField.getText().trim()));

            // Dates
            try {
                trade.setOpenTime(LocalDateTime.parse(openTimeField.getText().trim(), DATE_FORMATTER));
            } catch (DateTimeParseException ex) {
                // Try open time without trailing spaces or lenient parsing if needed
                throw new IllegalArgumentException("Open Time format must be yyyy-MM-dd HH:mm");
            }

            String closeTimeStr = closeTimeField.getText().trim();
            if (!closeTimeStr.isEmpty()) {
                try {
                    trade.setCloseTime(LocalDateTime.parse(closeTimeStr, DATE_FORMATTER));
                } catch (DateTimeParseException ex) {
                    throw new IllegalArgumentException("Close Time format must be yyyy-MM-dd HH:mm");
                }
            }

            // Metadata
            Object strategyObj = strategyField.getSelectedItem();
            trade.setStrategy(strategyObj != null ? strategyObj.toString().trim() : "");

            Object accountObj = accountField.getSelectedItem();
            trade.setAccount(accountObj != null ? accountObj.toString().trim() : "");

            trade.setComment(commentField.getText().trim());

            repository.save(trade);
            tradeAdded = true;

            JOptionPane.showMessageDialog(this,
                    "Trade added successfully!",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);
            dispose();

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                    "Please enter valid numbers for numeric fields.",
                    "Input Error",
                    JOptionPane.ERROR_MESSAGE);
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(this,
                    e.getMessage(),
                    "Input Error",
                    JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Error saving trade: " + e.getMessage(),
                    "System Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    public boolean isTradeAdded() {
        return tradeAdded;
    }
}
