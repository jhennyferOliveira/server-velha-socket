package model;

import javafx.util.Pair;
import lombok.Getter;
import lombok.Setter;
import view.extensions.CustomColor;

import java.awt.*;

public class InternalPlayer extends Player {

    public static InternalPlayer internalPlayer;

    private InternalPlayer(Color playerColor, Boolean currentPlayer, Move lastMove, Boolean external, Integer initGameNumber, Pair<String,String> occupiedTuple) {
        super(playerColor, currentPlayer, lastMove,  external, initGameNumber, occupiedTuple);
    }

    public static synchronized InternalPlayer getInstance() {
        if (internalPlayer == null)
            internalPlayer = new InternalPlayer(CustomColor.boardRed, false, null, false, null, new Pair<>("occupied", "0"));
        return internalPlayer;
    }
}
