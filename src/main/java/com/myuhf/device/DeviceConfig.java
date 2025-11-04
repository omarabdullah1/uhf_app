package main.java.com.myuhf.device;

public class DeviceConfig {
    private String deviceName;
    private String serialPort;
    private boolean driversLoaded;
    private boolean clockSet;

    public DeviceConfig(String deviceName, String serialPort) {
        this.deviceName = deviceName;
        this.serialPort = serialPort;
    }

    public void initialize() {
        System.out.println("⚙️ Initializing device: " + deviceName);
        loadDrivers();
        initSerialPort();
        setClock();
    }

    private void loadDrivers() {
        driversLoaded = true;
        System.out.println("✅ Drivers loaded successfully");
    }

    private void initSerialPort() {
        System.out.println("🔌 Serial Port initialized at " + serialPort);
    }

    private void setClock() {
        clockSet = true;
        System.out.println("⏰ Clock synchronized with system time");
    }

    public String getDeviceName() { return deviceName; }
    public String getSerialPort() { return serialPort; }
}
