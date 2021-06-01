package HW2.Send;

import HW2.Crypto.Encryptor;
import HW2.Packet.PacketFormat;
import PW1.CRC16;
import PW1.Packet;

import java.net.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class Sender {
    private final DatagramSocket socket;
    private final Encryptor encryptor;

    public Sender() throws SocketException {
        encryptor = new Encryptor();
        socket = new DatagramSocket();
    }

    private void foo() throws UnknownHostException {
        InetAddress IPAddress = InetAddress.getByName("localhost");
    }

    public void send(byte[] mess, int port, InetAddress target) throws InterruptedException, SocketException {
        DatagramPacket sendPacket =
                new DatagramPacket(mess, mess.length, target, 8000);
        try {
            socket.send(sendPacket);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public byte[] formPacketBytes(Packet packet) {
        byte[] res = null;
        try {
            byte[] message = packet.getMsg();
            String msgStr = new String(message);
            String encrypted = encryptor.encrypt(msgStr);
            byte[] msgEnc = encrypted.getBytes();
            byte[] head = ByteBuffer.allocate(14).order(ByteOrder.BIG_ENDIAN)
                    .put(PacketFormat.getMagicByte())
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

}
