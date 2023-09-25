package service.send;


import model.Message;
import network.handler.SendMessageHandler;
import service.MessageInterface;

public class MessageServiceSend implements MessageInterface {

    private final SendMessageHandler sendMessageHandler = SendMessageHandler.getInstance();
    @Override
    public void giveUp(Message message) {
        sendMessageHandler.setMessageToSend(message);
    }

    @Override
    public void passTurn(Message message) {
        var level = String.valueOf(message.getMove().level.level);
        var line = String.valueOf(message.getMove().row);
        var column = String.valueOf(message.getMove().column);
        var moveString = level + "," + line + "," + column;
        message.setTextMessage(moveString);
        sendMessageHandler.setMessageToSend(message);
    }

    @Override
    public void chatMessage(Message message) {
        sendMessageHandler.setMessageToSend(message);
    }

    @Override
    public void closeWindow(Message message) {
        sendMessageHandler.setMessageToSend(message);
    }

    public void initNumber(Message message) {
        sendMessageHandler.setMessageToSend(message);
    }

}
