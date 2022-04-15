package environment;

import environment.world.destination.DestinationRep;
import environment.world.packet.PacketRep;
import environment.world.wall.WallRep;

/**
 * A class for representations being data objects originating from Items.
 * They contain all values concerning their originating item.
 */
abstract public class Representation {

    int xPos;
    int yPos;

    /**
     * Initializes a new Representation
     *
     * @param x  X-coordinate of the Item this representation represents
     * @param y  Y-coordinate of the Item this representation represents
     */
    protected Representation(int x, int y) {
        setX(x);
        setY(y);
    }

    /**
     * Gets the x coordinate of the Item this representation represents
     * @return The x value
     */
    public int getX() {
        return xPos;
    }

    /**
     * Gets the y coordinate of the Item this representation represents
     * @return The y value
     */
    public int getY() {
        return yPos;
    }

    /**
     * Gets the x and y coordinate of the item this representation represents
     * @return the x and y value in one Coordinate
     */
    public Coordinate getCoordinates() {return new Coordinate(getX(),getY());}

    /**
     * Sets the x attribute of the Representation
     * @param x  The new x value
     */
    protected void setX(int x) {
        xPos = x;
    }

    /**
     * Sets the y attribute of the Representation
     * @param y  The new y value
     */
    protected void setY(int y) {
        yPos = y;
    }

    /**
     * Returns the character representation of the object class this
     * Representation is a representation of.
     */
    abstract public char getTypeChar();

    /**
     * Check if the representation is walkable.
     */
    abstract public boolean isWalkable();

    public static String rep2Initials(String rep){
        if (rep.equals(PacketRep.class.toString()))
            return "P";
        if (rep.equals(DestinationRep.class.toString()))
            return "D";
        if (rep.equals(WallRep.class.toString()))
            return "W";

        return rep.substring(0, 1).toUpperCase();
    }
}
