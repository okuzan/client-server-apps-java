package PW3.UDP;

import HW2.Packet.Packet;

public interface Server {
    void send(Packet packet);

    Packet receive();

}
