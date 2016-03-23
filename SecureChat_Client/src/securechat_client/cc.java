/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package securechat_client;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import Messages.Packet;


/**
 *
 * @author Mohammed Muayad
 */
public class cc {

    public cc ( ) throws InterruptedException  {
       ObjectOutputStream out;
        Socket sock = null ;
        try {
            int a=0;
            sock = new Socket("127.0.0.1",10000);
   
             new ReceiverThread (sock).start();
            
            while(true){
                System.out.println("Sending....");
            out=new ObjectOutputStream(sock.getOutputStream());
            Packet pkt = new Packet();
            pkt.setClientID("A1");
         //   pkt.setDestID("A2");
            pkt.setData1("HI From A1 "+a);

            out.writeObject(pkt);
            out.flush();
            a++;
            if (a==300)
                break;
            TimeUnit.SECONDS.sleep(1);
            }

        } catch (IOException ex) {
            Logger.getLogger(cc.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            sock.close();
        } catch (IOException ex) {
            Logger.getLogger(cc.class.getName()).log(Level.SEVERE, null, ex);
        }
       
   }
    
}
