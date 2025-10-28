# RFID SDK Documentation

## Overview

This document provides detailed information about the UHF RFID SDK used in this project, including class references, method descriptions, and usage examples.

## Core SDK Classes

### RFIDWithUHFSerialPortUR4

The main class for serial port communication with UHF RFID readers.

#### Constructor
```java
RFIDWithUHFSerialPortUR4 reader = new RFIDWithUHFSerialPortUR4();
```

#### Key Methods

##### `boolean init(String portName)`
Initialize the RFID reader on the specified serial port.

**Parameters:**
- `portName` - Serial port name (e.g., "/dev/ttyUSB0", "COM1")

**Returns:**
- `true` if initialization successful, `false` otherwise

**Example:**
```java
RFIDWithUHFSerialPortUR4 reader = new RFIDWithUHFSerialPortUR4();
boolean success = reader.init("/dev/ttyUSB0");
if (success) {
    System.out.println("Reader initialized successfully");
} else {
    System.out.println("Failed to initialize reader");
}
```

##### `boolean startInventoryTag()`
Start continuous inventory scanning for RFID tags.

**Returns:**
- `true` if inventory started successfully, `false` otherwise

**Example:**
```java
boolean started = reader.startInventoryTag();
if (started) {
    System.out.println("Inventory scanning started");
}
```

##### `void stopInventory()`
Stop the current inventory scanning process.

**Example:**
```java
reader.stopInventory();
System.out.println("Inventory scanning stopped");
```

##### `String readData(String accessPwd, int bank, int start, int length)`
Read data from a specific memory bank of an RFID tag.

**Parameters:**
- `accessPwd` - 8-character hex access password (e.g., "00000000")
- `bank` - Memory bank identifier (use IUHF constants)
- `start` - Starting word address
- `length` - Number of words to read

**Returns:**
- String containing the read data in hexadecimal format, or null if failed

**Example:**
```java
String data = reader.readData("00000000", IUHF.Bank_EPC, 2, 6);
if (data != null) {
    System.out.println("Read data: " + data);
} else {
    System.out.println("Failed to read data");
}
```

##### `boolean writeData(String accessPwd, int bank, int start, int length, String data)`
Write data to a specific memory bank of an RFID tag.

**Parameters:**
- `accessPwd` - 8-character hex access password
- `bank` - Memory bank identifier
- `start` - Starting word address
- `length` - Number of words to write
- `data` - Hex data string to write

**Returns:**
- `true` if write successful, `false` otherwise

**Example:**
```java
boolean success = reader.writeData("00000000", IUHF.Bank_EPC, 2, 6, "123456789ABCDEF012345678");
if (success) {
    System.out.println("Data written successfully");
} else {
    System.out.println("Failed to write data");
}
```

##### `void free()`
Release resources and close the connection to the RFID reader.

**Example:**
```java
reader.free();
System.out.println("Reader resources released");
```

### RFIDWithUHFNetworkUR4

The main class for network communication with UHF RFID readers.

#### Constructor
```java
RFIDWithUHFNetworkUR4 reader = new RFIDWithUHFNetworkUR4();
```

#### Key Methods

##### `boolean init(String ip, int port)`
Initialize the RFID reader via network connection.

**Parameters:**
- `ip` - IP address of the RFID reader (e.g., "192.168.99.200")
- `port` - TCP port number (typically 8888)

**Returns:**
- `true` if initialization successful, `false` otherwise

**Example:**
```java
RFIDWithUHFNetworkUR4 reader = new RFIDWithUHFNetworkUR4();
boolean success = reader.init("192.168.99.200", 8888);
if (success) {
    System.out.println("Network reader initialized successfully");
}
```

### IUHF Interface Constants

Memory bank constants for read/write operations:

```java
public interface IUHF {
    int Bank_RESERVED = 0;  // Kill/Access passwords
    int Bank_EPC = 1;       // Electronic Product Code
    int Bank_TID = 2;       // Tag Identifier
    int Bank_USER = 3;      // User memory
}
```

### UHFTAGInfo Class

Container class for tag information returned during inventory operations.

#### Key Methods

##### `String getEPC()`
Get the Electronic Product Code of the tag.

##### `int getRSSI()`
Get the Received Signal Strength Indicator value.

##### `int getAntenna()`
Get the antenna number that detected the tag.

**Example:**
```java
// In inventory callback
public void callback(int cmd, int state, UHFTAGInfo uhftagInfo) {
    if (uhftagInfo != null) {
        String epc = uhftagInfo.getEPC();
        int rssi = uhftagInfo.getRSSI();
        int antenna = uhftagInfo.getAntenna();
        System.out.println("Tag: " + epc + " RSSI: " + rssi + " Antenna: " + antenna);
    }
}
```

