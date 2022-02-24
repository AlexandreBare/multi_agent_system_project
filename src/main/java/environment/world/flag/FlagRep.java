package environment.world.flag;

import java.awt.Color;

import environment.Representation;

/**
 * A class for representations of flags.
 */
public class FlagRep extends Representation {

    private Color color;

    /**
     * Initializes a new FlagRep instance
     *
     * @param x      X-coordinate of the Flag this representation represents
     * @param y      Y-coordinate of the Flag this representation represents
     * @param aColor Color of the Flag this representation represents
     */
    protected FlagRep(int x, int y, Color aColor) {
        super(x, y);
        setColor(aColor);
    }

    /**
     * Gets the color of this FlagRep
     *
     * @return This flagRep's color
     */
    public Color getColor() {
        return color;
    }

    /**
     * Sets the color of this FlagRep
     *
     * @param aColor The new color value
     */
    protected void setColor(Color aColor) {
        color = aColor;
    }

    public char getTypeChar() {
        return ('F');
    }

    @Override
    public boolean isWalkable() {
        return true;
    }
}
