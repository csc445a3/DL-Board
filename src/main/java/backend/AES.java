package backend;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;

public class AES {

    private static final String ENCRYPTION = "AES";
    private static final String CIPHER = "AES/ECB/PKCS5Padding";
    private static final int PASS_SIZE = 16;

    private SecretKeySpec secretKey;

    private void setKey(byte[] pass){
        secretKey = new SecretKeySpec(pass, ENCRYPTION);
    }

    /**
     * Ecnrypts a byte under backend.AES
     * @param toEncrypt the byte to be encrypted
     * @return encyrpted bytes
     * @throws Exception cipher init
     */
    public byte[] encrypt(byte[] toEncrypt, byte[] pass) throws Exception{

        ByteBuffer bb = ByteBuffer.allocate(16);
        bb.put(pass);
        setKey(bb.array());

        Cipher cipher = Cipher.getInstance(CIPHER);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        byte[] encrypted = cipher.doFinal(toEncrypt);
        return encrypted;

    }

    /**
     * Decryptes bytes under backend.AES
     * @param encrypted byte array of encrypted data
     * @return byte array of decrypted data
     * @throws Exception cipher
     */
    public byte[] decrypt(byte[] encrypted, byte[] pass) throws Exception{

        ByteBuffer bb = ByteBuffer.allocate(PASS_SIZE);
        bb.put(pass);
        setKey(pass);

        Cipher cipher = Cipher.getInstance(CIPHER);
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        byte[] decrypted = cipher.doFinal(encrypted);
        return decrypted;

    }


}
