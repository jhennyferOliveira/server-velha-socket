package controller;

import model.*;
import model.enumeration.Board;
import model.enumeration.MessageCode;
import service.receive.InitialPlayerDefinitionService;
import service.receive.MessageServiceReceive;
import service.send.MessageServiceSend;
import view.MainView;
import view.extensions.CustomColor;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Objects;

public class MainViewController {

    private static MainViewController mainViewController;
    private final MessageServiceSend messageServiceSend = new MessageServiceSend();

    private final MessageServiceReceive messageServiceReceive = new MessageServiceReceive();

    // SHARED INSTANCES
    private final ExternalPlayer externalPlayer = ExternalPlayer.getInstance();
    private final InternalPlayer internalPlayer = InternalPlayer.getInstance();
    private final MainView mainView = MainView.getInstance();
    private final InitialPlayerDefinitionService initialPlayerDefinitionService = new InitialPlayerDefinitionService();


    public static synchronized MainViewController getInstance() {
        if (mainViewController == null)
            mainViewController = new MainViewController();
        return mainViewController;
    }

    // INITIAL GAME DEFINITION

    /**
     * Initial game handler. When the internal user clicks the generate random number, the init process begins by blocking the game until
     * receive the external player number
     */
    public void setUpRandomNumberButtonAction() {
        mainView.randomNumberButton.addActionListener(e -> {
            /* set internal player initial number */
            internalPlayer.initGameNumber = initialPlayerDefinitionService.generateRandomNumber();
            messageServiceSend.initNumber(Message.builder().code(MessageCode.Code.INIT_NUMBER).textMessage(internalPlayer.initGameNumber.toString()).build());
            mainView.randomNumberLabel.setText(mainView.randomNumberLabel.getText() + " " + internalPlayer.initGameNumber + ".");
            mainView.setUpLabelAndButtonVisibility();

            new Thread(() -> {
                /* lock screen until receive external player number */
                externalPlayer.lockThreadUntilExternalPlayerInitNumberIsSet();
                initialPlayerDefinitionService.decideInitialPlayer();
                mainView.randomNumberLabel.setText(mainView.randomNumberLabel.getText() + " " + internalPlayer.initGameNumber + ".");
                SwingUtilities.invokeLater(() ->  {
                    mainView.removeInitialCardAndShowGameBoard();
                    if (externalPlayer.currentPlayer) {
                        updatePlayerLabel(true);
                        blockPassTurnButtonInitialGame();
                    } else {
                        updatePlayerLabel(false);
                    }
                        }
                ); // roda a thread de UI

            }).start();
        });
    }

    /**
     * Player label update
     */
    public void updatePlayerLabel(Boolean external) {
        if (external) {
            mainView.playerLabel.setText("Rival está jogando");
            mainView.playerLabelSquare.setBackground(externalPlayer.playerColor);
        } else {
            mainView.playerLabel.setText("Você está jogando");
            mainView.playerLabelSquare.setBackground(internalPlayer.playerColor);
        }
    }

    // MOVEMENT HANDLER UI
    /**
     * Board movement: shows the move in the UI, sets player's last move, checks if move leads to the end of the game
     */
    public void addMovementToTheBoard(Move move, Player player) {
        for (int i = 0; i < mainView.rowLength; i++) {
            for (int j = 0; j < mainView.colLength; j++) {
                    if ((Objects.equals(move.level.level, Board.Level.Top.level) && move.row == i && move.column == j) && mainView.topButtons[i][j].getClientProperty("occupied").equals("-1") ) {
                        mainView.topButtons[i][j].setBackground(player.playerColor);
                        mainView.topButtons[i][j].putClientProperty(player.occupiedTuple.getKey(), player.occupiedTuple.getValue());
                        player.setLastMove(move);
                        endGameVerification(player);
                    } else if ((Objects.equals(move.level.level, Board.Level.Middle.level) && move.row == i && move.column == j) && mainView.middleButtons[i][j].getClientProperty("occupied") == "-1") {
                        mainView.middleButtons[i][j].setBackground(player.playerColor);
                        mainView.middleButtons[i][j].putClientProperty(player.occupiedTuple.getKey(), player.occupiedTuple.getValue());
                        player.setLastMove(move);
                        endGameVerification(player);
                    } else if ((Objects.equals(move.level.level, Board.Level.Bottom.level) && move.row == i && move.column == j) && mainView.bottomButtons[i][j].getClientProperty("occupied") == "-1") {
                        mainView.bottomButtons[i][j].setBackground(player.playerColor);
                        mainView.bottomButtons[i][j].putClientProperty(player.occupiedTuple.getKey(), player.occupiedTuple.getValue());
                        player.setLastMove(move);
                        endGameVerification(player);
                    }
            }
        }
    }

