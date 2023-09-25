package view.extensions;

import controller.MainViewController;
import model.InternalPlayer;

import javax.swing.*;

public class BoardButton extends JButton {

    private final InternalPlayer internalPlayer = InternalPlayer.getInstance();
    public BoardButton() {
        this.addActionListener(e -> {
            var move = this.getClientProperty("move");
            MainViewController.getInstance().setUpBoardButtonActionToSelectWithoutPassTurn(move.toString(), internalPlayer);
        });
    }
}
