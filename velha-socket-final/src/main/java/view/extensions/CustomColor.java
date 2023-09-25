package view.extensions;

import java.awt.*;

public class CustomColor extends Color {

    public CustomColor(int rgb) {
        super(rgb);
    }
    public static Color lightGrayCustom = new Color(234, 234, 234);

    public static Color blockGray = new Color(241, 241, 241, 80);
    public static Color boardRed = new Color(229, 171, 171);

    public static Color chatRed = new Color(222, 130, 130);

    public static Color giveUpRed = new Color(212, 74, 74);
    public static Color passTurnGreen = new Color(162, 221, 168);

    public static Color boardBlue = new Color(183, 209, 234);

    public static Color chatBlue = new Color(101, 142, 181);
}
