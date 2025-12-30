package com.tradingjournal.ui;

import com.tradingjournal.model.Trade;
import com.tradingjournal.model.TradeType;
import com.tradingjournal.model.TradeStatistics;
import com.tradingjournal.model.Account;
import com.tradingjournal.model.EquityPoint;
import com.tradingjournal.repository.StrategyRepository;
import com.tradingjournal.repository.AccountRepository;
import com.tradingjournal.repository.TradeRepository;
import com.tradingjournal.service.EquityCurveCalculator;
import com.tradingjournal.service.TradeAnalyzer;
import com.tradingjournal.service.TradeBuddyTxtImportService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Main window of the application
 */
public class MainWindow extends JFrame {

    private final TradeRepository repository;
    private final StrategyRepository strategyRepository;
    private final AccountRepository accountRepository;
    private final TradeAnalyzer analyzer;
    private final EquityCurveCalculator equityCalculator;
    private final TradeBuddyTxtImportService importService;

    private TradeTableModel tableModel;
    private JTable tradeTable;
    private JPanel statsPanel;
    private JLabel totalTradesLabel;
    private JLabel winRateLabel;
    private JLabel profitLabel;
    private JLabel profitFactorLabel;
    private JLabel accountBalanceLabel;

    // Key metrics labels
    private JLabel metricsTradesLabel;
    private JLabel metricsWinRateLabel;
    private JLabel metricsProfitLabel;
    private JLabel metricsProfitFactorLabel;
    private JLabel metricsProfitPctLabel;
    private JLabel metricsLongWinLabel;
    private JLabel metricsShortWinLabel;
    private JLabel metricsDrawdownLabel;
    private JLabel metricsAvgProfitLabel;
    private JLabel metricsAvgLossLabel;
    private JLabel metricsMaxProfitLabel;
    private JLabel metricsMaxLossLabel;

    // Analytics tab metrics labels (15 total: 10 standard + 5 time-based)
    private JLabel[] analyticsMetricsLabels = new JLabel[15];

    private JComboBox<String> strategyFilter;
    private JComboBox<String> accountFilter;
    private JComboBox<String> symbolFilter;
    private JComboBox<String> magicFilter;
    private JComboBox<String> timeFilter;
    private JComboBox<String> typeFilter;
    private EquityCurvePanel equityCurvePanel;
    private EquityCurvePanel analyticsEquityCurvePanel; // Second equity curve for Analytics tab
    private CombinedEquityChartPanel combinedEquityChartPanel;
    private LongShortProfitChartPanel longShortProfitChartPanel;
    private LongShortTradesChartPanel longShortTradesChartPanel;
    private PnLByDayChartPanel pnlByDayChartPanel;
    private EntriesByHourChartPanel entriesByHourChartPanel;
    private EntriesByWeekdayChartPanel entriesByWeekdayChartPanel;
    private EntriesByMonthChartPanel entriesByMonthChartPanel;
    private PnLByHourChartPanel pnlByHourChartPanel;
    private PnLByMonthChartPanel pnlByMonthChartPanel;
    private List<Trade> allTrades;
    private List<Trade> filteredTrades;

    private boolean isUpdatingFilters = false;

    // Custom date range
    private java.time.LocalDate customStartDate;
    private java.time.LocalDate customEndDate;

    public MainWindow(TradeRepository repository) {
        this.repository = repository;
        this.strategyRepository = new StrategyRepository();
        this.accountRepository = new AccountRepository();
        this.importService = new TradeBuddyTxtImportService();
        this.analyzer = new TradeAnalyzer();
        this.equityCalculator = new EquityCurveCalculator();
        this.allTrades = new ArrayList<>();

        initializeUI();
        loadTrades();
        updateStatistics();
    }

    private void initializeUI() {
        setTitle("Trading Journal");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 700);
        setLocationRelativeTo(null);

        // Set application icon
        setApplicationIcon();

        // Create menu bar
        createMenuBar();

        // Create main panel with border layout
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Create statistics panel at top
        statsPanel = createStatisticsPanel();
        mainPanel.add(statsPanel, BorderLayout.NORTH);

        // Create filter panel
        JPanel filterPanel = createFilterPanel();

        // Create tabbed pane for main content
        JTabbedPane tabbedPane = new JTabbedPane();

        // Tab 1: Main view with trades table and equity curve (original layout)
        JPanel mainViewTab = new JPanel(new BorderLayout());

        // Create split pane for trades table (top) and equity curve (bottom)
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setResizeWeight(0.5);
        splitPane.setDividerLocation(300);

        // Top: Trades table with buttons
        JPanel topPanel = new JPanel(new BorderLayout());
        JScrollPane tableScrollPane = createTradeTable();
        topPanel.add(tableScrollPane, BorderLayout.CENTER);
        topPanel.add(createButtonPanel(), BorderLayout.SOUTH);
        splitPane.setTopComponent(topPanel);

        // Bottom: Equity curve (left) and metrics panel (right)
        JPanel bottomPanel = new JPanel(new BorderLayout());
        equityCurvePanel = new EquityCurvePanel();
        JScrollPane mainEquityScrollPane = new JScrollPane(equityCurvePanel);
        mainEquityScrollPane.setBorder(BorderFactory.createTitledBorder("Equity Curve"));
        bottomPanel.add(mainEquityScrollPane, BorderLayout.CENTER);

        // Key metrics panel on the right
        JPanel metricsPanel = createKeyMetricsPanel();
        bottomPanel.add(metricsPanel, BorderLayout.EAST);

        splitPane.setBottomComponent(bottomPanel);
        mainViewTab.add(splitPane, BorderLayout.CENTER);

        // Wrap in scroll pane for vertical scrolling
        JScrollPane mainScrollPane = new JScrollPane(mainViewTab);
        mainScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        mainScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        mainScrollPane.getVerticalScrollBar().setUnitIncrement(16);

        // Add mouse wheel listener recursively to all components
        addMouseWheelListenerRecursively(mainViewTab, mainScrollPane);

        tabbedPane.addTab("Trades & Equity", mainScrollPane);

        // Tab 2: Analytics with multiple charts and metrics
        // Tab 2: Analytics with 5-row layout
        JPanel analyticsTab = new JPanel(new BorderLayout());
        analyticsTab.setBackground(Color.WHITE);

        // Row 1: Equity Curve (left) + Key Metrics (right)
        JSplitPane row1Split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        row1Split.setResizeWeight(0.75);

        analyticsEquityCurvePanel = new EquityCurvePanel();
        JScrollPane equityCurveScrollPane2 = new JScrollPane(analyticsEquityCurvePanel);
        equityCurveScrollPane2.setBorder(BorderFactory.createTitledBorder("Equity Curve"));
        row1Split.setLeftComponent(equityCurveScrollPane2);

        JPanel analyticsMetricsPanel = createAnalyticsKeyMetricsPanel();
        JScrollPane metricsScrollPane = new JScrollPane(analyticsMetricsPanel);
        metricsScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        metricsScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        metricsScrollPane.getVerticalScrollBar().setUnitIncrement(16);
        row1Split.setRightComponent(metricsScrollPane);

        // Row 2: Trade Distribution (left) + Long vs Short Performance (right)
        JSplitPane row2Split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        row2Split.setResizeWeight(0.5);

        longShortTradesChartPanel = new LongShortTradesChartPanel();
        JScrollPane tradesDistScrollPane = new JScrollPane(longShortTradesChartPanel);
        tradesDistScrollPane.setBorder(BorderFactory.createTitledBorder("Trade Distribution"));
        row2Split.setLeftComponent(tradesDistScrollPane);

        longShortProfitChartPanel = new LongShortProfitChartPanel();
        JScrollPane profitScrollPane = new JScrollPane(longShortProfitChartPanel);
        profitScrollPane.setBorder(BorderFactory.createTitledBorder("Long vs Short Performance"));
        row2Split.setRightComponent(profitScrollPane);

        // Row 3: Entries by Hour (left) + P/L by Hour (right)
        JSplitPane row3Split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        row3Split.setResizeWeight(0.5);

        entriesByHourChartPanel = new EntriesByHourChartPanel();
        JScrollPane entriesHourScrollPane = new JScrollPane(entriesByHourChartPanel);
        entriesHourScrollPane.setBorder(BorderFactory.createTitledBorder("Entries by Hour"));
        row3Split.setLeftComponent(entriesHourScrollPane);

