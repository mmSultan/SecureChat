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

    private final Socket clientSocekt;
    private ObjectOutputStream outStream;
    private final Packet sendPacket;


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
                
            case 2: // text pkt
                try{
                outStream= new ObjectOutputStream (clientSocekt.getOutputStream());
                outStream.writeObject(sendPacket);
                System.out.println("text Pkt Sent");
                } catch (IOException ex) {
                Logger.getLogger(SenderThread.class.getName()).log(Level.SEVERE, null, ex);
            } 
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
                System.out.println("pkt 5 not send");
                Logger.getLogger(SenderThread.class.getName()).log(Level.SEVERE, null, ex);
            } 
                break;
                
            case 7 : // send file
            try {
                outStream= new ObjectOutputStream (clientSocekt.getOutputStream());
                outStream.writeObject(sendPacket);
                System.out.println("7 Pkt Sent");
                
            } catch (IOException ex) {
                System.out.println("pkt 7 not send");
                Logger.getLogger(SenderThread.class.getName()).log(Level.SEVERE, null, ex);
            } 
                break;
                
            case 8 :
            try {
                // update chat list
                outStream= new ObjectOutputStream (clientSocekt.getOutputStream());
                outStream.writeObject(sendPacket);
                System.out.println("8 pkt Pkt Sent");
                
            } catch (IOException ex) {
                System.out.println("pkt 8 not send");
                Logger.getLogger(SenderThread.class.getName()).log(Level.SEVERE, null, ex);
            } 
                break;
                
             case 9 :
            try {
                // update chat list
                outStream= new ObjectOutputStream (clientSocekt.getOutputStream());
                outStream.writeObject(sendPacket);
                System.out.println("9 pkt Pkt Sent");
                
            } catch (IOException ex) {
                System.out.println("pkt 9 not send");
                Logger.getLogger(SenderThread.class.getName()).log(Level.SEVERE, null, ex);
            } 
                break;
               
    }  // switch 

       
    }// run
    
 
    
}// class
