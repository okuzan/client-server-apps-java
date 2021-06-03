package PW3.UDP;

import HW2.Packet.Packet;

import java.net.SocketAddress;

public class AddressedPacket {
    private final Packet packet;
    private final SocketAddress socketAddress;

    public AddressedPacket(Packet responsePacket, SocketAddress socketAddress) {
        this.packet = responsePacket;
        this.socketAddress = socketAddress;
    }

    public Packet getPacket() {
        return packet;
    }

    public SocketAddress getSocketAddress() {
        return socketAddress;
    }
}
