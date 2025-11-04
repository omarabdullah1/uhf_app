import axios from 'axios';
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

  // Health check
  const checkHealth = async () => {
    try {
      await apiCall('/health');
    } catch (error) {
      console.error('Health check failed:', error);
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

  // Read specific tag
  const readTag = async () => {
    try {
      const result = await apiCall('/read', 'POST', {
        bank: 'EPC',
        address: 0,
        length: 6
      });
      
      if (result.status === 'success') {
        addLog(`Read tag: ${JSON.stringify(result.data)}`, 'info');
        fetchTags();
      }
    } catch (error) {
      addLog('Failed to read tag', 'error');
    }
  };

  // Write to tag
  const writeTag = async () => {
    if (!writeData.trim()) {
      addLog('Please enter data to write', 'warning');
      return;
    }

    try {
      const result = await apiCall('/write', 'POST', {
        data: writeData,
        bank: 'USER',
        address: 0
      });
      
      if (result.status === 'success') {
        addLog(`Write successful: ${writeData}`, 'success');
        setWriteData('');
      }
    } catch (error) {
      addLog('Failed to write tag', 'error');
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
  const addTestTag = async () => {
    const testTagId = 'E200001660160' + Math.floor(Math.random() * 1000000000).toString().padStart(12, '0');
    
    try {
      const result = await apiCall('/tags/add', 'POST', {
        tagId: testTagId,
        rssi: Math.floor(Math.random() * 20) - 60,
        antenna: Math.floor(Math.random() * 4) + 1
      });
      
      if (result.status === 'success') {
        addLog(`Test tag added: ${testTagId}`, 'success');
        fetchTags();
      }
    } catch (error) {
      addLog('Failed to add test tag', 'error');
    }
  };

  // Auto-refresh data
  useEffect(() => {
    checkHealth();
    fetchStatus();
    fetchTags();
    
    const interval = setInterval(() => {
      if (isReading) {
        fetchTags();
      }
      fetchStatus();
    }, 3000);
    
    return () => clearInterval(interval);
  }, [isReading]);

  return (
    <div className="App">
      <div className="container-fluid">
        <header className="app-header">
          <h1>UHF RFID Reader Control Panel</h1>
          <div className="connection-status">
            <span className={`status-indicator ${isConnected ? 'connected' : 'disconnected'}`}>
              {isConnected ? '🟢 Connected' : '🔴 Disconnected'}
            </span>
            {loading && <span className="loading-indicator">⏳ Loading...</span>}
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

                {/* Single Read */}
                <div className="mb-3">
                  <button 
                    className="btn btn-primary w-100" 
                    onClick={readTag}
                    disabled={loading}
                  >
                    🔍 Read Single Tag
                  </button>
                </div>

                {/* Write Control */}
                <div className="mb-3">
                  <h6>✏️ Write Data</h6>
                  <div className="input-group">
                    <input
                      type="text"
                      className="form-control"
                      placeholder="Enter data to write"
                      value={writeData}
                      onChange={(e) => setWriteData(e.target.value)}
                      disabled={loading}
                    />
                    <button 
                      className="btn btn-warning" 
                      onClick={writeTag}
                      disabled={loading}
                    >
                      ✏️ Write
                    </button>
                  </div>
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

                {/* Network Check */}
                <div className="mb-3">
                  <button 
                    className="btn btn-info w-100" 
                    onClick={checkNetwork}
                    disabled={loading}
                  >
                    🌐 Check Network
                  </button>
                </div>

                {/* Simulation Controls */}
                <div className="mb-3">
                  <h6>🧪 Test Controls</h6>
                  <div className="btn-group w-100 mb-2" role="group">
                    <button 
                      className="btn btn-outline-secondary" 
                      onClick={addTestTag}
                      disabled={loading}
                    >
                      ➕ Add Test Tag
                    </button>
                    <button 
                      className="btn btn-outline-danger" 
                      onClick={clearAllTags}
                      disabled={loading}
                    >
                      🗑️ Clear All
                    </button>
                  </div>
                </div>
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
                <div>
                  <button 
                    className="btn btn-sm btn-outline-primary me-2"
                    onClick={fetchTags}
                    disabled={loading}
                  >
                    🔄 Refresh
                  </button>
                  <span className="badge bg-secondary">
                    {isReading ? '🔄 Live' : '⏸️ Static'}
                  </span>
                </div>
              </div>
              <div className="card-body">
                {tags.length === 0 ? (
                  <div className="empty-state">
                    <div className="empty-icon">📭</div>
                    <p className="text-muted text-center">No tags detected</p>
                    <p className="text-muted text-center small">
                      Start reading or add test tags to see data here
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
      </div>
    </div>
  );
}

export default App;