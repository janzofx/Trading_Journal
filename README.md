# 📈 Trading Journal

A professional, cross-platform Java desktop application for analyzing your MetaTrader 5 trading performance. Import your trade history directly and visualize your edge with comprehensive analytics and beautiful UI.

<img src="icon.png" alt="Trading Journal Icon" width="150"/>

## ✨ Features

- **📊 Advanced Analytics**: Real-time calculation of Win Rate, Profit Factor, Expected Value, Sharpe Ratio, and more
- **💹 Equity Curve**: Visualize your account growth with dynamic equity curve and drawdown analysis
- **📅 Period Analysis**: Breakdown performance by Weekday, Hour, and Month with detailed charts
- **🏷️ Strategy Tagging**: Tag trades by strategy/setup (e.g., "ORB", "Z-Score") to see what works best
- **✏️ Full Edit Capability**: Edit every field of your trades including prices, dates, and P/L
- **🔄 MT5 Integration**: Seamless import from MetaTrader 5 using our custom `TradeExporter` script
- **🎯 Smart Import**: Assign account and strategy to all trades during import
- **➕ Manual Trade Entry**: Add trades manually with a user-friendly dialog for data not in MT5
- **🗑️ Trade Management**: Delete individual trades or clear all at once
- **♻️ Rename & Update**: Rename accounts or strategies and automatically update all associated trades
- **📂 Multi-Account**: Manage multiple trading accounts with starting balances
- **📝 Trade Notes**: Add comments and post-trade analysis to your records
- **🎨 Professional UI**: Custom application icon and modern interface
- **🌍 Cross-Platform**: Works on Windows, macOS, and Linux

---

## 🚀 Getting Started

### Prerequisites

**Windows:**
- Windows 10/11
- Java 11+ (included in `.exe`, but JDK recommended for building)
- MetaTrader 5 (for exporting data)

**macOS:**
- macOS 10.14+
- Java 11+ (install via `brew install openjdk@11`)
- Maven (optional, for building: `brew install maven`)

**Linux:**
- Java 11+
- Maven (optional, for building)

### Installation

#### Windows
1. Download the latest release
2. Unzip to a folder (e.g., `C:\Journal`)
3. Double-click **`TradingJournal.exe`** to launch

#### macOS / Linux
1. Clone or download this repository
2. Make scripts executable:
   ```bash
   chmod +x run.sh build.sh
   ```
3. Build the application:
   ```bash
   ./build.sh
   ```
4. Run the application:
   ```bash
   ./run.sh
   ```
   Or directly:
   ```bash
   java -jar target/trading-journal-1.0.0.jar
   ```

---

## 📥 Importing Trades

### Step 1: Install Exporter Script (Windows Only)
1. Navigate to the **`exporter/`** folder in this repository
2. Copy **`TradeExporter.ex5`**
3. Open MetaTrader 5, go to **File** → **Open Data Folder**
4. Navigate to **`MQL5\Scripts\`** and paste the file there
5. In MT5 Navigator window (Ctrl+N), right-click **Scripts** and select **Refresh**

> **Note:** MT5 is primarily a Windows application. On macOS/Linux, you may need to use Wine or export trades manually.

### Step 2: Export Data
1. Drag **`TradeExporter`** from Navigator onto any chart
2. Click **OK**
3. A file named `TradingJournal_Export.txt` will be created in `MQL5\Files`

### Step 3: Import into Journal
1. Open **Trading Journal**
2. Click **"Import TXT"**
3. Select the exported text file
4. **Choose an account** for the imported trades
5. **Optionally choose a strategy** to assign to all trades
6. Your trades will appear instantly!

---

## 🧹 Managing Data

### Screenshots

<p align="center">
  <img src="screenshots/main_window.png" alt="Main Window" width="700"/>
  <br><em>Main application window with trade list and analytics</em>
</p>

### Managing Trades

**Clear All Trades:**
- Click **File** → **Clear All Trades** in the menu bar

**Delete Individual Trades:**
1. **Double-click** any trade in the table
2. Click the **"Delete"** button
3. Confirm the deletion

<p align="center">
  <img src="screenshots/delete_trade.png" alt="Delete Trade" width="500"/>
  <br><em>Trade Details dialog - Edit everything, then Save or Delete</em>
</p>

**Edit All Trade Fields:**
1. **Double-click** any trade to open Trade Details
2. Edit any field (prices, dates, P/L, commission, strategy, etc.)
3. Click **"Save"** to apply changes

> **💡 Tip:** Net Profit is calculated automatically from Profit + Commission + Swap

### Managing Strategies

<p align="center">
  <img src="screenshots/strategy_manager.png" alt="Strategy Manager" width="500"/>
  <br><em>Strategy Manager dialog</em>
</p>

**Tools** → **Manage Strategies**:
- **Add Strategy**: Create new strategy labels
- **Rename Selected**: Rename a strategy (automatically updates all trades)
- **Remove Selected**: Delete a strategy label

### Managing Accounts

<p align="center">
  <img src="screenshots/account_manager.png" alt="Account Manager" width="500"/>
  <br><em>Account Manager dialog</em>
</p>

**Tools** → **Manage Accounts**:
- **Add Account**: Create account with starting balance
- **Rename Selected**: Rename an account (automatically updates all trades)
- **Remove Selected**: Delete an account

---

## 🛠️ Building from Source

### Using Maven (Recommended)

```bash
# Clone the repository
git clone https://github.com/janzofx/Trading_Journal.git
cd Trading_Journal

