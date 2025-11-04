const express = require('express');
const cors = require('cors');
const bodyParser = require('body-parser');
const { spawn, exec } = require('child_process');
const path = require('path');
const axios = require('axios');

const app = express();
const PORT = process.env.PORT || 5000;
const JAVA_SERVER_PORT = 8080;
const JAVA_SERVER_URL = `http://localhost:${JAVA_SERVER_PORT}`;

// Middleware
app.use(cors());
app.use(bodyParser.json());
app.use(express.static('public'));

// Configuration
let SIMULATE_MODE = process.env.SIMULATE === 'true' || true;
let SIMULATION_MODE = SIMULATE_MODE; // For backwards compatibility with existing code
const JAVA_CLASSPATH = '../lib/*:../src/main/java';
const SDK_PATH = '../';

// Mock data for simulation
const mockData = {
    tags: [
        { id: 'E20000166016012345678901', rssi: -45, antenna: 1, timestamp: Date.now() },
        { id: 'E20000166016012345678902', rssi: -38, antenna: 2, timestamp: Date.now() },
        { id: 'E20000166016012345678903', rssi: -52, antenna: 1, timestamp: Date.now() },
    ],
    readerStatus: {
        connected: true,
        temperature: 25.5,
        power: 30,
        frequency: 915.0,
        reading: false
    },
    isReading: false
};

// Helper function to call Java server (REMOVED - now using direct SDK execution only)
// This function has been removed as we now use direct SDK execution for all commands

// Helper function to execute Java commands directly
// Function to execute Java command using existing methods
function executeJavaCommand(operation, params = {}) {
    console.log(`🔍 executeJavaCommand: operation=${operation}, SIMULATION_MODE=${SIMULATION_MODE}`);
    if (SIMULATION_MODE) {
        console.log(`[SIMULATION] Executing ${operation} with params:`, params);
        return Promise.resolve(simulateJavaResponse(operation, params));
    }
    
    const javaPath = 'java';
    const classpath = '../build/classes/main:../build/libs/my-uhf-app.jar:../libs/*';
    let command;
    
    // Map operations to actual Java command-line calls that use existing methods
    switch (operation) {
        case 'read':
            // Use ReadWriteForm read functionality via CommandLineMain
            const { accessPwd = '00000000', bank = 'EPC', start = '2', length = '6' } = params;
            command = `${javaPath} -cp "${classpath}" com.myuhf.CommandLineMain read ${accessPwd} ${bank} ${start} ${length}`;
            break;
            
        case 'write':
            // Use ReadWriteForm write functionality via CommandLineMain
            const { accessPwd: writePwd = '00000000', bank: writeBank = 'EPC', start: writeStart = '2', length: writeLength = '6', data } = params;
            command = `${javaPath} -cp "${classpath}" com.myuhf.CommandLineMain write ${writePwd} ${writeBank} ${writeStart} ${writeLength} ${data}`;
            break;
            
        case 'start':
            // Use InventoryForm start inventory functionality via CommandLineMain
            command = `${javaPath} -cp "${classpath}" com.myuhf.CommandLineMain inventory start`;
            break;
            
        case 'stop':
            // Use InventoryForm stop inventory functionality via CommandLineMain
            command = `${javaPath} -cp "${classpath}" com.myuhf.CommandLineMain inventory stop`;
            break;
            
        case 'connect':
            // Use UHFMainForm connection functionality via CommandLineMain
            const { mode = 'serial', port = '/dev/ttyUSB0', ip = '192.168.99.200', tcpPort = '8888' } = params;
            if (mode === 'network') {
                command = `${javaPath} -cp "${classpath}" com.myuhf.CommandLineMain connect network ${ip} ${tcpPort}`;
            } else {
                command = `${javaPath} -cp "${classpath}" com.myuhf.CommandLineMain connect serial ${port}`;
            }
            break;
            
        case 'status':
            // Check connection status via CommandLineMain
            command = `${javaPath} -cp "${classpath}" com.myuhf.CommandLineMain status`;
            break;
            
        // New configuration commands
        case 'setpower':
            const { power } = params;
            command = `${javaPath} -cp "${classpath}" com.myuhf.CommandLineMain setpower ${power}`;
            break;
            
        case 'setfrequency':
            const { frequency } = params;
            command = `${javaPath} -cp "${classpath}" com.myuhf.CommandLineMain setfrequency ${frequency}`;
            break;
            
        case 'setregion':
            const { region } = params;
            command = `${javaPath} -cp "${classpath}" com.myuhf.CommandLineMain setregion ${region}`;
            break;
            
        case 'setconnection':
            const { connectionType, port: connPort, ipAddress, tcpPort: connTcpPort, baudRate } = params;
            command = `${javaPath} -cp "${classpath}" com.myuhf.CommandLineMain setconnection ${connectionType} ${connPort || ''} ${ipAddress || ''} ${connTcpPort || ''} ${baudRate || ''}`;
            break;
            
        case 'getconfig':
            command = `${javaPath} -cp "${classpath}" com.myuhf.CommandLineMain getconfig`;
            break;
            
        case 'saveconfig':
            const { name: saveName } = params;
            command = `${javaPath} -cp "${classpath}" com.myuhf.CommandLineMain saveconfig ${saveName}`;
            break;
            
        case 'loadconfig':
            const { name: loadName } = params;
            command = `${javaPath} -cp "${classpath}" com.myuhf.CommandLineMain loadconfig ${loadName}`;
            break;
            
        default:
            throw new Error(`Unknown operation: ${operation}`);
    }
    
    console.log(`Executing: ${command}`);
    
    return new Promise((resolve, reject) => {
        exec(command, { cwd: __dirname }, (error, stdout, stderr) => {
            if (error) {
                console.error(`Error executing Java command: ${error.message}`);
                
                // Check if it's a ClassNotFoundException - common when Java classes aren't compiled
                if (error.message.includes('ClassNotFoundException') || error.message.includes('Could not find or load main class')) {
                    const betterError = new Error(`Production mode requires compiled Java classes. Please run './gradlew build' first, or switch to TEST mode for development.`);
                    betterError.details = error.message;
                    betterError.suggestion = 'Switch to TEST mode for development without Java compilation';
                    reject(betterError);
                    return;
                }
                
                reject(error);
                return;
            }
            if (stderr) {
                console.error(`Java stderr: ${stderr}`);
                
                // Check for common error conditions that should be treated as failures
                if (stderr.includes('Not connected to RFID reader') || 
                    stderr.includes('Connection failed') ||
                    stderr.includes('failed') ||
                    stderr.includes('Error')) {
                    const betterError = new Error(`RFID operation failed: ${stderr.trim()}`);
                    betterError.suggestion = 'Connect RFID hardware first, or use TEST mode for development';
                    reject(betterError);
                    return;
                }
            }
            console.log(`Java stdout: ${stdout}`);
            resolve(stdout.trim());
        });
    });
}

