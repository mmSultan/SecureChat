/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package securechat_client;

import Forms.ChatForm;
import java.net.*;
import java.io.*;
import Messages.Packet;
import Forms.LoginForm;
import Forms.MainForm;
import java.util.Map;

 

/**
 *
 * @author Mohammed Muayad
 */
public class ReceiverThread extends Thread {
    //private String clientID;
    private final Socket clientSocket;
    private ObjectInputStream inStream;
    private Packet rcvPkt;
    private Packet sendPkt;
    private String sessionID;


    public ReceiverThread(Socket clientSocket ) {
        this.clientSocket = clientSocket;
    }
    

    @Override
    public void run() {
        System.out.println("Thread Started.... Listenning");

        try {
            while (true){
            rcvPkt = new Packet();
            inStream = new ObjectInputStream(clientSocket.getInputStream());
            rcvPkt=(Packet) inStream.readObject();
            switch(rcvPkt.getType()){
         
                case 1: // Authintication
                    System.out.println("Recvd");
                    if(rcvPkt.getData1().equals("VALID")){ 
                       SecureChat_Client.loginFomr1.setVisible(false);
                       MainForm.tempList=rcvPkt.getOnlineList();
                       
                       new MainForm().setVisible(true);

                    } else { // invalid user or pass
                        LoginForm.errorLbl.setText("Invalid Username Or Password... Try Agin");
                        LoginForm.errorLbl.setVisible(true);
                    }
                    break;
                case 2: //DATA
                    
                    
                    break;
                case 3: //rcvd SessionID
                    sessionID=rcvPkt.getSessionID(); // get the sessionID

                    ChatForm newChat = new ChatForm();
                    newChat.setSessionID(sessionID);
                    newChat.setDestIDList(rcvPkt.getDestIDList());
                    newChat.visible(true);
                    
                    System.out.println("RECEIVED-3 : "+rcvPkt.getClientID()+" "+rcvPkt.getData1());
                    break;
                    
                    case 6: //rcvd updateOnline List Pkt.
                        Map.Entry<String, String> entry = rcvPkt.getOnlineList().entrySet().iterator().next();
                        if(rcvPkt.isAddToList()){ // add new user
                            MainForm.tempList.put(entry.getKey(), entry.getValue());
                            MainForm.updateList();
                           ChatForm.updateOnlineList();
                        }else{ //remove user from online
                            MainForm.tempList.remove(entry.getKey());
                            MainForm.updateList();
                            ChatForm.updateOnlineList();
                        }
 
                    break;
            }//switch

            
            
            
            
            
            
            
            
            
            
                System.out.println("RECEIVED : "+rcvPkt.getClientID()+" "+rcvPkt.getData1());

            } // while
        
        } catch (IOException | ClassNotFoundException ex) {
            System.out.println(ex);
        }
        
        System.out.println("Thread Closed");
 
    
    }
    
}
