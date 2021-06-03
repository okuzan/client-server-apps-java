package PW3.TCP;

import java.io.IOException;

public class ServerMain {
    public static void main(String[] args) throws IOException {
        ServerTCP server = new ServerTCP();
        server.start();
    }
}
