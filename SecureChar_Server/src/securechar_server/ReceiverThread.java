/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package securechar_server;

import java.net.*;
import java.io.*;
import Messages.Packet;
import Forms.MainForm;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 *
 * @author Mohammed Muayad
 */
public class ReceiverThread extends Thread {
    //private String clientID;
    private Socket clientSocket;
    private ObjectInputStream inStream;
    private Packet rcvPkt;
    private Packet sendPkt;
    private Packet tempPkt;
    private String clientID;
    private static int SessionCouter;
    private String sessionID;
    private boolean mainThread;
    public static int count;
    private String newClientID;

    public ReceiverThread(Socket clientSocket) {
        this.clientSocket = clientSocket;
        this.mainThread=true;
    }

 
    

    @Override
    public void run() {
        System.out.println("Receiving Thread Started");
      //  MainForm.logText.append("Receiving Thread Started \n");
        count++;
       // MainForm.logText.append("Active thread:"+count +" \n");
        try {
            while (true){
            rcvPkt = new Packet(); // create new packet
            inStream = new ObjectInputStream(clientSocket.getInputStream());// new input
            rcvPkt=(Packet) inStream.readObject(); // listen to the socket for incoming packets 
            clientID=rcvPkt.getClientID();
            sessionID=rcvPkt.getSessionID();
            switch(rcvPkt.getType()){
                case 1: // Authintication
                    sendPkt=new Packet();
                    sendPkt.setType((short)1);
                    sendPkt.setClientID(clientID);
                    MainForm.logText.append("Auth Rcvd \n");
                    if(rcvPkt.getData1().equals("a")){ 
                        sendPkt.setData1("VALID");   
                        sendPkt.setOnlineList(Queue.onlineList); // add the whole online list
                        Queue.addMainSocket(clientID, clientSocket);
                        Queue.addOnline(clientID);
                        new SenderThread(sendPkt).start(); // send replay valid
                        
                        //send update OnlineList
                        
                        HashMap <String,String> tempList = new HashMap<>();
                        tempList.put(clientID,Queue.onlineList.get(clientID)); // get the new user name added to online List
                        
                        
                        //send update to all online users
                         for(Map.Entry<String, Socket> mainSocketList : Queue.mainSocketList.entrySet()){
                            if(!mainSocketList.getKey().equals(clientID)) { // if it is not the current user send update
                                sendPkt=new Packet();
                                sendPkt.setType((short)6);
                                sendPkt.setOnlineList(tempList);
                                sendPkt.setClientID(mainSocketList.getKey());
                                sendPkt.setAddToList(true);
                                MainForm.logText.append("Sent-6 to "+mainSocketList.getKey()+" \n");
                                new SenderThread(sendPkt).start();
                            }
                         }// for     
                    } else { // invalid user or pass
                        Queue.addMainSocket(clientID, clientSocket);
                        sendPkt.setData1("INVALID");
                        new SenderThread(sendPkt).start();
                      
                    }
                    break;
                case 2: //DATA
                    
                    mainThread=false;
                    break;
                    
                case 3: //3-ReqSessionID
                    MainForm.logText.append("Request SessionID Pkt received \n");
                    SessionCouter++;
                    sessionID=Integer.toString(SessionCouter);
                    sendPkt = new Packet();
                    sendPkt.setClientID(clientID);
                    sendPkt.setType((short)3); //create New window Pkt
                    sendPkt.setDestIDList(rcvPkt.getDestIDList()); // put the Dest User ID
                    sendPkt.setSessionID(sessionID);
                    
                    
                    new SenderThread(sendPkt).start(); // sendback sessionID with NewWindow Pkt to the user to open newWindow
                    
                    List <String> destIDList=rcvPkt.getDestIDList();
                    sendPkt = new Packet();
                    sendPkt.setClientID(destIDList.get(0));
                    sendPkt.addDestID(clientID);
                    sendPkt.setType((short)3);
                    sendPkt.setSessionID(sessionID);
                    new SenderThread(sendPkt).start(); // send to the other client. that the first one wants to chat with him to open new window in his app 
                    
                    break;
                case 4://4-newChat
                     newClientID=clientID+"-"+rcvPkt.getSessionID();
                     Queue.addSessionSocket((newClientID), clientSocket); // save socket with new ID
                     mainThread=false;
                    break;
               case 5://5-UpdatechatList,
                    sessionID=rcvPkt.getSessionID();
              
                     for(String destID : rcvPkt.getDestIDList()){
                         if(!destID.equals(rcvPkt.getData1())){ // send to all except the invited user
                            sendPkt= new Packet();
                             System.out.println("5 sent to...."+destID);
                            //sendPkt=rcvPkt;
                            sendPkt.setType((short)5);
                            sendPkt.setClientID(destID);
                            sendPkt.setData1(rcvPkt.getData1());
                            sendPkt.setSessionID(sessionID);
                            sendPkt.setAddToList(rcvPkt.isAddToList());
                            new SenderThread(sendPkt).start();
                         }// if
                     }//for
                     if(rcvPkt.isAddToList()){// if it is invitink pkt
                    // send 3-pkt to invited user to open new chat window              
                      sendPkt = new Packet();
                      sendPkt.setType((short)3);
                      sendPkt.setClientID(rcvPkt.getData1());// get the ID of invited user
                      sendPkt.setSessionID(sessionID);
                      sendPkt.setDestIDList(rcvPkt.getDestIDList());// put the Dist List
                      sendPkt.addDestID(rcvPkt.getClientID()); // add the ID of the inviter user
                      sendPkt.removeDestId(rcvPkt.getData1());// remoce the ID of the invited user
                      new SenderThread(sendPkt).start();
                     }else{ // it is remove pkt
                      sendPkt = new Packet();
                      sendPkt.setType((short)5);
                      sendPkt.setClientID(rcvPkt.getData1());// get the ID of invited user
                      sendPkt.setSessionID(sessionID);
                      sendPkt.setAddToList(false);// remove type
                         System.out.println("Reoved PKT rcvd");
                      new SenderThread(sendPkt).start();
                     }
                      mainThread=false;
                     
                    break;
            }//switch
            

                System.out.println("Rcv: type: "+rcvPkt.getType()+"  "+rcvPkt.getClientID()+" "+rcvPkt.getData1());

            } // while
        
        } catch (IOException | ClassNotFoundException ex) {
          //  System.out.println(ex);
        }

        System.out.println("STATUS: "+mainThread);
        count--;
        MainForm.logText.append("Active thread:"+count +" \n");
        if(mainThread==true){
            //send update to all online users
        HashMap <String,String> tempList = new HashMap<>();
        tempList.put(clientID,Queue.onlineList.get(clientID)); // get the offline user name be removed to online List
        
        Queue.removeSessionSocket(clientID+"-"+sessionID); 
        Queue.removeMainSocket(clientID);
        Queue.removeOnline(clientID);
        
        for(Map.Entry<String, Socket> mainSocketList : Queue.mainSocketList.entrySet()){
           if(!mainSocketList.getKey().equals(clientID)) { // if it is not the current user send update
               sendPkt=new Packet();
               sendPkt.setType((short)6);
               sendPkt.setOnlineList(tempList);
               sendPkt.setClientID(mainSocketList.getKey());
               sendPkt.setAddToList(false);
               MainForm.logText.append("Sent to "+mainSocketList.getKey()+" \n");
               new SenderThread(sendPkt).start();
              }
            }
        }else{
            Queue.removeSessionSocket(clientID+"-"+sessionID); 
        }
        System.out.println("Thread Closed");
    
    }
    
}
