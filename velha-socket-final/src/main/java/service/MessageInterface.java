package service;


import model.Message;

public interface MessageInterface {
    void giveUp(Message message);
    void passTurn(Message message);
    void closeWindow(Message message);
    void chatMessage(Message message);
}
