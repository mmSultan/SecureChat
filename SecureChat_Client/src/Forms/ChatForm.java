
package Forms;

import Messages.Packet;
import java.awt.Color;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JTextPane;
import javax.swing.text.*;
import securechat_client.FileServices;
import securechat_client.Info;
import securechat_client.ReceiverThread;
import securechat_client.SenderThread;
import static java.lang.Thread.sleep;
import javax.swing.AbstractAction;
import javax.swing.Action;

/**
 *
 * @author Mohammed Muayad
 */
public class ChatForm extends javax.swing.JFrame {

    private Socket clientSocket;
    private static Socket fileClientSocket;
    private Packet sendPkt;
    private String clientID;
    private String sessionID;
    private ReceiverThread r1;
    private List <String> destIDList= new ArrayList<>();
    private List <String> tempChatList;
    private FileServices fileServices;
    private localReceiverTH  localRecverThread;
    private ObjectInputStream inStream;

    public void setDestIDList(List<String> destIDList) {
        this.destIDList = destIDList;
       
    }
     public void removeDestId(String ID){// remoce a certain ID from the list
        for(int i=0 ;i<destIDList.size();i++){
            if(destIDList.get(i).equals(ID)){
                destIDList.remove(i);
                return;
            }
        }
    }

    private void updateChatList(){
        tempChatList= new ArrayList<>();
         for (String id :destIDList){
            tempChatList.add(MainForm.tempOnlineList.get(id));
        }
         chatList.setListData(tempChatList.toArray());
    }

    public void setSessionID(String SessionID) {
        this.sessionID = SessionID;
        
    }
    
    
    public static void updateOnlineList(){
        try{
        onlineList.setModel(MainForm.onlineList.getModel());
        }
        catch(Throwable ex){}
    }
    
    
    // Function to add text with diffrent colors and styles to chatText2
    private void appendToPane(JTextPane tp, String msg, Color c)
    {
        StyleContext sc = StyleContext.getDefaultStyleContext();
        AttributeSet aset = sc.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Foreground, c);

        aset = sc.addAttribute(aset, StyleConstants.FontFamily, "Lucida Console");
        aset = sc.addAttribute(aset, StyleConstants.Alignment, StyleConstants.ALIGN_JUSTIFIED);