    // END GAME CHECKING

    /** Win case scenarios covered by the algorithm
     * <pre>
     * 1 - Bottom board Horizontal 3 possibilities, one in each line (B,0,0 && B,0,1 && B,0,2), (B,1,0 && B,1,1 && B,1,2), (B,2,0 && B,2,1 && B,2,2)
     * 2 - Bottom board Vertical 3 possibilities, one in each column
     * 3 - Bottom board Diagonal 2 possibilities (B,0,0 && B,1,1 && B,2,2) || (B,0,2 && B,1,1 && B,2,0)
     * 4 - Middle board Horizontal 3 possibilities, one in each line
     * 5 - Middle board Vertical 3 possibilities, one in each column
     * 6 - Middle board Diagonal 2 possibilities
     * 7 - Top board Horizontal 3 possibilities, one in each line
     * 8 - Top board Vertical 3 possibilities, one in each column
     * 9 - Top board Diagonal 2 possibilities
     * 10 - Between boards Horizontal 6 possibilities - 2 for each line (B,0,0 && M,0,1 && T,0,2), (T,0,0 && M,0,1 && B,0,2)
     * 11 - Between boards Vertical 6 possibilities - 2 for each column
     * 12 - Between boards  Diagonal 4 possibilities - 2 for each diagonal(B,M,T || T,M,B)
     * 13 - Between boards  Stack  9 possibilities - one for each column and line (B,0,0 && M,0,0 && T,0,0)
     * </pre>
     */
    public Boolean checkIfIsWinner(Player player) {
        var occupied = player.occupiedTuple.getValue();
        List<Board.Level> levels = List.of(Board.Level.Bottom, Board.Level.Middle, Board.Level.Top);
        var levelB = levels.get(0);
        var levelM = levels.get(1);
        var levelT = levels.get(2);

        var k = 0;
        for (int i = 0; i < mainView.rowLength; i++) {
            /* check horizontal lines */
            if (Objects.equals(getButtonValue(levelB, i, k), occupied) && Objects.equals(getButtonValue(levelB, i, k + 1), occupied) && Objects.equals(getButtonValue(levelB, i, k + 2), occupied)) {
                System.out.println("ganhou horizontal B");
                return true;
            } else if (Objects.equals(getButtonValue(levelM, i, k), occupied) && Objects.equals(getButtonValue(levelM, i, k + 1), occupied) && Objects.equals(getButtonValue(levelM, i, k + 2), occupied)) {
                System.out.println("ganhou horizontal M");
                return true;
            } else if (Objects.equals(getButtonValue(levelT, i, k), occupied) && Objects.equals(getButtonValue(levelT, i, k + 1), occupied) && Objects.equals(getButtonValue(levelT, i, k + 2), occupied)) {
                System.out.println("ganhou horizontal T");
                return true;
            }
            /* check vertical columns */
            else if (Objects.equals(getButtonValue(levelB, k, i), occupied) && Objects.equals(getButtonValue(levelB, k + 1, i ), occupied) && Objects.equals(getButtonValue(levelB, k + 2, i ), occupied)) {
                System.out.println("ganhou vertical B");
                return true;
            } else if (Objects.equals(getButtonValue(levelM, k, i), occupied) && Objects.equals(getButtonValue(levelM, k + 1, i ), occupied) && Objects.equals(getButtonValue(levelM, k + 2, i ), occupied)) {
                System.out.println("ganhou vertical M");
                return true;
            } else if (Objects.equals(getButtonValue(levelT, k, i), occupied) && Objects.equals(getButtonValue(levelT, k + 1, i ), occupied) && Objects.equals(getButtonValue(levelT, k + 2, i ), occupied)) {
                System.out.println("ganhou vertical T");
                return true;
            }
            /* check secondary diagonals for each level (bottom, middle and top)  in each iteration */
            else if (Objects.equals(getButtonValue(levels.get(i), k, k), occupied) && Objects.equals(getButtonValue(levels.get(i), k+1, k+1 ), occupied) && Objects.equals(getButtonValue(levels.get(i), k+2, k+2 ), occupied)) {
                System.out.println("ganhou diagonal secundaria" + levels.get(i));
                return true;
            }
            /* check main diagonals for each level (bottom, middle and top)  in each iteration */
            else if ((Objects.equals(getButtonValue(levels.get(i), k, k+2), occupied) && Objects.equals(getButtonValue(levels.get(i), k+1, k+1 ), occupied) && Objects.equals(getButtonValue(levels.get(i), k+2, k ), occupied))) {
                System.out.println("ganhou diagonal principal" + levels.get(i));
                return true;
            }

            /* check between boards Horizontal(Bottom, Middle, Top) for each line */
            else if (Objects.equals(getButtonValue(levelB, i, k), occupied) && Objects.equals(getButtonValue(levelM, i, k + 1), occupied) && Objects.equals(getButtonValue(levelT, i, k + 2), occupied)) {
                System.out.println("ganhou horizontal BMT");
                return true;
            }
            /* check between boards Horizontal(Top, Middle, Bottom) for each line */
            else if (Objects.equals(getButtonValue(levelT, i, k), occupied) && Objects.equals(getButtonValue(levelM, i, k + 1), occupied) && Objects.equals(getButtonValue(levelB, i, k + 2), occupied)) {
                System.out.println("ganhou horizontal TMB");
                return true;
            }

            /* check between boards Vertical(Bottom, Middle, Top) for each line */
            else if (Objects.equals(getButtonValue(levelB, k, i), occupied) && Objects.equals(getButtonValue(levelM, k + 1, i ), occupied) && Objects.equals(getButtonValue(levelT, k + 2, i ), occupied)) {
                System.out.println("ganhou vertical BMT");
                return true;
            }

            /* check between boards Vertical(Top, Middle, Bottom) for each line */
            else if (Objects.equals(getButtonValue(levelT, k, i), occupied) && Objects.equals(getButtonValue(levelM, k + 1, i ), occupied) && Objects.equals(getButtonValue(levelB, k + 2, i ), occupied)) {
                System.out.println("ganhou vertical TMB");
                return true;
            }

            /* check between boards secondary diagonal(Bottom, Middle, Top) */
            else if (Objects.equals(getButtonValue(levelB, k, k), occupied) && Objects.equals(getButtonValue(levelM, k+1, k+1 ), occupied) && Objects.equals(getButtonValue(levelT, k+2, k+2 ), occupied)) {
                System.out.println("ganhou diagonal secundaria" + levels.get(i));
                return true;
            }
            /* check between boards secondary diagonal(Top, Middle, Bottom) */
            else if (Objects.equals(getButtonValue(levelT, k, k), occupied) && Objects.equals(getButtonValue(levelM, k+1, k+1 ), occupied) && Objects.equals(getButtonValue(levelB, k+2, k+2 ), occupied)) {
                System.out.println("ganhou diagonal secundaria" + levels.get(i));
                return true;
            }
            /* check between boards main diagonal(Bottom, Middle, Top)  */
            else if ((Objects.equals(getButtonValue(levelB, k+2, k), occupied) && Objects.equals(getButtonValue(levelM, k+1, k+1 ), occupied) && Objects.equals(getButtonValue(levelT, k, k+2 ), occupied))) {
                System.out.println("ganhou diagonal principal" + levels.get(i));
                return true;
            }
            /* check between boards main diagonal  */
            else if ((Objects.equals(getButtonValue(levelT, k+2, k), occupied) && Objects.equals(getButtonValue(levelM, k+1, k+1 ), occupied) && Objects.equals(getButtonValue(levelB, k, k+2 ), occupied))) {
                System.out.println("ganhou diagonal principal" + levels.get(i));
                return true;
            }

            /* check stack */
            else if (Objects.equals(getButtonValue(levelB, i, k), occupied) && Objects.equals(getButtonValue(levelM, i, k), occupied) && Objects.equals(getButtonValue(levelT, i, k), occupied)) {
                System.out.println("ganhou stack");
                return true;
            }

            else if (Objects.equals(getButtonValue(levelB, i, k+1), occupied) && Objects.equals(getButtonValue(levelM, i, k+1), occupied) && Objects.equals(getButtonValue(levelT, i, k+1), occupied)) {
                System.out.println("ganhou stack");
                return true;
            }

            else if (Objects.equals(getButtonValue(levelB, i, k+2), occupied) && Objects.equals(getButtonValue(levelM, i, k+2), occupied) && Objects.equals(getButtonValue(levelT, i, k+2), occupied)) {
                System.out.println("ganhou stack");
                return true;
            }
        }
        return false;
    }

