#!/bin/bash

# Development Environment Shutdown Script
# This script stops all services running for RFID system development

set -e  # Exit on any error

echo "🛑 Stopping UHF RFID Development Environment..."

# Configuration
PID_DIR="pids"
LOG_DIR="logs"

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Function to kill process by PID file
kill_by_pid_file() {
    local pid_file=$1
    local service_name=$2
    
    if [ -f "$pid_file" ]; then
        local pid=$(cat "$pid_file")
        if ps -p $pid > /dev/null 2>&1; then
            echo -e "${BLUE}🔄 Stopping $service_name (PID: $pid)...${NC}"
            kill $pid
            
            # Wait for process to terminate
            local count=0
            while ps -p $pid > /dev/null 2>&1 && [ $count -lt 10 ]; do
                sleep 1
                count=$((count + 1))
            done
            
            # Force kill if still running
            if ps -p $pid > /dev/null 2>&1; then
                echo -e "${YELLOW}⚠️  Force killing $service_name...${NC}"
                kill -9 $pid
            fi
            
            echo -e "${GREEN}✅ $service_name stopped${NC}"
        else
            echo -e "${YELLOW}⚠️  $service_name PID file exists but process is not running${NC}"
        fi
        
        # Remove PID file
        rm -f "$pid_file"
    else
        echo -e "${YELLOW}⚠️  No PID file found for $service_name${NC}"
    fi
}

# Function to kill processes by name
kill_by_name() {
    local process_name=$1
    local service_name=$2
    
    local pids=$(pgrep -f "$process_name" || true)
    if [ -n "$pids" ]; then
        echo -e "${BLUE}🔄 Stopping $service_name processes...${NC}"
        pkill -f "$process_name" || true
        sleep 2
        
        # Check if any processes are still running
        local remaining_pids=$(pgrep -f "$process_name" || true)
        if [ -n "$remaining_pids" ]; then
            echo -e "${YELLOW}⚠️  Force killing remaining $service_name processes...${NC}"
            pkill -9 -f "$process_name" || true
        fi
        
        echo -e "${GREEN}✅ $service_name processes stopped${NC}"
    else
        echo -e "${YELLOW}⚠️  No $service_name processes found${NC}"
    fi
}

# Function to stop Node.js server
stop_node_server() {
    echo -e "${BLUE}🌐 Stopping Node.js server...${NC}"
    
    # Try PID file first
    kill_by_pid_file "$PID_DIR/server.pid" "Node.js server"
    
    # Also kill any remaining node processes running server.js
    kill_by_name "node.*server.js" "Node.js server"
    
    # Check if port 5000 is still in use
    if lsof -Pi :5000 -sTCP:LISTEN -t >/dev/null 2>&1; then
        echo -e "${YELLOW}⚠️  Port 5000 is still in use, killing processes...${NC}"
        lsof -ti:5000 | xargs kill -9 2>/dev/null || true
    fi
}

# Function to stop React client
stop_react_client() {
    echo -e "${BLUE}⚛️  Stopping React development server...${NC}"
    
    # Try PID file first
    kill_by_pid_file "$PID_DIR/client.pid" "React client"
    
    # Also kill any remaining React processes
    kill_by_name "npm.*start" "React client"
    kill_by_name "react-scripts.*start" "React scripts"
    
    # Check if port 3000 is still in use
    if lsof -Pi :3000 -sTCP:LISTEN -t >/dev/null 2>&1; then
        echo -e "${YELLOW}⚠️  Port 3000 is still in use, killing processes...${NC}"
        lsof -ti:3000 | xargs kill -9 2>/dev/null || true
    fi
}

# Function to stop any Java processes (if running in development mode)
stop_java_processes() {
    echo -e "${BLUE}☕ Checking for Java development processes...${NC}"
    
    # Look for CommandLineMain processes
    local java_pids=$(pgrep -f "CommandLineMain" || true)
    if [ -n "$java_pids" ]; then
        echo -e "${BLUE}🔄 Stopping Java CommandLineMain processes...${NC}"
        pkill -f "CommandLineMain" || true
        echo -e "${GREEN}✅ Java processes stopped${NC}"
    else
        echo -e "${YELLOW}⚠️  No Java CommandLineMain processes found${NC}"
    fi
}

