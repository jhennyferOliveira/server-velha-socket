package model;

import javafx.util.Pair;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.awt.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Player {

    public Color playerColor;

    public Boolean currentPlayer;

    public Move lastMove;

    public Boolean external;

    public Integer initGameNumber;

    public Pair<String, String> occupiedTuple;
}
