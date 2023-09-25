package network.handler;

import model.Message;
import model.enumeration.MessageCode;
import service.receive.InitialPlayerDefinitionService;
import service.receive.MessageServiceReceive;

import java.util.Arrays;
import java.util.List;

/**
 * Handle the received data by separating the action code from the message and redirecting to specific services.
 */
public class ReceivedMessageHandler {

    private final MessageServiceReceive messageService;
    private final InitialPlayerDefinitionService initialPlayerDefinitionService;

    public ReceivedMessageHandler() {
        this.messageService = new MessageServiceReceive();
        this.initialPlayerDefinitionService = new InitialPlayerDefinitionService();
    }
    public MessageCode.Code getCode(String message) {
        if (message.startsWith(MessageCode.Code.PASS_TURN.code)) {
            return MessageCode.Code.PASS_TURN;
        } else if (message.startsWith(MessageCode.Code.CHAT.code)) {
            return MessageCode.Code.CHAT;
        } else if (message.startsWith(MessageCode.Code.GIVE_UP.code)) {
            return MessageCode.Code.GIVE_UP;
        } else if (message.startsWith(MessageCode.Code.CLOSE_WINDOW.code)) {
            return MessageCode.Code.CLOSE_WINDOW;
        } else if (message.startsWith(MessageCode.Code.INIT_NUMBER.code)) {
            return MessageCode.Code.INIT_NUMBER;
        } else {
            return MessageCode.Code.EMPTY;
        }
    }

    public String getMessage(String message) {
        List<String> messageWithoutCode = Arrays.stream(message.split("\\|")).toList();
        return messageWithoutCode.get(messageWithoutCode.size()-1);
    }

    public void callService(String message) {


        Message builtMessage = Message.builder().textMessage(getMessage(message)).code(getCode(message)).build();
        switch (getCode(message)) {
            case GIVE_UP -> messageService.giveUp(builtMessage);
            case PASS_TURN -> messageService.passTurn(builtMessage);
            case CLOSE_WINDOW -> messageService.closeWindow(builtMessage);
            case INIT_NUMBER -> initialPlayerDefinitionService.setExternalPlayerRandomNumber(Integer.parseInt(builtMessage.getTextMessage()));
            case CHAT -> messageService.chatMessage(builtMessage);
            case EMPTY -> {}
        }
    }
}