// Parse hardware status output from Java CommandLineMain
function parseHardwareStatus(statusOutput) {
    const lines = statusOutput.split('\n');
    const status = {
        connected: false,
        inventoryRunning: false,
        temperature: null,
        power: null,
        frequency: null
    };
    
    for (const line of lines) {
        if (line.includes('Connection status: Connected')) {
            status.connected = true;
        } else if (line.includes('Connection status: Disconnected')) {
            status.connected = false;
        }
        
        if (line.includes('Inventory status: Running')) {
            status.inventoryRunning = true;
        } else if (line.includes('Inventory status: Stopped')) {
            status.inventoryRunning = false;
        }
        
        if (line.startsWith('Temperature: ')) {
            const tempStr = line.replace('Temperature: ', '').trim();
            if (!tempStr.includes('Unable to read')) {
                status.temperature = parseFloat(tempStr);
            }
        }
        
        if (line.startsWith('Power: ')) {
            const powerStr = line.replace('Power: ', '').trim();
            if (!powerStr.includes('Unable to read')) {
                status.power = parseInt(powerStr);
            }
        }
        
        if (line.startsWith('Frequency: ')) {
            const freqStr = line.replace('Frequency: ', '').trim();
            if (!freqStr.includes('Unable to read')) {
                status.frequency = parseFloat(freqStr);
            }
        }
    }
    
    return status;
}

