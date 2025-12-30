package com.tradingjournal.ui;

import com.tradingjournal.model.Trade;
import com.tradingjournal.repository.StrategyRepository;
import com.tradingjournal.repository.TradeRepository;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Dialog for managing strategy labels
 */
public class StrategyManagerDialog extends JDialog {

    private JTable strategyTable;
    private DefaultTableModel tableModel;
    private List<String> strategies;
    private StrategyRepository repository;
    private TradeRepository tradeRepository;

    public StrategyManagerDialog(Frame parent, List<String> existingStrategies, StrategyRepository repository,
            TradeRepository tradeRepository) {
        super(parent, "Strategy Manager", true);
        this.strategies = new ArrayList<String>(existingStrategies);
        this.repository = repository;
        this.tradeRepository = tradeRepository;
        initComponents();
        setLocationRelativeTo(parent);
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 5, 10));
        JLabel titleLabel = new JLabel("Manage Strategy Labels");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        headerPanel.add(titleLabel, BorderLayout.WEST);
        add(headerPanel, BorderLayout.NORTH);

        // Table
        String[] columns = { "Strategy Name" };
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        strategyTable = new JTable(tableModel);
        strategyTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        strategyTable.setRowHeight(25);

        loadStrategies();

        JScrollPane scrollPane = new JScrollPane(strategyTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        add(scrollPane, BorderLayout.CENTER);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 10, 10));

        JButton addButton = new JButton("Add Strategy");
        addButton.addActionListener(e -> addStrategy());
        buttonPanel.add(addButton);

        JButton renameButton = new JButton("Rename Selected");
        renameButton.addActionListener(e -> renameStrategy());
        buttonPanel.add(renameButton);

        JButton removeButton = new JButton("Remove Selected");
        removeButton.addActionListener(e -> removeStrategy());
        buttonPanel.add(removeButton);

        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> dispose());
        buttonPanel.add(closeButton);

        add(buttonPanel, BorderLayout.SOUTH);

        pack();
        setMinimumSize(new Dimension(400, 400));
    }

    private void loadStrategies() {
        tableModel.setRowCount(0);
        for (String strategy : strategies) {
            if (strategy != null && !strategy.isEmpty()) {
                tableModel.addRow(new Object[] { strategy });
            }
        }
    }

    private void addStrategy() {
        String strategyName = JOptionPane.showInputDialog(this,
                "Enter strategy name:",
                "Add Strategy",
                JOptionPane.PLAIN_MESSAGE);

        if (strategyName != null && !strategyName.trim().isEmpty()) {
            strategyName = strategyName.trim();

            // Check if already exists
            if (strategies.contains(strategyName)) {
                JOptionPane.showMessageDialog(this,
                        "Strategy '" + strategyName + "' already exists.",
                        "Duplicate Strategy",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            strategies.add(strategyName);
            tableModel.addRow(new Object[] { strategyName });

            // Save to repository
            repository.add(strategyName);

            JOptionPane.showMessageDialog(this,
                    "Strategy '" + strategyName + "' added and saved successfully!\n" +
                            "You can now assign this strategy to trades by editing them.",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void removeStrategy() {
        int selectedRow = strategyTable.getSelectedRow();

        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Please select a strategy to remove.",
                    "No Selection",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        String strategyName = (String) tableModel.getValueAt(selectedRow, 0);

        int confirm = JOptionPane.showConfirmDialog(this,
                "Remove strategy '" + strategyName + "'?\n" +
                        "Note: This will not remove the strategy from existing trades.",
                "Confirm Remove",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            strategies.remove(strategyName);
            tableModel.removeRow(selectedRow);

            // Remove from repository
            repository.remove(strategyName);

            JOptionPane.showMessageDialog(this,
                    "Strategy removed successfully!",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }

    public List<String> getStrategies() {
        return new ArrayList<String>(strategies);
    }

    private void renameStrategy() {
        int selectedRow = strategyTable.getSelectedRow();

        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Please select a strategy to rename.",
                    "No Selection",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        String oldStrategyName = (String) tableModel.getValueAt(selectedRow, 0);

        // Prompt for new name
        String newStrategyName = JOptionPane.showInputDialog(this,
                "Enter new name for strategy '" + oldStrategyName + "':",
                "Rename Strategy",
                JOptionPane.QUESTION_MESSAGE);

        if (newStrategyName == null || newStrategyName.trim().isEmpty()) {
            return; // User cancelled or entered empty name
        }

        newStrategyName = newStrategyName.trim();

        // Attempt to rename in repository
        if (repository.rename(oldStrategyName, newStrategyName)) {
            // Update all trades with the old strategy name
            List<Trade> allTrades = tradeRepository.findAll();
            boolean tradesUpdated = false;

            for (Trade trade : allTrades) {
                if (oldStrategyName.equalsIgnoreCase(trade.getStrategy())) {
                    trade.setStrategy(newStrategyName);
                    tradeRepository.save(trade);
                    tradesUpdated = true;
                }
            }

            // Update local list
            for (int i = 0; i < strategies.size(); i++) {
                if (strategies.get(i).equalsIgnoreCase(oldStrategyName)) {
                    strategies.set(i, newStrategyName);
                    break;
                }
            }
            loadStrategies();

            String message = "Strategy renamed successfully!";
            if (tradesUpdated) {
                message += "\nAll associated trades have been updated.";
            }

            JOptionPane.showMessageDialog(this,
                    message,
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this,
                    "Failed to rename strategy. A strategy with name '" + newStrategyName + "' may already exist.",
                    "Rename Failed",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}
