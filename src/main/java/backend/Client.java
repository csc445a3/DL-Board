package backend;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.nio.ByteBuffer;
import java.time.LocalDateTime;
import java.util.HashMap;

public class Client {

    private static final int PORT = 2710;
    private static final int PACKET_SIZE = 128;
    private static final int ENCRYPTED_SIZE = 144;


    MulticastSocket socket;
    InetAddress group;
    HashMap<String, MessagePacket> messages = new HashMap<String, MessagePacket>();
    AES aes;
    String password;


    public Client(String password) {
        this.password = password;

        try {
            aes = new AES();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void connect(String address) throws IOException {

        group = InetAddress.getByName(address);
        socket = new MulticastSocket(PORT);
        socket.setTimeToLive(25);
        socket.joinGroup(group);

    }

    public void close() throws IOException {
        socket.leaveGroup(group);
        socket.close();
    }

    public void send(String id, String message) throws Exception {

        LocalDateTime now = LocalDateTime.now();
        String timeStamp = now.toString();
        MessagePacket messagePacket = new MessagePacket(id, timeStamp, message);
        byte[] encyrptedData = aes.encrypt(messagePacket.createBytes(), password.getBytes());
        DatagramPacket outPacket = new DatagramPacket(encyrptedData, encyrptedData.length, group, PORT);


        socket.send(outPacket);

    }

    public MessagePacket receive()throws Exception {

        //For encryption you need to add 16 and then fill to the original packet size
        DatagramPacket inPacket = new DatagramPacket(new byte[ENCRYPTED_SIZE], ENCRYPTED_SIZE);
        socket.receive(inPacket);
        byte[] data = new byte[PACKET_SIZE];
        ByteBuffer bb = ByteBuffer.wrap(aes.decrypt(inPacket.getData(), password.getBytes()));
        bb.get(data);

        byte[] id = new byte[12];
        System.arraycopy(data, 0, id, 0, 12);
        byte[] timeStamp = new byte[24];
        System.arraycopy(data, 12, timeStamp, 0, 24);
        byte[] message = new byte[92];
        System.arraycopy(data, 36, message, 0, 92);

        MessagePacket mp = new MessagePacket(new String(id), new String(timeStamp), new String(message));

        //If the key doesn't exist in the hashmap, add it and return the mp
        if (!messages.containsKey(mp.getTimeStamp())) {
            messages.put(mp.getTimeStamp(), mp);
            return mp;
        }
        return new MessagePacket("","","");

    }

}
