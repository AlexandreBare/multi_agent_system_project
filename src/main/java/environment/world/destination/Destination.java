package environment.world.destination;

import java.awt.Color;
import java.util.logging.Logger;

import environment.Item;
import gui.video.Drawer;
import util.MyColor;

/**
 * A class for destinations for certain packets.
 */

public class Destination extends Item<DestinationRep> {
    
    // The color of the destination
    private final Color color;

    private final Logger logger = Logger.getLogger(Destination.class.getName());
    

    /**
     * Initializes a new Destination instance
     *
     * @param  x    x-coordinate of the Destination
     * @param  y    y-coordinate of the Destination
     * @param  col  the Destination's color
     */
    public Destination(int x, int y, Color col) {
        super(x, y);
        this.color = col;
        this.logger.fine(String.format("Destination created at %d %d", x, y));
    }

    /**
     * Initializes a new Destination instance
     *
     * @param  x    x-coordinate of the Destination
     * @param  y    y-coordinate of the Destination
     * @param  colorStr  the Destination's color
     */
    public Destination(int x, int y, String colorStr) {
        this(x, y, MyColor.getColor(colorStr));
    }

    /**
     * Gets the color of this Destination
     *
     * @return    This Destination's color
     */
    public Color getColor() {
        return this.color;
    }

    /**
     * Returns a representation of this Destination
     *
     * @return  A Representation of this Destination
     */
    @Override
    public DestinationRep getRepresentation() {
        return new DestinationRep(getX(), getY(), getColor());
    }

    /**
     * Draws this Destination on the GUI.
     *
     * @param drawer  The visiting drawer
     */
    public void draw(Drawer drawer) {
        drawer.drawDestination(this);
    }

    
    public String generateEnvironmentString() {
        return String.format("%s\n", this.getClass().getName()) +
                String.format("nbArgs %d\n", 3) +
                String.format("Integer %d\n", this.getX()) +
                String.format("Integer %d\n", this.getY()) +
                String.format("String \"%s\"\n", MyColor.getName(this.getColor()));
    }
}
