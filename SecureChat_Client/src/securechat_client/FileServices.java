/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package securechat_client;

import Messages.Packet;
import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

/**
 *
 * @author Mohammed Muayad
 */
public class FileServices {
    private String ClientID;
    private BufferedReader br;
    private BufferedWriter bw;
    private FileWriter fileWriter;
    private FileReader fileReader;
    private BufferedReader bReader;
    private  String Path;
    

    public FileServices() {
    }
    
    
 
    public void saveChat(Packet pkt){
    DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
    Date date = new Date();
   
    String fileDirectory="c:\\Secure Chat client\\History\\"+pkt.getClientID();
    String fileName=fileDirectory+pkt.getClientID()+"-"+dateFormat.format(date)+".txt";
    File file = new File(fileDirectory);
    
    //check if the directory is not exist;
    if(!file.exists()){
        file.mkdir();
    }
     // writer the file
        try {
            fileWriter=new FileWriter(fileName);
            fileWriter.write(pkt.getData1());
            fileWriter.close();
            
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(null, ex);
            Logger.getLogger(FileServices.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    
    public void getFile(String path){
        
    }
    
     public void saveFile(Packet pkt){
        FileOutputStream fos=null;
        Random rnd = new Random();
        try {
            String fileDirectory="c:\\Secure Chat client\\Received Files\\"+pkt.getClientID();
            String fileName=fileDirectory+"\\"+rnd.nextLong()+" "+pkt.getData1();
            File file = new File(fileDirectory);
           // check if the directory is not exist;
            if(!file.exists()){
                file.mkdirs();
            }      
            fos = new FileOutputStream (fileName);
            fos.write(pkt.getFile());
            fos.close();
        } catch (FileNotFoundException  ex) {
            JOptionPane.showMessageDialog(null, ex);
            Logger.getLogger(FileServices.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(null, ex);
            Logger.getLogger(FileServices.class.getName()).log(Level.SEVERE, null, ex);
        } 
        
    }
 
    public boolean getInfo() {
        try {
            String line;
            String fileDirectory="c:\\Secure Chat client\\";
            String fileName=fileDirectory+"info.txt";
            File file = new File(fileName);
            File dir = new File(fileDirectory);
            boolean chk1 = false,chk2 = false;
            //check if the directory is not exist;
            if(!file.exists()){
                JOptionPane.showMessageDialog(null, "info.txt not found. \nPlease create the file and include \n"
                        + "server port \n"
                        + "server IP\n"
                        + "File path: "+fileName);
                if(!dir.exists())
                    dir.mkdirs();
                System.exit(0);
                return false;
            }    fileReader=new FileReader(fileName);
            bReader = new BufferedReader(fileReader);
            while((line=bReader.readLine()) !=null){
                System.out.println(line);
                if(line.matches("\\d+")){
                    Info.setServerPort(Integer.parseInt(line));
                    chk1=true;
                }
                else if(line.matches("\\d+"+"."+"\\d+"+"."+"\\d+"+"."+"\\d+")){
                    Info.setServerIP(line);
                    chk2=true;
                }
            }
            bReader.close();
            if(!chk1 || !chk2 ){
                JOptionPane.showMessageDialog(null,"Wrong Server Port Format or IP Fomat. \n"
                        + "Please check "+fileName);
                System.exit(0);
            }
            return true;
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(null, ex);
            Logger.getLogger(FileServices.class.getName()).log(Level.SEVERE, null, ex);
        }
    return true;
}
}
