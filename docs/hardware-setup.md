# Hardware Setup Guide

## Overview

This guide provides step-by-step instructions for setting up UHF RFID hardware with the system, including reader configuration, antenna setup, and connection troubleshooting.

## Supported Hardware

### RFID Readers
- **UHF RFID Reader UR4 Series**
- **Frequency Range**: 860-960 MHz (varies by region)
- **Power Output**: 0-30 dBm (adjustable)
- **Antenna Ports**: 1-4 (depending on model)
- **Interfaces**: RS232/USB, Ethernet/TCP

### Compatible Tags
- **EPC Class 1 Gen 2 (ISO 18000-6C) Tags**
- **Memory**: 96-bit to 8KB user memory
- **Read Range**: 0.5m to 12m (depending on tag type and power)

## Connection Types

### Serial Connection (USB/RS232)

#### Required Hardware
- UHF RFID Reader with USB or RS232 interface
- USB cable (Type-A to Type-B) or RS232 cable
- Computer with available USB port or RS232 port

#### Setup Steps

1. **Install Drivers** (if required)
   ```bash
   # Linux: Check if device is recognized
   lsusb | grep -i "RFID\|Reader\|USB"
   
   # Check for serial devices
   ls -la /dev/ttyUSB* /dev/ttyACM*
   ```

2. **Connect Hardware**
   - Power on the RFID reader
   - Connect USB/RS232 cable between reader and computer
   - Wait for system to recognize the device

3. **Verify Connection**
   ```bash
   # Linux: Check device permissions
   ls -la /dev/ttyUSB0
   
   # If needed, fix permissions
   sudo chmod 666 /dev/ttyUSB0
   # Or add user to dialout group
   sudo usermod -a -G dialout $USER
   ```

4. **Test Connection**
   ```bash
   # Using CommandLineMain
   java -cp "build/libs/my-uhf-app.jar:libs/*" com.myuhf.CommandLineMain connect serial /dev/ttyUSB0
   
   # Or using API
   curl -X POST http://localhost:5000/api/communication \
     -H "Content-Type: application/json" \
     -d '{"mode":"serial","port":"/dev/ttyUSB0"}'
   ```

#### Common Serial Ports
| Operating System | Port Names |
|------------------|------------|
| **Linux** | `/dev/ttyUSB0`, `/dev/ttyACM0`, `/dev/ttyS0` |
| **Windows** | `COM1`, `COM2`, `COM3`, etc. |
| **macOS** | `/dev/tty.usbserial-*`, `/dev/cu.usbserial-*` |

### Network Connection (Ethernet/TCP)

#### Required Hardware
- UHF RFID Reader with Ethernet interface
- Ethernet cable (Cat5e or better)
- Network switch/router or direct connection to computer

#### Network Configuration

1. **Reader IP Configuration**
   - Default IP: `192.168.99.200`
   - Default Port: `8888`
   - Subnet: `255.255.255.0`

2. **Computer Network Setup**
   ```bash
   # Linux: Configure network interface
   sudo ip addr add 192.168.99.100/24 dev eth0
   
   # Or use NetworkManager
   nmcli con add type ethernet ifname eth0 con-name rfid-connection
   nmcli con modify rfid-connection ipv4.addresses 192.168.99.100/24
   nmcli con modify rfid-connection ipv4.method manual
   nmcli con up rfid-connection
   ```

3. **Test Network Connectivity**
   ```bash
   # Ping the reader
   ping 192.168.99.200
   
   # Test TCP connection
   telnet 192.168.99.200 8888
   
   # Or use netcat
   nc -zv 192.168.99.200 8888
   ```

4. **Connect via API**
   ```bash
   curl -X POST http://localhost:5000/api/communication \
     -H "Content-Type: application/json" \
     -d '{"mode":"network","ip":"192.168.99.200","tcpPort":"8888"}'
   ```

## Antenna Setup

### Antenna Types
- **Linear Polarized**: Better for oriented tags
- **Circular Polarized**: Better for randomly oriented tags
- **Near Field**: For close-range applications (< 1m)
- **Far Field**: For long-range applications (1-12m)

### Installation Guidelines

1. **Antenna Placement**
   - Mount antennas at least 60cm apart to avoid interference
   - Point antennas toward the expected tag locations
   - Avoid metal objects near antennas
   - Consider tag orientation when choosing antenna polarization

2. **Cable Requirements**
   - Use low-loss coaxial cable (RG-58, RG-213, or LMR-400)
   - Keep cable runs as short as possible
   - Use proper RF connectors (SMA, N-type, or TNC)
   - Consider cable loss in power calculations

3. **Power Settings**
   ```java
   // Set appropriate power level (0-30 dBm)
   reader.setPower(20);  // 20 dBm for indoor use
   reader.setPower(30);  // 30 dBm for outdoor/long range
   ```

### Multiple Antenna Configuration

```java
// Configure multiple antennas
for (int i = 1; i <= 4; i++) {
    reader.setAntenna(i);
    boolean enabled = reader.getAntennaState(i);
    System.out.println("Antenna " + i + ": " + (enabled ? "Enabled" : "Disabled"));
}
```

## Region-Specific Configuration

### Frequency Settings

#### North America (FCC)
```java
reader.setFrequency(915.0);  // Center frequency
reader.setRegion("US");
```

