package main.java.com.myuhf.NetworkConfig;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.InetAddress;

public class NetworkManager {

    public boolean checkInternet() {
        try {
            InetAddress address = InetAddress.getByName("8.8.8.8");
            return address.isReachable(3000);
        } catch (IOException e) {
            return false;
        }
    }

    public boolean pingApi(String apiUrl) {
        try {
            URL url = new URL(apiUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(3000);
            conn.connect();
            return conn.getResponseCode() == 200;
        } catch (IOException e) {
            return false;
        }
    }

    public void initTcpIp(String ip, int port) {
        System.out.println(" Initializing network TCP/IP connection at " + ip + ":" + port);
    }
}