// Helper function to parse configuration output
function parseConfigOutput(output) {
    const lines = output.split('\n');
    const config = {
        power: null,
        frequency: null,
        region: null,
        connectionType: null,
        port: null,
        ipAddress: null,
        tcpPort: null,
        baudRate: null
    };
    
    for (const line of lines) {
        if (line.includes('Power:')) {
            config.power = parseInt(line.split('Power:')[1].trim());
        } else if (line.includes('Frequency:')) {
            config.frequency = parseFloat(line.split('Frequency:')[1].trim());
        } else if (line.includes('Region:')) {
            config.region = line.split('Region:')[1].trim();
        } else if (line.includes('Connection:')) {
            config.connectionType = line.split('Connection:')[1].trim();
        } else if (line.includes('Port:')) {
            config.port = line.split('Port:')[1].trim();
        } else if (line.includes('IP:')) {
            config.ipAddress = line.split('IP:')[1].trim();
        } else if (line.includes('TCP Port:')) {
            config.tcpPort = parseInt(line.split('TCP Port:')[1].trim());
        } else if (line.includes('Baud Rate:')) {
            config.baudRate = parseInt(line.split('Baud Rate:')[1].trim());
        }
    }
    
    return config;
}

// Simulate Java SDK responses for development using existing methods
function simulateJavaResponse(operation, params) {
    console.log(`[SIMULATION] Executing ${operation} with params:`, params);
    
    switch (operation.toLowerCase()) {
        case 'start':
            mockData.isReading = true;
            mockData.readerStatus.reading = true;
            return {
                status: 'success',
                message: 'Inventory started using existing startInventoryTag() method',
                data: mockData.tags,
                timestamp: Date.now()
            };
            
        case 'stop':
            mockData.isReading = false;
            mockData.readerStatus.reading = false;
            return {
                status: 'success',
                message: 'Inventory stopped using existing stopInventory() method',
                timestamp: Date.now()
            };
            
        case 'read':
            // Add a new simulated tag occasionally
            if (Math.random() > 0.7) {
                const newTag = {
                    id: 'E200001660160' + Math.floor(Math.random() * 1000000000).toString().padStart(12, '0'),
                    rssi: Math.floor(Math.random() * 20) - 60,
                    antenna: Math.floor(Math.random() * 4) + 1,
                    timestamp: Date.now()
                };
                mockData.tags.push(newTag);
            }
            
            return {
                status: 'success',
                message: 'Tag read using existing readData() method from ReadWriteForm',
                data: mockData.tags[Math.floor(Math.random() * mockData.tags.length)],
                allTags: mockData.tags,
                timestamp: Date.now()
            };
            
        case 'write':
            return {
                status: 'success',
                message: 'Write operation completed',
                data: { written: params[0] || 'test_data' },
                timestamp: Date.now()
            };
            
        case 'checknetwork':
            return {
                status: 'success',
                message: 'Network check completed',
                data: {
                    internet: true,
                    api: true,
                    latency: Math.floor(Math.random() * 50) + 10
                },
                timestamp: Date.now()
            };
            
        case 'changecommunication':
            return {
                status: 'success',
                message: `Communication changed to ${params[0] || 'network'}`,
                data: { mode: params[0] || 'network' },
                timestamp: Date.now()
            };
            
        default:
            return {
                status: 'error',
                message: `Unknown method: ${method}`,
                timestamp: Date.now()
            };
    }
}

// API Routes

// Health check
app.get('/api/health', (req, res) => {
    res.json({
        status: 'running',
        server: 'Node.js RFID Server',
        simulation: SIMULATE_MODE,
        mode: SIMULATE_MODE ? 'TEST' : 'PRODUCTION',
        javaServer: JAVA_SERVER_URL,
        timestamp: Date.now()
    });
});

// Start reading tags
app.post('/api/start', async (req, res) => {
    try {
        console.log('Starting RFID reading using existing startInventoryTag()...');
        
        if (SIMULATION_MODE) {
            const result = simulateJavaResponse('start', {});
            res.json(result);
        } else {
            // Execute using existing inventory method
            const result = await executeJavaCommand('start');
            res.json({
                status: 'success',
                message: 'Inventory started using existing method',
                data: result,
                timestamp: Date.now()
            });
        }
    } catch (error) {
        res.status(500).json({ 
            status: 'error', 
            message: error.message,
            timestamp: Date.now()
        });
    }
});

