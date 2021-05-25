package Lab1;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;

public class Packet {
    private static final byte MAGIC_BYTE = 0x13;
    private static final String ALGO = "AES/CBC/PKCS5Padding";
    private static final String keyStr = "WuJnnHyi6bRPjJ4RRaMBF7+nx7bxEXMICzVFF1glREw=";
    private static final int ivLen = 16;
    private final byte clientId;
    private final long packetId;
    private final int code;
    private final int userId;
    private final byte[] msg;

    public Packet(byte clientId, long packetId, int code, int userId, byte[] msg) {
        this.clientId = clientId;
        this.packetId = packetId;
        this.msg = msg;
        this.code = code;
        this.userId = userId;
    }

    public static String encrypt(String input) throws Exception {

        // generating IV
        byte[] iv = new byte[ivLen];
        SecureRandom random = new SecureRandom();
        random.nextBytes(iv);
        IvParameterSpec ivSpec = new IvParameterSpec(iv);

        //restoring the key
        byte[] encoded = Base64.getDecoder().decode(keyStr);
        SecretKey key = new SecretKeySpec(encoded, "AES");

        //encrypting
        Cipher cipher = Cipher.getInstance(ALGO);
        cipher.init(Cipher.ENCRYPT_MODE, key, ivSpec);

        byte[] cipherBytes = cipher.doFinal(input.getBytes());

        //sending (ciphertext + iv) as String
        String cipherText = Base64.getEncoder().encodeToString(cipherBytes);
        String ivText = Base64.getEncoder().encodeToString(iv);
        return cipherText + ivText;
    }

    public static String decrypt(String msgEnc) throws Exception {

        //Getting ciphertext and IV from message
        String ivText = msgEnc.substring(msgEnc.length() - 24);
        String cipherText = msgEnc.substring(0, msgEnc.length() - 24);

        //converting them from string to byte array
        byte[] ivBytes = Base64.getDecoder().decode(ivText);
        byte[] cipherBytes = Base64.getDecoder().decode(cipherText);

        //restoring the key
        byte[] encoded = Base64.getDecoder().decode(keyStr);
        SecretKey key = new SecretKeySpec(encoded, "AES");

        //decrypting
        IvParameterSpec ivSpec = new IvParameterSpec(ivBytes);
        Cipher cipher = Cipher.getInstance(ALGO);
        cipher.init(Cipher.DECRYPT_MODE, key, ivSpec);
        byte[] plainText = cipher.doFinal(cipherBytes);
        //return as string
        return new String(plainText);
    }

    static byte[] encodePackage(Packet packet) {
        byte[] res = null;
        try {
            byte[] message = packet.getMsg();
            String msgStr = new String(message);
            String encrypted = encrypt(msgStr);
            byte[] msgEnc = encrypted.getBytes();
            byte[] head = ByteBuffer.allocate(14).order(ByteOrder.BIG_ENDIAN)
                    .put(MAGIC_BYTE)
                    .put(packet.getClientId())
                    .putLong(packet.getPacketId())
                    .putInt(msgEnc.length + 8) // + 4 + 4
                    .array();

            res = ByteBuffer.allocate(16 + msgEnc.length + 10) // + 4 + 4 + 2
                    .order(ByteOrder.BIG_ENDIAN)
                    .put(head)
                    .putShort(CRC16.crc16(head))
                    .putInt(packet.getCode())
                    .putInt(packet.getUserId())
                    .put(msgEnc)
                    .putShort(CRC16.crc16(msgEnc))
                    .array();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }

    static Packet decodePackage(byte[] bytes) {
        ByteBuffer buffer = ByteBuffer.wrap(bytes).order(ByteOrder.BIG_ENDIAN);
        if (buffer.get() != MAGIC_BYTE) {
            throw new IllegalArgumentException();
        }
        byte clientID = buffer.get();
        long packetID = buffer.getLong();
        int len = buffer.getInt();
        short crcHead = buffer.getShort();
        int code = buffer.getInt();
        int userId = buffer.getInt();

        System.out.println("client:  " + clientID);
        System.out.println("packetID:  " + packetID);
        System.out.println("Length:  " + len);
        System.out.println("Lab1.CRC16 (head):  " + crcHead);

        byte[] head = ByteBuffer.allocate(14)
                .order(ByteOrder.BIG_ENDIAN)
                .put(MAGIC_BYTE)
                .put(clientID)
                .putLong(packetID)
                .putInt(len)
                .array();

        if (CRC16.crc16(head) != crcHead) {
            throw new IllegalArgumentException("Lab1.CRC16 head validation failed!");
        }

        byte[] msgEnc = Arrays.copyOfRange(bytes, 24, 16 + len); // 16 + 4 + 4
        short crc16msg = buffer.getShort(16 + len);

        byte[] message = new byte[]{};
        try {
            String msgDecrypedStr = decrypt(new String(msgEnc));
            message = msgDecrypedStr.getBytes();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (CRC16.crc16(msgEnc) != crc16msg) {
            throw new IllegalArgumentException("Lab1.CRC16 message validation failed!");
        }

        return new Packet(clientID, packetID, code, userId, message);
    }

    public int getCode() {
        return code;
    }

    public int getUserId() {
        return userId;
    }

    public byte getClientId() {
        return clientId;
    }

    public long getPacketId() {
        return packetId;
    }

    public byte[] getMsg() {
        return msg;
    }

    public String getMsg3() {
//        return this.msg;
        return "";
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
