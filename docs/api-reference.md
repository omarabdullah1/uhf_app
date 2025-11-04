# API Reference Guide

## Base URL
```
http://localhost:5000
```

## Authentication
Currently, no authentication is required. In production environments, consider implementing API keys or OAuth.

## Content Type
All API requests should use:
```
Content-Type: application/json
```

## Response Format
All API responses follow this standard format:

```json
{
  "status": "success|error",
  "message": "Human readable message",
  "data": "Response data (varies by endpoint)",
  "timestamp": 1698528000000
}
```

## Error Handling

### HTTP Status Codes
- `200` - Success
- `400` - Bad Request (invalid parameters)
- `500` - Internal Server Error (hardware/system error)

### Error Response Format
```json
{
  "status": "error",
  "message": "Error description",
  "timestamp": 1698528000000
}
```

## Endpoints Reference

### Inventory Operations

#### Start Inventory
**POST** `/api/start`

Start RFID tag inventory scanning.

**Request:**
```bash
curl -X POST http://localhost:5000/api/start
```

**Response:**
```json
{
  "status": "success",
  "message": "Inventory started using existing method",
  "data": "Inventory started successfully",
  "timestamp": 1698528000000
}
```

**Errors:**
- Hardware not connected
- Already running
- Permission denied

#### Stop Inventory
**POST** `/api/stop`

Stop RFID tag inventory scanning.

**Request:**
```bash
curl -X POST http://localhost:5000/api/stop
```

**Response:**
```json
{
  "status": "success",
  "message": "Inventory stopped using existing method", 
  "data": "Inventory stopped successfully",
  "timestamp": 1698528000000
}
```

### Tag Operations

#### Read Tag Data
**POST** `/api/read`

Read data from RFID tag memory banks.

**Request Body:**
```json
{
  "accessPwd": "00000000",
  "bank": "EPC",
  "start": "2",
  "length": "6"
}
```

**Parameters:**
| Field | Type | Required | Default | Description |
|-------|------|----------|---------|-------------|
| accessPwd | string | No | "00000000" | 8-digit hex access password |
| bank | string | No | "EPC" | Memory bank (EPC, TID, USER, RESERVED) |
| start | string | No | "2" | Start word address |
| length | string | No | "6" | Number of words to read |

**Response:**
```json
{
  "status": "success",
  "message": "Tag read using existing ReadWriteForm method",
  "data": "300833B2DDD906C0000007D0",
  "timestamp": 1698528000000
}
```

**Example:**
```bash
curl -X POST http://localhost:5000/api/read \
  -H "Content-Type: application/json" \
  -d '{
    "accessPwd": "00000000",
    "bank": "EPC",
    "start": "2",
    "length": "6"
  }'
```

#### Write Tag Data
**POST** `/api/write`

Write data to RFID tag memory banks.

**Request Body:**
```json
{
  "accessPwd": "00000000",
  "bank": "EPC",
  "start": "2",
  "length": "6",
  "data": "123456789ABCDEF012345678"
}
```

**Parameters:**
| Field | Type | Required | Default | Description |
|-------|------|----------|---------|-------------|
| accessPwd | string | No | "00000000" | 8-digit hex access password |
| bank | string | No | "EPC" | Memory bank (EPC, TID, USER, RESERVED) |
| start | string | No | "2" | Start word address |
| length | string | No | "6" | Number of words to write |
| data | string | **Yes** | - | Hex data to write |

**Response:**
```json
{
  "status": "success",
  "message": "Tag written using existing ReadWriteForm method",
  "data": "Write successful",
  "timestamp": 1698528000000
}
```

**Example:**
```bash
curl -X POST http://localhost:5000/api/write \
  -H "Content-Type: application/json" \
  -d '{
    "accessPwd": "00000000",
    "bank": "EPC",
    "start": "2", 
    "length": "6",
    "data": "123456789ABCDEF012345678"
  }'
```

### Connection Management

#### Configure Connection
**POST** `/api/communication`

Configure RFID reader connection (serial or network).

**Serial Connection Request:**
```json
{
  "mode": "serial",
  "port": "/dev/ttyUSB0"
}
```

**Network Connection Request:**
```json
{
  "mode": "network",
  "ip": "192.168.99.200",
  "tcpPort": "8888"
}
```

**Parameters:**
| Field | Type | Required | Default | Description |
|-------|------|----------|---------|-------------|
| mode | string | **Yes** | - | Connection type (serial or network) |
| port | string | No | "/dev/ttyUSB0" | Serial port path |
| ip | string | No | "192.168.99.200" | Network IP address |
| tcpPort | string | No | "8888" | Network TCP port |

**Response:**
```json
{
  "status": "success",
  "message": "Communication mode changed using existing method",
  "data": "Connected to serial port: /dev/ttyUSB0",
  "timestamp": 1698528000000
}
```

**Examples:**
```bash
# Serial connection
curl -X POST http://localhost:5000/api/communication \
  -H "Content-Type: application/json" \
  -d '{"mode": "serial", "port": "/dev/ttyUSB0"}'

# Network connection
curl -X POST http://localhost:5000/api/communication \
  -H "Content-Type: application/json" \
  -d '{"mode": "network", "ip": "192.168.99.200", "tcpPort": "8888"}'
```

#### Check Network Status
**GET** `/api/network`

Check current connection and system status.

**Request:**
```bash
curl -X GET http://localhost:5000/api/network
```

**Response:**
```json
{
  "status": "success",
  "message": "Connection status checked",
  "data": "Connection status: Connected\nInventory status: Stopped\nRFID Reader: Available",
  "timestamp": 1698528000000
}
```

### Data Management

#### Get Tag List
**GET** `/api/tags`

Retrieve current list of discovered RFID tags.