// Stop reading tags using existing stopInventory()
app.post('/api/stop', async (req, res) => {
    try {
        console.log('Stopping RFID reading using existing stopInventory()...');
        
        if (SIMULATION_MODE) {
            const result = simulateJavaResponse('stop', {});
            res.json(result);
        } else {
            // Execute using existing stop method
            const result = await executeJavaCommand('stop');
            res.json({
                status: 'success',
                message: 'Inventory stopped using existing method',
                data: result,
                timestamp: Date.now()
            });
        }
    } catch (error) {
        res.status(500).json({ 
            status: 'error', 
            message: error.message,
            timestamp: Date.now()
        });
    }
});

// Read specific tag data using existing ReadWriteForm.readData()
app.post('/api/read', async (req, res) => {
    try {
        const { accessPwd = '00000000', bank = 'EPC', start = '2', length = '6' } = req.body;
        console.log('Reading tag data using existing readData()...', { accessPwd, bank, start, length });
        
        if (SIMULATION_MODE) {
            const result = simulateJavaResponse('read', { accessPwd, bank, start, length });
            res.json(result);
        } else {
            // Execute using existing read method
            const result = await executeJavaCommand('read', { accessPwd, bank, start, length });
            res.json({
                status: 'success',
                message: 'Tag read using existing ReadWriteForm method',
                data: result,
                timestamp: Date.now()
            });
        }
    } catch (error) {
        res.status(500).json({ 
            status: 'error', 
            message: error.message,
            timestamp: Date.now()
        });
    }
});

// Write tag data using existing ReadWriteForm.writeData()
app.post('/api/write', async (req, res) => {
    try {
        const { accessPwd = '00000000', bank = 'EPC', start = '2', length = '6', data } = req.body;
        
        if (!data) {
            return res.status(400).json({
                status: 'error',
                message: 'Data to write is required',
                timestamp: Date.now()
            });
        }
        
        console.log('Writing tag data using existing writeData()...', { accessPwd, bank, start, length, data });
        
        if (SIMULATION_MODE) {
            const result = simulateJavaResponse('write', { accessPwd, bank, start, length, data });
            res.json(result);
        } else {
            // Execute using existing write method
            const result = await executeJavaCommand('write', { accessPwd, bank, start, length, data });
            res.json({
                status: 'success',
                message: 'Tag written using existing ReadWriteForm method',
                data: result,
                timestamp: Date.now()
            });
        }
    } catch (error) {
        console.error(`Write operation failed:`, error);
        
        // Provide helpful error messages based on error type
        let userMessage = error.message;
        let suggestion = null;
        
        if (error.suggestion) {
            suggestion = error.suggestion;
        }
        
        res.status(500).json({ 
            status: 'error', 
            message: userMessage,
            suggestion: suggestion,
            mode: SIMULATE_MODE ? 'TEST' : 'PRODUCTION',
            timestamp: Date.now()
        });
    }
});

// Check network connectivity using existing connection methods
app.get('/api/network', async (req, res) => {
    try {
        console.log('Checking connection status...');
        
        if (SIMULATION_MODE) {
            const result = simulateJavaResponse('status', {});
            res.json(result);
        } else {
            // Execute status check using existing method
            const result = await executeJavaCommand('status');
            res.json({
                status: 'success',
                message: 'Connection status checked',
                data: result,
                timestamp: Date.now()
            });
        }
    } catch (error) {
        res.status(500).json({ 
            status: 'error', 
            message: error.message,
            timestamp: Date.now()
        });
    }
});

// Change communication mode using existing UHFMainForm connection methods
app.post('/api/communication', async (req, res) => {
    try {
        const { mode = 'serial', port = '/dev/ttyUSB0', ip = '192.168.99.200', tcpPort = '8888' } = req.body;
        
        if (!mode || !['serial', 'network'].includes(mode)) {
            return res.status(400).json({
                status: 'error',
                message: 'Valid communication mode (serial/network) is required',
                timestamp: Date.now()
            });
        }
        
        console.log('Changing communication using existing connection methods...', { mode, port, ip, tcpPort });
        
        if (SIMULATION_MODE) {
            const result = simulateJavaResponse('connect', { mode, port, ip, tcpPort });
            res.json(result);
        } else {
            // Execute using existing connection method
            const result = await executeJavaCommand('connect', { mode, port, ip, tcpPort });
            res.json({
                status: 'success',
                message: 'Communication mode changed using existing method',
                data: result,
                timestamp: Date.now()
            });
        }
    } catch (error) {
        res.status(500).json({ 
            status: 'error', 
            message: error.message,
            timestamp: Date.now()
        });
    }
});

