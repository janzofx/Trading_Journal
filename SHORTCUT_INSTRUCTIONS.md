# 🚀 Trading Journal - Installation & Usage Guide

This guide provides instructions on how to install, configure, and launch the Trading Journal application.

## 📦 Installation

### Prerequisites
- **Windows OS** (Windows 10/11 Recommended)
- **Java Runtime Environment** (JDK 21 is embedded/detected automatically)

### Quick Start
The application is distributed as a portable executable. No installation wizard is required.

1. **Navigate** to the application directory:
   ```
   C:\Algo Trading\Journal\
   ```
2. **Launch** the application:
   - Double-click **`TradingJournal.exe`** 💹

---

## 🖥️ Desktop Integration

To make the application easily accessible, you can create shortcuts or pin it to your taskbar.

### Create Desktop Shortcut
1. Right-click on `TradingJournal.exe`.
2. Select **Send to** > **Desktop (create shortcut)**.
3. A shortcut with the application icon will appear on your desktop.

### Pin to Taskbar / Start Menu
1. Right-click on `TradingJournal.exe`.
2. Select **Pin to taskbar** or **Pin to Start**.

---

## 🛠️ Building from Source

If you are a developer or need to recompile the application after editing the source code (located in `src/main/java`), follow these steps:

1. **Navigate** to the project root directory.
2. **Execute** the build script:
   - Double-click `build_app.bat`
   - _Or run via terminal:_ `.\build_app.bat`
3. **Wait** for the process to complete.
   - The script will compile all Java sources and package them into a JAR.
   - It will then update the executable wrapper.
4. **Restart** the application to see your changes.

---

## ❓ Troubleshooting

| Issue | Solution |
|-------|----------|
| **App fails to start** | Ensure that the `target/` directory exists and contains `trading-journal-1.0.0.jar`. Run `build_app.bat` to regenerate it. |
| **Icon incorrect** | The icon is embedded in the EXE. Try clearing your Windows icon cache or restarting Explorer if it glitches. |
| **Java not found** | The application attempts to use a bundled JDK. If moved to another machine, ensure Java 21 is installed and available in the system PATH. |

---
*Generated for Trading Journal Project*
