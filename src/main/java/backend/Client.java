package backend;

import backend.MessagePacket;
import backend.RequestPacket;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.security.*;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import javax.crypto.Cipher;
import java.util.stream.Collectors;
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author asuit
 */
public class Client {

    static InetAddress group = null;
    static MulticastSocket ms = null;
    static final int port = 2704;
    static String id;
    static InetAddress ip = null;
    static private PrivateKey privateKey;
    static private PublicKey publicKey;
    static private ArrayList<MessagePacket> messages;

    public Client() throws NoSuchAlgorithmException {
        try {
            //assign a random name
            String format = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
            StringBuilder sb = new StringBuilder();
            Random r = new Random();
            while (sb.length() < 12) { // length of the random string
                int index = (int) (r.nextFloat() * format.length());
                sb.append(format.charAt(index));
            }

            //set ip and id
            ip = InetAddress.getLocalHost();
            id = sb.toString();

            //setup encryption
            //using RSA
            KeyPairGenerator kg = KeyPairGenerator.getInstance("RSA");
            //key size of 1024
            kg.initialize(1024);
            KeyPair keyPair = kg.generateKeyPair();
            this.publicKey = keyPair.getPublic();
            this.privateKey = keyPair.getPrivate();

            //start connection
            Setup();

        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }

    public static void Setup() {
        try {

            //create a multicast socket
            ms = new MulticastSocket(port);

            //create an inetaddress group to join
            //this will be who we send messages to on the network
            group = InetAddress.getByName("225.0.0.0");
            ms.joinGroup(group);




        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }

    public static void send(byte[] outputMessage) throws Exception {

        try {
            //encrypt the message using RSA
            byte[] outMsg = encrypt(privateKey, outputMessage);

            DatagramPacket outgoingPacket
                    = new DatagramPacket(outputMessage, outputMessage.length, group, port);

            //send packets
            ms.send(outgoingPacket);

        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }

    }

    public static void sendMessage(byte[] outputMessage) throws Exception {

        try {
            LocalDateTime now = LocalDateTime.now(ZoneId.of("America/New_York"));

            MessagePacket msg = new MessagePacket(id, outputMessage, now);

            //Encrypt this message using RSA
            byte[] sendMsg = encrypt(privateKey, msg.getSendMessage());

            //send formatted message (SendMessage)
            DatagramPacket outgoingPacket
                    = new DatagramPacket(sendMsg, sendMsg.length, group, port);

            //send packets
            ms.send(outgoingPacket);

        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }

    }

    public static void sendRequest() throws Exception {

        try {

            RequestPacket msg = new RequestPacket(ip);

            //Encrypt this request using RSA
            byte[] sendMsg = encrypt(privateKey, msg.getSendMessage());

            DatagramPacket outgoingPacket
                    = new DatagramPacket(msg.getSendMessage(), msg.getSendMsgLength(), group, port);

            //send packets
            ms.send(outgoingPacket);

        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }

    }

    public static void recieve() throws Exception {
        try {

            byte[] buf = new byte[64000];
            DatagramPacket incomingPacket
                    = new DatagramPacket(buf, buf.length);

            ms.receive(incomingPacket);
            processPacket(incomingPacket);
            //decrypt message
//            byte[] recievedMessage = decrypt(publicKey, incomingPacket.getData());
//
//            return recievedMessage;

        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }

    }
    public static void processPacket(DatagramPacket p) throws Exception {
        //decrypt data using RSA
        byte[] incomingBytes = p.getData();
        incomingBytes = decrypt(publicKey, incomingBytes);

        //take the first two bytes of the incoming packet, which should be the opcode
        byte[] opcode = Arrays.copyOfRange(incomingBytes, 0, 1);

        if (opcode[0] == 0 && opcode[1] == 1) {
                //do nothing , request to server

        } else if (opcode[0] == 0 && opcode[1] == 2) {
           helper(incomingBytes);
        }
        else if(opcode[0] == 0 && opcode[1] == 3)
        {
          // test  UpdatePacket up = (UpdatePacket)incomingBytes;
            //todo deal with update


            byte [] lengthOfArray = Arrays.copyOfRange(incomingBytes,0,3);
            int len = ByteBuffer.wrap(lengthOfArray).getInt();
            String delimString = "cs*2/4$52c445a3";
            byte[] delimeter = delimString.getBytes();
//            byte [] allMessages = Arrays.copyOfRange(incomingBytes,x,incomingBytes.length-n);
//            convert that to a string:
//        split on split thing:
//            for(byte [] messagePacket : splitList){
//                helperMethod(messagePacket);
//            }
            byte [] messagecluster = Arrays.copyOfRange(incomingBytes,14,incomingBytes.length);
            String [] split = (new String(messagecluster)).split(delimString);
            for(String s: split)
            {
                byte [] ineffecient = s.getBytes();
                helper(ineffecient);
            }



        }

        //Do nothing if opcode is something else
        //probably should never be anything else
    }

    public static void helper(byte []mp)
    {
        byte[] clientID = Arrays.copyOfRange(mp, 2, 14);
        String id = new String(clientID);

        byte[] clientMSG = Arrays.copyOfRange(mp, 14, mp.length - 23);

        //Time is 23 Bytes long
        byte[] clientTime = Arrays.copyOfRange(mp, mp.length - 23, mp.length);
        String stringTime = new String(clientTime);
        LocalDateTime time = LocalDateTime.parse(stringTime);

        //Create the message packet
        MessagePacket p = new MessagePacket(id, clientMSG, time);
        specialAdd(p);


    }
public static void specialAdd(MessagePacket mp)
{
    messages.add(mp);
    messages.stream().distinct().sorted(Comparator.comparing(M->M.time)).collect(Collectors.toList());
    if(messages.size() > 5000)
    {// if there are more than 5000 messages in working set, just dump and keep 500 newest ones...
        List<MessagePacket> messages2 = messages.subList(0,500);
        messages.clear();
        messages2.stream().forEach(s->messages.add(s));

    }
}


//    public static void recieveUpdate() {
//        boolean recieving = true;
//        ArrayList<MessagePacket> recievedMessages = new ArrayList<>();
//
//        while (recieving) {
//            try {
//                ms.setSoTimeout(10000);
//                //add messages to an arraylist
//               // uncomment this  recievedMessages.add(recieve());
//
//
//
//
//                  /todo someone else tell me if this is needed, pretty sure its not but ill leave it here
//
//
//
//            } catch (SocketException e) {
//                //havent recieved any new updates
//                recieving = false;
//            } catch (Exception ex) {
//                System.out.println(ex.getMessage());
//            }
//
//        }
//
////        for (byte[] b : recievedMessages) {
////            String rMsgString = new String(b);
////            messages.add(rMsgString);
////        }
//
//    }

    public PrivateKey getPrivateKey() {
        return privateKey;
    }

    public PublicKey getPublicKey() {
        return publicKey;
    }

    public static byte[] encrypt(PrivateKey privateKey, byte[] message) throws Exception {
        Cipher c = Cipher.getInstance("RSA");
        c.init(Cipher.ENCRYPT_MODE, privateKey);

        return c.doFinal(message);
    }

    public static byte[] decrypt(PublicKey publicKey, byte[] encrypted) throws Exception {
        Cipher c = Cipher.getInstance("RSA");
        c.init(Cipher.DECRYPT_MODE, publicKey);

        return c.doFinal(encrypted);
    }

}