package network.configuration;
import network.handler.ReceivedMessageHandler;

import java.rmi.RemoteException;

public class Server implements DataHandlerInterface {
    public Server() throws RemoteException {
        super();

    }
    public static DataHandlerInterface remoteObject;
    private final ReceivedMessageHandler receivedMessageHandler = new ReceivedMessageHandler();

    /**
     * Message received from the client
     */
    @Override
    public void sendMessage(String message) {
        receivedMessageHandler.callService(message);
    }

}
