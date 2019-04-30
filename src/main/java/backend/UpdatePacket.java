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

//This packet is meant for the server
//and will allow it to send an updated list to the client



public class UpdatePacket {
    //
    //OPCODES
    //REQUEST = 03
    //
    //Format
    //Message Packet
    //opcode + ?
    
    private byte[] updateMessage;
    private byte[] sendMsg;

    public UpdatePacket(byte[] updateMessage) {

        byte[] opCode = {0, 3};

        try {
            //concatenate byte arrays to create outgoing message
            //which will be in our format
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            os.write(opCode);
            
            //os.write(ipBytes);

            //this will be the message we send out to the client
            //fully formatted
            sendMsg = os.toByteArray();

        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

    }

    public byte[] getSendMessage() {
        return this.sendMsg;
    }

 
}
