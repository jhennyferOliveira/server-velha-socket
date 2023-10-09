package network.handler;

import model.Message;
import network.configuration.Server;

import static model.enumeration.MessageCode.Code.*;

public class SendMessageHandler {

    public static SendMessageHandler sendMessageHandler;

    public static synchronized SendMessageHandler getInstance() {
        if (sendMessageHandler == null) {
            sendMessageHandler = new SendMessageHandler();
        }
        return sendMessageHandler;
    }

    /**
     * Getting remote object from server to send message using RMI
     */
    public void setMessageToSend(Message message) {
        switch (message.getCode()) {

            case GIVE_UP -> {
                try {
                    Server.remoteObject.sendMessage(GIVE_UP.code + message.getTextMessage());
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            }
            case PASS_TURN -> {
                try {
                    Server.remoteObject.sendMessage(PASS_TURN.code + message.getTextMessage());
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            }
            case CLOSE_WINDOW -> {
                try {
                    Server.remoteObject.sendMessage(CLOSE_WINDOW.code);
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            }
            case INIT_NUMBER -> {
                try {
                    Server.remoteObject.sendMessage(INIT_NUMBER.code + message.getTextMessage());
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            }
            case CHAT -> {
                try {
                    Server.remoteObject.sendMessage(CHAT.code + message.getTextMessage());
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            }
            case EMPTY -> {
                try {
                    Server.remoteObject.sendMessage(EMPTY.code + message.getTextMessage());
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            }
        }
    }
}
