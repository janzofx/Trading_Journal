#!/bin/bash
# Build script for Trading Journal on macOS/Linux

# Colors for output
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

echo "Building Trading Journal..."

# Check if Maven is installed
if command -v mvn &> /dev/null; then
    echo -e "${GREEN}Using Maven to build...${NC}"
    mvn clean package
    if [ $? -eq 0 ]; then
        echo -e "${GREEN}Build SUCCESS!${NC}"
        echo "JAR created at: target/trading-journal-1.0.0.jar"
        exit 0
    else
        echo -e "${RED}Maven build failed${NC}"
        exit 1
    fi
else
    echo -e "${YELLOW}Maven not found. Using javac...${NC}"
    
    # Check if Java compiler is installed
    if ! command -v javac &> /dev/null; then
        echo -e "${RED}Error: javac (Java compiler) not found${NC}"
        echo "Please install JDK 11 or higher"
        exit 1
    fi
    
    # Create directories
    mkdir -p target/classes
    
    # Find all Java files
    echo "Compiling Java sources..."
    find src/main/java -name "*.java" > sources.txt
    
    # Compile (using Gson from Maven dependencies if available)
    if [ -d "$HOME/.m2/repository" ]; then
        GSON_JAR=$(find "$HOME/.m2/repository/com/google/code/gson/gson" -name "gson-*.jar" | head -n 1)
        JFREECHART_JAR=$(find "$HOME/.m2/repository/org/jfree/jfreechart" -name "jfreechart-*.jar" | head -n 1)
        
        if [ -n "$GSON_JAR" ] && [ -n "$JFREECHART_JAR" ]; then
            javac -d target/classes -cp "$GSON_JAR:$JFREECHART_JAR" @sources.txt
        else
            echo -e "${YELLOW}Warning: Dependencies not found in Maven repository${NC}"
            echo "Please install Maven and run: mvn dependency:resolve"
            exit 1
        fi
    else
        echo -e "${RED}Error: Maven repository not found${NC}"
        echo "Please install Maven first: brew install maven"
        exit 1
    fi
    
    if [ $? -ne 0 ]; then
        echo -e "${RED}Compilation failed${NC}"
        rm sources.txt
        exit 1
    fi
    
    # Create JAR
    echo "Creating JAR..."
    cd target/classes
    jar cvfe ../trading-journal-1.0.0.jar com.tradingjournal.TradingJournalApp .
    cd ../..
    
    # Cleanup
    rm sources.txt
    
    echo -e "${GREEN}Build SUCCESS!${NC}"
    echo "JAR created at: target/trading-journal-1.0.0.jar"
    echo "Run with: ./run.sh or java -jar target/trading-journal-1.0.0.jar"
fi

exit 0
