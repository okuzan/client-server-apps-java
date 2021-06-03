package PW3.UDP;

import java.net.SocketException;

public class ServerTester {

    public static void main(String[] args) throws SocketException {
        ServerUDP server = new ServerUDP();
        server.start();
    }
}
