package util;

import javax.annotation.Nullable;
import java.awt.Color;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

/**
 * Helper class to convert colors from java.awt.Color to an own String
 * representation and vice versa.
 */
abstract public class MyColor {


    private static final Color b = Color.blue;
    private static final Color r = Color.red;
    private static final Color g = Color.green;
    private static final Color m = Color.magenta;
    private static final Color y = Color.yellow;
    private static final Color p = Color.pink;
    private static final Color[] colors = {b, r, g, m, y, p};

    private static final Logger logger = Logger.getLogger(MyColor.class.getName());



    //--------------------------------------------------------------------------
    //		INSPECTORS
    //--------------------------------------------------------------------------

    /**
     * Converts a color name in a Color object.
     */
    public static Color getColor(String color) {
        return switch (color) {
            case "yellow" -> Color.yellow;
            case "red" -> Color.red;
            case "green" -> Color.green;
            case "blue" -> Color.blue;
            case "pink" -> Color.pink;
            case "magenta" -> Color.magenta;
            default -> Color.black;
        };
    }

    /**
     * Converts an awt.Color object into a string representation.
     */
    public static String getName(Color color) {
        if (color.equals(Color.yellow)) {
            return "yellow";
        } else if (color.equals(Color.red)) {
            return "red";
        } else if (color.equals(Color.green)) {
            return "green";
        } else if (color.equals(Color.blue)) {
            return "blue";
        } else if (color.equals(Color.pink)) {
            return "pink";
        } else if (color.equals(Color.magenta)) {
            return "magenta";
        } else {
            return "black";
        }
    }

    @Nullable
    public static Color getColor(int i) {
        try {
            return colors[i];
        } catch (ArrayIndexOutOfBoundsException exc) {
            MyColor.logger.severe("Color doesn't exist in util.MyColor!");
            return null;
        }
    }

    public static List<Color> getColors() {
        return Arrays.asList(colors);
    }

    public static int getNbDefinedColors() {
        return colors.length;
    }

}
