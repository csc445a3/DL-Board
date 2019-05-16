package backend;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;

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
    //opcode + ID + Message + Time

    public String userID;
    public String messageString;
    public String sendMsgString;
    public LocalDateTime time;
    public byte[] messageBytes;
    public byte[] idBytes;
    public byte[] timeBytes;
    public byte[] sendMsg;

    public MessagePacket(String id, byte[] msg, LocalDateTime t) {
        userID = id;
        idBytes = userID.getBytes();

        messageBytes = msg;
        messageString = new String(messageBytes);

        time = t;
        timeBytes = time.toString().getBytes();

        byte[] opCode = {0, 2};

        try {
            //concatenate byte arrays to create outgoing message
            //which will be in our format
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            os.write(opCode);
            os.write(idBytes);
            os.write(messageBytes);
            os.write(timeBytes);

            //this will be the message we send out
            //fully formatted
            sendMsg = os.toByteArray();

            sendMsgString = new String(sendMsg);

        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

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
