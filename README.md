# 📈 Trading Journal

A professional, Java-based desktop application for analyzing your MetaTrader 5 trading performance. Import your trade history directly and visualize your edge with comprehensive analytics and beautiful UI.

<img src="icon.png" alt="Trading Journal Icon" width="150"/>

## ✨ Features

- **📊 Advanced Analytics**: Real-time calculation of Win Rate, Profit Factor, Expected Value, Sharpe Ratio, and more.
- **💹 Equity Curve**: Visualize your account growth with a dynamic equity curve and drawdown analysis.
- **📅 Period Analysis**: Breakdown performance by Weekday, Hour, and Month with detailed charts.
- **🏷️ Strategy Tagging**: Tag trades by strategy/setup (e.g., "ORB", "Z-Score") to see what works best.
- **🔄 MT5 Integration**: Seamless import from MetaTrader 5 using our custom `TradeExporter` script.
- **➕ Manual Trade Entry**: Add trades manually with a user-friendly dialog for data not in MT5.
- **🗑️ Trade Management**: Delete trades easily with a double-click action on any trade row.
- **📂 Multi-Account**: Manage multiple trading accounts in one place.
- **📝 Trade Notes**: Add comments and post-trade analysis to your records.
- **🎨 Professional UI**: Custom application icon and modern interface with native Windows feel.

---

## 🚀 Getting Started

### Prerequisites
- **Windows 10/11**
- **MetaTrader 5** (for exporting data)
- **Java** (Included in the executable, but having Java 21+ installed is recommended)

### Installation
1. Download the latest release.
2. Unzip the package to a folder (e.g., `C:\Journal`).
3. Double-click **`TradingJournal.exe`** to launch.

---

## 📥 Importing Trades

### Step 1: Install Exporter Script
1. Navigate to the **`exporter/`** folder in this repository.
2. Copy **`TradeExporter.ex5`**.
3. Open MetaTrader 5, go to **File** → **Open Data Folder**.
4. Navigate to **`MQL5\Scripts\`** and paste the file there.
5. In MT5 Navigator window (Ctrl+N), right-click **Scripts** and select **Refresh**.

### Step 2: Export Data
1. Drag **`TradeExporter`** from Navigator onto any chart.
2. Click **OK**.
3. A file named `TradingJournal_Export.txt` will be created in `MQL5\Files`.

### Step 3: Import into Journal
1. Open **Trading Journal**.
2. Click **"Import TXT"**.
3. Select the exported text file.
4. Your trades will appear instantly!

---

## 🧹 Managing Example Data

After first launch, the application may contain example trades, strategies, and accounts for demonstration purposes. Here's how to clean them up:

### Screenshots

<p align="center">
  <img src="screenshots/main_window.png" alt="Main Window" width="700"/>
  <br><em>Main application window with trade list and analytics</em>
</p>

### Deleting Trades

**Option 1: Clear All Trades at Once**
1. Click **File** → **Clear All Trades** in the menu bar
2. Confirm the deletion when prompted
3. All trades will be removed instantly

**Option 2: Delete Individual Trades**
1. **Double-click** any trade in the table to open the Trade Details dialog
2. Click the **"Delete"** button at the bottom
3. Confirm the deletion when prompted

<p align="center">
  <img src="screenshots/delete_trade.png" alt="Delete Trade" width="500"/>
  <br><em>Trade Details dialog with Delete button</em>
</p>

**Option 3: Delete the Trades File**
- Simply delete the `trades.json` file from your installation folder
- The file will be recreated empty when you restart the application

### Managing Strategies

1. Go to **Tools** → **Manage Strategies** in the menu bar
2. Select any strategy from the list
3. Click **"Remove Selected"** to delete it
4. Repeat for all strategies you want to remove

<p align="center">
  <img src="screenshots/strategy_manager.png" alt="Strategy Manager" width="500"/>
  <br><em>Strategy Manager dialog</em>
</p>

### Managing Accounts

1. Go to **Tools** → **Manage Accounts** in the menu bar
2. Select any account from the list
3. Click **"Remove Selected"** to delete it
4. Repeat for all accounts you want to remove

<p align="center">
  <img src="screenshots/account_manager.png" alt="Account Manager" width="500"/>
  <br><em>Account Manager dialog</em>
</p>

> **💡 Tip:** You can also add and edit strategies and accounts using the respective manager dialogs!

---

## 🛠️ Building from Source

To modify or build the application from code:

1. **Clone the repository**:
   ```bash
   git clone https://github.com/yourusername/trading-journal.git
   ```
2. **Build the project**:
   - Run `build_app.bat` (Windows)
   - Or use Maven: `mvn clean package`
3. **Launch**:
   - The build script will update `TradingJournal.exe`.

---

## 📊 Key Metrics Explained

| Metric | Description |
|--------|-------------|
| **Profit Factor** | Gross Profit / Gross Loss. (> 1.5 is good, > 2.0 is excellent) |
| **Win Rate** | Percentage of trades that ended in profit. |
| **Expectancy** | Average amount you can expect to win (or lose) per trade. |
| **Drawdown** | The largest peak-to-valley decline in account equity. |

---

## 📂 Project Structure

```
Trading Journal/
├── src/                  # Java Source Code
├── exporter/             # MT5 Export Script (.ex5 / .mq5)
├── target/               # Compiled Binaries
├── TradingJournal.exe    # Main Application
├── icon.png              # Application Icon (PNG)
├── icon.ico              # Application Icon (ICO)
├── build_app.bat         # Build Script
└── trades.json           # Your Trade Database (Auto-generated)
```

---

## 🤝 Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

1. Fork the project
2. Create your feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

---

**Happy Trading!** 📉📈
