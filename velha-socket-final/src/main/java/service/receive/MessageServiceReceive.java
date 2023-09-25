package service.receive;

import controller.MainViewController;
import model.*;
import model.enumeration.Board;
import service.MessageInterface;

public class MessageServiceReceive implements MessageInterface {

    private final ExternalPlayer externalPlayer = ExternalPlayer.getInstance();

    @Override
    public void giveUp(Message message) {
        var giveUpMessage = "Seu oponente desistiu!";
        MainViewController.getInstance().showWinnerCard(giveUpMessage);
    }

    @Override
    public void passTurn(Message message) {
        MainViewController.getInstance().releasePassTurnButton();
        MainViewController.getInstance().updatePlayerLabel(false);
        addMoveToTheBoard(message.getTextMessage(), externalPlayer);
    }

    @Override
    public void closeWindow(Message message) {
        var closedWindow = "Seu oponente desconectou.";
        MainViewController.getInstance().showWinnerCard(closedWindow);
    }

    @Override
    public void chatMessage(Message message) {
        MainViewController.getInstance().showReceivedChatMessage(message);
    }

    public void addMoveToTheBoard(String move, Player player) {
        var movePieces = move.split(",");
        var level = movePieces[0];
        var line = Integer.parseInt(movePieces[1]);
        var column = Integer.parseInt(movePieces[2]);

        switch (level) {
            case "B" -> {
                var playerMove = new Move(Board.Level.Bottom, line, column);
                MainViewController.getInstance().addMovementToTheBoard(playerMove, player);}
            case "M" -> {
                var playerMove = new Move(Board.Level.Middle, line, column);
                MainViewController.getInstance().addMovementToTheBoard(playerMove, player);
            }
            case "T" -> {
                var playerMove = new Move(Board.Level.Top, line, column);
                MainViewController.getInstance().addMovementToTheBoard(playerMove, player);
            }
        }
    }
}
