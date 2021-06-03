package PW3.UDP;

import HW2.Packet.Packet;
import HW2.Packet.PacketRandomizer;

import java.io.IOException;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import static HW2.Packet.Packet.decodePackage;

public class ClientUDP {
    private final DatagramSocket socket;
    private final Queue<Packet> QUEUE = new ConcurrentLinkedQueue<>(); // to resend if sth goes wrong
    private final int clientID;
    private int msgCounter = 0;
    private long lastPacketId = 0;

    public ClientUDP(int clientID) throws SocketException {
        this.clientID = clientID;
        this.socket = new DatagramSocket();
        Packet packet = PacketRandomizer.generate();
        try {
            send(Packet.encodePackage(packet));
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    public void send(final byte[] bytes) throws UnknownHostException {
        Packet packet = new Packet((byte) 10, ++msgCounter, 10, clientID, bytes);
        QUEUE.add(packet);
        byte[] encoded = Packet.encodePackage(packet);
        DatagramPacket datagramPacket = new DatagramPacket(encoded, encoded.length, InetAddress.getByName(null), ServerUDP.PORT);
        try {
            socket.send(datagramPacket);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //resending lost packets
    private void process(Packet packet) throws UnknownHostException {
        long pId = packet.getPacketId();
        System.out.println("LAST PACKET ID " + lastPacketId);
        System.out.println("SERVER PACKET ID " + pId);
        if (pId > lastPacketId + 1) { //if less it's resending
            Packet responsePacket = new Packet((byte) 0, msgCounter++, 911, 0, (String.valueOf(lastPacketId)).getBytes(StandardCharsets.UTF_8));
            send(Packet.encodePackage(responsePacket));
            System.out.println("RESEND REQUESTED BY CLIENT");
        }
        else {
            lastPacketId = pId;
            if (packet.getCode() == 200) {
                System.out.println("SERVER RECEIVED PACKET, DELETING FROM RESERVE QUEUE...");
                QUEUE.removeIf(p -> p.getPacketId() == pId);
            }
            if (packet.getCode() == 911) { //means resend
                System.out.println("SERVER REQUESTED RESENDING");
                long packetIdToResend = Long.parseLong(new String(packet.getMsg()));
                for (int i = 0; i < QUEUE.size(); i++) {
                    for (Packet packet1 : QUEUE) {
                        if (packet1.getPacketId() == packetIdToResend + 1)
                            send(Packet.encodePackage(packet));
                    }
                }
            }

        }
    }

    public Packet receive() throws UnknownHostException {
        DatagramPacket datagramPacket = new DatagramPacket(new byte[1000], 1000);
        Packet packet = null;
        try {
            socket.receive(datagramPacket);
            packet = decodePackage(Arrays.copyOfRange(datagramPacket.getData(), 0, datagramPacket.getLength()));
            process(packet);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return packet;
    }
}
