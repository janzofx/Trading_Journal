# 🚀 Trading Journal - Launch & Shortcuts Guide

This guide provides instructions on how to launch the Trading Journal on your specific operating system and create convenient shortcuts.

## 📦 Prerequisites

| OS | Requirement |
| :--- | :--- |
| **Windows** | Windows 10/11 • Java 11+ |
| **macOS** | macOS 10.14+ • Java 11+ |
| **Linux** | Ubuntu/Debian (or similar) • Java 11+ |

---

## 🪟 Windows Setup

### Launching
Simply double-click **`TradingJournal.exe`** in the main folder.

### Creating a Desktop Shortcut
1. Right-click on `TradingJournal.exe`.
2. Select **Send to** > **Desktop (create shortcut)**.
3. The shortcut with the correct icon will appear on your desktop.

### Pinning to Taskbar
1. Right-click on `TradingJournal.exe`.
2. Select **Pin to taskbar** or **Pin to Start**.

---

## 🍎 macOS Setup

### Launching
1. Open Terminal in the project folder.
2. Run the launch script:
   ```bash
   ./run.sh
   ```

### Creating a Dock Shortcut (Advanced)
To create a clickable app icon for your Dock:

1. Open **Automator** (Cmd + Space, type "Automator").
2. Choose **"Application"** as the document type.
3. Search for **"Run Shell Script"** action and drag it to the workflow.
4. Paste the following command (update the path to your folder!):
   ```bash
   cd "/Users/YOUR_USERNAME/path/to/Trading Journal"
   ./run.sh
   ```
5. Save the application as "Trading Journal" in your `/Applications` folder.
6. Drag the new app from `/Applications` to your Dock.

---

## 🐧 Linux Setup

### Launching
1. Open Terminal.
2. Make sure the script is executable: `chmod +x run.sh`
3. Run:
   ```bash
   ./run.sh
   ```

### Creating a Desktop Entry (.desktop)
Create a file named `TradingJournal.desktop` in `~/.local/share/applications/` with the following content:

```ini
[Desktop Entry]
Name=Trading Journal
Comment=Analyze trading performance
Exec=/path/to/trading-journal/run.sh
Icon=/path/to/trading-journal/icon.png
Terminal=false
Type=Application
Categories=Office;Finance;
```

---

## 🛠️ Building from Source

If you need to recompile the application:

*   **Windows**: Run `build_app.bat`
*   **macOS / Linux**: Run `./build.sh`

---

## ❓ Troubleshooting

| Issue | Solution |
|-------|----------|
| **App fails to start** | Ensure you have Java 11 or higher installed. Run `java -version` in your terminal to check. |
| **Icon missing** | On Windows, cache issues may occur; restart PC. On Linux, ensure the `.desktop` file points to the absolute path of `icon.png`. |
| **Permission Denied** | On macOS/Linux, ensure scripts are executable: `chmod +x run.sh build.sh` |
