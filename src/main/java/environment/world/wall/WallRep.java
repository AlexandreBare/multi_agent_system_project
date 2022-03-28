package environment.world.wall;

import environment.Coordinate;
import environment.Representation;

/**
 * A class for representations of walls.
 */
public abstract class WallRep extends Representation {

    /**
     * Initializes a new WallRep instance
     *
     * @param x  X-coordinate of the Wall this representation represents
     * @param y  Y-coordinate of the Wall this representation represents
     */
    protected WallRep(int x, int y) {
        super(x, y);
    }

    /**
     * Initializes a new WallRep instance
     *
     * @param coordinates  Coordinates of the Wall this representation represents
     */
    public WallRep(Coordinate coordinates) {
        super(coordinates.getX(), coordinates.getY());
    }

    public char getTypeChar() {
        return ('W');
    }

    @Override
    public boolean isWalkable() {
        return false;
    }


    public abstract boolean isSeeThrough(); 
}