# Build
mvn clean package

# Run
java -jar target/trading-journal-1.0.0.jar
```

### Platform-Specific Scripts

**Windows:**
```batch
build_app.bat
TradingJournal.exe
```

**macOS/Linux:**
```bash
./build.sh
./run.sh
```

---

## 📊 Key Metrics Explained

| Metric | Description |
|--------|-------------|
| **Profit Factor** | Gross Profit / Gross Loss. (> 1.5 is good, > 2.0 is excellent) |
| **Win Rate** | Percentage of trades that ended in profit |
| **Expectancy** | Average amount you can expect to win (or lose) per trade |
| **Sharpe Ratio** | Risk-adjusted return metric |
| **Max Drawdown** | The largest peak-to-valley decline in account equity |
| **Long/Short Win %** | Performance breakdown by trade direction |

---

## 📂 Project Structure

```
Trading Journal/
├── src/main/java/           # Java Source Code
│   └── com/tradingjournal/
│       ├── model/           # Data models (Trade, Account, etc.)
│       ├── repository/      # Data persistence layer
│       ├── service/         # Business logic
│       └── ui/              # Swing UI components
├── exporter/                # MT5 Export Script (.ex5 / .mq5)
├── screenshots/             # Application screenshots
├── target/                  # Compiled binaries (generated)
├── TradingJournal.exe       # Windows launcher
├── run.sh                   # macOS/Linux launcher
├── build_app.bat            # Windows build script
├── build.sh                 # macOS/Linux build script
├── icon.png                 # Application icon (PNG)
├── icon.ico                 # Application icon (ICO)
├── pom.xml                  # Maven configuration
├── trades.json              # Your trade database (auto-generated)
├── accounts.json            # Account data (auto-generated)
└── strategies.json          # Strategy labels (auto-generated)
```

---

## 🌍 Platform Support

| Platform | Status | Notes |
|----------|--------|-------|
| **Windows 10/11** | ✅ Full Support | Native `.exe` launcher with custom icon |
| **macOS** | ✅ Full Support | Run via `./run.sh` or JAR directly |
| **Linux** | ✅ Full Support | Run via `./run.sh` or JAR directly |
| **MT5 Integration** | ⚠️ Windows Only | MT5 is Windows-native (use Wine on macOS/Linux) |

---

## 🎨 New in Recent Updates

### 🆕 Latest Features
- ✅ **Full trade editing** - Edit every field in Trade Details dialog
- ✅ **Strategy selection during import** - Assign strategies when importing trades
- ✅ **Account renaming** - Rename accounts and auto-update all trades
- ✅ **Strategy renaming** - Rename strategies and auto-update all trades
- ✅ **macOS/Linux support** - Cross-platform shell scripts
- ✅ **Enhanced UI** - Professional custom icon and improved dialogs
- ✅ **Better screenshots** - Complete visual guide in README

---

## 🤝 Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

1. Fork the project
2. Create your feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

---

## 📄 License

This project is open source and available for personal and educational use.

---

**Happy Trading!** 📉📈

*Built with ❤️ using Java, Swing, and JFreeChart*
