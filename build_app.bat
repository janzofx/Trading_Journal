@echo off
echo Building Trading Journal...

set "JAVA_HOME=C:\Users\jakub\.antigravity\extensions\redhat.java-1.51.0-win32-x64\jre\21.0.9-win32-x86_64"
set "JAVAC=%JAVA_HOME%\bin\javac.exe"
set "JAR=%JAVA_HOME%\bin\jar.exe"

if not exist "%JAVAC%" (
    echo ERROR: JDK not found at %JAVAC%
    pause
    exit /b 1
)

if not exist "target\classes" mkdir "target\classes"

echo Compiling sources (Java 8 compatibility)...
"%JAVAC%" --release 8 -d target\classes -cp "target\lib\*" -sourcepath src\main\java src\main\java\com\tradingjournal\TradingJournalApp.java src\main\java\com\tradingjournal\ui\*.java src\main\java\com\tradingjournal\model\*.java src\main\java\com\tradingjournal\repository\*.java src\main\java\com\tradingjournal\service\*.java src\main\java\com\tradingjournal\util\*.java

if errorlevel 1 (
    echo Compilation FAILED!
    pause
    exit /b 1
)

echo Generating manifest...
setlocal enabledelayedexpansion
echo Main-Class: com.tradingjournal.TradingJournalApp> manifest.txt
echo Class-Path: .>> manifest.txt
for %%f in (target\lib\*.jar) do (
    echo  lib/%%~nxf>> manifest.txt
)

echo Creating JAR...
"%JAR%" cfm target\trading-journal-1.0.0.jar manifest.txt -C target\classes .
del manifest.txt
endlocal

echo Creating run_app.bat (as backup)...
echo @echo off > run_app.bat
echo "%JAVA_HOME%\bin\java.exe" -jar target\trading-journal-1.0.0.jar >> run_app.bat
echo pause >> run_app.bat

echo Build SUCCESS!
pause
