package model;

import lombok.*;
import model.enumeration.MessageCode;


@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Message {
    private MessageCode.Code code;
    private Move move;
    private String textMessage;
}
