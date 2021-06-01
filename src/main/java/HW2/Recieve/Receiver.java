package HW2.Recieve;

import HW2.Crypto.Decryptor;
import HW2.Packet.PacketFormat;
import HW2.Processor;
import HW2.Storage.Product;
import HW2.Storage.ProductGroup;
import HW2.Storage.Warehouse;
import PW1.CRC16;
import PW1.Packet;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.concurrent.LinkedBlockingQueue;

public class Receiver implements Runnable {
    private static boolean exit = false;
    private static int PORT = 8080;
    private final Decryptor decryptor;
    private final Processor processor;
    private final Socket socket;


    public Receiver(Socket accept, Processor processor) {
        decryptor = new Decryptor();
        this.processor = processor;
        this.socket = accept;
    }

    public static void main(String[] args) {
        Product p1 = new Product("med", 10, 130);
        Product p2 = new Product("hlib", 2, 15);
        ProductGroup group = new ProductGroup("harchi");
        group.addGoods(p1);
        group.addGoods(p2);
        LinkedBlockingQueue<ProductGroup> queue = new LinkedBlockingQueue<>();
        queue.add(group);
        Warehouse warehouse = new Warehouse(queue);
        Processor processor = new Processor(warehouse);

        //get quantity of med
        Packet pp1 = new Packet((byte) 1, 1, 0, 1, "med".getBytes(StandardCharsets.UTF_8));
        Packet pp2 = new Packet((byte) 1, 2, 1, 1, "med%4".getBytes(StandardCharsets.UTF_8));
        Packet pp3 = new Packet((byte) 1, 2, 1, 1, "med%3".getBytes(StandardCharsets.UTF_8));

        new Thread(new Server(processor)).start();
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

    @Override
    public void run() {
        try {
            InputStream inStream = socket.getInputStream();
            DataInputStream dis = new DataInputStream(inStream);
            OutputStream outStream = socket.getOutputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inStream));
            PrintWriter printWriter = new PrintWriter(outStream, true);

            while (inStream.read() != -1 && !exit) {
                int bLen = dis.readInt();
                byte[] theBytes = new byte[bLen];
                dis.readFully(theBytes);
                Packet packet = receive(theBytes);
                processor.process(packet);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            System.out.println("Disconnected");
        }

    }

    public static class Server implements Runnable {
        private final Processor processor;

        Server(Processor processor) {
            this.processor = processor;
        }

        @Override
        public void run() {
            try {
                ServerSocket server = new ServerSocket(PORT);
                while (!exit) new Thread(new Receiver(server.accept(), processor)).start();
            } catch (Exception e) {
            }
        }
    }
}
