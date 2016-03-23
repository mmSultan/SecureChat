/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package securechar_server;

import java.io.*;
import java.net.*;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import Messages.Packet;

/**
 *
 * @author Mohammed Muayad
 */
public class Client {
    
//    public static void main(String[] args) throws InterruptedException  {
//       ObjectOutputStream out;
//        Socket sock = null ;
//        try {
//            int a=0;
//            sock = new Socket("127.0.0.1",10000);
//            
//           // new ReceiverThread (sock).run();
//            while(true){
//                System.out.println("START");
//            out=new ObjectOutputStream(sock.getOutputStream());
//            Packet pkt = new Packet();
//            pkt.setClientID("A11");
//            pkt.setDestID("A11");
//            pkt.setData1("HIII "+a);
//            out.writeObject(pkt);
//            out.flush();
//            a++;
//            if (a==5)
//                break;
//            TimeUnit.SECONDS.sleep(1);
//            }
//            
//            
//        } catch (IOException ex) {
//            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
//        }
//        try {
//            sock.close();
//        } catch (IOException ex) {
//            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
//        }
//       
//   }
    
}
