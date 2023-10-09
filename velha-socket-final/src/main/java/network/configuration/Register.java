package network.configuration;

import view.MainView;

import javax.swing.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.RMIClientSocketFactory;
import java.rmi.server.RMIServerSocketFactory;
import java.rmi.server.UnicastRemoteObject;
import java.util.Properties;

public class Register {

    private static Integer port;
    private static String clientIPAddress;
    private static Integer clientPort;
    public static void main(String[] args) {

        setIPAddressAndPort();
        try {
            Server server = new Server();

            // Wraps the object so the client can call the remote method
            DataHandlerInterface stub = (DataHandlerInterface) UnicastRemoteObject.exportObject(server, 0);

            // Create RMI registry
            Registry registry = LocateRegistry.createRegistry(port);

            // Bind the remote object's stub in the registry
            registry.rebind("Server", stub);

            System.out.println("Server ready");

            // Looks for the client
            while (Server.remoteObject == null) {
                try {
                    Server.remoteObject = (DataHandlerInterface) Naming.lookup("//"+ clientIPAddress + ":" + clientPort + "/Client");
                    System.out.println("Object localized!");
                } catch(Exception e){
                    System.err.println("Trying to connect with the Client/Server");
                    Thread.sleep(2000);
                }
            }

            // Start the UI
            SwingUtilities.invokeLater(() -> {
                try {
                    MainView.getInstance().setUpFrame();
                } catch (UnsupportedLookAndFeelException | ClassNotFoundException | InstantiationException |
                         IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            });
        } catch (Exception e) {
            System.err.println("Server exception: " + e);
            e.printStackTrace();
        }
    }

    private static void setIPAddressAndPort() {
        Properties properties = new Properties();
        try (FileInputStream configFile = new FileInputStream("velha-socket-final/src/main/resources/config.properties")) {
            properties.load(configFile);
        } catch (IOException e) {
            System.err.println("Error reading the configuration file: " + e.getMessage());
            System.exit(1);
        }
        port = Integer.parseInt(properties.getProperty("port"));
        clientIPAddress = properties.getProperty("client-ip");
        clientPort = Integer.parseInt(properties.getProperty("client-port"));
    }
}