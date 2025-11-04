#!/bin/bash

# Development Environment Startup Script
# This script starts all services needed for RFID system development

set -e  # Exit on any error

echo "🚀 Starting UHF RFID Development Environment..."

# Configuration
JAVA_MAIN="com.myuhf.CommandLineMain"
SERVER_DIR="rfid-server"
CLIENT_DIR="rfid-client"
LOG_DIR="logs"
PID_DIR="pids"

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Create necessary directories
mkdir -p "$LOG_DIR" "$PID_DIR"

# Function to check if port is in use
check_port() {
    local port=$1
    if lsof -Pi :$port -sTCP:LISTEN -t >/dev/null 2>&1; then
        return 0  # Port is in use
    else
        return 1  # Port is free
    fi
}

# Function to start Java application
start_java_app() {
    echo -e "${BLUE}📦 Building Java application...${NC}"
    
    if [ ! -f "gradlew" ]; then
        echo -e "${RED}❌ Gradle wrapper not found. Please ensure you're in the correct directory.${NC}"
        exit 1
    fi
    
    # Build the application
    ./gradlew build -q
    
    if [ $? -ne 0 ]; then
        echo -e "${RED}❌ Failed to build Java application${NC}"
        exit 1
    fi
    
    echo -e "${GREEN}✅ Java application built successfully${NC}"
}

# Function to start Node.js server
start_node_server() {
    echo -e "${BLUE}🌐 Starting Node.js server...${NC}"
    
    if [ ! -d "$SERVER_DIR" ]; then
        echo -e "${YELLOW}⚠️  Server directory not found, skipping Node.js server${NC}"
        return 0
    fi
    
    cd "$SERVER_DIR"
    
    # Check if package.json exists
    if [ ! -f "package.json" ]; then
        echo -e "${YELLOW}⚠️  package.json not found in server directory${NC}"
        cd ..
        return 0
    fi
    
    # Install dependencies if node_modules doesn't exist
    if [ ! -d "node_modules" ]; then
        echo -e "${BLUE}📦 Installing server dependencies...${NC}"
        npm install
    fi
    
    # Check if port 5000 is available
    if check_port 5000; then
        echo -e "${YELLOW}⚠️  Port 5000 is already in use. Killing existing process...${NC}"
        pkill -f "node.*server.js" || true
        sleep 2
    fi
    
    # Start the server in background
    echo -e "${BLUE}🚀 Starting Express server on port 5000...${NC}"
    nohup node server.js > "../$LOG_DIR/server.log" 2>&1 &
    echo $! > "../$PID_DIR/server.pid"
    
    cd ..
    
    # Wait a moment and check if server started
    sleep 3
    if check_port 5000; then
        echo -e "${GREEN}✅ Node.js server started successfully${NC}"
    else
        echo -e "${RED}❌ Failed to start Node.js server${NC}"
        cat "$LOG_DIR/server.log" | tail -10
    fi
}

# Function to start React client
start_react_client() {
    echo -e "${BLUE}⚛️  Starting React development server...${NC}"
    
    if [ ! -d "$CLIENT_DIR" ]; then
        echo -e "${YELLOW}⚠️  Client directory not found, skipping React client${NC}"
        return 0
    fi
    
    cd "$CLIENT_DIR"
    
    # Check if package.json exists
    if [ ! -f "package.json" ]; then
        echo -e "${YELLOW}⚠️  package.json not found in client directory${NC}"
        cd ..
        return 0
    fi
    
    # Install dependencies if node_modules doesn't exist
    if [ ! -d "node_modules" ]; then
        echo -e "${BLUE}📦 Installing client dependencies...${NC}"
        npm install
    fi
    
    # Check if port 3000 is available
    if check_port 3000; then
        echo -e "${YELLOW}⚠️  Port 3000 is already in use. This might be another React app.${NC}"
    fi
    
    # Start React development server in background
    echo -e "${BLUE}🚀 Starting React development server on port 3000...${NC}"
    BROWSER=none nohup npm start > "../$LOG_DIR/client.log" 2>&1 &
    echo $! > "../$PID_DIR/client.pid"
    
    cd ..
    
    # Wait for React to start (it takes longer)
    echo -e "${BLUE}⏳ Waiting for React server to start...${NC}"
    sleep 10
    
    if check_port 3000; then
        echo -e "${GREEN}✅ React development server started successfully${NC}"
    else
        echo -e "${RED}❌ Failed to start React development server${NC}"
        cat "$LOG_DIR/client.log" | tail -10
    fi
}

