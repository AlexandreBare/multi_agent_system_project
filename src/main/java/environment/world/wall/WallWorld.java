package environment.world.wall;

import java.util.Collection;
import java.util.logging.Logger;

import com.google.common.eventbus.EventBus;

import environment.World;

/**
 * A class for a WallWorld, being a layer of the total world, that contains Walls.
 */
public class WallWorld extends World<Wall> {

    private final Logger logger = Logger.getLogger(WallWorld.class.getName());

    /**
     * Initializes a new WallWorld instance
     */
    public WallWorld(EventBus eventBus) {
        super(eventBus);
    }

    /**
     * Returns a string representation of this World
     *
     * @return "WallWorld"
     */
    public String toString() {
        return "WallWorld";
    }

    /**
     * Adds Walls to this WallWorld.
     *
     * @param walls An array containing the walls to add to this world
     */
    @Override
    public void placeItems(Collection<Wall> walls) {
        walls.forEach(this::placeItem);
    }

    /**
     * Adds a Wall to this WallWorld.
     *
     * @param wall The walls to place in this world
     */
    @Override
    public void placeItem(Wall wall) {
        try {
            this.putItem(wall);
        } catch (ClassCastException exc) {
            this.logger.severe("Can only place a Wall in WallWorld.");
        }
    }
}