        int len = tp.getDocument().getLength();
        tp.setCaretPosition(len);
        tp.setCharacterAttributes(aset, false);
        tp.replaceSelection(msg);
    }
    
    public class localReceiverTH extends Thread{
        @Override
     public void run() {
     Packet rcvPkt;
        System.out.println(Info.getClientID()+" Local Thread Sarted \n");
        try {
            while(true){
            inStream= new ObjectInputStream(clientSocket.getInputStream());
            rcvPkt=(Packet) inStream.readObject();
            
            switch (rcvPkt.getType()){
                
                case 2: //DATA packet
                    appendToPane(chatText2,MainForm.tempOnlineList.get(rcvPkt.getData2())+":"+
                            rcvPkt.getData1()+"\n",Color.black);
                    
                    break;
                case 5:
                     String addedOrRemovedUser=rcvPkt.getData1();
                     String inviterOrRemover=rcvPkt.getData2();
                    if(rcvPkt.isAddToList()){ // true means add to list
                     destIDList.add(rcvPkt.getData1());// add new user
                     updateChatList();
                     appendToPane(chatText2,MainForm.tempOnlineList.get(addedOrRemovedUser)+" has been invited by..."+inviterOrRemover+"\n",Color.blue);
                     System.out.println(Info.getClientID()+" Local rcvd pkt-5");
                    }else {//false means remove from list
                        if(!rcvPkt.getData1().equals(Info.getClientID())){// removed user is not me
                            removeDestId(rcvPkt.getData1()); // remove it  from chat list
                            updateChatList();// update it       
                            appendToPane(chatText2,MainForm.tempOnlineList.get(addedOrRemovedUser)+" has been removed by..."+inviterOrRemover+"\n",Color.red);

                        }else{ // removed user is  me
                            destIDList.clear();
                            updateChatList();
                            appendToPane(chatText2,"You have been removed from this session by..."+rcvPkt.getData2()+"\n",Color.red);
                        }
                    }
                    break;
                    
                case 7:
                    System.out.println("7 recevd ... client");
                    fileServices = new FileServices();
                    fileServices.saveFile(rcvPkt);
                    appendToPane(chatText2,rcvPkt.getData1()+" file received from "+MainForm.tempOnlineList.get(rcvPkt.getData2())+"\n"
                            + "Path:C:\\Secure Chat client\\...",Color.blue);
                    
                    break;
                
            }//switch

            } // while
        } catch (IOException | ClassNotFoundException  ex ) {
            Logger.getLogger(ChatForm.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
           // inStream.close();
            clientSocket.close();
           System.out.println("Closing socket "+ clientSocket);
        } catch (IOException ex) {
            Logger.getLogger(ReceiverThread.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//run
    }
    
  
    
    public void visible(boolean status){
       
        try {
            this.setTitle(Info.getClientID());
            clientSocket=new Socket(Info.getServerIP(),Info.getServerPort()); // start NEW SOCKET connection
            fileClientSocket=Info.getFileClientSocket();
            sendPkt=new Packet();
            sendPkt.setType((short)4); // create newChat packet
            sendPkt.setClientID(Info.getClientID());
            sendPkt.setSessionID(sessionID);
            updateChatList(); // update chat list
            clientID=Info.getClientID(); //get client Id from info class
            
            localRecverThread = new localReceiverTH ();
            localRecverThread.start();
 
  
            new SenderThread(sendPkt,clientSocket).start(); // send newChat Pkt
            this.setVisible(status);
            
        } catch (IOException ex) {
            Logger.getLogger(ChatForm.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//visible
    
    private boolean hasUser(String id){
        for(String entry : tempChatList ){
            if (entry.equals(id))
                return true;
        }
        return false;
    }
  
    public ChatForm() {
        initComponents();
        this.setLocationRelativeTo(null);
        this.getRootPane().setDefaultButton(sendBtn);
    }

       Action action = new AbstractAction()
{
    @Override
    public void actionPerformed(ActionEvent e)
    {
        System.out.println("some action");
    }
}; 
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jFileChooser1 = new javax.swing.JFileChooser();
        jDialog1 = new javax.swing.JDialog();
        jFileChooser2 = new javax.swing.JFileChooser();
        jScrollPane1 = new javax.swing.JScrollPane();
        chatList = new javax.swing.JList();
        jScrollPane2 = new javax.swing.JScrollPane();
        onlineList = new javax.swing.JList();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        sendBtn = new javax.swing.JButton();
        addBtn = new javax.swing.JButton();
        jScrollPane4 = new javax.swing.JScrollPane();
        sendText = new javax.swing.JTextArea();
        removBtn = new javax.swing.JButton();
        chatText = new javax.swing.JScrollPane();
        chatText2 = new javax.swing.JTextPane();
        sendFile = new javax.swing.JButton();

        javax.swing.GroupLayout jDialog1Layout = new javax.swing.GroupLayout(jDialog1.getContentPane());
        jDialog1.getContentPane().setLayout(jDialog1Layout);
        jDialog1Layout.setHorizontalGroup(
            jDialog1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        jDialog1Layout.setVerticalGroup(
            jDialog1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setLocation(new java.awt.Point(200, 200));
        setMaximumSize(new java.awt.Dimension(1200, 800));
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        chatList.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        chatList.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                chatListValueChanged(evt);
            }
        });
        jScrollPane1.setViewportView(chatList);

        onlineList.setModel(MainForm.onlineList.getModel());
        onlineList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        onlineList.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        onlineList.addContainerListener(new java.awt.event.ContainerAdapter() {
            public void componentAdded(java.awt.event.ContainerEvent evt) {
                onlineListComponentAdded(evt);
            }
        });
        onlineList.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                onlineListValueChanged(evt);
            }
        });
        jScrollPane2.setViewportView(onlineList);

        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("CHAT WITH");

        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setText("ONLINE USERS");

        sendBtn.setText("Send");
        sendBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                sendBtnActionPerformed(evt);
            }
        });

        addBtn.setText("Add To Chat");
        addBtn.setEnabled(false);
        addBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addBtnActionPerformed(evt);
            }
        });

        sendText.setColumns(20);
        sendText.setRows(5);
        sendText.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                sendTextKeyReleased(evt);
            }
        });
        jScrollPane4.setViewportView(sendText);

        removBtn.setText("Remove User");
        removBtn.setEnabled(false);
        removBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removBtnActionPerformed(evt);
            }
        });

        chatText2.setDoubleBuffered(true);
        chatText.setViewportView(chatText2);

        sendFile.setText("Send File");
        sendFile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                sendFileActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 427, Short.MAX_VALUE)
                    .addComponent(chatText, javax.swing.GroupLayout.PREFERRED_SIZE, 419, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(sendBtn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 18, Short.MAX_VALUE)
                        .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(sendFile, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(addBtn, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                            .addComponent(removBtn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addComponent(jLabel1)
                .addGap(5, 5, 5)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(chatText)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(removBtn)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 139, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(addBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(sendBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(sendFile, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    
    // sending text
    private void sendBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_sendBtnActionPerformed
     if(sendText.getText().length()!=0){
     sendPkt = new Packet();
     sendPkt.setType((short)2); // pkt type text
     sendPkt.setClientID(clientID); // current ID
     sendPkt.setSessionID(sessionID); // session Id for this chat, 
     sendPkt.setDestIDList(destIDList); // include all the chatList
     sendPkt.setData1(sendText.getText());// get the text
     sendPkt.setData2(clientID);
     new SenderThread(sendPkt,clientSocket).start(); // send it
     appendToPane(chatText2,"Me:"+sendText.getText()+"\n",Color.black);
     sendText.setText("");//clear it
     }
      //JOptionPane.showMessageDialog(null, "OK");
    }//GEN-LAST:event_sendBtnActionPerformed

    // closing event 
    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        try {
             //sending update chatList pkt to remove current user. with history
            if(destIDList.size()>0){
            sendPkt=new Packet();
            sendPkt.setType((short)5);
            sendPkt.setAddToList(false);
            sendPkt.setClientID(clientID);
            sendPkt.setDestIDList(destIDList);
            sendPkt.setData1(clientID);
            sendPkt.setData2(chatText2.getText());// history
            sendPkt.setSessionID(sessionID);
            sendPkt.setCreateFileSocket(false);
            new SenderThread(sendPkt,clientSocket).start();
            System.out.println("closing session pkt is sent");
            this.setVisible(false);
         //   sleep(2000);// sleep for 2 second to wait for sending thread finish before close the socket
            // before closing the socket
            
                        // sending the conversation to be archived by the server;
            sendPkt= new Packet();
            sendPkt.setType((short)8);// sending chat
            sendPkt.setClientID(clientID);
            sendPkt.setData2(chatText2.getText());
            sendPkt.setSessionID(sessionID);
            System.out.println("sending history pkt");
            new SenderThread(sendPkt,clientSocket).start();
            this.setVisible(false);
            sleep(2000);// sleep for 2 second to wait for sending thread finish before close the socket
            
            }else{// in case that the chatlist is empty so just send the history 
            // sending the conversation to be archived by the server;
            sendPkt= new Packet();
            sendPkt.setType((short)8);// sending chat
            sendPkt.setClientID(clientID);
            sendPkt.setData2(chatText2.getText());
            sendPkt.setSessionID(sessionID);
            System.out.println("sending history pkt");
            new SenderThread(sendPkt,clientSocket).start();
            this.setVisible(false);
            sleep(2000);// sleep for 2 second to wait for sending thread finish before close the socket

            }
            
            //inStream.close();
            clientSocket.close();
            localRecverThread.interrupt();

           
            
        } catch (IOException | InterruptedException ex) {
            Logger.getLogger(ChatForm.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_formWindowClosing

    private void addBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addBtnActionPerformed
       String invitedID = MainForm.getclientID((String) onlineList.getSelectedValue());//get the clientID
        if (!hasUser((String) onlineList.getSelectedValue())){
            destIDList.add(invitedID);// add it to current chat List
            updateChatList();
            sendPkt= new Packet();
            sendPkt.setType((short)5);
            sendPkt.setAddToList(true);
            sendPkt.setClientID(Info.getClientID());
            sendPkt.setData1(invitedID); // InvitedID user
            sendPkt.setDestIDList(destIDList);
            sendPkt.setSessionID(sessionID);
            appendToPane(chatText2,"You have invited "+onlineList.getSelectedValue()+"\n",Color.blue);
            new SenderThread(sendPkt,clientSocket).start(); // send the packet
        }
        else{
            JOptionPane.showMessageDialog(null, onlineList.getSelectedValue()+" Already invited to this session " );
        }
            
    }//GEN-LAST:event_addBtnActionPerformed

   
    private void onlineListValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_onlineListValueChanged
        // TODO add your handling code here:
        if(onlineList.getSelectedIndex()!=-1)
        addBtn.setEnabled(true);
        else
             addBtn.setEnabled(false);
        
//        ListModel tempOnlineList = onlineList.getModel();
//        boolean isOnline;
//        //Loop through all users in chat list    
//        System.out.println("Checking online");
//        for (int i=0 ;i<tempChatList.size();i++){
//             isOnline=false;
//            //Loop through online list to check whether the use is online or not
//            for(int j=0;j<tempOnlineList.getSize();j++){
//                if(tempChatList.get(i).equals(tempOnlineList.getElementAt(j))){
//                    System.out.println("found user");
//                    isOnline=true;
//                    break;
//                }//if
//            }//for j           
//                if(!isOnline){
//                   appendToPane(chatText2,tempChatList.get(i)+" has left chat",Color.red);
//                   tempChatList.remove(i);
//                   updateChatList();
//                }//if
//        }//for i
            
    }//GEN-LAST:event_onlineListValueChanged

    private void removBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removBtnActionPerformed
        String removeID = MainForm.getclientID((String) chatList.getSelectedValue());//get the clientID
        
        sendPkt= new Packet();
        sendPkt.setType((short)5);
        sendPkt.setAddToList(false); // set it as remove from list
        sendPkt.setClientID(Info.getClientID());
        sendPkt.setData1(removeID); // InvitedID user
        sendPkt.setDestIDList(destIDList);
        sendPkt.setSessionID(sessionID);
        new SenderThread(sendPkt,clientSocket).start(); // send the packet
        appendToPane(chatText2,"You have removed "+chatList.getSelectedValue()+"\n",Color.red);
        removeDestId(removeID);// remove from current chat list
        updateChatList();
    }//GEN-LAST:event_removBtnActionPerformed

    private void chatListValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_chatListValueChanged
        if(chatList.getSelectedIndex()!=-1){
            removBtn.setEnabled(true);
        }else{
             removBtn.setEnabled(false);
        }
    }//GEN-LAST:event_chatListValueChanged

    private void onlineListComponentAdded(java.awt.event.ContainerEvent evt) {//GEN-FIRST:event_onlineListComponentAdded
        // TODO add your handling code here:sout    
        System.out.println("Change happend online list");
    }//GEN-LAST:event_onlineListComponentAdded

    private void sendFileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_sendFileActionPerformed
         final JFileChooser fc = new JFileChooser();
         FileInputStream inStream;
         File file ;
         byte[] fileArray;
         int chk;
         double fileSize;
         
           
         try{
                chk =fc.showOpenDialog(sendFile);        //show dailog 
                if(chk== JFileChooser.APPROVE_OPTION ){ // if chose a file 
                    file= fc.getSelectedFile(); // get the file
             
                    fileSize=file.length() / 1048576 ; // in MB
                   if(fileSize <= 5){
                    fileArray = new byte[(int)file.length()]; // initialize the array
                    inStream = new FileInputStream(file);
                    inStream.read(fileArray);// assign the file into array;
                    
                    sendPkt= new Packet();
                    sendPkt.setType((short)7); // file transfare
                    sendPkt.setClientID(clientID); // current ID
                    sendPkt.setSessionID(sessionID); // session Id for this chat, 
                    sendPkt.setDestIDList(destIDList); // include all the chatList
                    sendPkt.setFile(fileArray);
                    sendPkt.setData1(file.getName()); // put the file name
                    sendPkt.setData2(clientID);
                    new SenderThread(sendPkt,fileClientSocket).start(); // send it
                    appendToPane(chatText2,file.getName()+" File sent\n",Color.blue);
                   }else{
                       JOptionPane.showMessageDialog(null, "The maximum file size allowed is 5MB. \n"
                               + "Your file size is: "+fileSize+" MB.");
                   }
                }// if
           }catch(HeadlessException | IOException ex){
               Logger.getLogger(ChatForm.class.getName()).log(Level.SEVERE, null, ex);
           }
               
        
    }//GEN-LAST:event_sendFileActionPerformed

    private void sendTextKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_sendTextKeyReleased
        // TODO add your handling code here:
        ActionEvent e = null;  
        if (evt.getKeyCode()==KeyEvent.VK_ENTER){
           sendBtnActionPerformed(e);
          }
    }//GEN-LAST:event_sendTextKeyReleased

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(ChatForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(ChatForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(ChatForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ChatForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new ChatForm().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addBtn;
    private javax.swing.JList chatList;
    private javax.swing.JScrollPane chatText;
    private javax.swing.JTextPane chatText2;
    private javax.swing.JDialog jDialog1;
    private javax.swing.JFileChooser jFileChooser1;
    private javax.swing.JFileChooser jFileChooser2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane4;
    private static javax.swing.JList onlineList;
    private javax.swing.JButton removBtn;
    private javax.swing.JButton sendBtn;
    private javax.swing.JButton sendFile;
    private javax.swing.JTextArea sendText;
    // End of variables declaration//GEN-END:variables
}
