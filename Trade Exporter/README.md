# Trade Exporter for MetaTrader 5

Export your MT5 trading history to TXT format for use with the Trading Journal application.

## 📋 Overview

**TradeExporter** is a high-performance MetaTrader 5 script that exports your closed trades to a generic format compatible with the Trading Journal. It uses advanced caching to ensure 100% data accuracy.

## ✨ Features

- ✅ **Instant Export**: Exports thousands of trades in milliseconds.
- ✅ **Complete Data**: Symbol, Ticket, Open/Close Time, Price, Commission, Swap, Profit.
- ✅ **Reliable**: Bypasses MT5 API limitations using smart caching.
- ✅ **Plug & Play**: Works with any broker and any symbol.

## 📦 Installation

### Option 1: Quick Install (Recommended)

1. **Locate the File**: Finds `TradeExporter.ex5` in this folder.
2. **Open MT5 Data Folder**:
   - In MetaTrader 5, go to **File** → **Open Data Folder**.
3. **Copy the File**:
   - Navigate to **`MQL5\Scripts\`**.
   - Paste `TradeExporter.ex5` into this folder.
4. **Refresh**:
   - In MT5 Navigator (Ctrl+N), right-click on **Scripts** and select **Refresh**.
   - `TradeExporter` should now appear in the list.

### Option 2: Compile from Source (Advanced)
If you want to modify the code:
1. Copy `TradeExporter.mq5` to `MQL5\Scripts\`.
2. Open it in **MetaEditor** (F4).
3. Press **F7** to Compile.

## � Usage

1. **Open any chart** in MT5.
2. **Double-click** or drag `TradeExporter` onto the chart.
3. **Configure Settings**:
   - **FileName**: `TradingJournal_Export.txt` (Default is fine).
   - **ExportAll**: `true` (exports entire history) or `false` (today only).
4. **Click OK**.
5. The file will be saved to your MT5 Files folder!

## 📂 Where is my file?

The exported text file is saved in:
```
[MT5 Data Folder]\MQL5\Files\TradingJournal_Export.txt
```

**To find it quickly:**
1. In MT5, go to **File** → **Open Data Folder**.
2. Open **`MQL5`** → **`Files`**.

## � Import into Trading Journal

1. Open **Trading Journal**.
2. Click **Import TXT**.
3. Select the file you just found.
4. Done!

## 🔧 Technical Details
- **Stop Loss / Take Profit**: Exported as 0.0 due to API performance optimization.
- **Open Positions**: Ignored (only closed trades are exported).

## 📝 License
Free to use with the Trading Journal application.
