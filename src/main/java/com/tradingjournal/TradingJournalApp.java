package com.tradingjournal;

import com.tradingjournal.repository.JsonTradeRepository;
import com.tradingjournal.repository.TradeRepository;
import com.tradingjournal.ui.MainWindow;

import javax.swing.*;

public class TradingJournalApp {
    public static void main(String[] args) {
        // Set Look and Feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> {
            TradeRepository repository = new JsonTradeRepository("trades.json");

            MainWindow window = new MainWindow(repository);
            window.setVisible(true);
        });
    }
}
