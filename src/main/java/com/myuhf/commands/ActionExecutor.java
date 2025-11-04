package main.java.com.myuhf.commands;

import main.java.com.myuhf.commands.*;
import main.java.com.myuhf.NetworkConfig.NetworkManager;
import main.java.com.myuhf.device.*;
import main.java.com.myuhf.NetworkCheck;
import main.java.com.myuhf.NetworkConfig.*;
import com.rscja.deviceapi.RFIDWithUHFSerialPortUR4;
import com.rscja.deviceapi.interfaces.IUHFInventoryCallback;
import com.rscja.deviceapi.entity.UHFTAGInfo;

public class ActionExecutor {
    private DeviceConfig device;
    private NetworkManager network;
    static String[] tagArray = new String[1000]; 
    static int IDcount =0;
    static int doublicate =0;
    
    public ActionExecutor(DeviceConfig device, NetworkManager network) {
        this.device = device;
        this.network = network;
    }

         // Callback when tags are read
        public void ReadInventory()
        {
             RFIDWithUHFSerialPortUR4 reader = new RFIDWithUHFSerialPortUR4();
             
        reader.setInventoryCallback(new IUHFInventoryCallback() {
            @Override
            public void callback(UHFTAGInfo uhftagInfo) {
                String epc = uhftagInfo.getEPC();
                String rssi = uhftagInfo.getRssi();
                String readerId = "FW3GQC1";

                 tagArray[IDcount] = epc;
                   
             //  array[IDcount]= epc;
               // System.out.println( "IDCOUNT"+ IDcount + " TAG: EPC=" + epc + "  RSSI=" + rssi);
                IDcount ++;
                   
               
                
                
        //         // Build JSON body
                // String jsonBody = "{ \"data\": [ " +
                //         "{ \"reader_idimi_number\": \"FW3GQC1\", " +
                //         "\"antenna_id\": \"1\", " +
                //         "\"tag_id\": \"" + epc + "\" } ] }";
String  jsonBody = "{\n" + "    \"rfids\": " + epc + "    \"reader_id\": "+ readerId +
                                            "    \"antenna_id\": " + rssi +
                                            "}";

                // Call NetworkCheck for POST
                String response = NetworkCheck.sendPost(
                        "http://67.217.244.159:5000/rfid_testing",  
                        jsonBody
                );
                System.out.println("API Response: " + response);

    //  for(int i=0; i < array.length ;i++)
    //     {
    //         System.out.println("Index :" + i + "EPC : " + epc );
    //     }
        // Print all stored tags 
                     System.out.println("=== All Tags Read So Far ===");
                    for (int i = 0; i < 100; i++) 
                    {
                        if(tagArray[i]== tagArray[i+1] )
                        {
                            System.out.println("Duplicate found " + doublicate);
                        }
                        System.out.println("Index: " + i + " | EPC: " + tagArray[i]);
                    }
                    System.out.println("===============================");
                }
            
       
        });
    }

    public void execute(CommandType command) {
        switch (command) {
            case START:
                System.out.println("▶ Starting RFID reading...");
                break;
            case STOP:
                System.out.println("⏹ Stopping RFID reader...");
                break;
            case RESTART:
                System.out.println(" Restarting device...");
                device.initialize();
                break;
            case CHECK_NETWORK:
                boolean internet = network.checkInternet();
                System.out.println(internet ? " Network OK" : " No Internet");
                break;
            default:
                System.out.println(" Unknown command: " + command);
        }
    }
}