## Inventory Callback Interface

### IUHFInventoryCallback

Interface for receiving inventory results in real-time.

```java
public interface IUHFInventoryCallback {
    void callback(int cmd, int state, UHFTAGInfo uhftagInfo);
}
```

**Parameters:**
- `cmd` - Command identifier
- `state` - Operation state
- `uhftagInfo` - Tag information object

**Example Implementation:**
```java
reader.setUHFInventoryCallback(new IUHFInventoryCallback() {
    @Override
    public void callback(int cmd, int state, UHFTAGInfo uhftagInfo) {
        if (state == 0 && uhftagInfo != null) {
            // Successful tag read
            String epc = uhftagInfo.getEPC();
            int rssi = uhftagInfo.getRSSI();
            System.out.println("Found tag: " + epc + " (RSSI: " + rssi + ")");
        }
    }
});
```

## Error Codes and Troubleshooting

### Common Return Values

- `0` - Success
- `-1` - General failure
- `-2` - Invalid parameter
- `-3` - Communication timeout
- `-4` - Hardware not connected
- `-5` - Permission denied

### Error Handling Best Practices

```java
// Always check return values
boolean result = reader.init("/dev/ttyUSB0");
if (!result) {
    System.err.println("Failed to initialize reader");
    // Handle error appropriately
    return;
}

// Use try-catch for operations that might throw exceptions
try {
    String data = reader.readData("00000000", IUHF.Bank_EPC, 2, 6);
    if (data != null && !data.isEmpty()) {
        System.out.println("Data: " + data);
    } else {
        System.out.println("No data read or empty response");
    }
} catch (Exception e) {
    System.err.println("Exception during read operation: " + e.getMessage());
}

// Always clean up resources
try {
    // RFID operations here
} finally {
    if (reader != null) {
        reader.stopInventory();
        reader.free();
    }
}
```

## Advanced Features

### Power Level Control

```java
// Set power level (0-30 dBm)
reader.setPower(30);
```

### Frequency Configuration

```java
// Set frequency (varies by region)
reader.setFrequency(915.0);  // US frequency
reader.setFrequency(868.0);  // EU frequency
```

### Antenna Selection

```java
// Select specific antenna (1-4)
reader.setAntenna(1);
```

### Session Configuration

```java
// Set session (0-3)
reader.setSession(0);
```

## Performance Optimization

### Best Practices

1. **Initialize Once**: Create reader instance once and reuse
2. **Proper Cleanup**: Always call `free()` when done
3. **Error Handling**: Check all return values
4. **Thread Safety**: SDK is not thread-safe, use synchronization
5. **Resource Management**: Don't leave inventory running indefinitely

### Sample Complete Implementation

```java
public class RFIDManager {
    private RFIDWithUHFSerialPortUR4 reader;
    private boolean isInventoryRunning = false;

    public boolean initialize(String port) {
        reader = new RFIDWithUHFSerialPortUR4();
        boolean success = reader.init(port);
        
        if (success) {
            // Set up inventory callback
            reader.setUHFInventoryCallback(new IUHFInventoryCallback() {
                @Override
                public void callback(int cmd, int state, UHFTAGInfo uhftagInfo) {
                    if (state == 0 && uhftagInfo != null) {
                        handleTagFound(uhftagInfo);
                    }
                }
            });
        }
        
        return success;
    }

    public boolean startInventory() {
        if (reader != null && !isInventoryRunning) {
            boolean started = reader.startInventoryTag();
            if (started) {
                isInventoryRunning = true;
            }
            return started;
        }
        return false;
    }

    public void stopInventory() {
        if (reader != null && isInventoryRunning) {
            reader.stopInventory();
            isInventoryRunning = false;
        }
    }

    public void cleanup() {
        stopInventory();
        if (reader != null) {
            reader.free();
            reader = null;
        }
    }

    private void handleTagFound(UHFTAGInfo tagInfo) {
        String epc = tagInfo.getEPC();
        int rssi = tagInfo.getRSSI();
        int antenna = tagInfo.getAntenna();
        
        System.out.println("Tag found: " + epc + 
                          " RSSI: " + rssi + 
                          " Antenna: " + antenna);
    }
}
```

## Integration with Project

This SDK is integrated into the project through:

1. **UHFMainForm.java** - Main GUI application
2. **CommandLineMain.java** - CLI interface
3. **ReadWriteForm.java** - Read/write operations
4. **InventoryForm.java** - Tag scanning operations

The Node.js server communicates with these Java classes via command-line execution, ensuring consistent behavior between GUI and API interfaces.