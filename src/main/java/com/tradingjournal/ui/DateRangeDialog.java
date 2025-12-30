package com.tradingjournal.ui;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Dialog for selecting custom date range
 */
public class DateRangeDialog extends JDialog {
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private JTextField startDateField;
    private JTextField endDateField;
    private LocalDate startDate;
    private LocalDate endDate;
    private boolean confirmed = false;

    public DateRangeDialog(Frame parent) {
        super(parent, "Select Date Range", true);
        initComponents();
        setLocationRelativeTo(parent);
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));

        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        // Start date
        gbc.gridx = 0;
        gbc.gridy = 0;
        mainPanel.add(new JLabel("Start Date (yyyy-MM-dd):"), gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        startDateField = new JTextField(LocalDate.now().minusMonths(1).format(DATE_FORMATTER), 15);
        mainPanel.add(startDateField, gbc);

        // End date
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        mainPanel.add(new JLabel("End Date (yyyy-MM-dd):"), gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        endDateField = new JTextField(LocalDate.now().format(DATE_FORMATTER), 15);
        mainPanel.add(endDateField, gbc);

        add(mainPanel, BorderLayout.CENTER);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        JButton okButton = new JButton("OK");
        okButton.addActionListener(e -> {
            if (validateAndSetDates()) {
                confirmed = true;
                dispose();
            }
        });
        buttonPanel.add(okButton);

        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> dispose());
        buttonPanel.add(cancelButton);

        add(buttonPanel, BorderLayout.SOUTH);

        pack();
        setMinimumSize(new Dimension(400, 150));
    }

    private boolean validateAndSetDates() {
        try {
            startDate = LocalDate.parse(startDateField.getText().trim(), DATE_FORMATTER);
            endDate = LocalDate.parse(endDateField.getText().trim(), DATE_FORMATTER);

            if (startDate.isAfter(endDate)) {
                JOptionPane.showMessageDialog(this,
                        "Start date must be before or equal to end date.",
                        "Invalid Date Range",
                        JOptionPane.ERROR_MESSAGE);
                return false;
            }

            return true;
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Please enter dates in format yyyy-MM-dd (e.g., 2025-01-01)",
                    "Invalid Date Format",
                    JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }
}