    public void endGameVerification(Player player) {
        if (checkIfIsWinner(player)) {
            if (player.external) {
                System.out.println("Jogador externo venceu");
                showLoserCard("Seu oponente ganhou!","Até a próxima!");
            } else {
                System.out.println("Jogador interno venceu");
                messageServiceSend.passTurn(Message.builder().code(MessageCode.Code.PASS_TURN).move(player.getLastMove()).build());
                showWinnerCard("Você jogou muito bem!");
            }
        }
    }

    public String getButtonValue(Board.Level level, int i, int j) {

        switch (level) {
            case Bottom-> {
                return mainView.bottomButtons[i][j].getClientProperty(mainView.occupied.getKey()).toString();
            }
            case Middle-> {
                return mainView.middleButtons[i][j].getClientProperty(mainView.occupied.getKey()).toString();
            }
            case Top -> {
                return mainView.topButtons[i][j].getClientProperty(mainView.occupied.getKey()).toString();
            }
            default -> {
                return mainView.occupied.getValue();
            }
        }
    }

    // PASS TURN ACTIONS

    public void setUpPassTurnButtonAction() {
        addMovementToTheBoard(internalPlayer.lastMove, internalPlayer);
        messageServiceSend.passTurn(Message.builder().code(MessageCode.Code.PASS_TURN).move(internalPlayer.lastMove).build());
        updatePlayerLabel(true);
        internalPlayer.lastMove.level = null;
        internalPlayer.lastMove.row = null;
        internalPlayer.lastMove.column = null;

        mainView.blockPassTurnButton();
        mainView.setBoardButtonsEnable(false, true);
    }

