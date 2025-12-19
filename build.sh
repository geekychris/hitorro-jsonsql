#!/bin/bash

set -e

echo "================================"
echo "Building Hitorro JSON SQL"
echo "================================"
echo ""

# Check if Maven is available
if ! command -v mvn &> /dev/null; then
    echo "Error: Maven (mvn) not found in PATH"
    exit 1
fi

# Clean and build
echo "Step 1: Cleaning previous builds..."
mvn clean

echo ""
echo "Step 2: Compiling source code..."
mvn compile

echo ""
echo "Step 3: Running tests..."
mvn test

echo ""
echo "Step 4: Packaging..."
mvn package

echo ""
echo "================================"
echo "Build Complete!"
echo "================================"
echo ""
echo "Artifacts:"
ls -lh target/*.jar 2>/dev/null || echo "No JAR files found"
