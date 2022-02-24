package environment.world.flag;

import java.util.Collection;
import java.util.logging.Logger;

import com.google.common.eventbus.EventBus;

import environment.World;

/**
 * A class for a FlagWorld, being a layer of the total world, that contains Flags.
 */
public class FlagWorld extends World<Flag> {

    private final Logger logger = Logger.getLogger(FlagWorld.class.getName());
    
    /**
     * Initializes a new FlagWorld instance
     */
    public FlagWorld(EventBus eventBus) {
        super(eventBus);
    }

    public String toString() {
        return "FlagWorld";
    }


    /**
     * Adds Flags to this FlagWorld.
     *
     * @param flags a collection containing the flags to place in this world
     */
    @Override
    public void placeItems(Collection<Flag> flags) {
        flags.forEach(this::placeItem);
    }

    /**
     * Adds a Flag to this FlagWorld.
     *
     * @param flag the flag to place in this world
     */
    @Override
    public void placeItem(Flag flag) {
        try {
            this.putItem(flag);
        } catch (ClassCastException exc) {
            this.logger.severe("Can only place a Flag in FlagWorld.");
        }
    }
}
