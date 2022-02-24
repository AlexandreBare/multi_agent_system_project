package environment.world.crumb;

import java.util.Collection;
import java.util.logging.Logger;

import com.google.common.eventbus.EventBus;

import environment.World;

/**
 * A class for a CrumbWorld, being a layer of the total world, that contains Crumbs.
 */
public class CrumbWorld extends World<Crumb> {

    private final Logger logger = Logger.getLogger(CrumbWorld.class.getName());

    /**
     * Initializes a new CrumbWorld instance
     */
    public CrumbWorld(EventBus eventBus) {
        super(eventBus);
    }

    public String toString() {
        return "CrumbWorld";
    }

    /**
     * Puts a new Crumb on a specified coordinate (x, y).
     * If a Crumb on that position already exists,
     * <code>number</code> is added to the number that Crumb.
     *
     * @param x      the x-coordinate for the new Crumb to put
     * @param y      the y-coordinate for the new Crumb to put
     * @param number the number of crumbs in the new Crumb to put
     */
    public void put(int x, int y, int number) {
        if (getItem(x, y) != null) {
            Crumb crumb = getItem(x, y);
            crumb.setNumber(crumb.getNumber() + number);
        } else {
            putItem(new Crumb(x, y, number));
        }
    }

    /**
     * Picks some crumbs from a Crumb on a specified coordinate (x, y).
     * If <code>number</code> is larger than the number of that Crumb,
     * Crumb is removed.
     *
     * @param x      the x-coordinate of the Crumb to pick from
     * @param y      the y-coordinate of the Crumb to pick from
     * @param number the number of crumbs to pick from the Crumb on (x, y)
     */
    public void pick(int x, int y, int number) {
        if (getItem(x, y) != null ) {
            Crumb crumb = getItem(x, y);
            if (crumb.getNumber() > number) {
                crumb.setNumber(crumb.getNumber() - number);
            } else {
                this.logger.severe("Not enough crumbs in this Crumb.");
            }
        } else {
            this.logger.severe("No Crumb to pick from");
        }
    }

    /**
     * Adds a Crumb to this CrumbWorld.
     *
     * @param crumb the Crumb to place in this world
     */
    @Override
    public void placeItem(Crumb crumb) {
        try {
            this.putItem(crumb);
        } catch (ClassCastException exc) {
            this.logger.severe("Can only place Crumbs in CrumbWorld.");
        }
    }

    /**
     * Adds Crumbs to this CrumbWorld.
     *
     * @param crumbs the Crumbs to place in this world
     */
    @Override
    public void placeItems(Collection<Crumb> crumbs) {
        crumbs.forEach(this::placeItem);
    }

}
