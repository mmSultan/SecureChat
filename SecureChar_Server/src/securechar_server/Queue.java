/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package securechar_server;
import java.net.*;
import java.util.HashMap;
import Messages.Packet;
import Forms.MainForm;
/**
 *
 * @author Mohammed Muayad
 */

public class Queue {
   public static HashMap <String, Socket> mainSocketList = new HashMap<>();
   public static HashMap <String, Socket> sessionSocketList = new HashMap<>();
   public static HashMap <String,String> onlineList = new HashMap<>();
   public static HashMap <String,Packet> packetBuffer = new HashMap<>();
   
   public static void addOnline (String clientID){
       // we need to fetch name from database
   onlineList.put(clientID, "Name:"+clientID);
   MainForm.onlineList.setListData(onlineList.keySet().toArray());
   System.out.println("Client "+clientID+" Is Online");
   
//   for (String name: onlineList.keySet()){
//            String key =name.toString();
//            String value = onlineList.get(name).toString();  
//            System.out.println(key + " " + value);  
//} 
   }
   public static void addMainSocket (String clientID,Socket sock){
   mainSocketList.put(clientID, sock);
   MainForm.socketList.setListData(mainSocketList.entrySet().toArray());
   }
   public static void addSessionSocket (String clientID,Socket sock){
   sessionSocketList.put(clientID, sock);
   MainForm.sessionList.setListData(sessionSocketList.entrySet().toArray());
   }
   
   public static void removeOnline(String clientID){
       onlineList.remove(clientID);
       MainForm.onlineList.setListData(onlineList.keySet().toArray());
       System.out.println("Client "+clientID+" Is Offlien");
   }
    public static void removeMainSocket(String clientID){
       mainSocketList.remove(clientID);
       MainForm.socketList.setListData(mainSocketList.entrySet().toArray());

   }
    public static void removeSessionSocket(String clientID){
       sessionSocketList.remove(clientID);
       MainForm.sessionList.setListData(sessionSocketList.entrySet().toArray());

   }    
    
   
   
}
