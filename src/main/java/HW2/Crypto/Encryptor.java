package HW2.Crypto;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import java.security.SecureRandom;
import java.util.Base64;

public class Encryptor {

    private static volatile Encryptor instance;
    private final Cipher cipher;
    private final CryptoCore crypto;
    private final SecretKey key;

    public Encryptor() {
        crypto = new CryptoCore();
        this.cipher = crypto.getCipher();
        this.key = crypto.getKey();
    }

    //Double Checked Locking idiom
    public static Encryptor getInstance() {
        Encryptor localInstance = instance;
        if (localInstance == null) {
            synchronized (Encryptor.class) {
                localInstance = instance;
                if (localInstance == null) {
                    instance = localInstance = new Encryptor();
                }
            }
        }
        return localInstance;
    }

    public String encrypt(String input) throws Exception {
        System.out.println(crypto);

        // generating IV
        byte[] iv = new byte[CryptoCore.getIvLen()];
        SecureRandom random = new SecureRandom();
        random.nextBytes(iv);
        IvParameterSpec ivSpec = new IvParameterSpec(iv);

        cipher.init(Cipher.ENCRYPT_MODE, key, ivSpec);
        byte[] cipherBytes = cipher.doFinal(input.getBytes());
        String cipherText = Base64.getEncoder().encodeToString(cipherBytes);
        String ivText = Base64.getEncoder().encodeToString(iv);
        return cipherText + ivText;
    }

}