    public void releasePassTurnButton() {
        mainView.releasePassTurnButton();
        mainView.setBoardButtonsEnable(true, true);
    }

    public void setUpBoardButtonActionToSelectWithoutPassTurn(String move, Player player) {
        if (Objects.nonNull(player.lastMove) && Objects.nonNull(player.lastMove.level)) {
            switch (player.lastMove.level) {
                case Bottom -> {
                    mainView.bottomButtons[player.lastMove.row][player.lastMove.column].setBackground(Color.white);
                    mainView.bottomButtons[player.lastMove.row][player.lastMove.column].putClientProperty(mainView.occupied.getKey(), mainView.occupied.getValue());
                }
                case Middle -> {
                    mainView.middleButtons[player.lastMove.row][player.lastMove.column].setBackground(Color.white);
                    mainView.middleButtons[player.lastMove.row][player.lastMove.column].putClientProperty(mainView.occupied.getKey(), mainView.occupied.getValue());
                }
                case Top-> {
                    mainView.topButtons[player.lastMove.row][player.lastMove.column].setBackground(Color.white);
                    mainView.topButtons[player.lastMove.row][player.lastMove.column].putClientProperty(mainView.occupied.getKey(), mainView.occupied.getValue());
                }

            }
        }
        messageServiceReceive.addMoveToTheBoard(move, player);
    }



    public void blockPassTurnButtonInitialGame() {
        mainView.blockPassTurnButton();
        mainView.setBoardButtonsEnable(false, true);
        updatePlayerLabel(true);
    }

    // END GAME INTERFACE HANDLER

    public void showLoserCard(String title, String message) {
        mainView.setAllButtonsEnable(false, true);
        mainView.endGameMessageTitle.setText(title);
        mainView.showEndGamePanel(message);
    }

    public void showWinnerCard(String message) {
        mainView.setAllButtonsEnable(false, true);
        mainView.showEndGamePanel(message);
    }

    public void giveUpAction() {
        Message messageToSend = Message.builder().code(MessageCode.Code.GIVE_UP).build();
        messageServiceSend.giveUp(messageToSend);
        showLoserCard("Ahhh você desistiu!", " Seu rival venceu.");
    }


    // CHAT ACTIONS
    public void sendChatMessage(String message) {
        Message messageToSend = Message.builder().textMessage(message).code(MessageCode.Code.CHAT).build();
        messageServiceSend.chatMessage(messageToSend);
    }

    public void showReceivedChatMessage(Message message) {
        var sender = "Oponente";
        mainView.addMessageToPane( sender, message.getTextMessage(), CustomColor.chatBlue);
    }


    // CLOSE WINDOW
    public void closeWindow() {
        messageServiceSend.closeWindow(Message.builder().code(MessageCode.Code.CLOSE_WINDOW).build());
    }

}
