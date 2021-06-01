package HW2;

import HW2.Send.Sender;
import PW1.Packet;

import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class Client {
    private static final int PORT = 8080;
    private static Socket socket;
    private PrintWriter printWriter;
    private DataOutputStream dos;

    public Client() {
        connect();
    }

    public static void main(String[] args) {
        Client client = new Client();
    }

    public void connect() {
        try {
            socket = new Socket("localhost", PORT);
            OutputStream outStream = socket.getOutputStream();
            InputStream inStream = socket.getInputStream();
            dos = new DataOutputStream(outStream);
            printWriter = new PrintWriter(socket.getOutputStream(), true);
            Packet pp1 = new Packet((byte) 1, 1, 0, 1, "med".getBytes(StandardCharsets.UTF_8));
            Sender sender = new Sender();
            byte[] bytes = sender.formPacketBytes(pp1);
            printWriter.print(bytes);
//            new Thread(new Listener()).start();

        } catch (Exception err) {
        }
    }
}
