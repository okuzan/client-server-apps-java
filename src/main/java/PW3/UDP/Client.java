package PW3.UDP;

import HW2.Packet.Packet;

import java.net.UnknownHostException;

public interface Client {
    void send(Packet packet) throws UnknownHostException;

    Packet receive() throws UnknownHostException;

    boolean isConnectionAvailable();

}
