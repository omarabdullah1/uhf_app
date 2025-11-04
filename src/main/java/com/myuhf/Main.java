package com.myuhf;

import com.rscja.deviceapi.RFIDWithUHFSerialPortUR4;

import com.rscja.deviceapi.RFIDWithUHFNetworkUR4;
import com.rscja.deviceapi.entity.UHFTAGInfo;
import com.rscja.deviceapi.interfaces.IUHFInventoryCallback;
import main.java.com.myuhf.commands.*;
import main.java.com.myuhf.NetworkCheck;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.net.InetAddress;
import java.net.URL;
import java.net.HttpURLConnection;
import main.java.com.myuhf.NetworkCheck; 

public class Main {

    //Main Initialization Part 


         
        // Optional: load native lib manually (only if needed)
        // System.load(new java.io.File("native/libTagReader.so").getAbsolutePath());

        // Device Configuration : Initialize SerialPort , Intialize Drivers  ,Set Device Name , Set Clock/Timer , .... //

        // Network Initialization : TCP/IP , Static IP , Network Check connection//

// #### Create Commands when it received over tcp/ip or Api make a certian Action Like ####: 
             
        // Start(), Stop() , Restart () , Read() , Write(), search(), sorting()

//         1 Threading & Event Handling

// Right now, your RFID reading and API sending are synchronous — everything happens in the same thread.
// If one API call blocks, it may delay tag reading.
// #### Use a background thread (or ExecutorService) for ####:

          // Continuous tag reading loop

        // Sending data asynchronously

      // Keeping the UI or main loop responsive

    //   2 Tag Deduplication / Buffer
//     Keep a buffer or HashSet for unique tags per cycle (like 15 seconds).
// Then send them once per scan cycle.

// 3 Error Handling & Recovery

// If the reader disconnects or fails to initialize (e.g., serial port unavailable), you should handle it gracefully.
   
// 4 Logging & Diagnostics

// For production or kiosk systems, you’ll want full logging — not just System.out.println().

// 5 Configuration File (.json / .properties)

// Right now, IP, COM port, API URLs are hardcoded.

// 6 TCP Command Server (Remote Control)

// You mentioned before: "Start(), Stop(), Restart(), Read(), etc."
// That’s a great idea — and it’s missing now.

//  Solution:
// Run a TCP socket listener that accepts JSON commands:

//  Graceful Shutdown / Resource Cleanup

// When exiting or stopping inventory, ensure the serial port and reader are properly freed.

//  Watchdog / Heartbeat System

// If the device runs 24/7 (kiosk mode),  need to make sure the app hasn’t frozen or the reader hasn’t stopped responding.

//  Solution:
// Run a small watchdog thread that checks:

// Reader is still connected

// Network reachable

// API responds

// If not, auto-restart the app or reader.

//  Offline Storage (when no Internet)

// If the API is unreachable, store tag data locally in a .json or .csv file and retry later.

//  Security / Encryption

// If your device communicates sensitive info, protect it.

// Use HTTPS for API calls.

// Secure bootloader or firmware validation.

// Encrypt config files or API tokens.

    // String epc = uhftagInfo.getEPC();
    static int IDcount =0;
    String antennaId = "1"; 
    String readerId = "FW3GQC1"; 
    static String[] tagArray = new String[1000]; 
    static int doublicate =0;

    public static void main(String[] args) {

        ActionExecutor Action = new ActionExecutor(null, null);

        UHFTAGInfo uhftagInfo = new UHFTAGInfo();
        System.out.println("=== Network Connectivity Test ===");
        boolean internet = NetworkCheck.hasInternet();

        System.out.println(internet ? " Internet Available" : " No Internet");

        // Choose serial or network mode by serial
        boolean useNetwork = false;
        String comPort = "/dev/ttyUSB0";
        String ip = "192.168.99.200";
        int port = 8888;

        try {
            RFIDWithUHFSerialPortUR4 reader = new RFIDWithUHFSerialPortUR4();
            System.out.println("Initializing serial reader on " + comPort);
            boolean inited = reader.init(comPort);
            if (!inited) {
                System.err.println(" Failed to open serial port " + comPort);
                return;
            }
            
        
        // Start Inventory (continuous scanning)
            
            Action.ReadInventory();

           
        // System.out.println("Counter :\t "+ IDcount++ + "  TAG: EPC=" + epc + "  RSSI=" + rssi);

        boolean started = reader.startInventoryTag();
        if (started) {
            System.out.println(" Inventory started...");
        } else {
            System.out.println(" Failed to start inventory.");
        }

      

        Thread.sleep(30_000);

        // Stop and cleanup
        reader.stopInventory();
        reader.free();
        System.out.println(" Inventory stopped. Exiting.");
       
        } catch (Throwable t) {
            t.printStackTrace();
        }

    }
}
