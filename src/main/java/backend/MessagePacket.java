package backend;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.time.LocalDateTime;
import java.util.Arrays;

//This packet is meant for the client and server
//it will hold the foramtting for message packets
//and allow information to be accessed easily
public class MessagePacket {
    //
    //OPCODES
    //MESSAGE = 02
    //
    //Format
    //Message Packet
    //opcode + ID + Time + Message

    public String userID;
    public String messageString;
    public String sendMsgString;
    public LocalDateTime time;
    public byte[] messageBytes;
    public byte[] idBytes = new byte[12];
    public byte[] timeBytes = new byte[23];
    public byte[] temp;
    public byte[] sendMsg = new byte[1280];

    public MessagePacket(byte [] id, byte[] msg, String t) {
        
        //idBytes = Arrays.copyOfRange(userID., 0, 0);

        messageBytes = msg;
        messageString = new String(messageBytes);

        timeBytes = t.getBytes();
        byte [] opcode = {0,2};
        ByteBuffer bb = ByteBuffer.allocate(1280);
        bb.put(opcode);
        System.arraycopy(id, 0, idBytes, 0, id.length);//Arrays.copyOfRange(id.getBytes(), 2, 14);
        bb.put(idBytes);
        bb.put(timeBytes);
       // msg.trim();
        bb.put(msg);
        

//        try {
            //concatenate byte arrays to create outgoing message
            //which will be in our format
//            ByteArrayOutputStream os = new ByteArrayOutputStream();
//            os.write(opCode);
//            os.write(idBytes);
//            os.write(timeBytes);
//            os.write(messageBytes);
//            

            //this will be the message we send out
            //fully formatted
      //      temp = os.toByteArray();
            
            sendMsg = bb.array();
            
            
            sendMsgString = new String(sendMsg);

//        } catch (IOException e) {
//            System.out.println(e.getMessage());
//        }

    }

    public byte[] getOriginalMsgBytes() {
        return this.messageBytes;
    }

    public byte[] getSendMessage() {
        return this.sendMsg;
    }

    public String getID() {
        return this.userID;
    }

    public String getMsgString() {
        return this.messageString;
    }

    public int getMsgLength() {
        return messageString.length();
    }

    public int getSendMsgLength() {
        return sendMsgString.length();
    }

}