        pnlByHourChartPanel = new PnLByHourChartPanel();
        JScrollPane pnlHourScrollPane = new JScrollPane(pnlByHourChartPanel);
        pnlHourScrollPane.setBorder(BorderFactory.createTitledBorder("P/L by Hour"));
        row3Split.setRightComponent(pnlHourScrollPane);

        // Row 4: Entries by Weekday (left) + Wins/Losses by Weekday (right)
        JSplitPane row4Split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        row4Split.setResizeWeight(0.5);

        entriesByWeekdayChartPanel = new EntriesByWeekdayChartPanel();
        JScrollPane entriesWeekdayScrollPane = new JScrollPane(entriesByWeekdayChartPanel);
        entriesWeekdayScrollPane.setBorder(BorderFactory.createTitledBorder("Entries by Weekday"));
        row4Split.setLeftComponent(entriesWeekdayScrollPane);

        pnlByDayChartPanel = new PnLByDayChartPanel();
        JScrollPane pnlDayScrollPane = new JScrollPane(pnlByDayChartPanel);
        pnlDayScrollPane.setBorder(BorderFactory.createTitledBorder("Wins/Losses by Weekday"));
        row4Split.setRightComponent(pnlDayScrollPane);

        // Row 5: Entries by Month (left) + P/L by Month (right)
        JSplitPane row5Split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        row5Split.setResizeWeight(0.5);

        entriesByMonthChartPanel = new EntriesByMonthChartPanel();
        JScrollPane entriesMonthScrollPane = new JScrollPane(entriesByMonthChartPanel);
        entriesMonthScrollPane.setBorder(BorderFactory.createTitledBorder("Entries by Month"));
        row5Split.setLeftComponent(entriesMonthScrollPane);

        pnlByMonthChartPanel = new PnLByMonthChartPanel();
        JScrollPane pnlMonthScrollPane = new JScrollPane(pnlByMonthChartPanel);
        pnlMonthScrollPane.setBorder(BorderFactory.createTitledBorder("P/L by Month"));
        row5Split.setRightComponent(pnlMonthScrollPane);

        // Combine rows 1-2
        JSplitPane rows12Split = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        rows12Split.setResizeWeight(0.5);
        rows12Split.setTopComponent(row1Split);
        rows12Split.setBottomComponent(row2Split);

        // Combine rows 3-4
        JSplitPane rows34Split = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        rows34Split.setResizeWeight(0.5);
        rows34Split.setTopComponent(row3Split);
        rows34Split.setBottomComponent(row4Split);

        // Combine rows 1-2 and 3-4
        JSplitPane rows1234Split = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        rows1234Split.setResizeWeight(0.5);
        rows1234Split.setTopComponent(rows12Split);
        rows1234Split.setBottomComponent(rows34Split);

        // Add row 5 at the bottom
        JSplitPane mainVertSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        mainVertSplit.setResizeWeight(0.8);
        mainVertSplit.setTopComponent(rows1234Split);
        mainVertSplit.setBottomComponent(row5Split);

        analyticsTab.add(mainVertSplit, BorderLayout.CENTER);

        // Wrap in scroll pane for vertical scrolling
        JScrollPane analyticsScrollPane = new JScrollPane(analyticsTab);
        analyticsScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        analyticsScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        analyticsScrollPane.getVerticalScrollBar().setUnitIncrement(16);

        // Add mouse wheel listener recursively to all components
        addMouseWheelListenerRecursively(analyticsTab, analyticsScrollPane);

        tabbedPane.addTab("Analytics", analyticsScrollPane);

        // Add filter and tabs to main panel
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.add(filterPanel, BorderLayout.NORTH);
        centerPanel.add(tabbedPane, BorderLayout.CENTER);
        mainPanel.add(centerPanel, BorderLayout.CENTER);