// Get current tag data
app.get('/api/tags', (req, res) => {
    if (SIMULATE_MODE) {
        res.json({
            status: 'success',
            data: mockData.tags,
            count: mockData.tags.length,
            timestamp: Date.now()
        });
    } else {
        // In real mode, this would fetch from actual reader
        res.json({ 
            status: 'success', 
            data: [], 
            count: 0,
            message: 'No simulation data available',
            timestamp: Date.now()
        });
    }
});

// Clear all tags (simulation only)
app.post('/api/tags/clear', (req, res) => {
    if (SIMULATE_MODE) {
        mockData.tags = [];
        res.json({
            status: 'success',
            message: 'All tags cleared',
            timestamp: Date.now()
        });
    } else {
        res.json({
            status: 'error',
            message: 'Clear function only available in simulation mode',
            timestamp: Date.now()
        });
    }
});

// Add a tag manually (simulation only)
app.post('/api/tags/add', (req, res) => {
    const { tagId, rssi = -50, antenna = 1 } = req.body;
    
    if (!tagId) {
        return res.status(400).json({
            status: 'error',
            message: 'Tag ID is required',
            timestamp: Date.now()
        });
    }
    
    if (SIMULATE_MODE) {
        const newTag = {
            id: tagId,
            rssi: rssi,
            antenna: antenna,
            timestamp: Date.now()
        };
        
        mockData.tags.push(newTag);
        
        res.json({
            status: 'success',
            message: 'Tag added successfully',
            data: newTag,
            timestamp: Date.now()
        });
    } else {
        res.json({
            status: 'error',
            message: 'Add tag function only available in simulation mode',
            timestamp: Date.now()
        });
    }
});

// Get reader status
app.get('/api/status', async (req, res) => {
    try {
        if (SIMULATION_MODE) {
            // TEST mode - return mock data
            res.json({
                status: 'success',
                data: {
                    ...mockData.readerStatus,
                    simulation: SIMULATE_MODE,
                    javaServerUrl: JAVA_SERVER_URL
                },
                simulation: SIMULATE_MODE,
                timestamp: Date.now()
            });
        } else {
            // PRODUCTION mode - get real hardware data
            console.log('Getting real hardware status...');
            
            try {
                const result = await executeJavaCommand('status');
                const hardwareStatus = parseHardwareStatus(result);
                
                res.json({
                    status: 'success',
                    data: {
                        connected: hardwareStatus.connected,
                        reading: hardwareStatus.inventoryRunning,
                        temperature: hardwareStatus.temperature,
                        power: hardwareStatus.power,
                        frequency: hardwareStatus.frequency,
                        simulation: SIMULATE_MODE,
                        javaServerUrl: JAVA_SERVER_URL
                    },
                    simulation: SIMULATE_MODE,
                    timestamp: Date.now()
                });
            } catch (error) {
                // If hardware status fails, return basic info with error
                res.json({
                    status: 'success',
                    data: {
                        connected: false,
                        reading: false,
                        temperature: null,
                        power: null,
                        frequency: null,
                        error: 'Hardware not connected',
                        simulation: SIMULATE_MODE,
                        javaServerUrl: JAVA_SERVER_URL
                    },
                    simulation: SIMULATE_MODE,
                    timestamp: Date.now()
                });
            }
        }
    } catch (error) {
        res.status(500).json({
            status: 'error',
            message: error.message,
            timestamp: Date.now()
        });
    }
});

// Get current RFID reader configuration
app.get('/api/config', async (req, res) => {
    try {
        if (SIMULATION_MODE) {
            // Return mock configuration
            res.json({
                status: 'success',
                data: {
                    power: 30,
                    frequency: 915.0,
                    region: 'FCC',
                    baudRate: 115200,
                    port: '/dev/ttyUSB0',
                    ipAddress: '192.168.99.200',
                    tcpPort: 8888,
                    connectionType: 'serial',
                    accessPassword: '00000000',
                    simulation: SIMULATE_MODE
                },
                simulation: SIMULATE_MODE,
                timestamp: Date.now()
            });
        } else {
            // Get real configuration from hardware
            const result = await executeJavaCommand('getconfig');
            const config = parseConfigOutput(result);
            
            res.json({
                status: 'success',
                data: config,
                simulation: SIMULATE_MODE,
                timestamp: Date.now()
            });
        }
    } catch (error) {
        res.status(500).json({
            status: 'error',
            message: error.message,
            timestamp: Date.now()
        });
    }
});

