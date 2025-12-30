#!/bin/bash
# Trading Journal Launcher for macOS/Linux

# Colors for output
GREEN='\033[0;32m'
RED='\033[0;31m'
NC='\033[0m' # No Color

echo "Starting Trading Journal..."

# Check if Java is installed
if ! command -v java &> /dev/null; then
    echo -e "${RED}Error: Java is not installed or not in PATH${NC}"
    echo "Please install Java 11 or higher from:"
    echo "https://www.oracle.com/java/technologies/downloads/"
    exit 1
fi

# Check Java version
JAVA_VERSION=$(java -version 2>&1 | head -n 1 | cut -d'"' -f2 | cut -d'.' -f1)
if [ "$JAVA_VERSION" -lt 11 ]; then
    echo -e "${RED}Error: Java 11 or higher is required${NC}"
    echo "Current version: $(java -version 2>&1 | head -n 1)"
    exit 1
fi

# Check if JAR exists
if [ ! -f "target/trading-journal-1.0.0.jar" ]; then
    echo -e "${RED}Error: JAR file not found${NC}"
    echo "Building application first..."
    ./build.sh
    if [ $? -ne 0 ]; then
        echo -e "${RED}Build failed. Please check the errors above.${NC}"
        exit 1
    fi
fi

# Run the application
echo -e "${GREEN}Launching Trading Journal...${NC}"
java -jar target/trading-journal-1.0.0.jar

exit 0
