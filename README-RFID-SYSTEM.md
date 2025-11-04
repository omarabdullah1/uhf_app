# UHF RFID Reader Control System

## Overview

This project implements a complete UHF RFID reader control system with:
- **Node.js Server** - REST API backend that integrates with Java SDK
- **React GUI** - Modern web interface for RFID operations
- **Java SDK Integration** - Direct integration with existing UHF reader SDK
- **Simulation Mode** - Development and testing without hardware

## Architecture

```
┌─────────────────┐    HTTP/REST    ┌─────────────────┐    Java SDK    ┌─────────────────┐
│   React GUI     │ ◄──────────────► │  Node.js Server │ ◄─────────────► │   Java RFID     │
│  (Port 3000)    │                 │   (Port 5000)   │                │   SDK/Hardware  │
└─────────────────┘                 └─────────────────┘                └─────────────────┘
```

## Features

### 🎛️ Control Panel
- **Start/Stop Reading** - Control RFID tag scanning
- **Single Tag Read** - Read individual tags on demand  
- **Write Operations** - Write data to RFID tags
- **Communication Mode** - Switch between Serial/Network
- **Network Testing** - Check connectivity status

### 📊 Real-time Monitoring
- **Live Tag Detection** - Real-time tag updates during scanning
- **Reader Status** - Temperature, power, frequency monitoring
- **Activity Logs** - Detailed operation logging with timestamps
- **Connection Status** - Visual server connection indicators

### 🧪 Development Features
- **Simulation Mode** - Test without hardware using mock data
- **Test Controls** - Add/clear test tags for development
- **API Testing** - Direct endpoint testing via Postman
- **Hot Reload** - Automatic refresh during development

## Quick Start

### 1. Start Development Environment
```bash
chmod +x start-dev.sh
./start-dev.sh
```

This will:
- Install all dependencies automatically
- Start Node.js server on port 5000
- Start React client on port 3000
- Enable simulation mode for testing

### 2. Access the Applications
- **React GUI**: http://localhost:3000
- **Node.js API**: http://localhost:5000
- **API Documentation**: http://localhost:5000/api/info

### 3. Stop Services
```bash
./stop-dev.sh
```

## Manual Setup

### Node.js Server
```bash
cd rfid-server
npm install
npm run simulate  # For development with simulation
npm run production  # For production with real hardware
```

### React Client
```bash
cd rfid-client
npm install
npm start
```

## API Endpoints

### Core RFID Operations
- `POST /api/start` - Start reading tags
- `POST /api/stop` - Stop reading tags
- `POST /api/read` - Read specific tag data
- `POST /api/write` - Write data to tags
- `GET /api/tags` - Get current detected tags

### System Operations
- `GET /api/status` - Get reader status
- `GET /api/network` - Check network connectivity
- `POST /api/communication` - Change communication mode
- `GET /api/health` - Server health check

### Development/Testing
- `POST /api/tags/add` - Add test tag (simulation)
- `POST /api/tags/clear` - Clear all tags (simulation)
- `POST /api/execute` - Execute custom commands

## Configuration

### Environment Variables (.env)
```
PORT=5000
SIMULATE=true
JAVA_SERVER_PORT=8080
LOG_LEVEL=debug
```

### Simulation vs Production Mode
- **Simulation Mode** (`SIMULATE=true`): Uses mock data for development
- **Production Mode** (`SIMULATE=false`): Connects to actual hardware

## Testing

### Using the GUI
1. Open http://localhost:3000
2. Use the control panel to test operations
3. Monitor logs and tag display in real-time
4. Test different communication modes

### Using Postman
```bash
# Test server health
GET http://localhost:5000/api/health

# Start reading
POST http://localhost:5000/api/start

# Get detected tags
GET http://localhost:5000/api/tags

# Write data
POST http://localhost:5000/api/write
Content-Type: application/json
{
  "data": "test_data_123",
  "bank": "USER",
  "address": 0
}
```

### Using curl
```bash
# Health check
curl http://localhost:5000/api/health

# Start reading
curl -X POST http://localhost:5000/api/start

# Check status
curl http://localhost:5000/api/status

# Get tags
curl http://localhost:5000/api/tags
```

## Development Workflow

### 1. Development Mode
- Run `./start-dev.sh` for full simulation environment
- Use React GUI for interactive testing
- Monitor logs for debugging
- Test API endpoints with Postman

### 2. Integration Testing
- Set `SIMULATE=false` in rfid-server/.env
- Ensure Java SDK server is running on port 8080
- Test with actual hardware if available

### 3. Production Deployment
- Build React app: `cd rfid-client && npm run build`
- Copy build files to Node.js server public directory
- Run Node.js server in production mode
- Configure hardware connections

## File Structure

```
my-uhf-app/
├── rfid-server/           # Node.js backend server
│   ├── server.js         # Main server implementation
│   ├── package.json      # Node.js dependencies
│   └── .env             # Environment configuration
├── rfid-client/          # React frontend application
│   ├── src/
│   │   ├── App.js       # Main React component
│   │   └── App.css      # Styling and themes
│   └── package.json     # React dependencies
├── src/main/java/        # Java SDK integration
├── start-dev.sh         # Development startup script
├── stop-dev.sh          # Development shutdown script
└── README.md           # This documentation
```

## Java SDK Integration

The Node.js server integrates with your existing Java SDK through:

### Command Execution
- Direct Java process spawning for SDK commands
- HTTP proxy to Java server (port 8080)
- Automatic fallback mechanisms

### Available SDK Methods
- `ActionExecutor.ReadInventory()` - Start continuous reading
- `ActionExecutor.executeCommand(CommandType)` - Execute specific commands
- `NetworkCheck.hasInternet()` - Network connectivity checks
- `UHFMainForm.changeCommunication()` - Communication mode switching

## Troubleshooting

### Common Issues

**Port Already in Use**
```bash
# Kill processes on ports
lsof -ti:3000 | xargs kill  # React
lsof -ti:5000 | xargs kill  # Node.js
```

**Java SDK Connection**
- Ensure Java server is running on port 8080
- Check JAVA_HOME environment variable
- Verify classpath in server.js

**Dependencies Issues**
```bash
# Reinstall dependencies
cd rfid-server && rm -rf node_modules && npm install
cd rfid-client && rm -rf node_modules && npm install
```

**Network Connectivity**
- Check firewall settings for ports 3000, 5000, 8080
- Ensure localhost resolution works
- Test with curl commands

### Debug Mode
Enable detailed logging by setting `LOG_LEVEL=debug` in `.env`

## Hardware Integration

### For Production Use
1. Set `SIMULATE=false` in rfid-server/.env
2. Ensure RFID hardware is connected
3. Start Java SDK server: `java -cp "lib/*:src/main/java" com.myuhf.server.RFIDServer`
4. Start Node.js server: `npm run production`
5. Access GUI at http://localhost:3000

### Online Session Testing
As mentioned in the requirements, you can arrange an online session to:
- Flash code to actual microcontroller
- Test with real RFID tags
- Validate network communication with hardware

## Support

For questions or issues:
1. Check the Activity Logs in the React GUI
2. Monitor server console output
3. Test API endpoints individually
4. Review Java SDK documentation
5. Use simulation mode for development testing

---

**Note**: This system is designed for both development (simulation) and production (hardware) use. Start with simulation mode to understand the workflow, then switch to production mode when ready to work with actual RFID hardware.