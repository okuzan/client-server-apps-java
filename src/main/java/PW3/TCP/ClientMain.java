package PW3.TCP;

import HW2.Packet.Packet;
import HW2.Packet.PacketRandomizer;

import java.io.IOException;
import java.util.Random;

public class ClientMain {
    public static void main(String[] args) {
        for (int i = 0; i < 2; i++) client();
    }

    private static void client() {
        new Thread(() -> {
            try {
                Thread.sleep(new Random().nextInt(1000));
                ClientTCP client = new ClientTCP();
                Packet packet = PacketRandomizer.generate();
                client.send(packet);
                System.out.println(client.receive());
            } catch (InterruptedException | IOException e) {
                e.printStackTrace();
            }
        }).start();
    }
}
