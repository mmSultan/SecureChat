/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package securechat_client;

import java.io.*;
import java.net.*;
import Messages.Packet;
import java.util.logging.Level;
import java.util.logging.Logger;
/**
 *
 * @author Mohammed Muayad
 */
public class SenderThread extends Thread {

    private Socket clientSocekt;
    private ObjectOutputStream outStream;
    private Packet sendPacket;


    public SenderThread(Packet sendPacket , Socket clientSocekt) {
        this.sendPacket = sendPacket;
        this.clientSocekt= clientSocekt;
    }
    
    
    @Override
    public void run()  {
        switch (sendPacket.getType()){
            
            case 1: // Auth Packet
            try {
         
                outStream= new ObjectOutputStream (clientSocekt.getOutputStream());
                outStream.writeObject(sendPacket);
                System.out.println("AUTH Sent");
                } catch (IOException ex) {
                    Logger.getLogger(SenderThread.class.getName()).log(Level.SEVERE, null, ex);
                }
                break;
                
            case 2: // Data pkt
//                try {
//                     List <String> destIDList=sendPacket.getDestIDList();
//                     for(String clientID : destIDList ){
//                        outStream= new ObjectOutputStream (clientSocekt.getOutputStream());
//                        outStream.writeObject(sendPacket);
//                     } //for
//                } catch (Throwable ex) {
//                  //  Logger.getLogger(SenderThread.class.getName()).log(Level.SEVERE, null, ex);
//                    System.out.println("Sending faild ");
//                }
            break;
            case 3 :
            try {
                // new connection
                outStream= new ObjectOutputStream (clientSocekt.getOutputStream());
                outStream.writeObject(sendPacket);
                System.out.println("Request SessionID Pkt Sent");
                
            } catch (IOException ex) {
                Logger.getLogger(SenderThread.class.getName()).log(Level.SEVERE, null, ex);
            } 
                break;
                
            case 4 :
            try {
                // 
                outStream= new ObjectOutputStream (clientSocekt.getOutputStream());
                outStream.writeObject(sendPacket);
                System.out.println("NewChat Pkt Sent");
                
            } catch (IOException ex) {
                Logger.getLogger(SenderThread.class.getName()).log(Level.SEVERE, null, ex);
            } 
                break;
                
            case 5 :
            try {
                // update chat list
                outStream= new ObjectOutputStream (clientSocekt.getOutputStream());
                outStream.writeObject(sendPacket);
                System.out.println("NewChat Pkt Sent");
                
            } catch (IOException ex) {
                Logger.getLogger(SenderThread.class.getName()).log(Level.SEVERE, null, ex);
            } 
                break;
                
                
            
    }  // switch 
       
    }
    
}
