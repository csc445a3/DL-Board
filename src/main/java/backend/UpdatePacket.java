package backend;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.nio.ByteBuffer;


//This packet is meant for the server
//and will allow it to send an updated list to the client



public class UpdatePacket {
    //
    //OPCODES
    //REQUEST = 03
    //
    //Format
    //Message Packet
    //opcode + size + message
    
    private byte[] updateMessage;
    private byte[] sendMsg;
    private String sendMsgString;

    public UpdatePacket(int messageSize, MessagePacket[] messages) {
        
        byte[] size = ByteBuffer.allocate(4).putInt(messageSize).array();
        byte[] opCode = {0, 3};
        String delimString = "csc445a3";
        byte[] delimeter = delimString.getBytes();
        //concatenate byte arrays to create outgoing message
        //which will be in our format
        System.arraycopy(opCode, 0, sendMsg, 0, opCode.length);
        System.arraycopy(size, 0, sendMsg, sendMsg.length, size.length);
        
        for(MessagePacket m: messages){
            //add a delimeter before each message
            System.arraycopy(delimeter, 0, sendMsg, sendMsg.length, delimeter.length);
            System.arraycopy(m.getSendMessage(), 0, sendMsg, sendMsg.length, messages.length);
        }
        
        sendMsgString = new String(sendMsg);
            
       

    }

    public byte[] getSendMessage() {
        return this.sendMsg;
    }
    
    public int getSendMsgLength() {
        return sendMsgString.length();
    }
 
}
