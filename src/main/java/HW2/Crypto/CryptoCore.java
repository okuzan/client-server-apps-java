package HW2.Crypto;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

public class CryptoCore {
    private static final String ALGO = "AES";
    private static final String TRANSFORMATION = "AES/CBC/PKCS5Padding";
    private static final String keyStr = "WuJnnHyi6bRPjJ4RRaMBF7+nx7bxEXMICzVFF1glREw=";
    private static final int ivLen = 16;
    private final Cipher cipher;
    private SecretKey key;

    public CryptoCore() {
        this.cipher = createCipher();
    }


    public static int getIvLen() {
        return ivLen;
    }

    private Cipher createCipher() {
        //restoring the key
        byte[] encoded = Base64.getDecoder().decode(keyStr);
        key = new SecretKeySpec(encoded, ALGO);
        try {
            return Cipher.getInstance(TRANSFORMATION);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Cipher getCipher() {
        return cipher;
    }

    public SecretKey getKey() {
        return key;
    }

    public String generatorKey() throws NoSuchAlgorithmException {
        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        SecureRandom secureRandom = new SecureRandom();
        int keyBitSize = 256;
        keyGenerator.init(keyBitSize, secureRandom);
        SecretKey key = keyGenerator.generateKey();
        System.out.println(Base64.getEncoder().encodeToString(key.getEncoded()));
        return Base64.getEncoder().encodeToString(key.getEncoded());
    }

}
