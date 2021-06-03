package PW3.UDP;

import HW2.Packet.Packet;

import java.net.SocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class ClientProcessor extends Thread {
    private final Queue<Packet> queue = new ConcurrentLinkedQueue<>();
    private final int userID;
    private long lastPackedID = 0;
    private long msgCounter = 0;
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
            if (packet != null) {
                System.out.format("[client %s] Processing packet %s\n", userID, packet.getPacketId());
                Packet responsePacket;
                //checking if no losses
                System.out.println("THIS PACKET ID " + packet.getPacketId());
                System.out.println("LAST PACKET ID " + lastPackedID);
                if (packet.getPacketId() != lastPackedID + 1) {
                    responsePacket = new Packet((byte) 0, ++msgCounter, 911, 0, (String.valueOf(lastPackedID)).getBytes(StandardCharsets.UTF_8));
                    System.out.println("RESEND REQUESTED");
                    ServerQueue.QUEUE.add(new AddressedPacket(responsePacket, socketAddress));
                } else {
                    responsePacket = new Packet((byte) 0, ++msgCounter, 200, 0, String.valueOf(packet.getPacketId()).getBytes(StandardCharsets.UTF_8));
                    lastPackedID = packet.getPacketId();
                    System.out.println("SUCCESS REPORTED");
                    ServerQueue.QUEUE.add(new AddressedPacket(responsePacket, socketAddress));

                    //resending if needed
                    if (packet.getCode() == 911) for (int i = 0; i < queue.size(); i++)
                        for (Packet packet1 : queue)
                            if (packet1.getPacketId() == Long.parseLong(new String(packet.getMsg())) + 1) {
                                ServerQueue.QUEUE.add(new AddressedPacket(packet1, socketAddress));
                                System.out.println("SERVER ADDED LOST MSG TO ITS QUEUE");
                            }
                }
            }
        }
    }
}
