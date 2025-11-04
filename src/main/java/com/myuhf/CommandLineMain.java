package com.myuhf;

import com.rscja.deviceapi.RFIDWithUHFSerialPortUR4;
import com.rscja.deviceapi.RFIDWithUHFNetworkUR4;
import com.rscja.deviceapi.entity.UHFTAGInfo;
import com.rscja.deviceapi.interfaces.IUHFInventoryCallback;
import com.rscja.deviceapi.interfaces.IUHF;
import com.uhf.UHFMainForm;
import com.uhf.form.ReadWriteForm;
import com.uhf.form.InventoryForm;

import java.util.HashMap;
import java.util.Map;

/**
 * Command-line interface for RFID operations using existing GUI methods
 * This allows Node.js server to call existing Java functionality without creating new implementations
 */
public class CommandLineMain {
    
    private static RFIDWithUHFSerialPortUR4 serialReader;
    private static RFIDWithUHFNetworkUR4 networkReader;
    private static boolean isConnected = false;
    private static boolean isInventoryRunning = false;
    
    // Bank mapping for read/write operations
    private static Map<String, Integer> bankMap = new HashMap<String, Integer>() {
        {
            put("RESERVED", IUHF.Bank_RESERVED);
            put("EPC", IUHF.Bank_EPC);
            put("TID", IUHF.Bank_TID);
            put("USER", IUHF.Bank_USER);
        }
    };

