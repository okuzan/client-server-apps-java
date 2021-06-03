package PW3.UDP;

import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.Random;

public class ClientTester {
    public static void main(String[] args) {
        for (int i = 0; i < 20; i++) client(i);
    }

    private static void client(int id) {
        new Thread(() -> {
            try {
                Thread.sleep(new Random().nextInt(1000));
                ClientUDP client = new ClientUDP(id);
                client.send(("Hello world" + id).getBytes(StandardCharsets.UTF_8));
                System.out.println(client.receive());
            } catch (InterruptedException | SocketException | UnknownHostException e) {
                e.printStackTrace();
            }
        }).start();
    }
}
