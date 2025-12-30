@echo off
echo Building Trading Journal...

set "JAVA_HOME=C:\Users\jakub\.antigravity\extensions\redhat.java-1.50.0-win32-x64\jre\21.0.9-win32-x86_64"
set "JAVAC=%JAVA_HOME%\bin\javac.exe"
set "JAR=%JAVA_HOME%\bin\jar.exe"

if not exist "%JAVAC%" (
    echo ERROR: JDK not found at %JAVAC%
    pause
    exit /b 1
)

if not exist "target\classes" mkdir "target\classes"

echo Compiling sources...
"%JAVAC%" -d target\classes -cp "target\lib\*" @sources_quoted.txt

if errorlevel 1 (
    echo Compilation FAILED!
    pause
    exit /b 1
)

echo Creating JAR...
"%JAR%" cfe target\trading-journal-1.0.0.jar com.tradingjournal.TradingJournalApp -C target\classes .

echo Build SUCCESS!
pause
