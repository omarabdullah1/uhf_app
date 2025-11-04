# RFID System - Using Existing Java Methods

## Overview
The Node.js server has been updated to use existing Java methods from the GUI application instead of creating new implementations. This ensures we leverage the proven functionality already built into the UHF RFID SDK.

## Architecture Changes

### Before (Using Non-existent Methods)
- Node.js called `ActionExecutor.ReadInventory()`, `ActionExecutor.stop()`, etc.
- These methods didn't exist as standalone functions
- Would require creating new implementations

### After (Using Existing Methods)
- Node.js calls `CommandLineMain` which uses existing GUI methods
- Leverages `UHFMainForm.ur4.startInventoryTag()`, `UHFMainForm.ur4.stopInventory()`
- Uses `ReadWriteForm.readData()` and `ReadWriteForm.writeData()` functionality
- Connects via existing connection methods

## Key Components

### 1. CommandLineMain.java
**Location**: `src/main/java/com/myuhf/CommandLineMain.java`

**Purpose**: Command-line interface that bridges Node.js to existing GUI methods

**Operations Supported**:
- `connect serial [port]` - Connect via serial port (uses `RFIDWithUHFSerialPortUR4`)
- `connect network [ip] [port]` - Connect via network (uses `RFIDWithUHFNetworkUR4`)
- `inventory start` - Start inventory using `UHFMainForm.ur4.startInventoryTag()`
- `inventory stop` - Stop inventory using `UHFMainForm.ur4.stopInventory()`
- `read <accessPwd> <bank> <start> <length>` - Read tag using `UHFMainForm.ur4.readData()`
- `write <accessPwd> <bank> <start> <length> <data>` - Write tag using `UHFMainForm.ur4.writeData()`
- `status` - Check connection and inventory status

### 2. Updated Node.js Server
**Location**: `rfid-server/server.js`

**Changes Made**:
- `executeJavaCommand()` function updated to use operation-based parameters
- All API endpoints updated to call `CommandLineMain` instead of non-existent methods
- Proper parameter mapping for read/write operations
- Enhanced error handling and logging

### 3. Existing Methods Used

#### From UHFMainForm.java:
- `UHFMainForm.ur4` - Static reference to RFID reader instance
- Connection management via `RFIDWithUHFSerialPortUR4` and `RFIDWithUHFNetworkUR4`

#### From InventoryForm.java:
- `startInventory()` method that calls `UHFMainForm.ur4.startInventoryTag()`
- `stopInventory()` method that calls `UHFMainForm.ur4.stopInventory()`

#### From ReadWriteForm.java:
- `btnReadActionPerformed()` logic using `UHFMainForm.ur4.readData()`
- `btnWriteActionPerformed()` logic using `UHFMainForm.ur4.writeData()`
- Bank mapping: RESERVED, EPC, TID, USER

## API Endpoints

### POST /api/start
**Description**: Start RFID inventory scanning
**Java Method**: `CommandLineMain inventory start`
**Underlying**: `UHFMainForm.ur4.startInventoryTag()`

### POST /api/stop
**Description**: Stop RFID inventory scanning
**Java Method**: `CommandLineMain inventory stop`
**Underlying**: `UHFMainForm.ur4.stopInventory()`

### POST /api/read
**Parameters**:
```json
{
  "accessPwd": "00000000",
  "bank": "EPC",
  "start": "2",
  "length": "6"
}
```
**Java Method**: `CommandLineMain read <accessPwd> <bank> <start> <length>`
**Underlying**: `UHFMainForm.ur4.readData()`

### POST /api/write
**Parameters**:
```json
{
  "accessPwd": "00000000",
  "bank": "EPC", 
  "start": "2",
  "length": "6",
  "data": "1234567890ABCDEF"
}
```
**Java Method**: `CommandLineMain write <accessPwd> <bank> <start> <length> <data>`
**Underlying**: `UHFMainForm.ur4.writeData()`

### POST /api/communication
**Parameters**:
```json
{
  "mode": "serial",
  "port": "/dev/ttyUSB0"
}
```
or
```json
{
  "mode": "network",
  "ip": "192.168.99.200",
  "tcpPort": "8888"
}
```
**Java Method**: `CommandLineMain connect <mode> [parameters]`
**Underlying**: `RFIDWithUHFSerialPortUR4.init()` or `RFIDWithUHFNetworkUR4.init()`

### GET /api/network
**Description**: Check connection status
**Java Method**: `CommandLineMain status`
**Returns**: Connection and inventory status

## Benefits of This Approach

1. **No New Code**: Uses existing, tested GUI methods
2. **Proven Functionality**: Leverages methods already working in the GUI
3. **Consistency**: Same behavior between GUI and API
4. **Maintainability**: Single source of truth for RFID operations
5. **Error Handling**: Existing error handling and validation

## Usage Examples

### Command Line Testing
```bash
# Connect to serial port
java -cp "build/libs/my-uhf-app.jar:libs/*" com.myuhf.CommandLineMain connect serial /dev/ttyUSB0

# Start inventory
java -cp "build/libs/my-uhf-app.jar:libs/*" com.myuhf.CommandLineMain inventory start

# Read EPC bank
java -cp "build/libs/my-uhf-app.jar:libs/*" com.myuhf.CommandLineMain read 00000000 EPC 2 6

# Stop inventory
java -cp "build/libs/my-uhf-app.jar:libs/*" com.myuhf.CommandLineMain inventory stop
```

### Running the System
1. **Build the Java project**: `./gradlew build`
2. **Start Node.js server**: `cd rfid-server && npm start`
3. **Start React client**: `cd rfid-client && npm start`
4. **Access GUI**: `http://localhost:3000`

## Simulation Mode
The server still supports simulation mode for development:
- Set `SIMULATION_MODE=true` in `rfid-server/server.js`
- All operations return simulated responses
- No actual RFID hardware required for testing

## Next Steps
1. Test the system with actual RFID hardware
2. Verify all operations work correctly
3. Add more detailed logging if needed
4. Consider adding tag inventory callback handling for real-time updates