package HW2.Recieve;

import HW2.Packet.PacketRandomizer;
import PW1.Packet;

public class FakeReceiver implements IReceive {

    @Override
    public Packet receivePacket() {
        System.out.println("PACKER RECEIVED(GENERATED)");
        return PacketRandomizer.generate();
    }
}
