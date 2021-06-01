package HW2.Crypto;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import java.util.Base64;

public class Decryptor {
    private static volatile Decryptor instance;
    private final Cipher cipher;
    private final SecretKey key;

    public Decryptor() {
        CryptoCore crypto = new CryptoCore();
        this.cipher = crypto.getCipher();
        this.key = crypto.getKey();
    }

    //Double Checked Locking idiom
    public static Decryptor getInstance() {
        Decryptor localInstance = instance;
        if (localInstance == null) {
            synchronized (Decryptor.class) {
                localInstance = instance;
                if (localInstance == null) {
                    instance = localInstance = new Decryptor();
                }
            }
        }
        return localInstance;
    }

    public String decrypt(String msgEnc) throws Exception {

        //Getting ciphertext and IV from message
        String ivText = msgEnc.substring(msgEnc.length() - 24);
        String cipherText = msgEnc.substring(0, msgEnc.length() - 24);

        //converting them from string to byte array
        byte[] ivBytes = Base64.getDecoder().decode(ivText);
        byte[] cipherBytes = Base64.getDecoder().decode(cipherText);

        //decrypting
        IvParameterSpec ivSpec = new IvParameterSpec(ivBytes);
        cipher.init(Cipher.DECRYPT_MODE, key, ivSpec);
        byte[] plainText = cipher.doFinal(cipherBytes);
        //return as string
        return new String(plainText);

    }

}
