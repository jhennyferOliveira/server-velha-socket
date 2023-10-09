package network.configuration;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface DataHandlerInterface extends Remote {
    void sendMessage(String message) throws RemoteException;

}
