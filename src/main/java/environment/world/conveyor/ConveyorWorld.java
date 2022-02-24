package environment.world.conveyor;

import java.util.Collection;
import java.util.logging.Logger;

import com.google.common.eventbus.EventBus;

import environment.World;
import environment.world.destination.DestinationWorld;


/**
 * A class for a DestinationWorld, being a layer of the total world, that contains Destinations.
 */
public class ConveyorWorld extends World<Conveyor> {

    private final Logger logger = Logger.getLogger(DestinationWorld.class.getName());

    /**
     * Initializes a new DestinationWorld instance
     */
    public ConveyorWorld(EventBus eventBus) {
        super(eventBus);
    }


    public String toString() {
        return "ConveyorWorld";
    }


    /**
     * Adds Conveyors to this ConveyorWorld.
     *
     * @param conveyors a collection containing the conveyors to add to this world
     */
    @Override
    public void placeItems(Collection<Conveyor> conveyors) {
        conveyors.forEach(this::placeItem);
    }

    /**
     * Adds a Conveyor to this ConveyorWorld.
     *
     * @param conv the conveyor to place in this world
     */
    @Override
    public void placeItem(Conveyor conv) {
        try {
            this.putItem(conv);
            getEnvironment().addActiveItem(conv);
        } catch (ClassCastException exc) {
            this.logger.severe( "Can only place a Conveyor in ConveyorWorld.");
        }
    }
}
