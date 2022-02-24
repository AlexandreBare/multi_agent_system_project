package environment.world.flag;

import java.awt.Color;
import java.util.logging.Logger;

import environment.Item;
import gui.video.Drawer;

/**
 * A class for flags with a certain color.
 */
@SuppressWarnings("FieldCanBeLocal")
public class Flag extends Item<FlagRep> {

    // The color of this Flag.
    private final Color color;

    private final Logger logger = Logger.getLogger(Flag.class.getName());
    
    
    /**
     * Initializes a new Flag instance
     *
     * @param x       x-coordinate of the Flag
     * @param y       y-coordinate of the Flag
     * @param col     the Flag's color
     */
    public Flag(int x, int y, Color col) {
        super(x, y);
        this.color = col;
        this.logger.fine(String.format("Flag created at %d %d", x, y));
    }

    /**
     * Gets the color of this Flag.
     *
     * @return This Flag's color
     */
    public Color getColor() {
        return this.color;
    }

    /**
     * Returns a representation of this Flag.
     *
     * @return A Flag-representation
     */
    @Override
    public FlagRep getRepresentation() {
        return (new FlagRep(getX(), getY(), getColor()));
    }

    /**
     * Draws this Flag on the GUI.
     *
     * @param drawer The visiting drawer
     */
    @Override
    public void draw(Drawer drawer) {
        drawer.drawFlag(this);
    }

}