    public static void main(String[] args) {
        if (args.length == 0) {
            printUsage();
            return;
        }

        try {
            String operation = args[0].toLowerCase();
            
            switch (operation) {
                case "connect":
                    handleConnect(args);
                    break;
                case "inventory":
                    handleInventory(args);
                    break;
                case "read":
                    handleRead(args);
                    break;
                case "write":
                    handleWrite(args);
                    break;
                case "status":
                    handleStatus();
                    break;
                default:
                    System.err.println("Unknown operation: " + operation);
                    printUsage();
                    System.exit(1);
            }
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
    
    private static void handleConnect(String[] args) {
        if (args.length < 2) {
            System.err.println("Connect requires mode parameter: serial or network");
            return;
        }
        
        String mode = args[1].toLowerCase();
        
        try {
            if ("serial".equals(mode)) {
                String port = args.length > 2 ? args[2] : "/dev/ttyUSB0";
                connectSerial(port);
            } else if ("network".equals(mode)) {
                String ip = args.length > 2 ? args[2] : "192.168.99.200";
                String portStr = args.length > 3 ? args[3] : "8888";
                int port = Integer.parseInt(portStr);
                connectNetwork(ip, port);
            } else {
                System.err.println("Invalid mode. Use 'serial' or 'network'");
            }
        } catch (Exception e) {
            System.err.println("Connection failed: " + e.getMessage());
        }
    }
    
    private static void connectSerial(String port) throws Exception {
        serialReader = new RFIDWithUHFSerialPortUR4();
        boolean success = serialReader.init(port);
        
        if (success) {
            UHFMainForm.ur4 = serialReader;
            isConnected = true;
            System.out.println("Connected to serial port: " + port);
        } else {
            System.err.println("Failed to connect to serial port: " + port);
            throw new Exception("Serial connection failed");
        }
    }
    
    private static void connectNetwork(String ip, int port) throws Exception {
        networkReader = new RFIDWithUHFNetworkUR4();
        boolean success = networkReader.init(ip, port);
        
        if (success) {
            UHFMainForm.ur4 = networkReader;
            isConnected = true;
            System.out.println("Connected to network: " + ip + ":" + port);
        } else {
            System.err.println("Failed to connect to network: " + ip + ":" + port);
            throw new Exception("Network connection failed");
        }
    }
    
    private static void handleInventory(String[] args) {
        if (args.length < 2) {
            System.err.println("Inventory requires action: start or stop");
            return;
        }
        
        String action = args[1].toLowerCase();
        
        if (!isConnected || UHFMainForm.ur4 == null) {
            System.err.println("Not connected to RFID reader");
            return;
        }
        
        if ("start".equals(action)) {
            startInventory();
        } else if ("stop".equals(action)) {
            stopInventory();
        } else {
            System.err.println("Invalid inventory action. Use 'start' or 'stop'");
        }
    }
    
    private static void startInventory() {
        try {
            boolean success = UHFMainForm.ur4.startInventoryTag();
            if (success) {
                isInventoryRunning = true;
                System.out.println("Inventory started successfully");
            } else {
                System.err.println("Failed to start inventory");
            }
        } catch (Exception e) {
            System.err.println("Error starting inventory: " + e.getMessage());
        }
    }
    
    private static void stopInventory() {
        try {
            if (isInventoryRunning) {
                UHFMainForm.ur4.stopInventory();
                isInventoryRunning = false;
                System.out.println("Inventory stopped successfully");
            } else {
                System.out.println("Inventory is not running");
            }
        } catch (Exception e) {
            System.err.println("Error stopping inventory: " + e.getMessage());
        }
    }
    
    private static void handleRead(String[] args) {
        if (args.length < 5) {
            System.err.println("Read requires: accessPwd bank start length");
            return;
        }
        
        if (!isConnected || UHFMainForm.ur4 == null) {
            System.err.println("Not connected to RFID reader");
            return;
        }
        
        try {
            String accessPwd = args[1];
            String bank = args[2].toUpperCase();
            int start = Integer.parseInt(args[3]);
            int length = Integer.parseInt(args[4]);
            
            Integer bankCode = bankMap.get(bank);
            if (bankCode == null) {
                System.err.println("Invalid bank: " + bank + ". Use RESERVED, EPC, TID, or USER");
                return;
            }
            
            String result = UHFMainForm.ur4.readData(accessPwd, bankCode, start, length);
            
            if (result != null && !result.isEmpty()) {
                System.out.println("Read successful: " + result);
            } else {
                System.err.println("Read failed or returned empty data");
            }
        } catch (Exception e) {
            System.err.println("Error reading tag: " + e.getMessage());
        }
    }
    
    private static void handleWrite(String[] args) {
        if (args.length < 6) {
            System.err.println("Write requires: accessPwd bank start length data");
            return;
        }
        
        if (!isConnected || UHFMainForm.ur4 == null) {
            System.err.println("Not connected to RFID reader");
            return;
        }
        
        try {
            String accessPwd = args[1];
            String bank = args[2].toUpperCase();
            int start = Integer.parseInt(args[3]);
            int length = Integer.parseInt(args[4]);
            String data = args[5];
            
            Integer bankCode = bankMap.get(bank);
            if (bankCode == null) {
                System.err.println("Invalid bank: " + bank + ". Use RESERVED, EPC, TID, or USER");
                return;
            }
            
            boolean success = UHFMainForm.ur4.writeData(accessPwd, bankCode, start, length, data);
            
            if (success) {
                System.out.println("Write successful");
            } else {
                System.err.println("Write failed");
            }
        } catch (Exception e) {
            System.err.println("Error writing tag: " + e.getMessage());
        }
    }
    
    private static void handleStatus() {
        System.out.println("Connection status: " + (isConnected ? "Connected" : "Disconnected"));
        System.out.println("Inventory status: " + (isInventoryRunning ? "Running" : "Stopped"));
        
        if (isConnected && UHFMainForm.ur4 != null) {
            try {
                // Get real hardware parameters
                System.out.println("RFID Reader: Available");
                
                // Read temperature from hardware
                try {
                    float temperature = UHFMainForm.ur4.getReaderTemperature();
                    System.out.println("Temperature: " + temperature);
                } catch (Exception e) {
                    System.out.println("Temperature: Unable to read (" + e.getMessage() + ")");
                }
                
                // Read power level from hardware
                try {
                    int power = UHFMainForm.ur4.getPower();
                    System.out.println("Power: " + power);
                } catch (Exception e) {
                    System.out.println("Power: Unable to read (" + e.getMessage() + ")");
                }
                
                // Read frequency from hardware
                try {
                    float frequency = UHFMainForm.ur4.getFrequency();
                    System.out.println("Frequency: " + frequency);
                } catch (Exception e) {
                    System.out.println("Frequency: Unable to read (" + e.getMessage() + ")");
                }
                
            } catch (Exception e) {
                System.out.println("Status check error: " + e.getMessage());
            }
        } else {
            System.out.println("RFID Reader: Not connected");
        }
    }
    
    private static void printUsage() {
        System.out.println("RFID Command Line Interface");
        System.out.println("Usage: java com.myuhf.CommandLineMain <operation> [parameters]");
        System.out.println();
        System.out.println("Operations:");
        System.out.println("  connect serial [port]           - Connect via serial port (default: /dev/ttyUSB0)");
        System.out.println("  connect network [ip] [port]     - Connect via network (default: 192.168.99.200:8888)");
        System.out.println("  inventory start                 - Start inventory scanning");
        System.out.println("  inventory stop                  - Stop inventory scanning");
        System.out.println("  read <accessPwd> <bank> <start> <length> - Read tag data");
        System.out.println("  write <accessPwd> <bank> <start> <length> <data> - Write tag data");
        System.out.println("  status                          - Show connection and inventory status");
        System.out.println();
        System.out.println("Banks: RESERVED, EPC, TID, USER");
        System.out.println("Example:");
        System.out.println("  java com.myuhf.CommandLineMain connect serial /dev/ttyUSB0");
        System.out.println("  java com.myuhf.CommandLineMain inventory start");
        System.out.println("  java com.myuhf.CommandLineMain read 00000000 EPC 2 6");
    }
}