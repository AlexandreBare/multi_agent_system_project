package environment.world.wall;

import environment.Item;

/**
 * A class for walls, being simple Items with a certain position.
 */
public abstract class Wall extends Item<WallRep> {

    /**
     * Initializes a new Wall instance
     *
     * @param x  X-coordinate of the Wall
     * @param y  Y-coordinate of the Wall
     */
    public Wall(int x, int y) {
        super(x, y);
    }

    public abstract String generateEnvironmentString();

}
