/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Forms;

import Messages.Packet;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import securechat_client.Info;
import securechat_client.ReceiverThread;
import securechat_client.SenderThread;

/**
 *
 * @author Mohammed Muayad
 */
public class ChatForm extends javax.swing.JFrame {

    private Socket clientSocket;
    private Packet sendPkt;
    private String clientID;
    private String sessionID;
    private ReceiverThread r1;
    private List <String> destIDList= new ArrayList<>();
    private List <String> tempChatList;

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
            tempChatList.add(MainForm.tempList.get(id));
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
    
    // update online list in each chat window
    private Thread updateOnlineList = new Thread(new Runnable() {
        @Override
        public void run() {
         while(true){  
             try {
                 onlineList.setModel(MainForm.onlineList.getModel());
                 Thread.sleep(3000);
             } catch (InterruptedException ex) {
                 Logger.getLogger(ChatForm.class.getName()).log(Level.SEVERE, null, ex);
             }
         }
        }
    
    });
    
    // locla receiving thread
    private Thread localReceiver = new Thread(new Runnable() { // Receiver thread to get packet related to current chat
    @Override
    public void run() {
     ObjectInputStream inStream;
     Packet rcvPkt;
        System.out.println(Info.getClientID()+" Local Thread Sarted");
        try {
            while(true){
            inStream= new ObjectInputStream(clientSocket.getInputStream());
            rcvPkt=(Packet) inStream.readObject();
            
            switch (rcvPkt.getType()){
                case 5:
                    if(rcvPkt.isAddToList()){ // true means add to list
                     destIDList.add(rcvPkt.getData1());// add new user
                     updateChatList();
                     System.out.println(Info.getClientID()+" Local rcvd pkt-5");
                    }else {//false means remove from list
                        if(!rcvPkt.getData1().equals(Info.getClientID())){// removed user is not me
                            removeDestId(rcvPkt.getData1()); // remove it  from chat list
                            updateChatList();// update it
                        }else{ // removed user is  me
                            chatText.append("I should be closed");
                           
                            
                            dispose();
                        }
                    }
                    break;
                
            }//switch

            } // while
        } catch (Throwable  ex ) {
           // Logger.getLogger(ChatForm.class.getName()).log(Level.SEVERE, null, ex);
        }
     
         
    }
    });  
           
    
    public void visible(boolean status){
       
        try {
            this.setTitle(Info.getClientID());
            clientSocket=new Socket(Info.getServerIP(),Info.getServerPort()); // start NEW SOCKET connection
            sendPkt=new Packet();
            sendPkt.setType((short)4); // create newChat packet
            sendPkt.setClientID(Info.getClientID());
            sendPkt.setSessionID(sessionID);
            updateChatList(); // update chat list
            
            
            
            localReceiver.start();
          //  updateOnlineList.start();
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
    }

    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        chatList = new javax.swing.JList();
        jScrollPane2 = new javax.swing.JScrollPane();
        onlineList = new javax.swing.JList();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        chatText = new javax.swing.JTextArea();
        sendBtn = new javax.swing.JButton();
        addBtn = new javax.swing.JButton();
        jScrollPane4 = new javax.swing.JScrollPane();
        sendText = new javax.swing.JTextArea();
        removBtn = new javax.swing.JButton();

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
        jScrollPane1.setViewportView(chatList);

        onlineList.setModel(MainForm.onlineList.getModel());
        onlineList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        onlineList.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
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

        chatText.setColumns(20);
        chatText.setRows(5);
        chatText.setMaximumSize(new java.awt.Dimension(1200, 800));
        jScrollPane3.setViewportView(chatText);

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
        jScrollPane4.setViewportView(sendText);

        removBtn.setText("Remove User");
        removBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removBtnActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 427, Short.MAX_VALUE)
                    .addComponent(jScrollPane3)
                    .addComponent(sendBtn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(addBtn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(removBtn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jScrollPane3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addGap(5, 5, 5)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 114, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(removBtn)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 152, Short.MAX_VALUE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(sendBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(addBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void sendBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_sendBtnActionPerformed
    
    }//GEN-LAST:event_sendBtnActionPerformed

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        try {
            // TODO add your handling code here:
            System.out.println("CLOSING socket");
            localReceiver.interrupt();
            
            clientSocket.close();
        } catch (IOException ex) {
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
            for(String s :destIDList){
                System.out.println("DESTLIST: "+s);
            }
            new SenderThread(sendPkt,clientSocket).start(); // send the packet
        }
        else
           System.out.println("User Already Exist");  
    }//GEN-LAST:event_addBtnActionPerformed

    private void onlineListValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_onlineListValueChanged
        // TODO add your handling code here:
        if(onlineList.getSelectedIndex()!=-1)
        addBtn.setEnabled(true);
        else
             addBtn.setEnabled(false);
        System.out.println("Change!!!!!");
            
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
        
        removeDestId(removeID);// remove from current chat list
        updateChatList();
    }//GEN-LAST:event_removBtnActionPerformed

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
    private javax.swing.JTextArea chatText;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private static javax.swing.JList onlineList;
    private javax.swing.JButton removBtn;
    private javax.swing.JButton sendBtn;
    private javax.swing.JTextArea sendText;
    // End of variables declaration//GEN-END:variables
}
