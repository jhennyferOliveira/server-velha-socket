package model.enumeration;

public class Board {
    public enum Level {
        Bottom("B"), Middle("M"), Top("T");

        public final String level;
        Level(String level) {
            this.level = level;
        }
    }
}
