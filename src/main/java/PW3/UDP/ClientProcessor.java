package PW3.UDP;

import HW2.Packet.Packet;

import java.net.SocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class ClientProcessor extends Thread {
    private final Queue<Packet> queue = new ConcurrentLinkedQueue<>();
    private final int userID;
    private SocketAddress socketAddress;

    public ClientProcessor(final int userID) {
        super("Client processor" + userID);
        this.userID = userID;
        start();
    }

    public void acceptPacket(Packet packet, SocketAddress socketAddress) {
        System.out.println("ACCEPTED: " + packet);
        this.socketAddress = socketAddress;
        queue.add(packet);
    }

    @Override
    public void run() {
        while (true) {
            Packet packet = queue.poll();
            if (packet != null){
                System.out.format("[client %s] Processing packet %s\n", userID, packet.getPacketId());
                Packet responsePacket = new Packet((byte) 1, 10L, 19, 10, "accepted".getBytes(StandardCharsets.UTF_8));
                ServerQueue.QUEUE.add(new AddressedPacket(responsePacket, socketAddress));
            }
        }
    }
}
