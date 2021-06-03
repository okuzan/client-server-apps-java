package PW3.TCP;

import HW2.Packet.Packet;
import HW2.Packet.PacketFormat;
import PW3.UDP.ServerUDP;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;

public class ClientTCP {
    private static final int NUM_ATTEMPTS = 5;
    private Socket socket;
    private InputStream inputStream;
    private OutputStream outputStream;

    public ClientTCP() throws IOException {
        socket = null;
        try {
            socket = new Socket(InetAddress.getByName(null), ServerUDP.PORT);
            inputStream = socket.getInputStream();
            outputStream = socket.getOutputStream();

        } catch (Exception e) {
            System.out.println("Couldn't establish connection with the server");
        }
    }

    public void send(Packet packet) throws IOException, InterruptedException {
        if (serverAvailable()) {
            outputStream.write(Packet.encodePackage(packet));
            outputStream.flush();
        } else {
            System.out.println("Server is currently unavailable");
            for (int i = 0; i < NUM_ATTEMPTS; i++) {
                Thread.sleep(2000);
                System.out.printf("Attempt to send No %s\n", i + 1);
                if (serverAvailable()) {
                    System.out.println("Sent!");
                    outputStream.write(Packet.encodePackage(packet));
                    outputStream.flush();
                    return;
                } else {
                    System.out.println("Failed");
                }
            }
            System.out.println("Sending canceled");
        }
    }

    public Packet receive() throws IOException {
        byte[] msgBytes = new byte[PacketFormat.getMsgLen()];
        inputStream.read(msgBytes);
        Packet packet = Packet.decodePackage(msgBytes);
        System.out.println("RECEIVED");
        System.out.println(packet);
        return packet;
    }

    public static boolean serverAvailable() {
        try (Socket s = new Socket(InetAddress.getByName(null), ServerUDP.PORT)) {
            return true;
        } catch (IOException ignored) {
        }
        return false;
    }
}
