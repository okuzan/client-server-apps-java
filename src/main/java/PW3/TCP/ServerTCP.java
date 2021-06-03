package PW3.TCP;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerTCP {
    private final ServerSocket serverSocket;
    private static final int MAX_THREADS = 20;
    private int usersConnected = 0;
    public static final int PORT = 8080;

    public ServerTCP() throws IOException {
        this.serverSocket = new ServerSocket(PORT);
    }

    public void close() throws IOException {
        serverSocket.close();
    }

    public void start() throws IOException {
        while(true){
            Socket clientSocket = serverSocket.accept();
            if(usersConnected < MAX_THREADS)
                new ClientHandler(clientSocket, usersConnected++).start();
            else
                System.out.println("LIMIT IS REACHED");
        }
    }
}