# Function to show status
show_status() {
    echo -e "\n${BLUE}📊 Service Status:${NC}"
    echo "===================="
    
    # Check Java application
    if [ -f "build/libs/my-uhf-app.jar" ]; then
        echo -e "${GREEN}✅ Java application: Built${NC}"
    else
        echo -e "${RED}❌ Java application: Not built${NC}"
    fi
    
    # Check Node.js server
    if check_port 5000; then
        echo -e "${GREEN}✅ Node.js server: Running on port 5000${NC}"
    else
        echo -e "${RED}❌ Node.js server: Not running${NC}"
    fi
    
    # Check React client
    if check_port 3000; then
        echo -e "${GREEN}✅ React client: Running on port 3000${NC}"
    else
        echo -e "${RED}❌ React client: Not running${NC}"
    fi
    
    # Show PIDs if available
    echo -e "\n${BLUE}🔍 Process Information:${NC}"
    if [ -f "$PID_DIR/server.pid" ]; then
        local server_pid=$(cat "$PID_DIR/server.pid")
        if ps -p $server_pid > /dev/null 2>&1; then
            echo -e "Node.js server PID: $server_pid"
        fi
    fi
    
    if [ -f "$PID_DIR/client.pid" ]; then
        local client_pid=$(cat "$PID_DIR/client.pid")
        if ps -p $client_pid > /dev/null 2>&1; then
            echo -e "React client PID: $client_pid"
        fi
    fi
}

# Function to show URLs
show_urls() {
    echo -e "\n${BLUE}🌐 Service URLs:${NC}"
    echo "===================="
    echo -e "React Client:      ${GREEN}http://localhost:3000${NC}"
    echo -e "Node.js API:       ${GREEN}http://localhost:5000${NC}"
    echo -e "API Documentation: ${GREEN}http://localhost:5000/api-docs${NC}"
    echo -e "Health Check:      ${GREEN}http://localhost:5000/api/health${NC}"
}

# Function to show usage information
show_usage() {
    echo -e "\n${BLUE}📖 Development Commands:${NC}"
    echo "========================"
    echo -e "Test Java CLI:     ${YELLOW}java -cp \"build/libs/my-uhf-app.jar:libs/*\" $JAVA_MAIN --help${NC}"
    echo -e "Test API:          ${YELLOW}curl http://localhost:5000/api/health${NC}"
    echo -e "View server logs:  ${YELLOW}tail -f $LOG_DIR/server.log${NC}"
    echo -e "View client logs:  ${YELLOW}tail -f $LOG_DIR/client.log${NC}"
    echo -e "Stop services:     ${YELLOW}./stop-dev.sh${NC}"
    echo -e "Test system:       ${YELLOW}./test-system.sh${NC}"
}

# Main execution
main() {
    echo -e "${BLUE}🎯 UHF RFID Development Environment${NC}"
    echo "===================================="
    
    # Start Java application
    start_java_app
    
    # Start Node.js server
    start_node_server
    
    # Start React client
    start_react_client
    
    # Show final status
    show_status
    show_urls
    show_usage
    
    echo -e "\n${GREEN}🎉 Development environment is ready!${NC}"
    echo -e "${BLUE}💡 Tip: Use 'Ctrl+C' to stop this script, but services will continue running.${NC}"
    echo -e "${BLUE}💡 Use './stop-dev.sh' to stop all services.${NC}"
}

# Handle Ctrl+C gracefully
trap 'echo -e "\n${YELLOW}👋 Development script stopped. Services are still running.${NC}"; exit 0' INT

# Run main function
main

# Wait for user input to keep script alive
echo -e "\n${BLUE}Press Enter to exit (services will continue running)...${NC}"
read -r