// Update RFID reader power
app.post('/api/config/power', async (req, res) => {
    try {
        const { power } = req.body;
        
        if (!power || power < 5 || power > 33) {
            return res.status(400).json({
                status: 'error',
                message: 'Power must be between 5 and 33 dBm',
                timestamp: Date.now()
            });
        }
        
        console.log('Setting power to:', power, 'dBm');
        
        if (SIMULATION_MODE) {
            mockData.readerStatus.power = power;
            res.json({
                status: 'success',
                message: `Power set to ${power} dBm (simulated)`,
                data: { power: power },
                simulation: SIMULATE_MODE,
                timestamp: Date.now()
            });
        } else {
            // Set real hardware power
            const result = await executeJavaCommand('setpower', { power });
            
            res.json({
                status: 'success',
                message: `Power set to ${power} dBm`,
                data: { power: power },
                simulation: SIMULATE_MODE,
                timestamp: Date.now()
            });
        }
    } catch (error) {
        res.status(500).json({
            status: 'error',
            message: error.message,
            timestamp: Date.now()
        });
    }
});

// Update RFID reader frequency
app.post('/api/config/frequency', async (req, res) => {
    try {
        const { frequency } = req.body;
        
        if (!frequency || frequency < 840 || frequency > 960) {
            return res.status(400).json({
                status: 'error',
                message: 'Frequency must be between 840 and 960 MHz',
                timestamp: Date.now()
            });
        }
        
        console.log('Setting frequency to:', frequency, 'MHz');
        
        if (SIMULATION_MODE) {
            mockData.readerStatus.frequency = frequency;
            res.json({
                status: 'success',
                message: `Frequency set to ${frequency} MHz (simulated)`,
                data: { frequency: frequency },
                simulation: SIMULATE_MODE,
                timestamp: Date.now()
            });
        } else {
            // Set real hardware frequency
            const result = await executeJavaCommand('setfrequency', { frequency });
            
            res.json({
                status: 'success',
                message: `Frequency set to ${frequency} MHz`,
                data: { frequency: frequency },
                simulation: SIMULATE_MODE,
                timestamp: Date.now()
            });
        }
    } catch (error) {
        res.status(500).json({
            status: 'error',
            message: error.message,
            timestamp: Date.now()
        });
    }
});

// Update RFID reader region
app.post('/api/config/region', async (req, res) => {
    try {
        const { region } = req.body;
        const validRegions = ['FCC', 'ETSI', 'CHN'];
        
        if (!region || !validRegions.includes(region)) {
            return res.status(400).json({
                status: 'error',
                message: 'Region must be one of: FCC, ETSI, CHN',
                timestamp: Date.now()
            });
        }
        
        console.log('Setting region to:', region);
        
        if (SIMULATION_MODE) {
            res.json({
                status: 'success',
                message: `Region set to ${region} (simulated)`,
                data: { region: region },
                simulation: SIMULATE_MODE,
                timestamp: Date.now()
            });
        } else {
            // Set real hardware region
            const result = await executeJavaCommand('setregion', { region });
            
            res.json({
                status: 'success',
                message: `Region set to ${region}`,
                data: { region: region },
                simulation: SIMULATE_MODE,
                timestamp: Date.now()
            });
        }
    } catch (error) {
        res.status(500).json({
            status: 'error',
            message: error.message,
            timestamp: Date.now()
        });
    }
});

// Update connection settings
app.post('/api/config/connection', async (req, res) => {
    try {
        const { connectionType, port, ipAddress, tcpPort, baudRate } = req.body;
        
        if (!connectionType || !['serial', 'network'].includes(connectionType)) {
            return res.status(400).json({
                status: 'error',
                message: 'Connection type must be serial or network',
                timestamp: Date.now()
            });
        }
        
        console.log('Updating connection settings:', { connectionType, port, ipAddress, tcpPort, baudRate });
        
        if (SIMULATION_MODE) {
            res.json({
                status: 'success',
                message: `Connection settings updated (simulated)`,
                data: { connectionType, port, ipAddress, tcpPort, baudRate },
                simulation: SIMULATE_MODE,
                timestamp: Date.now()
            });
        } else {
            // Update real connection settings
            const result = await executeJavaCommand('setconnection', { 
                connectionType, port, ipAddress, tcpPort, baudRate 
            });
            
            res.json({
                status: 'success',
                message: `Connection settings updated`,
                data: { connectionType, port, ipAddress, tcpPort, baudRate },
                simulation: SIMULATE_MODE,
                timestamp: Date.now()
            });
        }
    } catch (error) {
        res.status(500).json({
            status: 'error',
            message: error.message,
            timestamp: Date.now()
        });
    }
});

