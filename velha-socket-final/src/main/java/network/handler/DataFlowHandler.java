package network.handler;

import java.io.DataInputStream;
import java.io.DataOutputStream;

/**
 * Handles receiving and sending data
 */
@SuppressWarnings("InfiniteLoopStatement")
public class DataFlowHandler {

    private final ReceivedMessageHandler receivedMessageHandler = new ReceivedMessageHandler();

    public static DataFlowHandler dataFlowHandler;
    private String messageToSend;

    private final Object lock = new Object();

    public static synchronized DataFlowHandler getInstance() {
        if (dataFlowHandler == null) {
            dataFlowHandler = new DataFlowHandler();
        }
        return dataFlowHandler;
    }

    /**
     * Set message to send to the client, this function will unlock the thread that is waiting for this value if the value is different from null
     */
    public void setMessageToSend(String message) {
        synchronized (lock) {
            this.messageToSend = message;
            lock.notifyAll();
        }
    }

    /**
     * Get message that needs to be sent to the client, this will lock the thread waiting for this value until it's different from null.
     */
    public String getMessageToSend() {
        synchronized (lock) {
            while (messageToSend == null) {
                try {
                    lock.wait();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
            return messageToSend;
        }
    }


    /**
     * Transform the message in a outputStream and send it to the client
     */
    public void sendMessage(DataOutputStream outputStream) {
        try {
            while (true) {
                outputStream.writeUTF(getMessageToSend());
                outputStream.flush();
                setMessageToSend(null);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Transform message from input stream in String, and call specific service related to the message
     */
    public void receiveMessage(DataInputStream inputStream) {
        try {
            while (true) {
                String receivedMessage = inputStream.readUTF();
                receivedMessageHandler.callService(receivedMessage);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
