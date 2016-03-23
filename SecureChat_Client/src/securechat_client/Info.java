/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package securechat_client;

import java.net.Socket;

/**
 *
 * @author Mohammed Muayad
 */
public class Info {
   private  static int serverPort=10000; 
   private  static String serverIP="192.168.0.16";
   private  static String clientID;
   private static Socket clientSocket;



   

    public static Socket getClientSocket() {
        return clientSocket;
    }

    public static void setClientSocket(Socket clientSocket) {
        Info.clientSocket = clientSocket;
    }

    public static int getServerPort() {
        return serverPort;
    }

    public static String getServerIP() {
        return serverIP;
    }

    public static String getClientID() {
        return clientID;
    }

    public static void setClientID(String clientID) {
        Info.clientID = clientID;
    }
    
   
}
