package network.handler;

import model.Message;

import static model.enumeration.MessageCode.Code.*;

/**
 * Interacts with the DataFlowHandler class to send messages
 */
public class SendMessageHandler {

    private final DataFlowHandler dataFlowHandler = DataFlowHandler.getInstance();

    public static SendMessageHandler sendMessageHandler;

    public static synchronized SendMessageHandler getInstance() {
        if (sendMessageHandler == null) {
            sendMessageHandler = new SendMessageHandler();
        }
        return sendMessageHandler;
    }

    public void setMessageToSend(Message message) {
        switch (message.getCode()) {
            case GIVE_UP -> dataFlowHandler.setMessageToSend( GIVE_UP.code);
            case PASS_TURN -> dataFlowHandler.setMessageToSend(PASS_TURN.code + message.getTextMessage());
            case CLOSE_WINDOW -> dataFlowHandler.setMessageToSend(CLOSE_WINDOW.code);
            case INIT_NUMBER -> dataFlowHandler.setMessageToSend(INIT_NUMBER.code + message.getTextMessage());
            case CHAT -> dataFlowHandler.setMessageToSend(CHAT.code + message.getTextMessage());
            case EMPTY -> dataFlowHandler.setMessageToSend(EMPTY.code);
        }
    }
}