// Save current configuration to file
app.post('/api/config/save', async (req, res) => {
    try {
        const { name = 'default' } = req.body;
        
        console.log('Saving configuration as:', name);
        
        if (SIMULATION_MODE) {
            res.json({
                status: 'success',
                message: `Configuration saved as "${name}" (simulated)`,
                data: { configName: name },
                simulation: SIMULATE_MODE,
                timestamp: Date.now()
            });
        } else {
            // Save real configuration
            const result = await executeJavaCommand('saveconfig', { name });
            
            res.json({
                status: 'success',
                message: `Configuration saved as "${name}"`,
                data: { configName: name },
                simulation: SIMULATE_MODE,
                timestamp: Date.now()
            });
        }
    } catch (error) {
        res.status(500).json({
            status: 'error',
            message: error.message,
            timestamp: Date.now()
        });
    }
});

// Load configuration from file
app.post('/api/config/load', async (req, res) => {
    try {
        const { name = 'default' } = req.body;
        
        console.log('Loading configuration:', name);
        
        if (SIMULATION_MODE) {
            // Return mock loaded config
            res.json({
                status: 'success',
                message: `Configuration "${name}" loaded (simulated)`,
                data: {
                    configName: name,
                    power: 25,
                    frequency: 902.75,
                    region: 'FCC',
                    connectionType: 'serial'
                },
                simulation: SIMULATE_MODE,
                timestamp: Date.now()
            });
        } else {
            // Load real configuration
            const result = await executeJavaCommand('loadconfig', { name });
            const config = parseConfigOutput(result);
            
            res.json({
                status: 'success',
                message: `Configuration "${name}" loaded`,
                data: config,
                simulation: SIMULATE_MODE,
                timestamp: Date.now()
            });
        }
    } catch (error) {
        res.status(500).json({
            status: 'error',
            message: error.message,
            timestamp: Date.now()
        });
    }
});

// Toggle simulation mode
app.post('/api/mode/toggle', (req, res) => {
    const newMode = !SIMULATE_MODE;
    SIMULATE_MODE = newMode;
    SIMULATION_MODE = newMode; // Keep both variables in sync
    
    console.log(`🔄 Mode switched: SIMULATE_MODE=${SIMULATE_MODE}, SIMULATION_MODE=${SIMULATION_MODE}`);
    
    // Reset mock data when switching modes
    if (SIMULATE_MODE) {
        mockData.tags = [
            { id: 'E20000166016012345678901', rssi: -45, antenna: 1, timestamp: Date.now() },
            { id: 'E20000166016012345678902', rssi: -38, antenna: 2, timestamp: Date.now() },
            { id: 'E20000166016012345678903', rssi: -52, antenna: 1, timestamp: Date.now() },
        ];
    } else {
        mockData.tags = [];
    }
    
    res.json({
        status: 'success',
        message: `Mode switched to ${SIMULATE_MODE ? 'TEST' : 'PRODUCTION'}`,
        data: {
            simulation: SIMULATE_MODE,
            mode: SIMULATE_MODE ? 'TEST' : 'PRODUCTION'
        },
        timestamp: Date.now()
    });
});

// Get current mode
app.get('/api/mode', (req, res) => {
    res.json({
        status: 'success',
        data: {
            simulation: SIMULATE_MODE,
            mode: SIMULATE_MODE ? 'TEST' : 'PRODUCTION',
            description: SIMULATE_MODE ? 'Using mock data for testing' : 'Using real hardware connection'
        },
        simulation: SIMULATE_MODE,
        timestamp: Date.now()
    });
});

