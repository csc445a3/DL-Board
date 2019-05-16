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
        String delimString = "cs*2/4$52c445a3";
        int numdelim = messageSize;
        ByteBuffer bb = ByteBuffer.allocate(4+opCode.length+ 1280*messageSize+numdelim*delimString.getBytes().length);

        bb.put(opCode);
        bb.put(size);
        byte[] delimeter = delimString.getBytes();
   
        for(MessagePacket m: messages){
         
        bb.put(delimeter);
        bb.put(m.getSendMessage());
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