#### Europe (ETSI)
```java
reader.setFrequency(868.0);  // Center frequency
reader.setRegion("EU");
```

#### Asia-Pacific
```java
reader.setFrequency(922.0);  // Varies by country
reader.setRegion("AS");
```

### Power Limitations
| Region | Max Power | Notes |
|--------|-----------|-------|
| **US/Canada** | 30 dBm (1W) | FCC Part 15.247 |
| **Europe** | 27 dBm (500mW) | ETSI EN 302 208 |
| **Japan** | 24 dBm (250mW) | ARIB STD-T108 |
| **Korea** | 23 dBm (200mW) | KCC regulations |

## Performance Optimization

### Read Range Optimization

1. **Tag Selection**
   - Use high-sensitivity tags for long range
   - Consider tag size and material
   - Match tag impedance to antenna

2. **Power Level Tuning**
   ```java
   // Start with low power and increase gradually
   for (int power = 10; power <= 30; power += 5) {
       reader.setPower(power);
       // Test read performance
       boolean success = testReadPerformance();
       if (success) break;
   }
   ```

3. **Antenna Positioning**
   - Optimize antenna height and angle
   - Use multiple antennas for coverage
   - Minimize interference sources

### Environmental Considerations

#### Indoor Deployment
- Account for multipath reflections
- Consider metal shelving and equipment interference
- Use appropriate antenna patterns

#### Outdoor Deployment
- Weather-proof all connections
- Use UV-resistant cables and antennas
- Consider temperature effects on performance

## Troubleshooting

### Connection Issues

#### Serial Port Problems
```bash
# Problem: Permission denied
# Solution: Fix permissions
sudo chmod 666 /dev/ttyUSB0
sudo usermod -a -G dialout $USER

# Problem: Device not found
# Solution: Check if device is connected
dmesg | tail -20
lsusb

# Problem: Port already in use
# Solution: Find and kill process using port
sudo lsof /dev/ttyUSB0
sudo kill -9 <PID>
```

#### Network Connection Problems
```bash
# Problem: Cannot ping reader
# Solution: Check network configuration
ip addr show
ip route show

# Problem: Connection refused
# Solution: Check if reader is listening
nmap -p 8888 192.168.99.200

# Problem: Firewall blocking connection
# Solution: Open required ports
sudo ufw allow 8888
```

### Performance Issues

#### Low Read Rate
1. **Check Power Level**
   ```java
   int currentPower = reader.getPower();
   System.out.println("Current power: " + currentPower + " dBm");
   ```

2. **Verify Antenna Connection**
   ```java
   boolean antenna1 = reader.getAntennaState(1);
   boolean antenna2 = reader.getAntennaState(2);
   ```

3. **Check Frequency Settings**
   ```java
   double frequency = reader.getFrequency();
   System.out.println("Current frequency: " + frequency + " MHz");
   ```

#### Tag Reading Errors
1. **Tag Orientation**: Ensure tags are properly oriented relative to antennas
2. **Distance**: Verify tags are within read range
3. **Interference**: Check for nearby metal objects or other RF sources
4. **Tag Condition**: Verify tags are not damaged

### Hardware Diagnostics

#### System Health Check
```bash
# Run comprehensive system test
./test-system.sh

# Or manual checks
java -cp "build/libs/my-uhf-app.jar:libs/*" com.myuhf.CommandLineMain status

curl -X GET http://localhost:5000/api/network
```

#### Log Analysis
```bash
# Check system logs
journalctl -u rfid-system -f

# Check application logs
tail -f logs/rfid-system.log

# Check for hardware errors
dmesg | grep -i "usb\|serial\|tty"
```

## Maintenance

### Regular Maintenance Tasks

1. **Weekly**
   - Check antenna connections
   - Verify read performance with test tags
   - Review system logs for errors

2. **Monthly**
   - Clean antenna surfaces
   - Check cable connections for corrosion
   - Update system logs and performance metrics

3. **Quarterly**
   - Calibrate read range with standard tags
   - Check compliance with local regulations
   - Review and update configuration settings

### Firmware Updates

1. **Check Current Version**
   ```java
   String version = reader.getFirmwareVersion();
   System.out.println("Firmware version: " + version);
   ```

2. **Update Process**
   - Download latest firmware from manufacturer
   - Use firmware upgrade utility
   - Verify update completed successfully

## Safety Guidelines

### RF Safety
- Follow local RF exposure guidelines
- Use appropriate warning signs in high-power installations
- Maintain safe distances from antennas during operation

### Electrical Safety
- Use proper grounding for all equipment
- Follow electrical codes for power connections
- Use surge protection for outdoor installations

### Operational Safety
- Train operators on proper procedures
- Implement lockout/tagout procedures for maintenance
- Keep emergency contact information readily available

## Support Resources

### Manufacturer Support
- **Technical Support**: Contact reader manufacturer
- **Documentation**: Refer to reader technical manuals
- **Firmware Updates**: Check manufacturer website

### Community Resources
- **RFID Forums**: Online communities for troubleshooting
- **Standards Organizations**: EPCglobal, ISO 18000-6C
- **Training Resources**: RFID certification programs

### Emergency Contacts
- **System Administrator**: [Contact Information]
- **Hardware Vendor**: [Contact Information]
- **RF Engineer**: [Contact Information]