// Generic command executor
app.post('/api/execute', async (req, res) => {
    try {
        const { command, params = [] } = req.body;
        
        if (!command) {
            return res.status(400).json({
                status: 'error',
                message: 'Command is required',
                timestamp: Date.now()
            });
        }
        
        // Direct SDK execution - works for both simulation and production
        const result = await executeJavaCommand('com.myuhf.commands.ActionExecutor', command, params);
        
        res.json(result);
    } catch (error) {
        res.status(500).json({ 
            status: 'error', 
            message: error.message,
            timestamp: Date.now()
        });
    }
});

// Proxy endpoint removed - now using direct SDK execution only

// Server info endpoint
app.get('/api/info', (req, res) => {
    res.json({
        name: 'RFID Node.js Server',
        version: '1.0.0',
        description: 'Node.js server for UHF RFID SDK integration with direct Java execution',
        execution: 'Direct SDK execution',
        simulation: SIMULATE_MODE,
        javaClasspath: JAVA_CLASSPATH,
        endpoints: [
            'GET /api/health - Health check',
            'POST /api/start - Start reading tags (executes ActionExecutor.ReadInventory)',
            'POST /api/stop - Stop reading tags (executes ActionExecutor.stop)',
            'POST /api/read - Read tag data (executes ReadWriteForm.read)',
            'POST /api/write - Write tag data (executes ReadWriteForm.write)',
            'GET /api/network - Check network (executes NetworkCheck.checkNetwork)',
            'POST /api/communication - Change communication mode (executes UHFMainForm.changeCommunication)',
            'GET /api/tags - Get current tags (simulation data)',
            'POST /api/tags/add - Add tag (simulation only)',
            'POST /api/tags/clear - Clear tags (simulation only)',
            'GET /api/status - Get reader status',
            'GET /api/mode - Get current mode (TEST/PRODUCTION)',
            'POST /api/mode/toggle - Toggle between TEST and PRODUCTION modes',
            'POST /api/execute - Execute custom command (ActionExecutor)',
            '--- Configuration Endpoints ---',
            'GET /api/config - Get current RFID reader configuration',
            'POST /api/config/power - Update power setting (5-33 dBm)',
            'POST /api/config/frequency - Update frequency (840-960 MHz)',
            'POST /api/config/region - Update region (FCC/ETSI/CHN)',
            'POST /api/config/connection - Update connection settings',
            'POST /api/config/save - Save configuration to file',
            'POST /api/config/load - Load configuration from file'
        ],
        timestamp: Date.now()
    });
});

// Error handling middleware
app.use((error, req, res, next) => {
    console.error('Server error:', error);
    res.status(500).json({
        status: 'error',
        message: 'Internal server error',
        timestamp: Date.now()
    });
});

// 404 handler
app.use('*', (req, res) => {
    res.status(404).json({
        status: 'error',
        message: `Endpoint not found: ${req.method} ${req.originalUrl}`,
        availableEndpoints: '/api/info',
        timestamp: Date.now()
    });
});

// Start server
app.listen(PORT, () => {
    console.log(`🚀 Node.js RFID Server running on http://localhost:${PORT}`);
    console.log(`📊 Simulation mode: ${SIMULATE_MODE ? 'ON (Mock Data)' : 'OFF (Real Hardware)'}`);
    console.log(`⚙️  Execution mode: Direct Java SDK execution`);
    console.log(`📋 API Info: http://localhost:${PORT}/api/info`);
    console.log(`🔧 Available endpoints:`);
    console.log(`   POST /api/start - Start reading (→ ActionExecutor.ReadInventory)`);
    console.log(`   POST /api/stop - Stop reading (→ ActionExecutor.stop)`);
    console.log(`   POST /api/read - Read tag (→ ReadWriteForm.read)`);
    console.log(`   POST /api/write - Write tag (→ ReadWriteForm.write)`);
    console.log(`   GET  /api/network - Check network (→ NetworkCheck.checkNetwork)`);
    console.log(`   POST /api/communication - Change mode (→ UHFMainForm.changeCommunication)`);
    console.log(`   GET  /api/tags - Get current tags`);
    console.log(`   GET  /api/status - Get reader status`);
    console.log(`   GET  /api/mode - Get current mode (TEST/PRODUCTION)`);
    console.log(`   POST /api/mode/toggle - Toggle TEST/PRODUCTION mode`);
    console.log(`   POST /api/execute - Execute custom command`);
    console.log(`==================================================`);
});

module.exports = app;