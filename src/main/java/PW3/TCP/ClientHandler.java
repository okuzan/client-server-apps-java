package PW3.TCP;

import HW2.Packet.Packet;
import HW2.Packet.PacketFormat;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class ClientHandler extends Thread {
    private final Socket socket;
    private final InputStream inputStream;
    private final OutputStream outputStream;

    public ClientHandler(Socket clientSocket, int usersConnected) throws IOException {
        this.socket = clientSocket;
        inputStream = socket.getInputStream();
        outputStream = socket.getOutputStream();
    }

    @Override
    public void run() {
        byte[] msgBytes = new byte[PacketFormat.getMsgLen()];
        try {
            int readBytes = inputStream.read(msgBytes);
            if (readBytes != -1) {
                System.out.println("Actually read: " + readBytes);
                Packet packet = Packet.decodePackage(Arrays.copyOfRange(msgBytes, 0, readBytes));
                System.out.println("RECEIVED");
                System.out.println(packet);
                Packet response = new Packet((byte) 10, 10, 10, 10, "accepted".getBytes(StandardCharsets.UTF_8));
                outputStream.write(Packet.encodePackage(response));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