# Function to clean up temporary files
cleanup_files() {
    echo -e "${BLUE}🧹 Cleaning up temporary files...${NC}"
    
    # Remove PID directory if empty
    if [ -d "$PID_DIR" ]; then
        if [ -z "$(ls -A $PID_DIR)" ]; then
            rmdir "$PID_DIR"
            echo -e "${GREEN}✅ Removed empty PID directory${NC}"
        else
            echo -e "${YELLOW}⚠️  PID directory not empty, keeping it${NC}"
        fi
    fi
    
    # Rotate logs if they're getting large (> 10MB)
    if [ -d "$LOG_DIR" ]; then
        for log_file in "$LOG_DIR"/*.log; do
            if [ -f "$log_file" ]; then
                local size=$(stat -f%z "$log_file" 2>/dev/null || stat -c%s "$log_file" 2>/dev/null || echo 0)
                if [ "$size" -gt 10485760 ]; then  # 10MB
                    mv "$log_file" "${log_file}.old"
                    echo -e "${BLUE}📝 Rotated large log file: $(basename "$log_file")${NC}"
                fi
            fi
        done
    fi
}

# Function to show final status
show_final_status() {
    echo -e "\n${BLUE}📊 Final Status Check:${NC}"
    echo "======================"
    
    # Check ports
    local ports_in_use=""
    
    if lsof -Pi :3000 -sTCP:LISTEN -t >/dev/null 2>&1; then
        ports_in_use="${ports_in_use}3000 "
        echo -e "${RED}❌ Port 3000: Still in use${NC}"
    else
        echo -e "${GREEN}✅ Port 3000: Free${NC}"
    fi
    
    if lsof -Pi :5000 -sTCP:LISTEN -t >/dev/null 2>&1; then
        ports_in_use="${ports_in_use}5000 "
        echo -e "${RED}❌ Port 5000: Still in use${NC}"
    else
        echo -e "${GREEN}✅ Port 5000: Free${NC}"
    fi
    
    # Check for remaining processes
    local remaining_procs=""
    
    if pgrep -f "node.*server.js" >/dev/null 2>&1; then
        remaining_procs="${remaining_procs}node-server "
        echo -e "${RED}❌ Node.js server processes still running${NC}"
    else
        echo -e "${GREEN}✅ No Node.js server processes${NC}"
    fi
    
    if pgrep -f "react-scripts" >/dev/null 2>&1; then
        remaining_procs="${remaining_procs}react "
        echo -e "${RED}❌ React processes still running${NC}"
    else
        echo -e "${GREEN}✅ No React processes${NC}"
    fi
    
    if pgrep -f "CommandLineMain" >/dev/null 2>&1; then
        remaining_procs="${remaining_procs}java "
        echo -e "${RED}❌ Java CommandLineMain processes still running${NC}"
    else
        echo -e "${GREEN}✅ No Java CommandLineMain processes${NC}"
    fi
    
    # Summary
    if [ -n "$ports_in_use" ] || [ -n "$remaining_procs" ]; then
        echo -e "\n${YELLOW}⚠️  Some services may still be running. Manual cleanup may be needed.${NC}"
        if [ -n "$ports_in_use" ]; then
            echo -e "${YELLOW}Ports still in use: $ports_in_use${NC}"
        fi
        if [ -n "$remaining_procs" ]; then
            echo -e "${YELLOW}Processes still running: $remaining_procs${NC}"
        fi
    else
        echo -e "\n${GREEN}✅ All services stopped successfully!${NC}"
    fi
}

# Function to show manual cleanup commands
show_manual_cleanup() {
    echo -e "\n${BLUE}🔧 Manual Cleanup Commands (if needed):${NC}"
    echo "======================================="
    echo -e "Kill all Node.js processes:   ${YELLOW}pkill -f node${NC}"
    echo -e "Kill processes on port 3000:  ${YELLOW}lsof -ti:3000 | xargs kill -9${NC}"
    echo -e "Kill processes on port 5000:  ${YELLOW}lsof -ti:5000 | xargs kill -9${NC}"
    echo -e "Kill Java processes:          ${YELLOW}pkill -f CommandLineMain${NC}"
    echo -e "View running processes:       ${YELLOW}ps aux | grep -E '(node|react|java.*CommandLineMain)'${NC}"
    echo -e "Check port usage:             ${YELLOW}lsof -i :3000,:5000${NC}"
}

# Main execution
main() {
    echo -e "${BLUE}🎯 UHF RFID Development Environment Shutdown${NC}"
    echo "============================================="
    
    # Create directories if they don't exist
    mkdir -p "$PID_DIR" "$LOG_DIR"
    
    # Stop services
    stop_react_client
    stop_node_server
    stop_java_processes
    
    # Cleanup
    cleanup_files
    
    # Show status
    show_final_status
    show_manual_cleanup
    
    echo -e "\n${GREEN}🏁 Shutdown complete!${NC}"
    echo -e "${BLUE}💡 Use './start-dev.sh' to start the development environment again.${NC}"
}

# Handle Ctrl+C gracefully
trap 'echo -e "\n${YELLOW}Shutdown interrupted!${NC}"; exit 1' INT

# Run main function
main