**Request:**
```bash
curl -X GET http://localhost:5000/api/tags
```

**Response:**
```json
{
  "status": "success",
  "tags": [
    {
      "id": "E20000166016012345678901",
      "rssi": -45,
      "antenna": 1,
      "timestamp": 1698528000000,
      "count": 5
    },
    {
      "id": "E20000166016987654321098",
      "rssi": -52,
      "antenna": 2,
      "timestamp": 1698527900000,
      "count": 3
    }
  ],
  "totalTags": 2,
  "totalReads": 8,
  "timestamp": 1698528000000
}
```

#### Clear Tag List
**POST** `/api/tags/clear`

Clear the current tag list.

**Request:**
```bash
curl -X POST http://localhost:5000/api/tags/clear
```

**Response:**
```json
{
  "status": "success",
  "message": "Tag list cleared",
  "timestamp": 1698528000000
}
```

#### Add Test Tag
**POST** `/api/tags/add`

Add a test tag to the list (development/testing only).

**Request Body:**
```json
{
  "epc": "E20000166016012345678901",
  "rssi": -45,
  "antenna": 1
}
```

**Response:**
```json
{
  "status": "success",
  "message": "Test tag added",
  "timestamp": 1698528000000
}
```

### System Information

#### Get System Status
**GET** `/api/status`

Get comprehensive system status information.

**Request:**
```bash
curl -X GET http://localhost:5000/api/status
```

**Response:**
```json
{
  "status": "success",
  "system": {
    "server": {
      "running": true,
      "mode": "simulation",
      "port": 5000,
      "uptime": 3600
    },
    "rfid": {
      "connected": true,
      "reading": false,
      "connectionType": "serial",
      "port": "/dev/ttyUSB0",
      "lastActivity": 1698528000000
    },
    "inventory": {
      "running": false,
      "totalTags": 15,
      "totalReads": 247,
      "startTime": null
    }
  },
  "timestamp": 1698528000000
}
```

#### Execute Custom Command
**POST** `/api/execute`

Execute custom Java commands (advanced usage).

**Request Body:**
```json
{
  "operation": "status",
  "parameters": {}
}
```

**Response:**
```json
{
  "status": "success",
  "message": "Command executed successfully",
  "data": "Command output here",
  "timestamp": 1698528000000
}
```

## Memory Bank Reference

### Bank Types
| Bank | ID | Purpose | Size | Access |
|------|----|---------|----- |--------|
| RESERVED | 0 | Kill/Access passwords | 32 bits | Read/Write |
| EPC | 1 | Electronic Product Code | 96-496 bits | Read/Write |
| TID | 2 | Tag Identifier | 64-128 bits | Read Only |
| USER | 3 | User Memory | 0-8192 bits | Read/Write |

### EPC Bank Structure
```
Word 0: [PC + EPC Length]     # Protocol Control (16 bits)
Word 1: [EPC Data starts]     # Beginning of EPC data
Word 2: [Custom data area]    # ← Default write location (start="2")
Word 3: [Data continues...]
Word 4: [Data continues...]
Word 5: [Data continues...]
Word 6: [Data ends here]      # ← Default read end (length="6")
```

### Data Format Guidelines
- **Hex Data**: Use uppercase A-F, e.g., "123456789ABCDEF0"
- **Word Alignment**: Addresses are in 16-bit words, not bytes
- **Length**: Length parameter is in words (1 word = 2 bytes = 16 bits)
- **Access Password**: Always 8 hex characters, "00000000" for no password

## Rate Limiting
Currently no rate limiting is implemented. For production use, consider implementing:
- Request per second limits
- Concurrent operation limits
- Hardware access queuing

## WebSocket Support
Future versions may include WebSocket support for real-time tag updates:
```javascript
// Future WebSocket API
const ws = new WebSocket('ws://localhost:5000/ws');
ws.onmessage = function(event) {
  const tagData = JSON.parse(event.data);
  console.log('New tag:', tagData);
};
```

## SDK Integration
This API is built on top of existing Java GUI methods:
- Read/Write operations use `ReadWriteForm` methods
- Inventory operations use `InventoryForm` methods  
- Connection management uses `UHFMainForm` methods
- All operations are executed via `CommandLineMain` interface

## Testing Examples

### Complete Workflow Test
```bash
#!/bin/bash
# Complete API workflow test

echo "1. Check status"
curl -s http://localhost:5000/api/status | jq

echo "2. Configure serial connection"
curl -s -X POST http://localhost:5000/api/communication \
  -H "Content-Type: application/json" \
  -d '{"mode":"serial","port":"/dev/ttyUSB0"}' | jq

echo "3. Start inventory"
curl -s -X POST http://localhost:5000/api/start | jq

echo "4. Wait for tags..."
sleep 5

echo "5. Get discovered tags"
curl -s http://localhost:5000/api/tags | jq

echo "6. Read first tag"
curl -s -X POST http://localhost:5000/api/read \
  -H "Content-Type: application/json" \
  -d '{"bank":"EPC","start":"2","length":"6"}' | jq

echo "7. Stop inventory"
curl -s -X POST http://localhost:5000/api/stop | jq

echo "8. Clear tag list"
curl -s -X POST http://localhost:5000/api/tags/clear | jq
```

### Error Testing
```bash
# Test invalid parameters
curl -X POST http://localhost:5000/api/write \
  -H "Content-Type: application/json" \
  -d '{"bank":"INVALID","data":"test"}'

# Test missing required parameters
curl -X POST http://localhost:5000/api/write \
  -H "Content-Type: application/json" \
  -d '{"bank":"EPC"}'

# Test malformed JSON
curl -X POST http://localhost:5000/api/read \
  -H "Content-Type: application/json" \
  -d '{"bank":"EPC"'
```