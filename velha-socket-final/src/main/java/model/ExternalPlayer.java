package model;

import javafx.util.Pair;
import view.extensions.CustomColor;

import java.awt.*;

public class ExternalPlayer extends Player {
    public static ExternalPlayer externalPlayer;

    private ExternalPlayer(Color playerColor, Boolean currentPlayer, Move lastMove, Integer initGameNumber, Boolean external, Pair<String,String> occupiedTuple) {
        super(playerColor, currentPlayer,  lastMove,  external, initGameNumber, occupiedTuple);
    }
    public static synchronized ExternalPlayer getInstance() {
        if (externalPlayer == null)
            externalPlayer = new ExternalPlayer(CustomColor.boardBlue, false, null, null, true,  new Pair<>("occupied", "1"));
        return externalPlayer;
    }

    private final Object lockForInitGameNumber = new Object();


    /**
     * Wake up the current thread that are waiting for the value
     */
    public void setExternalPlayerRandomNumber(Integer randomNumber) {
        synchronized (lockForInitGameNumber) {
            initGameNumber = randomNumber;
            lockForInitGameNumber.notifyAll(); // acorda threads que estao esperando por esse valor
        }
    }

    /**
     * A lock is used to block the current thread while the value is null
     */
    public void lockThreadUntilExternalPlayerInitNumberIsSet() {
        synchronized (lockForInitGameNumber) {
            while (initGameNumber == null) {
                try {
                    lockForInitGameNumber.wait();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }
}
