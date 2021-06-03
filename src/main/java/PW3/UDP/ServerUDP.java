package PW3.UDP;

import HW2.Packet.Packet;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.Arrays;
import java.util.concurrent.ConcurrentHashMap;

import static HW2.Packet.Packet.decodePackage;

public class ServerUDP {
    public static final int PORT = 8080;
    private final DatagramSocket socket;
    private final ConcurrentHashMap<Integer, ClientProcessor> clientMap;

    public ServerUDP() throws SocketException {
        this.socket = new DatagramSocket(PORT);
        this.clientMap = new ConcurrentHashMap<>();

    }

    public void start() {
        new Thread(this::send, "Sender").start();
        new Thread(this::receive, "Receiver").start();
    }

    private void send() {
        while (true) {
            try {
                AddressedPacket packet = ServerQueue.QUEUE.poll();
                if (packet != null) {
                    byte[] packetBytes = Packet.encodePackage(packet.getPacket());
                    DatagramPacket datagramPacket = new DatagramPacket(packetBytes, packetBytes.length, packet.getSocketAddress());
                    socket.send(datagramPacket);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void receive() {
        while (true) {
            DatagramPacket datagramPacket = new DatagramPacket(new byte[1000], 1000);
            try {
                socket.receive(datagramPacket);
            } catch (IOException e) {
                e.printStackTrace();
            }
            Packet packet = decodePackage(Arrays.copyOfRange(datagramPacket.getData(), 0, datagramPacket.getLength()));
            clientMap.computeIfAbsent(packet.getUserId(), ClientProcessor::new)
                    .acceptPacket(packet, datagramPacket.getSocketAddress());
        }
    }
}
