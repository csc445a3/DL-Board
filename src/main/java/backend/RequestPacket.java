/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package backend;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.InetAddress;


/**
 *
 * @author asuit
 */

//This packet is meant for the client
//and will allow them to ask for a refresh on messages from the server
//the server then can send an update directly to the client based on its ip



public class RequestPacket {
    //
    //OPCODES
    //REQUEST = 01
    //
    //Format
    //Message Packet
    //opcode + IP
    
    private InetAddress ip;
    private byte[] sendMsg;
    private byte[] ipBytes;
    
    
    public RequestPacket(InetAddress clientIP){
        
        ip = clientIP;
        ipBytes = ip.toString().getBytes();
        byte[] opCode = {0, 1};
        
        try{
            //concatenate byte arrays to create outgoing message
            //which will be in our format
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            os.write(opCode);
            os.write(ipBytes);
          
            //this will be the message we send out to the server
            //fully formatted
            sendMsg = os.toByteArray();
        
        }catch(IOException e){
            System.out.println(e.getMessage());
        }
        
    }
    
    public byte[] getSendMessage() {
        return this.sendMsg;
    }
    
    public InetAddress getIP() {
        return this.ip;
    }
  
}