        add(mainPanel);
    }

    private void createMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        // File menu
        JMenu fileMenu = new JMenu("File");

        JMenuItem importItem = new JMenuItem("Import from TXT...");
        importItem.addActionListener(e -> importTxtFile());
        fileMenu.add(importItem);

        fileMenu.addSeparator();

        JMenuItem exitItem = new JMenuItem("Exit");
        exitItem.addActionListener(e -> System.exit(0));
        fileMenu.add(exitItem);

        menuBar.add(fileMenu);

        // View menu
        JMenu viewMenu = new JMenu("View");

        JMenuItem refreshItem = new JMenuItem("Refresh");
        refreshItem.addActionListener(e -> {
            loadTrades();
            updateStatistics();
        });
        viewMenu.add(refreshItem);

        JMenuItem clearItem = new JMenuItem("Clear All Trades");
        clearItem.addActionListener(e -> clearAllTrades());
        viewMenu.add(clearItem);

        menuBar.add(viewMenu);

        // Tools menu
        JMenu toolsMenu = new JMenu("Tools");

        JMenuItem strategyManagerItem = new JMenuItem("Strategy Manager");
        strategyManagerItem.addActionListener(e -> showStrategyManager());
        toolsMenu.add(strategyManagerItem);

        JMenuItem accountManagerItem = new JMenuItem("Account Manager");
        accountManagerItem.addActionListener(e -> showAccountManager());
        toolsMenu.add(accountManagerItem);

        menuBar.add(toolsMenu);

        // Help menu
        JMenu helpMenu = new JMenu("Help");

        JMenuItem aboutItem = new JMenuItem("About");
        aboutItem.addActionListener(e -> {
            String message = "<html>" +
                    "<h2>Trading Journal</h2>" +
                    "<p><b>Import and analyze your MetaTrader 5 trading data</b></p>" +
                    "<br>" +
                    "<p><b>How to use:</b></p>" +
                    "<ol>" +
                    "<li>Export trades from MT5 to TXT file using TradeExporter.mq5</li>" +
                    "<li>Click 'Import from TXT' and select the exported file</li>" +
                    "<li>View your trades and comprehensive analytics</li>" +
                    "</ol>" +
                    "<br>" +
                    "<p><b>Key Features:</b></p>" +
                    "<ul>" +
                    "<li>Multi-account support with account management</li>" +
                    "<li>Real-time equity curve visualization</li>" +
                    "<li>15+ comprehensive trading metrics</li>" +
                    "<li>Advanced filtering (date, account, strategy, symbol)</li>" +
                    "<li>Performance analytics by hour, weekday, and month</li>" +
                    "<li>Long vs Short trade analysis</li>" +
                    "<li>Entry and P/L distribution charts</li>" +
                    "<li>Trade notes and editing capabilities</li>" +
                    "<li>Strategy management</li>" +
                    "</ul>" +
                    "<br>" +
                    "<p><b>Tips:</b></p>" +
                    "<ul>" +
                    "<li>Double-click a trade to view detailed information</li>" +
                    "<li>Use filters to analyze specific periods or strategies</li>" +
                    "<li>All charts update dynamically with applied filters</li>" +
                    "<li>Mouse wheel scrolling works throughout the app</li>" +
                    "<li>Resize chart panels by dragging split dividers</li>" +
                    "</ul>" +
                    "</html>";
            JOptionPane.showMessageDialog(this, message, "About Trading Journal",
                    JOptionPane.INFORMATION_MESSAGE);
        });
        helpMenu.add(aboutItem);

        menuBar.add(helpMenu);

        setJMenuBar(menuBar);
    }

    private JPanel createStatisticsPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 5, 15, 0));
        panel.setBorder(BorderFactory.createTitledBorder("Statistics"));
        panel.setPreferredSize(new Dimension(0, 80));

        // Create labels
        totalTradesLabel = createStatLabel("Total Trades", "0");
        winRateLabel = createStatLabel("Win Rate", "0%");
        profitLabel = createStatLabel("Net Profit", "$0.00");
        profitFactorLabel = createStatLabel("Profit Factor", "0.00");
        accountBalanceLabel = createStatLabel("Account Balance", "$0.00");

        panel.add(totalTradesLabel);
        panel.add(winRateLabel);
        panel.add(profitLabel);
        panel.add(profitFactorLabel);
        panel.add(accountBalanceLabel);

        return panel;
    }

    private JLabel createStatLabel(String title, String value) {
        JLabel label = new JLabel("<html><b>" + title + ":</b><br/>" + value + "</html>");
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setBorder(BorderFactory.createEtchedBorder());
        return label;
    }

    /**
     * Create key metrics panel for display next to equity curve
     */
    private JPanel createKeyMetricsPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createTitledBorder("Key Metrics"));
        panel.setPreferredSize(new Dimension(250, 0));

        metricsTradesLabel = new JLabel("Total Trades: 0");
        metricsWinRateLabel = new JLabel("Win Rate: 0.0%");
        metricsProfitFactorLabel = new JLabel("Profit Factor: 0.00");
        metricsProfitLabel = new JLabel("Net Profit: $0.00");
        metricsProfitPctLabel = new JLabel("Profit %: 0.0%");
        metricsLongWinLabel = new JLabel("Long Wins: 0.0%");
        metricsShortWinLabel = new JLabel("Short Wins: 0.0%");
        metricsDrawdownLabel = new JLabel("Max Drawdown: $0.00");
        metricsAvgProfitLabel = new JLabel("Avg Profit Trade: $0.00");
        metricsAvgLossLabel = new JLabel("Avg Loss Trade: $0.00");
        metricsMaxProfitLabel = new JLabel("Max Profit Trade: $0.00");
        metricsMaxLossLabel = new JLabel("Max Loss Trade: $0.00");

        // Style the labels
        Font labelFont = new Font("Arial", Font.PLAIN, 13);
        metricsTradesLabel.setFont(labelFont);
        metricsWinRateLabel.setFont(labelFont);
        metricsProfitFactorLabel.setFont(labelFont);
        metricsProfitLabel.setFont(labelFont);
        metricsProfitPctLabel.setFont(labelFont);
        metricsLongWinLabel.setFont(labelFont);
        metricsShortWinLabel.setFont(labelFont);
        metricsDrawdownLabel.setFont(labelFont);
        metricsAvgProfitLabel.setFont(labelFont);
        metricsAvgLossLabel.setFont(labelFont);
        metricsMaxProfitLabel.setFont(labelFont);
        metricsMaxLossLabel.setFont(labelFont);

        // Add spacing
        panel.add(Box.createVerticalStrut(10));
        panel.add(metricsTradesLabel);
        panel.add(Box.createVerticalStrut(8));
        panel.add(metricsWinRateLabel);
        panel.add(Box.createVerticalStrut(8));
        panel.add(metricsProfitFactorLabel);
        panel.add(Box.createVerticalStrut(8));
        panel.add(metricsProfitLabel);
        panel.add(Box.createVerticalStrut(8));
        panel.add(metricsProfitPctLabel);
        panel.add(Box.createVerticalStrut(8));
        panel.add(metricsLongWinLabel);
        panel.add(Box.createVerticalStrut(8));
        panel.add(metricsShortWinLabel);
        panel.add(Box.createVerticalStrut(8));
        panel.add(metricsDrawdownLabel);
        panel.add(Box.createVerticalStrut(8));
        panel.add(metricsAvgProfitLabel);
        panel.add(Box.createVerticalStrut(8));
        panel.add(metricsAvgLossLabel);
        panel.add(Box.createVerticalStrut(8));
        panel.add(metricsMaxProfitLabel);
        panel.add(Box.createVerticalStrut(8));
        panel.add(metricsMaxLossLabel);
        panel.add(Box.createVerticalGlue());

        return panel;
    }

    /**
     * Create extended key metrics panel for Analytics tab with time-based metrics
     */
    private JPanel createAnalyticsKeyMetricsPanel() {
        JPanel panel = new JPanel(new GridLayout(0, 2, 10, 5)); // 2 columns, 10px hgap, 5px vgap
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("Key Metrics"),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)));
        panel.setPreferredSize(new Dimension(380, 200)); // Wider
        panel.setMinimumSize(new Dimension(380, 200));

        Font labelFont = new Font("Arial", Font.PLAIN, 11); // Smaller font

        // Initialize labels using array
        analyticsMetricsLabels[0] = new JLabel("Total Trades: 0");
        analyticsMetricsLabels[1] = new JLabel("Win Rate: 0.0%");
        analyticsMetricsLabels[2] = new JLabel("Profit Factor: 0.00");
        analyticsMetricsLabels[3] = new JLabel("Net Profit: $0.00");
        analyticsMetricsLabels[4] = new JLabel("Long Wins: 0.0%");
        analyticsMetricsLabels[5] = new JLabel("Short Wins: 0.0%");
        analyticsMetricsLabels[6] = new JLabel("Avg. Profit Trade: $0.00");
        analyticsMetricsLabels[7] = new JLabel("Avg. Losing Trade: $0.00");
        analyticsMetricsLabels[8] = new JLabel("Max Profit Trade: $0.00");
        analyticsMetricsLabels[9] = new JLabel("Max Loss Trade: $0.00");
        analyticsMetricsLabels[10] = new JLabel("Avg. Holding Time: 0h");
        analyticsMetricsLabels[11] = new JLabel("Avg. Short Hold: 0h");
        analyticsMetricsLabels[12] = new JLabel("Avg. Long Hold: 0h");
        analyticsMetricsLabels[13] = new JLabel("Avg. Profit Hold: 0h");
        analyticsMetricsLabels[14] = new JLabel("Avg. Loss Hold: 0h");

        // Apply font to all labels and add to panel
        for (JLabel label : analyticsMetricsLabels) {
            label.setFont(labelFont);
            panel.add(label);
        }

        return panel;
    }

    private JScrollPane createTradeTable() {
        tableModel = new TradeTableModel();
        tradeTable = new JTable(tableModel);

        tradeTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tradeTable.setAutoCreateRowSorter(true);
        tradeTable.setRowHeight(25);

        // Add model listener to save changes when table is edited
        tableModel.addTableModelListener(e -> {
            if (e.getType() == javax.swing.event.TableModelEvent.UPDATE) {
                int row = e.getFirstRow();
                int column = e.getColumn();

                // Strategy column is 9, Account column is 10
                if (row >= 0 && (column == 9 || column == 10)) {
                    Trade trade = tableModel.getTrades().get(row);
                    repository.save(trade);
                    updateFilters();

                    // If account changed, update equity curve too
                    if (column == 10) {
                        applyFilters(); // This will update equity curve with new account data
                    }
                }
            }
        });

        // Set up custom cell editor for Strategy column (column 9)
        setupStrategyColumnEditor();

        // Add double-click listener to view trade details
        tradeTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int row = tradeTable.getSelectedRow();
                    if (row != -1) {
                        int modelRow = tradeTable.convertRowIndexToModel(row);
                        Trade trade = tableModel.getTradeAt(modelRow);
                        if (trade != null) {
                            showTradeDetails(trade);
                        }
                    }
                }
            }
        });

        tradeTable.setFillsViewportHeight(true);

        JScrollPane scrollPane = new JScrollPane(tradeTable);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        return scrollPane;
    }

    private void addManualTrade() {
        // Load suggestions
        List<String> strategies = strategyRepository.loadAll();
        // Add extra strategies from current trades
        for (String s : equityCalculator.getUniqueStrategies(allTrades)) {
            if (!strategies.contains(s))
                strategies.add(s);
        }

        List<String> accounts = new ArrayList<>();
        for (Account a : accountRepository.loadAll()) {
            accounts.add(a.getName());
        }
        // Add extra accounts from trades
        for (String a : equityCalculator.getUniqueAccounts(allTrades)) {
            if (!accounts.contains(a))
                accounts.add(a);
        }

        AddManualTradeDialog dialog = new AddManualTradeDialog(this, repository, strategies, accounts);
        dialog.setVisible(true);

        if (dialog.isTradeAdded()) {
            loadTrades();
            updateStatistics();
        }
    }

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        JButton importButton = new JButton("Import TXT");
        importButton.addActionListener(e -> importTxtFile());
        panel.add(importButton);

        JButton addTradeButton = new JButton("Add Manual Trade");
        addTradeButton.addActionListener(e -> addManualTrade());
        panel.add(addTradeButton);

        JButton refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(e -> {
            loadTrades();
            updateStatistics();
        });
        panel.add(refreshButton);

        return panel;
    }

    private void importTxtFile() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Select TradeBuddy TXT Export File");
        fileChooser.setFileFilter(new FileNameExtensionFilter(
                "Text Files (*.txt)", "txt"));

        int result = fileChooser.showOpenDialog(this);

        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();

            // Show progress dialog
            SwingWorker<List<Trade>, Void> worker = new SwingWorker<List<Trade>, Void>() {
                @Override
                protected List<Trade> doInBackground() throws Exception {
                    return importService.importFromTxt(selectedFile);
                }

                @Override
                protected void done() {
                    try {
                        List<Trade> importedTrades = get();

                        if (importedTrades.isEmpty()) {
                            JOptionPane.showMessageDialog(MainWindow.this,
                                    "No trades found in the file.",
                                    "Import Complete",
                                    JOptionPane.INFORMATION_MESSAGE);
                        } else {
                            // Prompt for account assignment using dropdown
                            List<Account> accounts = accountRepository.loadAll();
                            String account = null;

                            if (accounts.isEmpty()) {
                                int create = JOptionPane.showConfirmDialog(MainWindow.this,
                                        "No accounts created yet. Would you like to create one now?\n" +
                                                "You must create an account to import trades.",
                                        "No Accounts",
                                        JOptionPane.YES_NO_OPTION);

                                if (create == JOptionPane.YES_OPTION) {
                                    showAccountManager();
                                    accounts = accountRepository.loadAll(); // Reload
                                }
                            }

                            if (!accounts.isEmpty()) {
                                Object[] accountNames = accounts.stream().map(Account::getName).toArray();
                                Object selected = JOptionPane.showInputDialog(
                                        MainWindow.this,
                                        "Select Account for imported trades:",
                                        "Account Assignment",
                                        JOptionPane.QUESTION_MESSAGE,
                                        null,
                                        accountNames,
                                        accountNames[0]);
                                if (selected != null) {
                                    account = selected.toString();
                                }
                            } else {
                                JOptionPane.showMessageDialog(MainWindow.this,
                                        "Import cancelled: No account selected.");
                                return;
                            }

                            // Assign account to all imported trades (if user provided one)
                            if (account != null && !account.trim().isEmpty()) {
                                for (Trade trade : importedTrades) {
                                    trade.setAccount(account.trim());
                                }
                            }

                            // Prompt for strategy assignment (optional)
                            List<String> strategies = strategyRepository.loadAll();
                            String strategy = null;

                            if (!strategies.isEmpty()) {
                                // Add "None" option for users who don't want to assign a strategy
                                Object[] strategyOptions = new Object[strategies.size() + 1];
                                strategyOptions[0] = "(None)";
                                for (int i = 0; i < strategies.size(); i++) {
                                    strategyOptions[i + 1] = strategies.get(i);
                                }

                                Object selectedStrategy = JOptionPane.showInputDialog(
                                        MainWindow.this,
                                        "Optionally select a Strategy for imported trades:",
                                        "Strategy Assignment (Optional)",
                                        JOptionPane.QUESTION_MESSAGE,
                                        null,
                                        strategyOptions,
                                        strategyOptions[0]);

                                if (selectedStrategy != null && !"(None)".equals(selectedStrategy)) {
                                    strategy = selectedStrategy.toString();
                                }
                            }

                            // Assign strategy to all imported trades (if user provided one)
                            if (strategy != null && !strategy.trim().isEmpty()) {
                                for (Trade trade : importedTrades) {
                                    trade.setStrategy(strategy.trim());
                                }
                            }

                            // Merge with existing trades to preserve Strategy, Comment, etc.
                            for (Trade importedTrade : importedTrades) {
                                // If ticket exists, preserve local data
                                java.util.Optional<Trade> existingOpt = repository
                                        .findByTicket(importedTrade.getTicket());
                                if (existingOpt.isPresent()) {
                                    Trade existing = existingOpt.get();

                                    // Preserve Strategy
                                    if (importedTrade.getStrategy() == null || importedTrade.getStrategy().isEmpty()) {
                                        importedTrade.setStrategy(existing.getStrategy());
                                    }

                                    // Preserve Comment
                                    if (importedTrade.getComment() == null || importedTrade.getComment().isEmpty()) {
                                        importedTrade.setComment(existing.getComment());
                                    }

                                    // Preserve Magic Number if imported is 0/missing
                                    if (importedTrade.getMagicNumber() == 0) {
                                        importedTrade.setMagicNumber(existing.getMagicNumber());
                                    }

                                    // If user didn't force a new account, preserve existing
                                    if (account == null || account.trim().isEmpty()) {
                                        importedTrade.setAccount(existing.getAccount());
                                    }
                                }
                            }

                            repository.saveAll(importedTrades);
                            loadTrades();
                            updateStatistics();

                            JOptionPane.showMessageDialog(MainWindow.this,
                                    "Successfully imported " + importedTrades.size() + " trades.",
                                    "Import Complete",
                                    JOptionPane.INFORMATION_MESSAGE);
                        }
                    } catch (Exception e) {
                        JOptionPane.showMessageDialog(MainWindow.this,
                                "Error importing file: " + e.getMessage(),
                                "Import Error",
                                JOptionPane.ERROR_MESSAGE);
                        e.printStackTrace();
                    }
                }
            };

            worker.execute();
        }
    }

    private void loadTrades() {
        allTrades = repository.findAll();
        // tableModel.setTrades(allTrades); // applyFilters does this
        updateFilters();
        applyFilters();
        setupStrategyColumnEditor(); // Refresh strategy dropdown
        setupAccountColumnEditor(); // Refresh account dropdown
        updateCombinedEquityChart(); // Update analytics chart
    }

    private void updateEquityCurve() {
        // Use filteredTrades if available, otherwise use allTrades
        List<Trade> tradesToDisplay = (filteredTrades != null) ? filteredTrades : allTrades;

        if (tradesToDisplay == null || tradesToDisplay.isEmpty()) {
            if (equityCurvePanel != null) {
                equityCurvePanel.setEquityCurve(new ArrayList<>());
            }
            if (analyticsEquityCurvePanel != null) {
                analyticsEquityCurvePanel.setEquityCurve(new ArrayList<>());
            }
            return;
        }

        String selectedAccount = (String) accountFilter.getSelectedItem();
        double startingBalance = 0.0;

        // If a specific account is selected, attempt to get its starting balance
        if (selectedAccount != null && !"All Accounts".equals(selectedAccount)) {
            Optional<Account> accOpt = accountRepository.findByName(selectedAccount);
            if (accOpt.isPresent()) {
                startingBalance = accOpt.get().getStartingBalance();
            }
        }

        List<EquityPoint> equityCurve = equityCalculator.calculateEquityCurve(tradesToDisplay, startingBalance);

        // Update both equity curve panels
        if (equityCurvePanel != null) {
            equityCurvePanel.setEquityCurve(equityCurve);
        }
        if (analyticsEquityCurvePanel != null) {
            analyticsEquityCurvePanel.setEquityCurve(equityCurve);
        }
    }

    private void updateStatistics() {
        updateStatistics(repository.findAll());
    }

    private void updateStatistics(List<Trade> trades) {
        TradeStatistics stats = analyzer.calculateStatistics(trades);

        totalTradesLabel.setText("<html><b>Total Trades:</b><br/>" + stats.getTotalTrades() + "</html>");
        winRateLabel
                .setText("<html><b>Win Rate:</b><br/>" + String.format("%.1f%%", stats.getWinRate() * 100) + "</html>");

        String profitColor = stats.getNetProfit() >= 0 ? "green" : "red";
        profitLabel.setText("<html><b>Net Profit:</b><br/><font color='" + profitColor + "'>$" +
                String.format("%.2f", stats.getNetProfit()) + "</font></html>");

        profitFactorLabel.setText("<html><b>Profit Factor:</b><br/>" +
                String.format("%.2f", stats.getProfitFactor()) + "</html>");

        // Calculate account balance (starting balance + net profit)
        double startingBalance = 0.0;
        String selectedAccount = (String) accountFilter.getSelectedItem();

        if (selectedAccount != null && !"All Accounts".equals(selectedAccount)) {
            // Single account selected - use its starting balance
            Optional<Account> accOpt = accountRepository.findByName(selectedAccount);
            if (accOpt.isPresent()) {
                startingBalance = accOpt.get().getStartingBalance();
            }
        } else {
            // "All Accounts" selected - sum all account starting balances
            List<Account> allAccounts = accountRepository.loadAll();
            for (Account acc : allAccounts) {
                startingBalance += acc.getStartingBalance();
            }
        }

        double currentBalance = startingBalance + stats.getNetProfit();
        String balanceColor = currentBalance >= startingBalance ? "green" : "red";
        accountBalanceLabel.setText("<html><b>Account Balance:</b><br/><font color='" + balanceColor + "'>$" +
                String.format("%.2f", currentBalance) + "</font></html>");

        // Update key metrics panel
        updateKeyMetrics(trades);

        // Update analytics metrics panel
        updateAnalyticsKeyMetrics(trades);

        // Update long/short profit chart
        if (longShortProfitChartPanel != null) {
            longShortProfitChartPanel.setTrades(trades);
        }

        // Update long/short trades percentage chart
        if (longShortTradesChartPanel != null) {
            longShortTradesChartPanel.setTrades(trades);
        }

        // Update P/L by day chart
        if (pnlByDayChartPanel != null) {
            pnlByDayChartPanel.setTrades(trades);
        }

        // Update entry distribution charts
        if (entriesByHourChartPanel != null) {
            entriesByHourChartPanel.setTrades(trades);
        }
        if (entriesByWeekdayChartPanel != null) {
            entriesByWeekdayChartPanel.setTrades(trades);
        }
        if (entriesByMonthChartPanel != null) {
            entriesByMonthChartPanel.setTrades(trades);
        }

        // Update P/L by hour and month charts
        if (pnlByHourChartPanel != null) {
            pnlByHourChartPanel.setTrades(trades);
        }
        if (pnlByMonthChartPanel != null) {
            pnlByMonthChartPanel.setTrades(trades);
        }
    }

    private void updateKeyMetrics(List<Trade> trades) {
        if (metricsTradesLabel == null) {
            System.out.println("DEBUG: metricsTradesLabel is null, metrics panel not initialized yet");
            return;
        }

        if (trades == null || trades.isEmpty()) {
            metricsTradesLabel.setText("Total Trades: 0");
            metricsWinRateLabel.setText("Win Rate: 0.0%");
            metricsProfitFactorLabel.setText("Profit Factor: 0.00");
            metricsProfitLabel.setText("Net Profit: $0.00");
            metricsProfitPctLabel.setText("Profit: $0.00");
            metricsLongWinLabel.setText("Long Wins: 0.0%");
            metricsShortWinLabel.setText("Short Wins: 0.0%");
            metricsDrawdownLabel.setText("Max Drawdown: $0.00");
            metricsAvgProfitLabel.setText("Avg Profit Trade: $0.00");
            metricsAvgLossLabel.setText("Avg Loss Trade: $0.00");
            metricsMaxProfitLabel.setText("Max Profit Trade: $0.00");
            metricsMaxLossLabel.setText("Max Loss Trade: $0.00");
            return;
        }

        TradeStatistics stats = analyzer.calculateStatistics(trades);

        // Calculate long/short win rates
        long longTrades = 0;
        long longWins = 0;
        long shortTrades = 0;
        long shortWins = 0;

        // Calculate profit/loss metrics
        double totalProfitWins = 0.0;
        int profitTradeCount = 0;
        double totalLosses = 0.0;
        int lossTradeCount = 0;
        double maxProfit = Double.NEGATIVE_INFINITY;
        double maxLoss = Double.POSITIVE_INFINITY;

        for (Trade t : trades) {
            // Long/Short tracking
            if (t.getType() == TradeType.BUY) {
                longTrades++;
                if (t.isWinner())
                    longWins++;
            } else if (t.getType() == TradeType.SELL) {
                shortTrades++;
                if (t.isWinner())
                    shortWins++;
            }

            // Profit/Loss tracking
            double netProfit = t.getNetProfit();
            if (netProfit > 0) {
                totalProfitWins += netProfit;
                profitTradeCount++;
                if (netProfit > maxProfit) {
                    maxProfit = netProfit;
                }
            } else if (netProfit < 0) {
                totalLosses += netProfit;
                lossTradeCount++;
                if (netProfit < maxLoss) {
                    maxLoss = netProfit;
                }
            }
        }

        double longWinRate = longTrades > 0 ? (double) longWins / longTrades * 100.0 : 0.0;
        double shortWinRate = shortTrades > 0 ? (double) shortWins / shortTrades * 100.0 : 0.0;

        double avgProfit = profitTradeCount > 0 ? totalProfitWins / profitTradeCount : 0.0;
        double avgLoss = lossTradeCount > 0 ? totalLosses / lossTradeCount : 0.0;

        // Calculate drawdown
        List<EquityPoint> equityCurve = equityCalculator.calculateEquityCurve(trades);
        double maxDrawdown = calculateMaxDrawdown(equityCurve);

        metricsTradesLabel.setText("Total Trades: " + stats.getTotalTrades());
        metricsWinRateLabel.setText(String.format("Win Rate: %.1f%%", stats.getWinRate() * 100));
        metricsProfitFactorLabel.setText(String.format("Profit Factor: %.2f", stats.getProfitFactor()));
        metricsProfitLabel.setText(String.format("Net Profit: $%.2f", stats.getNetProfit()));
        metricsProfitPctLabel.setText(String.format("Profit: $%.2f", stats.getNetProfit()));
        metricsLongWinLabel.setText(String.format("Long Wins: %.1f%%", longWinRate));
        metricsShortWinLabel.setText(String.format("Short Wins: %.1f%%", shortWinRate));
        metricsDrawdownLabel.setText(String.format("Max Drawdown: $%.2f", maxDrawdown));
        metricsAvgProfitLabel.setText(String.format("Avg Profit Trade: $%.2f", avgProfit));
        metricsAvgLossLabel.setText(String.format("Avg Loss Trade: $%.2f", avgLoss));
        metricsMaxProfitLabel.setText(String.format("Max Profit Trade: $%.2f",
                maxProfit == Double.NEGATIVE_INFINITY ? 0.0 : maxProfit));
        metricsMaxLossLabel.setText(String.format("Max Loss Trade: $%.2f",
                maxLoss == Double.POSITIVE_INFINITY ? 0.0 : maxLoss));
    }

    /**
     * Update analytics key metrics panel (includes time-based metrics)
     */
    private void updateAnalyticsKeyMetrics(List<Trade> trades) {
        if (analyticsMetricsLabels[0] == null || trades == null || trades.isEmpty()) {
            // Set all to zero/default
            for (int i = 0; i < analyticsMetricsLabels.length; i++) {
                if (analyticsMetricsLabels[i] != null) {
                    switch (i) {
                        case 0:
                            analyticsMetricsLabels[i].setText("Total Trades: 0");
                            break;
                        case 1:
                            analyticsMetricsLabels[i].setText("Win Rate: 0.0%");
                            break;
                        case 2:
                            analyticsMetricsLabels[i].setText("Profit Factor: 0.00");
                            break;
                        case 3:
                            analyticsMetricsLabels[i].setText("Net Profit: $0.00");
                            break;
                        case 4:
                            analyticsMetricsLabels[i].setText("Long Wins: 0.0%");
                            break;
                        case 5:
                            analyticsMetricsLabels[i].setText("Short Wins: 0.0%");
                            break;
                        case 6:
                            analyticsMetricsLabels[i].setText("Avg. Profit Trade: $0.00");
                            break;
                        case 7:
                            analyticsMetricsLabels[i].setText("Avg. Losing Trade: $0.00");
                            break;
                        case 8:
                            analyticsMetricsLabels[i].setText("Max Profit Trade: $0.00");
                            break;
                        case 9:
                            analyticsMetricsLabels[i].setText("Max Loss Trade: $0.00");
                            break;
                        case 10:
                            analyticsMetricsLabels[i].setText("Avg. Holding Time: 0h");
                            break;
                        case 11:
                            analyticsMetricsLabels[i].setText("Avg. Short Hold: 0h");
                            break;
                        case 12:
                            analyticsMetricsLabels[i].setText("Avg. Long Hold: 0h");
                            break;
                        case 13:
                            analyticsMetricsLabels[i].setText("Avg. Profit Hold: 0h");
                            break;
                        case 14:
                            analyticsMetricsLabels[i].setText("Avg. Loss Hold: 0h");
                            break;
                    }
                }
            }
            return;
        }

        // Calculate statistics
        TradeStatistics stats = analyzer.calculateStatistics(trades);

        int longTrades = 0, shortTrades = 0, longWins = 0, shortWins = 0;
        double totalProfitWins = 0.0, totalLosses = 0.0;
        int profitTradeCount = 0, lossTradeCount = 0;
        double maxProfit = Double.NEGATIVE_INFINITY, maxLoss = Double.POSITIVE_INFINITY;

        // Time tracking (in milliseconds)
        long totalHoldTime = 0, totalShortHoldTime = 0, totalLongHoldTime = 0;
        long totalProfitHoldTime = 0, totalLossHoldTime = 0;
        int shortCount = 0, longCount = 0, profitHoldCount = 0, lossHoldCount = 0;

        for (Trade t : trades) {
            // Calculate hold time
            long holdTime = t.getCloseTime().atZone(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli() -
                    t.getOpenTime().atZone(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli();
            totalHoldTime += holdTime;

            // Long/Short tracking
            if (t.getType() == TradeType.BUY) {
                longTrades++;
                totalLongHoldTime += holdTime;
                longCount++;
                if (t.isWinner())
                    longWins++;
            } else if (t.getType() == TradeType.SELL) {
                shortTrades++;
                totalShortHoldTime += holdTime;
                shortCount++;
                if (t.isWinner())
                    shortWins++;
            }

            // Profit/Loss tracking
            double netProfit = t.getNetProfit();
            if (netProfit > 0) {
                totalProfitWins += netProfit;
                profitTradeCount++;
                totalProfitHoldTime += holdTime;
                profitHoldCount++;
                if (netProfit > maxProfit)
                    maxProfit = netProfit;
            } else if (netProfit < 0) {
                totalLosses += Math.abs(netProfit);
                lossTradeCount++;
                totalLossHoldTime += holdTime;
                lossHoldCount++;
                if (netProfit < maxLoss)
                    maxLoss = netProfit;
            }
        }

        // Update labels
        analyticsMetricsLabels[0].setText("Total Trades: " + stats.getTotalTrades());
        analyticsMetricsLabels[1].setText(String.format("Win Rate: %.1f%%", stats.getWinRate() * 100));
        analyticsMetricsLabels[2].setText(String.format("Profit Factor: %.2f", stats.getProfitFactor()));
        analyticsMetricsLabels[3].setText(String.format("Net Profit: $%.2f", stats.getNetProfit()));
        analyticsMetricsLabels[4].setText(String.format("Long Wins: %.1f%%",
                longTrades > 0 ? (longWins * 100.0 / longTrades) : 0.0));
        analyticsMetricsLabels[5].setText(String.format("Short Wins: %.1f%%",
                shortTrades > 0 ? (shortWins * 100.0 / shortTrades) : 0.0));
        analyticsMetricsLabels[6].setText(String.format("Avg. Profit Trade: $%.2f",
                profitTradeCount > 0 ? totalProfitWins / profitTradeCount : 0.0));
        analyticsMetricsLabels[7].setText(String.format("Avg. Losing Trade: $%.2f",
                lossTradeCount > 0 ? totalLosses / lossTradeCount : 0.0));
        analyticsMetricsLabels[8].setText(String.format("Max Profit Trade: $%.2f",
                maxProfit == Double.NEGATIVE_INFINITY ? 0.0 : maxProfit));
        analyticsMetricsLabels[9].setText(String.format("Max Loss Trade: $%.2f",
                maxLoss == Double.POSITIVE_INFINITY ? 0.0 : maxLoss));

        // Time-based metrics (convert milliseconds to readable format)
        analyticsMetricsLabels[10].setText("Avg. Holding Time: " +
                formatDuration(trades.size() > 0 ? totalHoldTime / trades.size() : 0));
        analyticsMetricsLabels[11].setText("Avg. Short Hold: " +
                formatDuration(shortCount > 0 ? totalShortHoldTime / shortCount : 0));
        analyticsMetricsLabels[12].setText("Avg. Long Hold: " +
                formatDuration(longCount > 0 ? totalLongHoldTime / longCount : 0));
        analyticsMetricsLabels[13].setText("Avg. Profit Hold: " +
                formatDuration(profitHoldCount > 0 ? totalProfitHoldTime / profitHoldCount : 0));
        analyticsMetricsLabels[14].setText("Avg. Loss Hold: " +
                formatDuration(lossHoldCount > 0 ? totalLossHoldTime / lossHoldCount : 0));
    }

    /**
     * Format milliseconds duration to readable string (e.g., "2d 5h", "3h 30m",
     * "45m")
     */
    private String formatDuration(long millis) {
        if (millis == 0)
            return "0h";

        long days = millis / (24 * 60 * 60 * 1000);
        long hours = (millis % (24 * 60 * 60 * 1000)) / (60 * 60 * 1000);
        long minutes = (millis % (60 * 60 * 1000)) / (60 * 1000);

        if (days > 0) {
            return String.format("%dd %dh", days, hours);
        } else if (hours > 0) {
            return String.format("%dh %dm", hours, minutes);
        } else {
            return String.format("%dm", minutes);
        }
    }

    private double calculateMaxDrawdown(List<EquityPoint> equityCurve) {
        if (equityCurve == null || equityCurve.isEmpty())
            return 0.0;

        double maxDrawdown = 0.0;
        double peak = equityCurve.get(0).getCumulativeProfit();

        for (EquityPoint point : equityCurve) {
            double current = point.getCumulativeProfit();
            if (current > peak) {
                peak = current;
            }
            double drawdown = peak - current;
            if (drawdown > maxDrawdown) {
                maxDrawdown = drawdown;
            }
        }

        return maxDrawdown;
    }

    private void showTradeDetails(Trade trade) {
        // Load strategies for dropdown
        List<String> savedStrategies = strategyRepository.loadAll();
        List<String> tradeStrategies = equityCalculator.getUniqueStrategies(allTrades);

        // Combine both lists (avoiding duplicates)
        List<String> allStrategies = new ArrayList<String>(savedStrategies);
        for (String strategy : tradeStrategies) {
            if (!allStrategies.contains(strategy)) {
                allStrategies.add(strategy);
            }
        }

        TradeDetailDialog dialog = new TradeDetailDialog(this, trade, repository, allStrategies);
        dialog.setVisible(true);

        // Refresh if changes were saved or deleted
        if (dialog.isSaved() || dialog.isDeleted()) {
            loadTrades();
            updateStatistics();
        }
    }

    private void clearAllTrades() {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete all trades?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            repository.deleteAll();
            loadTrades();
            updateStatistics();
        }
    }

    // The showAboutDialog method was removed and its content moved to an action
    // listener.
    // The new action listener for 'aboutItem' should be placed where 'aboutItem' is
    // initialized or added to a menu.

    /**
     * Create filter panel with strategy and account dropdowns
     */
    private JPanel createFilterPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setBorder(BorderFactory.createTitledBorder("Filters"));

        JLabel strategyLabel = new JLabel("Strategy:");
        panel.add(strategyLabel);

        strategyFilter = new JComboBox<String>();
        strategyFilter.setPreferredSize(new Dimension(150, 25));
        strategyFilter.addActionListener(e -> applyFilters());
        panel.add(strategyFilter);

        panel.add(Box.createHorizontalStrut(20));

        JLabel accountLabel = new JLabel("Account:");
        panel.add(accountLabel);

        accountFilter = new JComboBox<String>();
        accountFilter.setPreferredSize(new Dimension(150, 25));
        accountFilter.addActionListener(e -> applyFilters());
        panel.add(accountFilter);

        panel.add(Box.createHorizontalStrut(20));

        JLabel symbolLabel = new JLabel("Symbol:");
        panel.add(symbolLabel);

        symbolFilter = new JComboBox<String>();
        symbolFilter.setPreferredSize(new Dimension(120, 25));
        symbolFilter.addActionListener(e -> applyFilters());
        panel.add(symbolFilter);

        panel.add(Box.createHorizontalStrut(20));

        JLabel magicLabel = new JLabel("Magic:");
        panel.add(magicLabel);

        magicFilter = new JComboBox<String>();
        magicFilter.setPreferredSize(new Dimension(100, 25));
        magicFilter.addActionListener(e -> applyFilters());
        panel.add(magicFilter);

        panel.add(Box.createHorizontalStrut(20));

        JLabel timeLabel = new JLabel("Period:");
        panel.add(timeLabel);

        timeFilter = new JComboBox<String>(new String[] {
                "All Time", "Today", "Last 7 Days", "Last 30 Days",
                "Last 90 Days", "This Month", "Last Month", "This Year", "Custom Range..."
        });
        timeFilter.setPreferredSize(new Dimension(130, 25));
        timeFilter.addActionListener(e -> {
            if ("Custom Range...".equals(timeFilter.getSelectedItem())) {
                showCustomDateRangeDialog();
            } else {
                applyFilters();
            }
        });
        panel.add(timeFilter);

        panel.add(Box.createHorizontalStrut(20));

        JLabel typeLabel = new JLabel("Type:");
        panel.add(typeLabel);

        typeFilter = new JComboBox<String>(new String[] { "All Types", "Long Only", "Short Only" });
        typeFilter.setPreferredSize(new Dimension(110, 25));
        typeFilter.addActionListener(e -> applyFilters());
        panel.add(typeFilter);

        JButton clearButton = new JButton("Clear Filters");
        clearButton.addActionListener(e -> clearFilters());
        panel.add(clearButton);

        return panel;
    }

    /**
     * Update filter dropdowns with current data
     */
    private void updateFilters() {
        if (strategyFilter == null || accountFilter == null)
            return;

        isUpdatingFilters = true; // Prevent action listeners during update

        // Populate strategy filter
        String currentStrategy = (String) strategyFilter.getSelectedItem();
        strategyFilter.removeAllItems();
        strategyFilter.addItem("All Strategies");

        // Load strategies from repository first
        List<String> repoStrategies = strategyRepository.loadAll();
        List<String> addedStrategies = new ArrayList<>();

        for (String strategy : repoStrategies) {
            strategyFilter.addItem(strategy);
            addedStrategies.add(strategy);
        }

        // Add any other strategies found in trades
        for (String strategy : equityCalculator.getUniqueStrategies(allTrades)) {
            if (!addedStrategies.contains(strategy)) {
                strategyFilter.addItem(strategy);
            }
        }

        if (currentStrategy != null) {
            strategyFilter.setSelectedItem(currentStrategy);
        } else {
            strategyFilter.setSelectedIndex(0);
        }

        // Populate account filter
        String currentAccount = (String) accountFilter.getSelectedItem();
        accountFilter.removeAllItems();
        accountFilter.addItem("All Accounts");

        // Load accounts from repository
        List<Account> repoAccounts = accountRepository.loadAll();
        List<String> addedAccounts = new ArrayList<>();

        for (Account acc : repoAccounts) {
            accountFilter.addItem(acc.getName());
            addedAccounts.add(acc.getName());
        }

        // Add any other accounts found in trades
        for (String account : equityCalculator.getUniqueAccounts(allTrades)) {
            if (!addedAccounts.contains(account)) {
                accountFilter.addItem(account);
            }
        }

        if (currentAccount != null) {
            accountFilter.setSelectedItem(currentAccount);
        } else {
            accountFilter.setSelectedIndex(0);
        }

        // Populate symbol filter
        String currentSymbol = (String) symbolFilter.getSelectedItem();
        symbolFilter.removeAllItems();
        symbolFilter.addItem("All Symbols");
        for (String symbol : getUniqueSymbols(allTrades)) {
            symbolFilter.addItem(symbol);
        }
        if (currentSymbol != null)
            symbolFilter.setSelectedItem(currentSymbol);

        // Populate magic filter
        String currentMagic = (String) magicFilter.getSelectedItem();
        magicFilter.removeAllItems();
        magicFilter.addItem("All Magic");
        for (String magic : getUniqueMagicNumbers(allTrades)) {
            magicFilter.addItem(magic);
        }
        if (currentMagic != null)
            magicFilter.setSelectedItem(currentMagic);

        isUpdatingFilters = false; // Re-enable action listeners
    }

    /**
     * Update the combined equity chart showing all strategies
     */
    private void updateCombinedEquityChart() {
        if (combinedEquityChartPanel == null || allTrades == null) {
            return;
        }

        // Get all unique strategies
        List<String> strategies = equityCalculator.getUniqueStrategies(allTrades);

        if (strategies.isEmpty()) {
            combinedEquityChartPanel.setStrategyCurves(new HashMap<>());
            return;
        }

        // Calculate equity curve for each strategy
        Map<String, List<EquityPoint>> strategyCurves = new HashMap<>();

        for (String strategy : strategies) {
            // Filter trades by strategy
            List<Trade> strategyTrades = new ArrayList<>();
            for (Trade trade : allTrades) {
                if (strategy.equals(trade.getStrategy())) {
                    strategyTrades.add(trade);
                }
            }

            // Get starting balance for the account if applicable
            double startingBalance = 0.0;
            if (!strategyTrades.isEmpty()) {
                String account = strategyTrades.get(0).getAccount();
                if (account != null && !account.isEmpty()) {
                    Optional<com.tradingjournal.model.Account> accOpt = accountRepository.findByName(account);
                    if (accOpt.isPresent()) {
                        startingBalance = accOpt.get().getStartingBalance();
                    }
                }
            }

            // Calculate equity curve
            List<EquityPoint> curve = equityCalculator.calculateEquityCurve(strategyTrades, startingBalance);
            if (!curve.isEmpty()) {
                strategyCurves.put(strategy, curve);
            }
        }

        combinedEquityChartPanel.setStrategyCurves(strategyCurves);
    }

    /**
     * Apply filters to trades and update UI
     */
    private void applyFilters() {
        if (allTrades == null || isUpdatingFilters)
            return;

        String selectedStrategy = (String) strategyFilter.getSelectedItem();
        String selectedAccount = (String) accountFilter.getSelectedItem();
        String selectedSymbol = (String) symbolFilter.getSelectedItem();
        String selectedMagic = (String) magicFilter.getSelectedItem();
        String selectedTime = (String) timeFilter.getSelectedItem();
        String selectedType = (String) typeFilter.getSelectedItem();

        // Filter trades
        this.filteredTrades = new ArrayList<Trade>(allTrades);

        if (selectedStrategy != null && !selectedStrategy.equals("All Strategies")) {
            List<Trade> temp = new ArrayList<Trade>();
            for (Trade t : filteredTrades) {
                if (selectedStrategy.equals(t.getStrategy())) {
                    temp.add(t);
                }
            }
            filteredTrades = temp;
        }

        if (selectedAccount != null && !selectedAccount.equals("All Accounts")) {
            List<Trade> temp = new ArrayList<Trade>();
            for (Trade t : filteredTrades) {
                if (selectedAccount.equals(t.getAccount())) {
                    temp.add(t);
                }
            }
            filteredTrades = temp;
        }

        if (selectedSymbol != null && !selectedSymbol.equals("All Symbols")) {
            List<Trade> temp = new ArrayList<Trade>();
            for (Trade t : filteredTrades) {
                if (selectedSymbol.equals(t.getSymbol())) {
                    temp.add(t);
                }
            }
            filteredTrades = temp;
        }

        if (selectedMagic != null && !selectedMagic.equals("All Magic")) {
            List<Trade> temp = new ArrayList<Trade>();
            for (Trade t : filteredTrades) {
                if (selectedMagic.equals(String.valueOf(t.getMagicNumber()))) {
                    temp.add(t);
                }
            }
            filteredTrades = temp;
        }

        if (selectedTime != null && !selectedTime.equals("All Time")) {
            filteredTrades = filterByTimePeriod(filteredTrades, selectedTime);
        }

        if (selectedType != null && !selectedType.equals("All Types")) {
            List<Trade> temp = new ArrayList<Trade>();
            for (Trade t : filteredTrades) {
                if (selectedType.equals("Long Only") && t.getType() == TradeType.BUY) {
                    temp.add(t);
                } else if (selectedType.equals("Short Only") && t.getType() == TradeType.SELL) {
                    temp.add(t);
                }
            }
            filteredTrades = temp;
        }

        // Update table
        tableModel.setTrades(filteredTrades);

        // Update all statistics with filtered data
        updateStatistics(filteredTrades);

        // Update equity curve
        updateEquityCurve();
    }

    /**
     * Clear all filters
     */
    private void clearFilters() {
        if (strategyFilter != null && strategyFilter.getItemCount() > 0) {
            strategyFilter.setSelectedIndex(0);
        }
        if (accountFilter != null && accountFilter.getItemCount() > 0) {
            accountFilter.setSelectedIndex(0);
        }
        if (symbolFilter != null && symbolFilter.getItemCount() > 0) {
            symbolFilter.setSelectedIndex(0);
        }
        if (magicFilter != null && magicFilter.getItemCount() > 0) {
            magicFilter.setSelectedIndex(0);
        }
        if (timeFilter != null && timeFilter.getItemCount() > 0) {
            timeFilter.setSelectedIndex(0);
        }
        if (typeFilter != null && typeFilter.getItemCount() > 0) {
            typeFilter.setSelectedIndex(0);
        }
    }

    /**
     * Get unique symbols from trades
     */
    private List<String> getUniqueSymbols(List<Trade> trades) {
        List<String> symbols = new ArrayList<String>();
        for (Trade t : trades) {
            String symbol = t.getSymbol();
            if (symbol != null && !symbol.isEmpty() && !symbols.contains(symbol)) {
                symbols.add(symbol);
            }
        }
        java.util.Collections.sort(symbols);
        return symbols;
    }

    /**
     * Get unique magic numbers from trades
     */
    private List<String> getUniqueMagicNumbers(List<Trade> trades) {
        List<String> magics = new ArrayList<String>();
        for (Trade t : trades) {
            String magic = String.valueOf(t.getMagicNumber());
            if (!magics.contains(magic)) {
                magics.add(magic);
            }
        }
        java.util.Collections.sort(magics);
        return magics;
    }

    /**
     * Filter trades by time period
     */
    private List<Trade> filterByTimePeriod(List<Trade> trades, String period) {
        java.time.LocalDateTime now = java.time.LocalDateTime.now();
        java.time.LocalDateTime cutoff = null;

        switch (period) {
            case "Today":
                cutoff = now.toLocalDate().atStartOfDay();
                break;
            case "Last 7 Days":
                cutoff = now.minusDays(7);
                break;
            case "Last 30 Days":
                cutoff = now.minusDays(30);
                break;
            case "Last 90 Days":
                cutoff = now.minusDays(90);
                break;
            case "This Month":
                cutoff = now.withDayOfMonth(1).toLocalDate().atStartOfDay();
                break;
            case "Last Month":
                java.time.LocalDateTime firstOfThisMonth = now.withDayOfMonth(1).toLocalDate().atStartOfDay();
                cutoff = firstOfThisMonth.minusMonths(1);
                java.time.LocalDateTime endOfLastMonth = firstOfThisMonth.minusSeconds(1);

                List<Trade> lastMonthTrades = new ArrayList<Trade>();
                for (Trade t : trades) {
                    if (t.getCloseTime() != null &&
                            !t.getCloseTime().isBefore(cutoff) &&
                            !t.getCloseTime().isAfter(endOfLastMonth)) {
                        lastMonthTrades.add(t);
                    }
                }
                return lastMonthTrades;
            case "This Year":
                cutoff = now.withDayOfYear(1).toLocalDate().atStartOfDay();
                break;
            case "Custom Range...":
                if (customStartDate != null && customEndDate != null) {
                    List<Trade> customTrades = new ArrayList<Trade>();
                    java.time.LocalDateTime startDateTime = customStartDate.atStartOfDay();
                    java.time.LocalDateTime endDateTime = customEndDate.atTime(23, 59, 59);

                    for (Trade t : trades) {
                        if (t.getCloseTime() != null &&
                                !t.getCloseTime().isBefore(startDateTime) &&
                                !t.getCloseTime().isAfter(endDateTime)) {
                            customTrades.add(t);
                        }
                    }
                    return customTrades;
                }
                return trades;
            default:
                return trades;
        }

        if (cutoff == null) {
            return trades;
        }

        List<Trade> filtered = new ArrayList<Trade>();
        final java.time.LocalDateTime finalCutoff = cutoff;
        for (Trade t : trades) {
            if (t.getCloseTime() != null && !t.getCloseTime().isBefore(finalCutoff)) {
                filtered.add(t);
            }
        }
        return filtered;
    }

    /**
     * Show custom date range selection dialog
     */
    private void showCustomDateRangeDialog() {
        DateRangeDialog dialog = new DateRangeDialog(this);
        dialog.setVisible(true);

        if (dialog.isConfirmed()) {
            customStartDate = dialog.getStartDate();
            customEndDate = dialog.getEndDate();
            applyFilters();
        } else {
            // User canceled, reset to All Time
            timeFilter.setSelectedIndex(0);
        }
    }

    /**
     * Show strategy manager dialog
     */
    private void showStrategyManager() {
        // Load saved strategies from repository
        List<String> savedStrategies = strategyRepository.loadAll();

        // Also include strategies from existing trades
        List<String> tradeStrategies = equityCalculator.getUniqueStrategies(allTrades);

        // Combine both lists (avoiding duplicates)
        List<String> allStrategies = new ArrayList<String>(savedStrategies);
        for (String strategy : tradeStrategies) {
            if (!allStrategies.contains(strategy)) {
                allStrategies.add(strategy);
            }
        }

        StrategyManagerDialog dialog = new StrategyManagerDialog(this, allStrategies, strategyRepository);
        dialog.setVisible(true);

        // Refresh filters after dialog closes
        updateFilters();
    }

    /**
     * Set up custom cell editor for Strategy column
     */
    private void setupStrategyColumnEditor() {
        // Load strategies for dropdown
        List<String> savedStrategies = strategyRepository.loadAll();
        List<String> tradeStrategies = equityCalculator.getUniqueStrategies(allTrades);

        // Combine both lists (avoiding duplicates)
        List<String> allStrategies = new ArrayList<String>();
        allStrategies.add(""); // Empty option

        for (String strategy : savedStrategies) {
            if (!allStrategies.contains(strategy)) {
                allStrategies.add(strategy);
            }
        }
        for (String strategy : tradeStrategies) {
            if (!allStrategies.contains(strategy) && strategy != null && !strategy.isEmpty()) {
                allStrategies.add(strategy);
            }
        }

        // Create combo box editor
        JComboBox<String> strategyCombo = new JComboBox<String>();
        for (String strategy : allStrategies) {
            strategyCombo.addItem(strategy);
        }

        DefaultCellEditor editor = new DefaultCellEditor(strategyCombo);
        tradeTable.getColumnModel().getColumn(9).setCellEditor(editor);
    }

    /**
     * Set up custom cell editor for Account column
     */
    private void setupAccountColumnEditor() {
        // Load accounts for dropdown
        List<Account> accounts = accountRepository.loadAll();
        List<String> accountNames = new ArrayList<>();
        accountNames.add("");

        for (Account acc : accounts) {
            accountNames.add(acc.getName());
        }

        // Include existing unique accounts from trades that might not be in repo yet
        List<String> tradeAccounts = equityCalculator.getUniqueAccounts(allTrades);
        for (String acc : tradeAccounts) {
            if (!accountNames.contains(acc)) {
                accountNames.add(acc);
            }
        }

        JComboBox<String> accountCombo = new JComboBox<>();
        for (String name : accountNames) {
            accountCombo.addItem(name);
        }

        DefaultCellEditor editor = new DefaultCellEditor(accountCombo);
        tradeTable.getColumnModel().getColumn(10).setCellEditor(editor);
    }

    /**
     * Show account manager dialog
     */
    private void showAccountManager() {
        AccountManagerDialog dialog = new AccountManagerDialog(this, accountRepository, repository);
        dialog.setVisible(true);

        // Refresh dropdowns and curve
        setupAccountColumnEditor();
        updateFilters();
        updateEquityCurve();
    }

    /**
     * Recursively adds mouse wheel listeners to all components to enable scrolling
     * anywhere in the Analytics tab
     */
    private void addMouseWheelListenerRecursively(Component component, JScrollPane scrollPane) {
        component.addMouseWheelListener(e -> {
            JScrollBar verticalScrollBar = scrollPane.getVerticalScrollBar();
            int scrollAmount = e.getUnitsToScroll() * verticalScrollBar.getUnitIncrement();
            verticalScrollBar.setValue(verticalScrollBar.getValue() + scrollAmount);
        });

        if (component instanceof Container) {
            Container container = (Container) component;
            for (Component child : container.getComponents()) {
                addMouseWheelListenerRecursively(child, scrollPane);
            }
        }
    }

    private void setApplicationIcon() {
        try {
            Image iconImage = null;
            File iconFile = new File("icon.png");

            if (iconFile.exists()) {
                iconImage = Toolkit.getDefaultToolkit().getImage(iconFile.getAbsolutePath());
            } else {
                // Fallback to generating from emoji
                int size = 64;
                java.awt.image.BufferedImage image = new java.awt.image.BufferedImage(size, size,
                        java.awt.image.BufferedImage.TYPE_INT_ARGB);
                Graphics2D g2 = image.createGraphics();

                // Enable anti-aliasing
                g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Draw the emoji
                Font font = new Font("Segoe UI Emoji", Font.PLAIN, 48);
                if (!font.canDisplay("💹".codePointAt(0))) {
                    font = new Font("Dialog", Font.PLAIN, 48);
                }
                g2.setFont(font);

                // Center the emoji
                FontMetrics fm = g2.getFontMetrics();
                int x = (size - fm.stringWidth("💹")) / 2;
                int y = ((size - fm.getHeight()) / 2) + fm.getAscent();

                g2.drawString("💹", x, y);
                g2.dispose();
                iconImage = image;
            }

            if (iconImage != null) {
                // Set the icon
                setIconImage(iconImage);

                // Also set for taskbar via reflection to be safe on older Java versions
                try {
                    Class<?> taskbarClass = Class.forName("java.awt.Taskbar");
                    Object taskbar = taskbarClass.getMethod("getTaskbar").invoke(null);
                    taskbarClass.getMethod("setIconImage", Image.class).invoke(taskbar, iconImage);
                } catch (Throwable t) {
                    // Taskbar class not present or not supported
                }
            }
        } catch (Exception e) {
            System.err.println("Could not set icon: " + e.getMessage());
        }
    }
}
