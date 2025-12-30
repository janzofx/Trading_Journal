package com.tradingjournal.ui;

import com.tradingjournal.model.Account;
import com.tradingjournal.model.Trade;
import com.tradingjournal.repository.AccountRepository;
import com.tradingjournal.repository.TradeRepository;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Dialog for managing accounts and their starting balances
 */
public class AccountManagerDialog extends JDialog {

    private JTable accountTable;
    private DefaultTableModel tableModel;
    private List<Account> accounts;
    private final AccountRepository repository;
    private final TradeRepository tradeRepository;

    public AccountManagerDialog(Frame parent, AccountRepository repository, TradeRepository tradeRepository) {
        super(parent, "Account Manager", true);
        this.repository = repository;
        this.tradeRepository = tradeRepository;
        this.accounts = repository.loadAll();
        initComponents();
        setLocationRelativeTo(parent);
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 5, 10));
        JLabel titleLabel = new JLabel("Manage Accounts");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        headerPanel.add(titleLabel, BorderLayout.WEST);
        add(headerPanel, BorderLayout.NORTH);

        // Table
        String[] columns = { "Account Name", "Starting Balance" };
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        accountTable = new JTable(tableModel);
        accountTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        accountTable.setRowHeight(25);

        loadAccounts();

        JScrollPane scrollPane = new JScrollPane(accountTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        add(scrollPane, BorderLayout.CENTER);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 10, 10));

        JButton addButton = new JButton("Add Account");
        addButton.addActionListener(e -> addAccount());
        buttonPanel.add(addButton);

        JButton renameButton = new JButton("Rename Selected");
        renameButton.addActionListener(e -> renameAccount());
        buttonPanel.add(renameButton);

        JButton removeButton = new JButton("Remove Selected");
        removeButton.addActionListener(e -> removeAccount());
        buttonPanel.add(removeButton);

        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> dispose());
        buttonPanel.add(closeButton);

        add(buttonPanel, BorderLayout.SOUTH);

        pack();
        setMinimumSize(new Dimension(450, 400));
    }

    private void loadAccounts() {
        tableModel.setRowCount(0);
        for (Account account : accounts) {
            tableModel.addRow(new Object[] {
                    account.getName(),
                    String.format("$%.2f", account.getStartingBalance())
            });
        }
    }

    private void addAccount() {
        // Create panel for input
        JPanel inputPanel = new JPanel(new GridLayout(2, 2, 5, 5));
        JTextField nameField = new JTextField();
        JTextField balanceField = new JTextField("0.00");

        inputPanel.add(new JLabel("Account Name:"));
        inputPanel.add(nameField);
        inputPanel.add(new JLabel("Starting Balance:"));
        inputPanel.add(balanceField);

        int result = JOptionPane.showConfirmDialog(this, inputPanel,
                "Add New Account", JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {
            String name = nameField.getText().trim();
            String balanceStr = balanceField.getText().trim();

            if (name.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Account name cannot be empty.",
                        "Invalid Input", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Check duplicates
            if (accounts.stream().anyMatch(a -> a.getName().equalsIgnoreCase(name))) {
                JOptionPane.showMessageDialog(this, "Account '" + name + "' already exists.",
                        "Duplicate Account", JOptionPane.WARNING_MESSAGE);
                return;
            }

            try {
                double balance = Double.parseDouble(balanceStr);
                Account newAccount = new Account(name, balance);

                repository.add(newAccount);
                accounts = repository.loadAll(); // Reload from repo
                loadAccounts();

                JOptionPane.showMessageDialog(this, "Account added successfully!",
                        "Success", JOptionPane.INFORMATION_MESSAGE);

            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Invalid balance format. Please enter a valid number.",
                        "Invalid Input", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void removeAccount() {
        int selectedRow = accountTable.getSelectedRow();

        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Please select an account to remove.",
                    "No Selection",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        String accountName = (String) tableModel.getValueAt(selectedRow, 0);

        int confirm = JOptionPane.showConfirmDialog(this,
                "Remove account '" + accountName + "'?\n" +
                        "Note: Trades assigned to this account will preserve their account tag.",
                "Confirm Remove",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            repository.remove(accountName);
            accounts = repository.loadAll(); // Reload
            loadAccounts();

            JOptionPane.showMessageDialog(this,
                    "Account removed successfully!",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void renameAccount() {
        int selectedRow = accountTable.getSelectedRow();

        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Please select an account to rename.",
                    "No Selection",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        String oldAccountName = (String) tableModel.getValueAt(selectedRow, 0);

        // Prompt for new name
        String newAccountName = JOptionPane.showInputDialog(this,
                "Enter new name for account '" + oldAccountName + "':",
                "Rename Account",
                JOptionPane.QUESTION_MESSAGE);

        if (newAccountName == null || newAccountName.trim().isEmpty()) {
            return; // User cancelled or entered empty name
        }

        newAccountName = newAccountName.trim();

        // Attempt to rename in repository
        if (repository.rename(oldAccountName, newAccountName)) {
            // Update all trades with the old account name
            List<Trade> allTrades = tradeRepository.findAll();
            boolean tradesUpdated = false;

            for (Trade trade : allTrades) {
                if (oldAccountName.equalsIgnoreCase(trade.getAccount())) {
                    trade.setAccount(newAccountName);
                    tradeRepository.save(trade);
                    tradesUpdated = true;
                }
            }

            accounts = repository.loadAll(); // Reload
            loadAccounts();

            String message = "Account renamed successfully!";
            if (tradesUpdated) {
                message += "\nAll associated trades have been updated.";
            }

            JOptionPane.showMessageDialog(this,
                    message,
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this,
                    "Failed to rename account. An account with name '" + newAccountName + "' may already exist.",
                    "Rename Failed",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}
