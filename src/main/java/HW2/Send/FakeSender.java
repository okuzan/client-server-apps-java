package HW2.Send;

import HW2.Packet.PacketRandomizer;
import PW1.Packet;

public class FakeSender implements ISend {

    @Override
    public void sendPacket() {
        Packet packet = PacketRandomizer.generate();
        System.out.println(packet);
    }
}
