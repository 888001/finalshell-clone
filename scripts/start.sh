#!/bin/bash

# FinalShell Clone Launcher

cd "$(dirname "$0")/.."

# Check Java installation
if ! command -v java &> /dev/null; then
    echo "Java is not installed or not in PATH."
    echo "Please install Java 8 or higher."
    exit 1
fi

# Set JVM options
JAVA_OPTS="-Xms256m -Xmx1024m -Dfile.encoding=UTF-8"

# Find the JAR file
JAR_FILE=$(ls target/*-jar-with-dependencies.jar 2>/dev/null | head -1)

if [ -z "$JAR_FILE" ]; then
    echo "JAR file not found. Please build the project first:"
    echo "  mvn clean package"
    exit 1
fi

# Start application
echo "Starting FinalShell Clone..."
java $JAVA_OPTS -jar "$JAR_FILE"
