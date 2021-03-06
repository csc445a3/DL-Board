package backend;

import java.nio.ByteBuffer;

public class MessagePacket {

    private static final int PACKET_SIZE = 128;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    private String id;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    private String message;

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    private String timeStamp;

    /**
     * Assigns local variables
     * @param id id of the user from GUI.
     * @param timeStamp timeStamp of the message String representation
     *                  of LocalDateTime object.
     * @param message message content from GUI.
     */
    public MessagePacket(String id, String timeStamp, String message){

        this.id = id;
        this.timeStamp = timeStamp;
        this.message = message;

    }

    /**
     * Creates a byte array based off the local variables of
     * id, timeStamp, and message.
     * @return returns a byte array for the client
     * to send or encrypt.
     */
    public byte[] createBytes(){

        //Create a buffer
        ByteBuffer buffer = ByteBuffer.allocate(PACKET_SIZE);

        //Fill the id
        byte[]idBytes = new byte[12];
        System.arraycopy(id.getBytes(), 0, idBytes, 0, id.length());

        //Fill the timeStamp
        byte[]time = new byte[24];
        System.arraycopy(timeStamp.getBytes(), 0, time, 0, timeStamp.length());

        //Fill the message
        buffer.put(idBytes);
        buffer.put(time);
        buffer.put(message.getBytes());

        return buffer.array();

    }


}
