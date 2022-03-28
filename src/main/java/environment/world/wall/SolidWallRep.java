package environment.world.wall;

import environment.Coordinate;

/**
 * A class for representations of walls.
 */
public class SolidWallRep extends WallRep {

    /**
     * Initializes a new ObscureWallRep instance
     *
     * @param x  X-coordinate of the Wall this representation represents
     * @param y  Y-coordinate of the Wall this representation represents
     */
    protected SolidWallRep(int x, int y) {
        super(x, y);
    }

    /**
     * Initializes a new ObscureWallRep instance
     *
     * @param coordinates  Coordinates of the Wall this representation represents
     */
    public SolidWallRep(Coordinate coordinates) {
        super(coordinates.getX(), coordinates.getY());
    }

    public char getTypeChar() {
        return ('W');
    }

    @Override
    public boolean isWalkable() {
        return false;
    }

    @Override
    public boolean isSeeThrough() {
        return false;
    }
}
