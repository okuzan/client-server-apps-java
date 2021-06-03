package PW3.UDP;

import HW2.Packet.Packet;

import java.io.IOException;
import java.net.*;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import static HW2.Packet.Packet.decodePackage;

public class ClientUDP {
    private final DatagramSocket socket;
    private final Queue<Packet> QUEUE = new ConcurrentLinkedQueue<>(); // to resend if sth goes wrong
    private final int clientID;

    public ClientUDP(int clientID) throws SocketException {
        this.clientID = clientID;
        this.socket = new DatagramSocket();
    }

    public void send(final byte[] bytes) throws UnknownHostException {
        Packet packet = new Packet((byte) 10, 10, 10, clientID, bytes);
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
        if (packet.getCode() == 911) { //means resend
            int packetIdToResend = Integer.parseInt(new String(packet.getMsg()));
            for (int i = 0; i < QUEUE.size(); i++) {
                for (Packet packet1 : QUEUE) {
                    if (packet1.getPacketId() == packetIdToResend)
                        send(Packet.encodePackage(packet));
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

    public boolean isConnectionAvailable() {
        return false;
    }
}
