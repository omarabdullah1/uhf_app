#!/bin/bash

echo "🛑 Stopping RFID Development Environment..."

# Function to stop process by PID file
stop_process() {
    local pidfile=$1
    local service_name=$2
    
    if [ -f "$pidfile" ]; then
        PID=$(cat "$pidfile")
        if kill -0 $PID 2>/dev/null; then
            kill $PID
            echo "✅ $service_name stopped (PID: $PID)"
        else
            echo "⚠️  $service_name was not running"
        fi
        rm "$pidfile"
    else
        echo "⚠️  $service_name PID file not found"
    fi
}

# Stop server and client
stop_process ".server_pid" "Node.js server"
stop_process ".client_pid" "React client"

# Also try to kill by port (fallback)
echo "🔍 Checking for remaining processes..."

# Kill processes on port 5000 (Node.js server)
NODE_PID=$(lsof -ti:5000)
if [ ! -z "$NODE_PID" ]; then
    kill $NODE_PID
    echo "✅ Process on port 5000 terminated"
fi

# Kill processes on port 3000 (React client)
REACT_PID=$(lsof -ti:3000)
if [ ! -z "$REACT_PID" ]; then
    kill $REACT_PID
    echo "✅ Process on port 3000 terminated"
fi

echo "👋 All RFID development services stopped."