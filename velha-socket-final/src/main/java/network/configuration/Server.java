package network.configuration;

import network.handler.DataFlowHandler;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server extends Thread {
    private final DataFlowHandler dataFlowHandler = DataFlowHandler.getInstance();
    private Socket socket;

    public void serverSocketConfiguration() {
        try {
            int port = 9090;
            try (ServerSocket serverSocket = new ServerSocket(port)) {
                System.out.println("Aguardando conexão...");
                socket = serverSocket.accept();
                System.out.println("Conexão Estabelecida.");
            }
            var outputStream = new DataOutputStream(socket.getOutputStream());
            this.start();
            dataFlowHandler.sendMessage(outputStream);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void run() {
        DataInputStream inputStream;
        try {
            inputStream = new DataInputStream(socket.getInputStream());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        dataFlowHandler.receiveMessage(inputStream);
    }
}
