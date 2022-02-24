package environment.world.destination;

import java.util.Collection;
import java.util.logging.Logger;

import com.google.common.eventbus.EventBus;

import environment.Coordinate;
import environment.World;
import util.MyColor;

/**
 * A class for a DestinationWorld, being a layer of the total world, that contains Destinations.
 */
public class DestinationWorld extends World<Destination> {

    private final Logger logger = Logger.getLogger(DestinationWorld.class.getName());

    /**
     * Initializes a new DestinationWorld instance
     */
    public DestinationWorld(EventBus eventBus) {
        super(eventBus);
    }



    public String toString() {
        return "DestinationWorld";
    }

    /**
     * Adds a number of Destinations randomly to this DestinationWorld.
     *
     * @param nbDestinations the number of destinations to add to this world
     */
    public void createWorld(int nbDestinations) {
        for (int i = 0; i < nbDestinations; i++) {
            boolean ok = false;
            while (!ok) {
                Coordinate c = getRandomCoordinate(getEnvironment().getWidth(),
                                               getEnvironment().getHeight());
                if (getEnvironment().isFreePos(c.getX(), c.getY())) {
                    placeItem(new Destination(c.getX(), c.getY(),
                                              MyColor.getColor(i)));
                    ok = true;
                }
            }
        }
    }

    /**
     * Adds Destinations to this DestinationWorld.
     *
     * @param destinations a collection containing the destinations to add to this world
     */
    @Override
    public void placeItems(Collection<Destination> destinations) {
        destinations.forEach(this::placeItem);
    }

    /**
     * Adds a Destination to this DestinationWorld.
     *
     * @param dest the destination to place in this world
     */
    @Override
    public void placeItem(Destination dest) {
        try {
            this.putItem(dest);
        } catch (ClassCastException exc) {
            this.logger.severe("Can only place a Destination in DestinationWorld.");
        }
    }
}
