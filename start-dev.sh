#!/bin/bash

echo "🚀 Starting RFID Development Environment..."
echo "========================================"

# Function to check if a command exists
command_exists() {
    command -v "$1" >/dev/null 2>&1
}

# Check requirements
if ! command_exists node; then
    echo "❌ Node.js is not installed. Please install Node.js first."
    exit 1
fi

if ! command_exists npm; then
    echo "❌ npm is not installed. Please install npm first."
    exit 1
fi

# Start Node.js server in simulation mode
echo "📡 Starting Node.js RFID Server..."
cd rfid-server

# Check if dependencies are installed
if [ ! -d "node_modules" ]; then
    echo "📦 Installing Node.js server dependencies..."
    npm install
fi

# Start server in background
npm run simulate &
SERVER_PID=$!
echo "✅ Node.js server started (PID: $SERVER_PID)"

# Wait for server to start
echo "⏳ Waiting for server to initialize..."
sleep 5

# Test server connection
if curl -s http://localhost:5000/api/health > /dev/null; then
    echo "✅ Server is running and accessible"
else
    echo "⚠️  Server might still be starting up..."
fi

# Go back to parent directory
cd ..

# Start React client
echo "🌐 Starting React Client..."
cd rfid-client

# Check if dependencies are installed
if [ ! -d "node_modules" ]; then
    echo "📦 Installing React client dependencies..."
    npm install
fi

# Start React app in background
npm start &
CLIENT_PID=$!
echo "✅ React client started (PID: $CLIENT_PID)"

# Store PIDs for cleanup
echo $SERVER_PID > .server_pid
echo $CLIENT_PID > .client_pid

echo ""
echo "🎉 RFID Development Environment Started!"
echo "========================================"
echo "📡 Node.js Server: http://localhost:5000"
echo "🌐 React Client:   http://localhost:3000"
echo "📋 API Info:       http://localhost:5000/api/info"
echo "📊 Simulation:     ENABLED"
echo ""
echo "💡 Tips:"
echo "   - The system runs in simulation mode for development"
echo "   - Use the React GUI to test all RFID operations"
echo "   - Check the Activity Logs for real-time feedback"
echo "   - API endpoints are documented at /api/info"
echo ""
echo "🛑 To stop: Press Ctrl+C or run ./stop-dev.sh"
echo ""

# Wait for user input to stop
echo "Press Enter to stop all services..."
read

# Cleanup function
cleanup() {
    echo ""
    echo "🛑 Stopping RFID Development Environment..."
    
    if [ -f .server_pid ]; then
        SERVER_PID=$(cat .server_pid)
        if kill -0 $SERVER_PID 2>/dev/null; then
            kill $SERVER_PID
            echo "✅ Node.js server stopped"
        fi
        rm .server_pid
    fi
    
    if [ -f .client_pid ]; then
        CLIENT_PID=$(cat .client_pid)
        if kill -0 $CLIENT_PID 2>/dev/null; then
            kill $CLIENT_PID
            echo "✅ React client stopped"
        fi
        rm .client_pid
    fi
    
    echo "👋 Development environment stopped."
}

# Set trap for cleanup on script exit
trap cleanup EXIT

# Wait for processes
wait