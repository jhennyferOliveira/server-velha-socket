package model.enumeration;

public class MessageCode {
    public enum Code {
        CHAT("#chat|"), PASS_TURN("#pass|"), GIVE_UP("#giveup|"), CLOSE_WINDOW("#close|"), INIT_NUMBER("#init|"), EMPTY(""), WIN("#win|");
        public final String code;
        Code(String code) {
            this.code = code;
        }
    }
}
