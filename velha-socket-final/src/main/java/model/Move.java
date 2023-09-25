package model;

import lombok.*;
import model.enumeration.Board;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Move {
    public Board.Level level;
    public Integer row;
    public Integer column;
}
