package backend;

import DataStorage.User;

import java.io.*;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import javax.crypto.Cipher;

/**
 *
 * @author asuit
 */
public class Server {

    static InetAddress group = null;
    static MulticastSocket ms = null;
    static final int port = 2704;
    static private PrivateKey privateKey;
    static private PublicKey publicKey;

    public Server() throws NoSuchAlgorithmException, Exception {

        //setup encryption
        //using RSA
        KeyPairGenerator kg = KeyPairGenerator.getInstance("RSA");
        //key size of 1024
        kg.initialize(1024);
        KeyPair keyPair = kg.generateKeyPair();
        this.publicKey = keyPair.getPublic();
        this.privateKey = keyPair.getPrivate();

        Setup();

    }

    
    
    //Creates a multicast socket and
    //Connects the server to the multicast group
    public static void Setup() throws Exception {
        try {

            //create a multicast socket
            ms = new MulticastSocket(port);

            //create an inetaddress group to join
            //this will be who we send messages to on the network
            group = InetAddress.getByName("225.0.0.0");
            ms.joinGroup(group);

        } catch (IOException ex) {
            ex.printStackTrace();
            System.out.println(ex.getMessage());
        }
    }

    //Basic send method for any message
    //Encrypts and sends to all on multicast
    public static void send(byte[] outputMessage) throws Exception {

        try {
            //encrypt the message using RSA
            byte[] outMsg = encrypt(privateKey, outputMessage);

            DatagramPacket outgoingPacket
                    = new DatagramPacket(outMsg, outMsg.length, group, port);

            //send packets
            ms.send(outgoingPacket);

        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }

    }

    
    
    //Send updates to clients
    //takes ip to send to specific client
    public static void sendUpdate(InetAddress ip) throws Exception {
        try {
            
            Set messageSet = getAllMessages("users");
            MessagePacket[] messages = (MessagePacket[]) messageSet.toArray();
            
            
            UpdatePacket updMsg = new UpdatePacket(messages.length, messages);

            //encrypt the message using RSA
            byte[] updateMsg = encrypt(privateKey, updMsg.getSendMessage());

            DatagramPacket outgoingPacket
                    = new DatagramPacket(updateMsg, updateMsg.length, ip, port);

            //send packets
            ms.send(outgoingPacket);

        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }

    }
    //Send updates to all clients
    public static void sendUpdateAll() throws Exception {
        try {
            
            Set messageSet = getAllMessages("users");
            MessagePacket[] messages = (MessagePacket[]) messageSet.toArray();
            
            
            UpdatePacket updMsg = new UpdatePacket(messages.length, messages);

            //encrypt the message using RSA
            byte[] updateMsg = encrypt(privateKey, updMsg.getSendMessage());

            DatagramPacket outgoingPacket
                    = new DatagramPacket(updateMsg, updateMsg.length, group, port);

            //send packets
            ms.send(outgoingPacket);

        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }

    }
    
    //General recieve method
    //recieves on multicast then sends to processing
    public static DatagramPacket recieve() throws Exception {
        try {

            byte[] buf = new byte[64000];
            DatagramPacket incomingPacket
                    = new DatagramPacket(buf, buf.length);

            ms.receive(incomingPacket);

            //send to processing
            processPacket(incomingPacket);

            return incomingPacket;
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
        return null;
    }

    
    //checks opcode on packet and creates an instance of a specific packet
    //depending on what it is
    //If it is a Request we send updated messages to the client that requested it
    //If it is a Message we add it to the list of messages
    public static void processPacket(DatagramPacket p) throws Exception {
        //decrypt data using RSA
        byte[] incomingBytes = p.getData();
        incomingBytes = decrypt(publicKey, incomingBytes);

        //take the first two bytes of the incoming packet, which should be the opcode
        byte[] opcode = Arrays.copyOfRange(incomingBytes, 0, 1);

        if (opcode[0] == 0 && opcode[1] == 1) {
            //this is the opcode for
            //a requestpacket from client to server
            //
            //the rest of the message should be an ip

            try {
                byte[] clientIP = Arrays.copyOfRange(incomingBytes, 1, incomingBytes.length);

                InetAddress ip = InetAddress.getByAddress(clientIP);

                //after finding the ip create a RequestPacket so we can get info from it
                RequestPacket rp = new RequestPacket(ip);

                sendUpdate(rp.getIP());
                
            } catch (IOException ex) {
                System.out.println("Error in processing requestpacket");
                System.out.println(ex.getMessage());
            }

        } else if (opcode[0] == 0 && opcode[1] == 2) {
            //this is the opcode for
            //a messagepacket from client to other clients, processed by the server

            try {
                //how many bytes does the IP Store as?
                byte[] clientIP = Arrays.copyOfRange(incomingBytes, 1, 2);

                InetAddress ip = InetAddress.getByAddress(clientIP);

                //ID is 12 bytes long
                byte[] clientID = Arrays.copyOfRange(incomingBytes, 2, 14);
                String id = new String(clientID);

                byte[] clientMSG = Arrays.copyOfRange(incomingBytes, 14, incomingBytes.length - 23);

                //Time is 23 Bytes long
                byte[] clientTime = Arrays.copyOfRange(incomingBytes, incomingBytes.length - 23, incomingBytes.length);
                String stringTime = new String(clientTime);
                LocalDateTime time = LocalDateTime.parse(stringTime);

                //Create the message packet
                MessagePacket mp = new MessagePacket(id, clientMSG, time);
                
                //store message
                User u = new User(id);
                u.put(new String(clientMSG), id, stringTime);
                
                //update all users
                sendUpdateAll();
            } catch (IOException ex) {
                System.out.println("Error in processing messagepacket");
                System.out.println(ex.getMessage());
            }

        }

        //Do nothing if opcode is something else
    }

    //Encrypys a byte[] using RSA
    public static byte[] encrypt(PrivateKey privateKey, byte[] message) throws Exception {
        Cipher c = Cipher.getInstance("RSA");
        c.init(Cipher.ENCRYPT_MODE, privateKey);

        return c.doFinal(message);
    }
    
    //Decrypts a byte[] using RSA
    public static byte[] decrypt(PublicKey publicKey, byte[] encrypted) throws Exception {
        Cipher c = Cipher.getInstance("RSA");
        c.init(Cipher.DECRYPT_MODE, publicKey);

        return c.doFinal(encrypted);
    }

    public static Set getAllMessages(String pathname){
        Set messageList = new HashSet();
        File dir = new File(pathname);
        System.out.println(dir.toPath().toString());
        for(File f : dir.listFiles()){
            if(f.isFile()) {
                if(!f.toPath().toString().toLowerCase().contains(".ds_store")) {
                    System.out.println(f.toPath().toString());
                    byte[] temp = new byte[(int)f.length()];
                    try {
                        FileInputStream fis = new FileInputStream(f);
                        fis.read(temp);
                        messageList.add(new String(temp));
                    }catch(IOException e){
                        e.printStackTrace();
                    }
                }
            }
            else
                messageList.addAll(getAllMessages(f.toPath().toString()));
        }
        return messageList;
    }

}
