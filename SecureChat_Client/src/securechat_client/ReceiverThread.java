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
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

 

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
    private String sessionID;
    private static Socket fileClientSocket;
    private FileServices fileServices;


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
         
                case 1: // Authintication packet
                    System.out.println("Recvd");
                    if(rcvPkt.getData1().equals("VALID")){ 
                       // create a socekt for receiving files;
                       fileClientSocket= new Socket(Info.getServerIP(),Info.getServerPort());
                       new ReceiverThread(fileClientSocket).start();//recv thread for files;
                       sendPkt = new Packet();
                       sendPkt.setType((short)7);
                       sendPkt.setClientID(Info.getClientID());
                       sendPkt.setCreateFileSocket(true);
                       new SenderThread(sendPkt,fileClientSocket).start();// send the packet
                       Info.setFileClientSocket(fileClientSocket);
                        
                       SecureChat_Client.loginFomr1.setVisible(false);
                       MainForm.tempOnlineList=rcvPkt.getOnlineList();
                       new MainForm().setVisible(true);

                    } else { // invalid user or pass
                        LoginForm.errorLbl.setText("Invalid Username Or Password... Try Agin");
                        LoginForm.errorLbl.setVisible(true);
                    }
                    break;

                case 3: //rcvd SessionID packet
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
                            MainForm.tempOnlineList.put(entry.getKey(), entry.getValue());
                            MainForm.updateList();
                           ChatForm.updateOnlineList();
                        }else{ //remove user from online
                            MainForm.tempOnlineList.remove(entry.getKey());
                            MainForm.updateList();
                            ChatForm.updateOnlineList();
                        }
 
                    break;
                    
                     case 7:
                    System.out.println("7 recevd ... client");
                    fileServices = new FileServices();
                    fileServices.saveFile(rcvPkt);
                    JOptionPane.showMessageDialog(null, rcvPkt.getData1()+" file received from "
                        +MainForm.tempOnlineList.get(rcvPkt.getData2())+"\n Stored at c:\\Secure Chat client\\Received Files");
                    
                    break;
            }//switch

                System.out.println("RECEIVED : "+rcvPkt.getClientID()+" "+rcvPkt.getData1());

            } // while
        
        } catch (IOException | ClassNotFoundException ex) {
            System.out.println(ex);
        }
        
        try {
        //    inStream.close();
            clientSocket.close();
            System.out.println(" Closing socket"+ clientSocket);
        } catch (IOException ex) {
            Logger.getLogger(ReceiverThread.class.getName()).log(Level.SEVERE, null, ex);
        }

        System.out.println("Thread Closed");
 
    
    }
    
}
