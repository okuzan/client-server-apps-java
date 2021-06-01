package HW2.Recieve;

import HW2.Crypto.Decryptor;
import HW2.Packet.PacketFormat;
import PW1.CRC16;
import PW1.Packet;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;

public class Receiver2 {
    private static boolean exit = false;
    private static int PORT = 8080;
    private final Decryptor decryptor;


    public Receiver2() {
        decryptor = new Decryptor();

    }

    public Packet receive(byte[] bytes) {

        ByteBuffer buffer = ByteBuffer.wrap(bytes).order(ByteOrder.BIG_ENDIAN);

        if (buffer.get() != PacketFormat.getMagicByte()) {
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
        //recreating head to check CRC
        byte[] head = ByteBuffer.allocate(14)
                .order(ByteOrder.BIG_ENDIAN)
                .put(PacketFormat.getMagicByte())
                .put(clientID)
                .putLong(packetID)
                .putInt(len)
                .array();

        //checking CRC
        if (CRC16.crc16(head) != crcHead) {
            throw new IllegalArgumentException("Lab1.CRC16 head validation failed!");
        }

        //retrieving useful part
        byte[] msgEnc = Arrays.copyOfRange(bytes, 24, 16 + len); // 16 + 4 + 4
        short crc16msg = buffer.getShort(16 + len);

        byte[] message = new byte[]{};
        try {
            String msgDecryptedStr = decryptor.decrypt(new String(msgEnc));
            message = msgDecryptedStr.getBytes();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (CRC16.crc16(msgEnc) != crc16msg) {
            throw new IllegalArgumentException("Lab1.CRC16 message validation failed!");
        }

        return new Packet(clientID, packetID, code, userId, message);
    }

}
