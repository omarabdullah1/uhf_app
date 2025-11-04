import axios from 'axios';
import 'bootstrap/dist/css/bootstrap.min.css';
import { useEffect, useState } from 'react';
import './App.css';

const API_BASE_URL = 'http://localhost:5000/api';

function App() {
  const [tags, setTags] = useState([]);
  const [readerStatus, setReaderStatus] = useState({});
  const [isReading, setIsReading] = useState(false);
  const [logs, setLogs] = useState([]);
  const [communicationMode, setCommunicationMode] = useState('network');
  const [writeData, setWriteData] = useState('');
  const [isConnected, setIsConnected] = useState(false);
  const [loading, setLoading] = useState(false);
  const [isTestMode, setIsTestMode] = useState(true);
  const [serverInfo, setServerInfo] = useState({});
  const [config, setConfig] = useState({
    power: 30,
    frequency: 915.0,
    region: 'FCC',
    connectionType: 'serial',
    port: '/dev/ttyUSB0',
    ipAddress: '192.168.99.200',
    tcpPort: 8888,
    baudRate: 115200
  });
  const [showConfig, setShowConfig] = useState(false);
  const [autoUpdate, setAutoUpdate] = useState(true);

  // Add log entry
  const addLog = (message, type = 'info') => {
    const logEntry = {
      id: Date.now(),
      timestamp: new Date().toLocaleTimeString(),
      message,
      type
    };
    setLogs(prev => [logEntry, ...prev.slice(0, 49)]); // Keep last 50 logs
  };

  // API call wrapper
  const apiCall = async (endpoint, method = 'GET', data = null) => {
    try {
      setLoading(true);
      const config = {
        method,
        url: `${API_BASE_URL}${endpoint}`,
        headers: { 'Content-Type': 'application/json' },
        timeout: 10000
      };
      
      if (data) config.data = data;
      
      const response = await axios(config);
      addLog(`${method} ${endpoint} - Success`, 'success');
      setIsConnected(true);
      
      // Update server mode info from response if available
      if (response.data.simulation !== undefined) {
        setIsTestMode(response.data.simulation);
      }
      
      return response.data;
    } catch (error) {
      const errorMsg = error.response?.data?.message || error.message;
      addLog(`${method} ${endpoint} - Error: ${errorMsg}`, 'error');
      setIsConnected(false);
      throw error;
    } finally {
      setLoading(false);
    }
  };

  // Health check and get server info
  const checkHealth = async () => {
    try {
      const result = await apiCall('/health');
      if (result.simulation !== undefined) {
        setIsTestMode(result.simulation);
      }
      setServerInfo(result);
    } catch (error) {
      console.error('Health check failed:', error);
    }
  };

  // Get server info
  const getServerInfo = async () => {
    try {
      const result = await apiCall('/info');
      setServerInfo(result);
      if (result.simulation !== undefined) {
        setIsTestMode(result.simulation);
      }
    } catch (error) {
      console.error('Failed to get server info:', error);
    }
  };

  // Fetch current tags
  const fetchTags = async () => {
    try {
      const result = await apiCall('/tags');
      if (result.status === 'success') {
        setTags(result.data || []);
      }
    } catch (error) {
      console.error('Error fetching tags:', error);
    }
  };

  // Fetch reader status
  const fetchStatus = async () => {
    try {
      const result = await apiCall('/status');
      if (result.status === 'success') {
        setReaderStatus(result.data || {});
        setIsReading(result.data?.reading || false);
      }
    } catch (error) {
      console.error('Error fetching status:', error);
    }
  };

  // Start reading
  const startReading = async () => {
    try {
      const result = await apiCall('/start', 'POST');
      if (result.status === 'success') {
        setIsReading(true);
        addLog('Started reading tags', 'success');
        fetchTags(); // Refresh tags
      }
    } catch (error) {
      addLog('Failed to start reading', 'error');
    }
  };

  // Stop reading
  const stopReading = async () => {
    try {
      const result = await apiCall('/stop', 'POST');
      if (result.status === 'success') {
        setIsReading(false);
        addLog('Stopped reading tags', 'success');
      }
    } catch (error) {
      addLog('Failed to stop reading', 'error');
    }
  };

  // Read specific tag with enhanced feedback
  const readTag = async (bankType = 'EPC', startAddr = 2, lengthWords = 6) => {
    try {
      addLog(`Reading ${bankType} bank (start: ${startAddr}, length: ${lengthWords})...`, 'info');
      
      const result = await apiCall('/read', 'POST', {
        accessPwd: '00000000',
        bank: bankType,
        start: startAddr.toString(),
        length: lengthWords.toString()
      });
      
      if (result.status === 'success') {
        if (isTestMode) {
          addLog(`✅ Read ${bankType} successful: ${result.data || 'No data'}`, 'success');
          if (result.allTags) {
            setTags(result.allTags);
          }
        } else {
          addLog(`✅ Read ${bankType} successful: ${result.data}`, 'success');
        }
        fetchTags();
      }
    } catch (error) {
      addLog(`❌ Failed to read ${bankType} bank: ${error.message}`, 'error');
    }
  };

  // Write to tag with enhanced validation and feedback
  const writeTag = async (bankType = 'USER', startAddr = 0, dataToWrite = null) => {
    const finalData = dataToWrite || writeData.trim();
    
    if (!finalData) {
      addLog('⚠️ Please enter data to write', 'warning');
      return;
    }

    // Validate hex data
    if (!/^[0-9A-Fa-f]+$/.test(finalData)) {
      addLog('⚠️ Data must be hexadecimal (0-9, A-F)', 'warning');
      return;
    }

    try {
      addLog(`Writing to ${bankType} bank (start: ${startAddr}): ${finalData}`, 'info');
      
      const result = await apiCall('/write', 'POST', {
        accessPwd: '00000000',
        bank: bankType,
        start: startAddr.toString(),
        length: Math.ceil(finalData.length / 4).toString(), // Convert hex chars to words
        data: finalData
      });
      
      if (result.status === 'success') {
        addLog(`✅ Write to ${bankType} successful: ${finalData}`, 'success');
        if (!dataToWrite) setWriteData(''); // Only clear if using the input field
        
        // Auto-read back to verify in test mode
        if (isTestMode) {
          setTimeout(() => {
            addLog('🔍 Verifying write operation...', 'info');
            readTag(bankType, startAddr, Math.ceil(finalData.length / 4));
          }, 1000);
        }
      }
    } catch (error) {
      addLog(`❌ Failed to write to ${bankType}: ${error.message}`, 'error');
    }
  };

  // Check network
  const checkNetwork = async () => {
    try {
      const result = await apiCall('/network');
      if (result.status === 'success') {
        addLog(`Network status: ${JSON.stringify(result.data)}`, 'info');
      }
    } catch (error) {
      addLog('Network check failed', 'error');
    }
  };

  // Change communication mode
  const changeCommunicationMode = async (mode) => {
    try {
      const result = await apiCall('/communication', 'POST', { mode });
      if (result.status === 'success') {
        setCommunicationMode(mode);
        addLog(`Communication changed to ${mode}`, 'success');
      }
    } catch (error) {
      addLog(`Failed to change communication to ${mode}`, 'error');
    }
  };

  // Clear all tags (simulation only)
  const clearAllTags = async () => {
    try {
      const result = await apiCall('/tags/clear', 'POST');
      if (result.status === 'success') {
        setTags([]);
        addLog('All tags cleared', 'success');
      }
    } catch (error) {
      addLog('Failed to clear tags', 'error');
    }
  };

  // Add test tag (simulation only)
  const addTestTag = async (customData = null) => {
    if (!isTestMode) {
      addLog('⚠️ Test functions only available in test mode', 'warning');
      return;
    }

    const testTagId = customData?.tagId || 'E200001660160' + Math.floor(Math.random() * 1000000000).toString().padStart(12, '0');
    
    try {
      const result = await apiCall('/tags/add', 'POST', {
        tagId: testTagId,
        rssi: customData?.rssi || Math.floor(Math.random() * 20) - 60,
        antenna: customData?.antenna || Math.floor(Math.random() * 4) + 1
      });
      
      if (result.status === 'success') {
        addLog(`✅ Test tag added: ${testTagId}`, 'success');
        fetchTags();
      }
    } catch (error) {
      addLog('❌ Failed to add test tag: ' + error.message, 'error');
    }
  };

  // Add multiple test tags quickly
  const addMultipleTestTags = async () => {
    if (!isTestMode) {
      addLog('⚠️ Test functions only available in test mode', 'warning');
      return;
    }

    addLog('🔄 Adding multiple test tags...', 'info');
    
    const testTags = [
      { tagId: 'E20000166016001234567890', rssi: -42, antenna: 1 },
      { tagId: 'E20000166016001234567891', rssi: -38, antenna: 2 },
      { tagId: 'E20000166016001234567892', rssi: -55, antenna: 1 },
      { tagId: 'E20000166016001234567893', rssi: -33, antenna: 3 },
      { tagId: 'E20000166016001234567894', rssi: -48, antenna: 2 }
    ];

    for (let i = 0; i < testTags.length; i++) {
      await addTestTag(testTags[i]);
      if (i < testTags.length - 1) {
        await new Promise(resolve => setTimeout(resolve, 300));
      }
    }
    
    addLog('✅ Multiple test tags added successfully', 'success');
  };

  // Toggle test/production mode
  const toggleMode = async () => {
    try {
      const result = await apiCall('/mode/toggle', 'POST');
      if (result.status === 'success') {
        setIsTestMode(result.data.simulation);
        addLog(`🔄 Mode switched to ${result.data.mode}`, 'success');
        
        // Clear tags when switching modes
        setTags([]);
        
        // Refresh status and reload data
        await fetchStatus();
        await fetchTags();
        
        // Show appropriate message for new mode
        if (result.data.simulation) {
          addLog('🧪 Test mode active - Mock data available for testing', 'info');
        } else {
          addLog('🚀 Production mode active - Connect to real hardware', 'info');
        }
      }
    } catch (error) {
      addLog('Failed to toggle mode', 'error');
    }
  };

  // Test read/write operations
  const testReadWrite = async () => {
    if (!isTestMode) {
      addLog('⚠️ Test functions only available in test mode', 'warning');
      return;
    }

    addLog('🧪 Starting Read/Write test sequence...', 'info');
    
    try {
      // Test 1: Read EPC bank
      addLog('Test 1: Reading EPC bank...', 'info');
      await readTag('EPC', 2, 6);
      
      await new Promise(resolve => setTimeout(resolve, 1500));
      
      // Test 2: Write to USER bank
      addLog('Test 2: Writing test data to USER bank...', 'info');
      await writeTag('USER', 0, '48656C6C6F576F726C64'); // "HelloWorld" in hex
      
      await new Promise(resolve => setTimeout(resolve, 1500));
      
      // Test 3: Read back USER bank
      addLog('Test 3: Reading back USER bank...', 'info');
      await readTag('USER', 0, 5);
      
      await new Promise(resolve => setTimeout(resolve, 1000));
      
      addLog('✅ Read/Write test sequence completed', 'success');
      
    } catch (error) {
      addLog('❌ Read/Write test failed: ' + error.message, 'error');
    }
  };

  // Test inventory operations
  const testInventory = async () => {
    if (!isTestMode) {
      addLog('⚠️ Test functions only available in test mode', 'warning');
      return;
    }

    addLog('🧪 Starting Inventory test sequence...', 'info');
    
    try {
      // Clear existing tags
      await clearAllTags();
      await new Promise(resolve => setTimeout(resolve, 500));
      
      // Add some test tags
      addLog('Adding test tags...', 'info');
      await addTestTag();
      await new Promise(resolve => setTimeout(resolve, 500));
      await addTestTag();
      await new Promise(resolve => setTimeout(resolve, 500));
      
      // Start inventory
      addLog('Starting inventory scan...', 'info');
      await startReading();
      
      // Let it run for a few seconds
      await new Promise(resolve => setTimeout(resolve, 3000));
      
      // Stop inventory
      addLog('Stopping inventory scan...', 'info');
      await stopReading();
      
      addLog('✅ Inventory test sequence completed', 'success');
      
    } catch (error) {
      addLog('❌ Inventory test failed: ' + error.message, 'error');
    }
  };

  // Fetch current configuration
  const fetchConfig = async () => {
    try {
      const response = await apiCall('/config');
      if (response.status === 'success') {
        setConfig(response.data);
        addLog('📋 Configuration loaded successfully', 'success');
      }
    } catch (error) {
      addLog('❌ Failed to load configuration: ' + error.message, 'error');
    }
  };

  // Update power setting
  const updatePower = async () => {
    try {
      const power = parseInt(document.getElementById('powerInput').value);
      if (power < 5 || power > 33) {
        addLog('⚠️ Power must be between 5 and 33 dBm', 'warning');
        return;
      }

      addLog(`🔧 Setting power to ${power} dBm...`, 'info');
      
      const response = await apiCall('/config/power', 'POST', { power });
      
      if (response.status === 'success') {
        setConfig(prev => ({ ...prev, power }));
        addLog(`✅ Power set to ${power} dBm`, 'success');
      } else {
        addLog('❌ Failed to set power: ' + response.message, 'error');
      }
    } catch (error) {
      addLog('❌ Failed to set power: ' + error.message, 'error');
    }
  };

  // Update frequency setting
  const updateFrequency = async () => {
    try {
      const frequency = parseFloat(document.getElementById('frequencyInput').value);
      if (frequency < 840 || frequency > 960) {
        addLog('⚠️ Frequency must be between 840 and 960 MHz', 'warning');
        return;
      }

      addLog(`🔧 Setting frequency to ${frequency} MHz...`, 'info');
      
      const response = await apiCall('/config/frequency', 'POST', { frequency });
      
      if (response.status === 'success') {
        setConfig(prev => ({ ...prev, frequency }));
        addLog(`✅ Frequency set to ${frequency} MHz`, 'success');
      } else {
        addLog('❌ Failed to set frequency: ' + response.message, 'error');
      }
    } catch (error) {
      addLog('❌ Failed to set frequency: ' + error.message, 'error');
    }
  };

  // Update region setting
  const updateRegion = async () => {
    try {
      const region = document.getElementById('regionSelect').value;
      
      addLog(`🔧 Setting region to ${region}...`, 'info');
      
      const response = await apiCall('/config/region', 'POST', { region });
      
      if (response.status === 'success') {
        setConfig(prev => ({ ...prev, region }));
        addLog(`✅ Region set to ${region}`, 'success');
      } else {
        addLog('❌ Failed to set region: ' + response.message, 'error');
      }
    } catch (error) {
      addLog('❌ Failed to set region: ' + error.message, 'error');
    }
  };

  // Update connection settings
  const updateConnectionSettings = async () => {
    try {
      const connectionType = document.getElementById('connectionTypeSelect').value;
      const port = document.getElementById('portInput').value;
      const ipAddress = document.getElementById('ipInput').value;
      const tcpPort = parseInt(document.getElementById('tcpPortInput').value);
      const baudRate = parseInt(document.getElementById('baudRateSelect').value);
      
      addLog(`🔧 Updating connection settings...`, 'info');
      
      const response = await apiCall('/config/connection', 'POST', {
        connectionType, port, ipAddress, tcpPort, baudRate
      });
      
      if (response.status === 'success') {
        setConfig(prev => ({ 
          ...prev, 
          connectionType, 
          port, 
          ipAddress, 
          tcpPort, 
          baudRate 
        }));
        addLog(`✅ Connection settings updated`, 'success');
      } else {
        addLog('❌ Failed to update connection settings: ' + response.message, 'error');
      }
    } catch (error) {
      addLog('❌ Failed to update connection settings: ' + error.message, 'error');
    }
  };

  // Save configuration
  const saveConfig = async () => {
    try {
      const name = document.getElementById('configNameInput').value || 'default';
      
      addLog(`💾 Saving configuration as "${name}"...`, 'info');
      
      const response = await apiCall('/config/save', 'POST', { name });
      
      if (response.status === 'success') {
        addLog(`✅ Configuration saved as "${name}"`, 'success');
      } else {
        addLog('❌ Failed to save configuration: ' + response.message, 'error');
      }
    } catch (error) {
      addLog('❌ Failed to save configuration: ' + error.message, 'error');
    }
  };

  // Load configuration
  const loadConfig = async () => {
    try {
      const name = document.getElementById('configNameInput').value || 'default';
      
      addLog(`📂 Loading configuration "${name}"...`, 'info');
      
      const response = await apiCall('/config/load', 'POST', { name });
      
      if (response.status === 'success') {
        setConfig(response.data);
        addLog(`✅ Configuration "${name}" loaded`, 'success');
      } else {
        addLog('❌ Failed to load configuration: ' + response.message, 'error');
      }
    } catch (error) {
      addLog('❌ Failed to load configuration: ' + error.message, 'error');
    }
  };

  // Get current mode
  const getCurrentMode = async () => {
    try {
      const result = await apiCall('/mode');
      if (result.status === 'success') {
        setIsTestMode(result.data.simulation);
        addLog(`Current mode: ${result.data.mode}`, 'info');
      }
    } catch (error) {
      addLog('Failed to get current mode', 'error');
    }
  };

  // Initialize app - restore automatic loading
  useEffect(() => {
    const initializeApp = async () => {
      await checkHealth();
      await getServerInfo();
      await fetchStatus();
      await fetchTags();
    };
    
    initializeApp();
  }, []);

  // Manual refresh function
  const manualRefresh = async () => {
    addLog('🔄 Manual refresh initiated...', 'info');
    try {
      await Promise.all([
        fetchTags(),
        fetchStatus(),
        fetchConfig()
      ]);
      addLog('✅ Manual refresh completed', 'success');
    } catch (error) {
      addLog('❌ Manual refresh failed: ' + error.message, 'error');
    }
  };

  // Toggle auto-update function
  const toggleAutoUpdate = () => {
    setAutoUpdate(!autoUpdate);
    addLog(`🔄 Auto-update ${!autoUpdate ? 'enabled' : 'disabled'}`, 'info');
  };

  // Auto-refresh data periodically and when actively reading (only if auto-update is enabled)
  useEffect(() => {
    if (!autoUpdate) return;

    const interval = setInterval(() => {
      if (isReading) {
        // Fast refresh when actively reading
        fetchTags();
        fetchStatus();
      } else {
        // Slower refresh when not reading (just status)
        fetchStatus();
      }
    }, isReading ? 2000 : 5000);
    
    return () => clearInterval(interval);
  }, [isReading, autoUpdate]);

  return (
    <div className="App">
      <div className="container-fluid">
        <header className="app-header">
          <div className="d-flex justify-content-between align-items-center">
            <h1>UHF RFID Reader Control Panel</h1>
            <div className="header-controls">
              {/* Auto-Update Toggle */}
              <div className="form-check form-switch">
                <input 
                  className="form-check-input" 
                  type="checkbox" 
                  id="autoUpdateSwitch"
                  checked={autoUpdate}
                  onChange={toggleAutoUpdate}
                />
                <label className="form-check-label" htmlFor="autoUpdateSwitch">
                  <small>{autoUpdate ? '🔄 Auto-Update ON' : '⏸️ Auto-Update OFF'}</small>
                </label>
              </div>
              
              {/* Manual Refresh Button */}
              <button 
                className="btn btn-outline-primary btn-sm refresh-btn" 
                onClick={manualRefresh}
                disabled={loading}
                title="Manual refresh all data"
              >
                🔄 Refresh
              </button>
              
              <div className="mode-indicator">
                <span className={`badge ${isTestMode ? 'bg-warning' : 'bg-success'} fs-6`}>
                  {isTestMode ? '🧪 TEST MODE' : '🚀 PRODUCTION MODE'}
                </span>
              </div>
              <div className="connection-status">
                <span className={`status-indicator ${isConnected ? 'connected' : 'disconnected'}`}>
                  {isConnected ? '🟢 Connected' : '🔴 Disconnected'}
                </span>
                {loading && <span className="loading-indicator">⏳ Loading...</span>}
              </div>
            </div>
          </div>
        </header>
        
        {/* Status Bar */}
        <div className="row mb-4">
          <div className="col-md-12">
            <div className="card status-card">
              <div className="card-header">
                <h5>📡 Reader Status</h5>
              </div>
              <div className="card-body">
                <div className="row">
                  <div className="col-md-3">
                    <strong>Connection:</strong> 
                    <span className={`ms-2 badge ${readerStatus.connected ? 'bg-success' : 'bg-danger'}`}>
                      {readerStatus.connected ? 'Connected' : 'Disconnected'}
                    </span>
                  </div>
                  <div className="col-md-2">
                    <strong>Temperature:</strong> {readerStatus.temperature || 'N/A'}°C
                  </div>
                  <div className="col-md-2">
                    <strong>Power:</strong> {readerStatus.power || 'N/A'} dBm
                  </div>
                  <div className="col-md-2">
                    <strong>Frequency:</strong> {readerStatus.frequency || 'N/A'} MHz
                  </div>
                  <div className="col-md-3">
                    <strong>Reading:</strong> 
                    <span className={`ms-2 badge ${isReading ? 'bg-success pulse' : 'bg-secondary'}`}>
                      {isReading ? '🔄 Active' : '⏸️ Stopped'}
                    </span>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>

        <div className="row">
          {/* Control Panel */}
          <div className="col-md-4">
            <div className="card mb-4">
              <div className="card-header">
                <h5>🎛️ Control Panel</h5>
              </div>
              <div className="card-body">
                {/* Reading Controls */}
                <div className="mb-3">
                  <h6>📖 Reading Control</h6>
                  <div className="btn-group w-100" role="group">
                    <button 
                      className="btn btn-success" 
                      onClick={startReading}
                      disabled={isReading || loading}
                    >
                      ▶️ Start Reading
                    </button>
                    <button 
                      className="btn btn-danger" 
                      onClick={stopReading}
                      disabled={!isReading || loading}
                    >
                      ⏹️ Stop Reading
                    </button>
                  </div>
                </div>

                {/* Enhanced Read Controls */}
                <div className="mb-3">
                  <h6>🔍 Read Operations</h6>
                  <div className="btn-group w-100 mb-2" role="group">
                    <button 
                      className="btn btn-primary btn-sm" 
                      onClick={() => readTag('EPC')}
                      disabled={loading}
                    >
                      � Read EPC
                    </button>
                    <button 
                      className="btn btn-info btn-sm" 
                      onClick={() => readTag('TID')}
                      disabled={loading}
                    >
                      🆔 Read TID
                    </button>
                    <button 
                      className="btn btn-secondary btn-sm" 
                      onClick={() => readTag('USER')}
                      disabled={loading}
                    >
                      👤 Read USER
                    </button>
                  </div>
                </div>

                {/* Enhanced Write Control */}
                <div className="mb-3">
                  <h6>✏️ Write Operations</h6>
                  <div className="input-group mb-2">
                    <input
                      type="text"
                      className="form-control"
                      placeholder="Enter hex data (e.g., 48656C6C6F)"
                      value={writeData}
                      onChange={(e) => setWriteData(e.target.value.toUpperCase())}
                      disabled={loading}
                    />
                    <button 
                      className="btn btn-warning" 
                      onClick={() => writeTag()}
                      disabled={loading || !writeData.trim()}
                    >
                      ✏️ Write USER
                    </button>
                  </div>
                  <div className="btn-group w-100" role="group">
                    <button 
                      className="btn btn-outline-warning btn-sm" 
                      onClick={() => writeTag('EPC', 2, '123456789ABCDEF012345678')}
                      disabled={loading}
                    >
                      📝 Write EPC
                    </button>
                    <button 
                      className="btn btn-outline-secondary btn-sm" 
                      onClick={() => writeTag('USER', 0, '48656C6C6F576F726C64')}
                      disabled={loading}
                    >
                      📝 Write Hello
                    </button>
                  </div>
                  <small className="text-muted">Data must be in hexadecimal format</small>
                </div>

                {/* Communication Mode */}
                <div className="mb-3">
                  <h6>📡 Communication Mode</h6>
                  <div className="btn-group w-100" role="group">
                    <button 
                      className={`btn ${communicationMode === 'network' ? 'btn-primary' : 'btn-outline-primary'}`}
                      onClick={() => changeCommunicationMode('network')}
                      disabled={loading}
                    >
                      🌐 Network
                    </button>
                    <button 
                      className={`btn ${communicationMode === 'serial' ? 'btn-primary' : 'btn-outline-primary'}`}
                      onClick={() => changeCommunicationMode('serial')}
                      disabled={loading}
                    >
                      🔌 Serial
                    </button>
                  </div>
                </div>

                {/* System Initialization */}
                <div className="mb-3">
                  <h6>🔄 System Status</h6>
                  <div className="btn-group w-100 mb-2" role="group">
                    <button 
                      className="btn btn-outline-info" 
                      onClick={fetchStatus}
                      disabled={loading}
                    >
                      📊 Get Status
                    </button>
                    <button 
                      className="btn btn-outline-info" 
                      onClick={fetchTags}
                      disabled={loading}
                    >
                      🏷️ Load Tags
                    </button>
                  </div>
                  <button 
                    className="btn btn-info w-100" 
                    onClick={checkNetwork}
                    disabled={loading}
                  >
                    🌐 Check Network
                  </button>
                </div>

                {/* Mode Controls */}
                <div className="mb-3">
                  <h6>⚙️ System Mode</h6>
                  <div className="card border-info mb-2">
                    <div className="card-body py-2">
                      <div className="d-flex justify-content-between align-items-center">
                        <small className="text-muted">Current Mode:</small>
                        <span className={`badge ${isTestMode ? 'bg-warning' : 'bg-success'}`}>
                          {isTestMode ? '🧪 Test' : '🚀 Production'}
                        </span>
                      </div>
                    </div>
                  </div>
                  <div className="btn-group w-100 mb-2" role="group">
                    <button 
                      className="btn btn-outline-primary btn-sm" 
                      onClick={getCurrentMode}
                      disabled={loading}
                    >
                      🔍 Check Mode
                    </button>
                    <button 
                      className="btn btn-outline-warning btn-sm" 
                      onClick={toggleMode}
                      disabled={loading}
                    >
                      🔄 Toggle Mode
                    </button>
                  </div>
                  <small className="text-muted">
                    {isTestMode 
                      ? "Test mode: Using mock data for development" 
                      : "Production mode: Connecting to real hardware"
                    }
                  </small>
                </div>

                {/* Enhanced Test Controls - Only show in test mode */}
                {isTestMode && (
                  <div className="mb-3">
                    <h6>🧪 Test Controls</h6>
                    <div className="btn-group w-100 mb-2" role="group">
                      <button 
                        className="btn btn-outline-secondary btn-sm" 
                        onClick={() => addTestTag()}
                        disabled={loading}
                      >
                        ➕ Add Tag
                      </button>
                      <button 
                        className="btn btn-outline-info btn-sm" 
                        onClick={addMultipleTestTags}
                        disabled={loading}
                      >
                        ➕➕ Add 5 Tags
                      </button>
                      <button 
                        className="btn btn-outline-danger btn-sm" 
                        onClick={clearAllTags}
                        disabled={loading}
                      >
                        🗑️ Clear All
                      </button>
                    </div>
                    <div className="btn-group w-100 mb-2" role="group">
                      <button 
                        className="btn btn-outline-primary btn-sm" 
                        onClick={testReadWrite}
                        disabled={loading}
                      >
                        🔬 Test R/W
                      </button>
                      <button 
                        className="btn btn-outline-success btn-sm" 
                        onClick={testInventory}
                        disabled={loading}
                      >
                        📊 Test Inventory
                      </button>
                    </div>
                    <small className="text-muted">Automated test sequences for development</small>
                  </div>
                )}
              </div>
            </div>

            {/* Logs */}
            <div className="card">
              <div className="card-header d-flex justify-content-between align-items-center">
                <h5>📋 Activity Logs</h5>
                <button 
                  className="btn btn-sm btn-outline-secondary"
                  onClick={() => setLogs([])}
                >
                  🗑️ Clear
                </button>
              </div>
              <div className="card-body logs-container">
                {logs.length === 0 ? (
                  <p className="text-muted text-center">No activity logs</p>
                ) : (
                  logs.map(log => (
                    <div key={log.id} className={`log-entry log-${log.type}`}>
                      <small className="text-muted">{log.timestamp}</small><br/>
                      <span className="log-message">{log.message}</span>
                    </div>
                  ))
                )}
              </div>
            </div>
          </div>

          {/* Tags Display */}
          <div className="col-md-8">
            <div className="card">
              <div className="card-header d-flex justify-content-between align-items-center">
                <h5>🏷️ Detected Tags ({tags.length})</h5>
                <div className="d-flex align-items-center gap-2">
                  <button 
                    className="btn btn-sm btn-outline-primary"
                    onClick={fetchTags}
                    disabled={loading}
                    title="Refresh tags only"
                  >
                    🏷️ Refresh Tags
                  </button>
                  <button 
                    className="btn btn-sm btn-outline-info"
                    onClick={fetchStatus}
                    disabled={loading}
                    title="Refresh status only"
                  >
                    � Refresh Status
                  </button>
                  <div className="tags-header-badges">
                    <span className="badge bg-secondary">
                      {isReading ? '🔄 Live' : '⏸️ Static'}
                    </span>
                    <span className={`badge ${autoUpdate ? 'bg-success' : 'bg-warning'}`}>
                      {autoUpdate ? '🔄 Auto' : '⏸️ Manual'}
                    </span>
                  </div>
                </div>
              </div>
              <div className="card-body">
                {tags.length === 0 ? (
                  <div className="empty-state">
                    <div className="empty-icon">📭</div>
                    <p className="text-muted text-center">No tags detected</p>
                    <p className="text-muted text-center small">
                      {isTestMode 
                        ? "Start reading or add test tags to see data here" 
                        : "Connect to RFID hardware and start reading to see tags"
                      }
                    </p>
                  </div>
                ) : (
                  <div className="table-responsive">
                    <table className="table table-hover table-striped">
                      <thead className="table-dark">
                        <tr>
                          <th>🏷️ Tag ID</th>
                          <th>📶 RSSI</th>
                          <th>📡 Antenna</th>
                          <th>⏰ Timestamp</th>
                          <th>🔄 Actions</th>
                        </tr>
                      </thead>
                      <tbody>
                        {tags.map((tag, index) => (
                          <tr key={tag.id || index} className="tag-row">
                            <td>
                              <code className="tag-id">{tag.id}</code>
                            </td>
                            <td>
                              <span className={`rssi-value ${tag.rssi > -40 ? 'rssi-good' : tag.rssi > -60 ? 'rssi-fair' : 'rssi-poor'}`}>
                                {tag.rssi} dBm
                              </span>
                            </td>
                            <td>
                              <span className="badge bg-info">Ant {tag.antenna}</span>
                            </td>
                            <td>
                              <small>{new Date(tag.timestamp).toLocaleTimeString()}</small>
                            </td>
                            <td>
                              <button 
                                className="btn btn-sm btn-outline-primary"
                                onClick={() => addLog(`Selected tag: ${tag.id}`, 'info')}
                              >
                                📋 Select
                              </button>
                            </td>
                          </tr>
                        ))}
                      </tbody>
                    </table>
                  </div>
                )}
              </div>
            </div>
          </div>
        </div>

        {/* Configuration Panel */}
        <div className="row mt-4">
          <div className="col-12">
            <div className="card">
              <div className="card-header d-flex justify-content-between align-items-center">
                <h5 className="mb-0">⚙️ RFID Reader Configuration</h5>
                <div>
                  <button 
                    className="btn btn-outline-primary btn-sm me-2" 
                    onClick={fetchConfig}
                    disabled={loading}
                  >
                    🔄 Refresh Config
                  </button>
                  <button 
                    className="btn btn-primary btn-sm" 
                    onClick={() => setShowConfig(!showConfig)}
                  >
                    {showConfig ? '🔼 Hide Config' : '🔽 Show Config'}
                  </button>
                </div>
              </div>
              
              {showConfig && (
                <div className="card-body">
                  {/* Power Settings */}
                  <div className="row mb-3">
                    <div className="col-md-6">
                      <h6>🔌 Power Settings</h6>
                      <div className="input-group mb-2">
                        <span className="input-group-text">Power (dBm)</span>
                        <input
                          type="number"
                          id="powerInput"
                          className="form-control"
                          min="5"
                          max="33"
                          defaultValue={config.power}
                          placeholder="5-33 dBm"
                        />
                        <button className="btn btn-outline-success" onClick={updatePower} disabled={loading}>
                          Set Power
                        </button>
                      </div>
                      <small className="text-muted">Current: {config.power} dBm</small>
                    </div>
                    
                    <div className="col-md-6">
                      <h6>📡 Frequency Settings</h6>
                      <div className="input-group mb-2">
                        <span className="input-group-text">Frequency (MHz)</span>
                        <input
                          type="number"
                          id="frequencyInput"
                          className="form-control"
                          min="840"
                          max="960"
                          step="0.25"
                          defaultValue={config.frequency}
                          placeholder="840-960 MHz"
                        />
                        <button className="btn btn-outline-success" onClick={updateFrequency} disabled={loading}>
                          Set Frequency
                        </button>
                      </div>
                      <small className="text-muted">Current: {config.frequency} MHz</small>
                    </div>
                  </div>
                  
                  {/* Region Settings */}
                  <div className="row mb-3">
                    <div className="col-md-6">
                      <h6>🌍 Region Settings</h6>
                      <div className="input-group mb-2">
                        <span className="input-group-text">Region</span>
                        <select id="regionSelect" className="form-select" defaultValue={config.region}>
                          <option value="FCC">FCC (US/Canada)</option>
                          <option value="ETSI">ETSI (Europe)</option>
                          <option value="CHN">CHN (China)</option>
                        </select>
                        <button className="btn btn-outline-success" onClick={updateRegion} disabled={loading}>
                          Set Region
                        </button>
                      </div>
                      <small className="text-muted">Current: {config.region}</small>
                    </div>
                    
                    <div className="col-md-6">
                      <h6>🔗 Connection Type</h6>
                      <div className="input-group mb-2">
                        <span className="input-group-text">Type</span>
                        <select id="connectionTypeSelect" className="form-select" defaultValue={config.connectionType}>
                          <option value="serial">Serial/USB</option>
                          <option value="network">Network/TCP</option>
                        </select>
                      </div>
                      <small className="text-muted">Current: {config.connectionType}</small>
                    </div>
                  </div>
                  
                  {/* Connection Details */}
                  <div className="row mb-3">
                    <div className="col-md-6">
                      <h6>🔌 Serial Connection</h6>
                      <div className="input-group mb-2">
                        <span className="input-group-text">Port</span>
                        <input
                          type="text"
                          id="portInput"
                          className="form-control"
                          defaultValue={config.port}
                          placeholder="/dev/ttyUSB0"
                        />
                      </div>
                      <div className="input-group mb-2">
                        <span className="input-group-text">Baud Rate</span>
                        <select id="baudRateSelect" className="form-select" defaultValue={config.baudRate}>
                          <option value="9600">9600</option>
                          <option value="19200">19200</option>
                          <option value="38400">38400</option>
                          <option value="57600">57600</option>
                          <option value="115200">115200</option>
                        </select>
                      </div>
                    </div>
                    
                    <div className="col-md-6">
                      <h6>🌐 Network Connection</h6>
                      <div className="input-group mb-2">
                        <span className="input-group-text">IP Address</span>
                        <input
                          type="text"
                          id="ipInput"
                          className="form-control"
                          defaultValue={config.ipAddress}
                          placeholder="192.168.99.200"
                        />
                      </div>
                      <div className="input-group mb-2">
                        <span className="input-group-text">TCP Port</span>
                        <input
                          type="number"
                          id="tcpPortInput"
                          className="form-control"
                          defaultValue={config.tcpPort}
                          placeholder="8888"
                        />
                      </div>
                    </div>
                  </div>
                  
                  {/* Action Buttons */}
                  <div className="row">
                    <div className="col-12">
                      <div className="d-flex gap-2 flex-wrap">
                        <button className="btn btn-primary" onClick={updateConnectionSettings} disabled={loading}>
                          🔧 Update Connection Settings
                        </button>
                        
                        <div className="input-group" style={{maxWidth: '300px'}}>
                          <input
                            type="text"
                            id="configNameInput"
                            className="form-control"
                            placeholder="Configuration name"
                            defaultValue="default"
                          />
                          <button className="btn btn-success" onClick={saveConfig} disabled={loading}>
                            💾 Save Config
                          </button>
                          <button className="btn btn-info" onClick={loadConfig} disabled={loading}>
                            📂 Load Config
                          </button>
                        </div>
                      </div>
                    </div>
                  </div>
                </div>
              )}
            </div>
          </div>
        </div>

      </div>
    </div>
  );
}

export default App;
