# 📈 Trading Journal

A professional, Java-based desktop application for analyzing your MetaTrader 5 trading performance. Import your trade history directly and visualize your edge.

![Trading Journal](chart_icon_1767028702865.png)

## ✨ Features

- **📊 Advanced Analytics**: Real-time calculation of Win Rate, Profit Factor, Expected Value, and more.
- **💹 Equity Curve**: Visualize your account growth with a dynamic equity curve.
- **📅 Period Analysis**: Breakdown performance by Weekday, Hour, and Month.
- **🏷️ Strategy Tagging**: Tag trades by strategy/setup (e.g., "ORB", "Z-Score") to see what works best.
- **🔄 MT5 Integration**: Seamless import from MetaTrader 5 using our custom `TradeExporter` script.
- **📂 Multi-Account**: Manage multiple trading accounts in one place.
- **📝 Trade Notes**: Add comments and post-trade analysis to your records.

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
