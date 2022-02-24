package environment.world.pheromone;

import java.util.Collection;
import java.util.logging.Logger;

import com.google.common.eventbus.EventBus;

import environment.CellPerception;
import environment.World;

/**
 * A class for a PheromoneWorld, being a layer of the total world, that contains Pheromones.
 */
public class PheromoneWorld extends World<Pheromone> {

    private final Logger logger = Logger.getLogger(PheromoneWorld.class.getName());

    /**
     * Initializes a new PheromoneWorld instance
     */
    public PheromoneWorld(EventBus eventBus) {
        super(eventBus);
    }

    public String toString() {
        return "PheromoneWorld";
    }

    /**
     * Puts a new pheromone on a specified coordinate (x, y).
     * If at some time a pheromone on that position already exists, that
     * pheromone is strengthened.
     *
     * @param x the x-coordinate for the new pheromone
     * @param y the x-coordinate for the new pheromone
     */
    public void put(int x, int y) {
        if (getItem(x, y) != null) {
            getItem(x, y).reinforce();
        } else {
            putItem(new Pheromone(getEnvironment(), x, y));
        }
    }

    /**
     * Puts a directed pheromone in this world with the default lifetime
     *
     * @param x      The x-coordinate for the pheromone to put
     * @param y      The y-coordinate for the pheromone to put
     * @param target The target for the pheromone to put
     */
    public void putDirected(int x, int y, CellPerception target) {
        if (getItem(x, y) != null) {
            //( (Pheromone) getItem(x, y)).reinforce();
            DirPheromone pheromone = (DirPheromone) this.getItem(x, y);
            pheromone.reinforce();
            pheromone.setTarget(target);
        } else {
            putItem(new DirPheromone(getEnvironment(), x, y, target));
        }
    }

    /**
     * Puts a new pheromone on a specified coordinate (x, y) with a given
     * lifetime.
     * If at some time a pheromone on that position already exists, thad
     * pheromone is strengthened with <code>lifetime</code>.
     *
     * @param x        The x-coordinate for the pheromone to put
     * @param y        The y-coordinate for the pheromone to put
     * @param lifetime The lifetime for the pheromone to put
     */
    public void put(int x, int y, int lifetime) {
        if (getItem(x, y) != null) {
            getItem(x, y).reinforce(lifetime);
        } else {
            putItem(new Pheromone(getEnvironment(), x, y, lifetime));
        }
    }

    /**
     * Puts a new directed pheromone on a specified coordinate (x, y) with
     * a given lifetime and target.
     * If at some time a pheromone on that position already exists, thad
     * pheromone is strengthened with <code>lifetime</code>.
     *
     * @param x        The x-coordinate for the DirPheromone to put
     * @param y        The y-coordinate for the DirPheromone to put
     * @param lifetime The lifetime for the DirPheromone to put
     * @param target   The target for the DirPheromone to put
     */
    public void putDirected(int x, int y, int lifetime, CellPerception target) {
        if (getItem(x, y) != null) {
            //( (Pheromone) getItem(x, y)).reinforce(lifetime);
            DirPheromone pheromone = (DirPheromone) this.getItem(x, y);

            pheromone.reinforce(lifetime);
            pheromone.setTarget(target);
        } else {
            putItem(new DirPheromone(getEnvironment(), x, y, lifetime, target));
        }
    }

    /**
     * Puts a new pheromone on a specified coordinate and on all surrounding
     * areas. If at some time a Pheromone on that position already exists, that
     * Pheromone is strengthened.
     *
     * @param x The x-coordinate of the pheromone to put
     * @param y The y-coordinate of the pheromone to put
     */
    public void putAndPropagate(int x, int y) {
        for (int i = x - 1; i <= x + 1; i++) {
            for (int j = y - 1; j <= y + 1; j++) {
                if (getItem(i, j) != null) {
                    getItem(i, j).reinforce();
                } else {
                    putItem(new Pheromone(getEnvironment(), i, j));
                }
            }
        }
    }

    /**
     * Puts a new pheromone on a specified coordinate with given lifetime
     * and on all surrounding areas with a diminished factor of lifetime.
     * If at some time a Pheromone on that position already exists, that
     * Pheromone is strengthened.
     *
     * @param x        The x-coordinate of the pheromone to put
     * @param y        The y-coordinate of the pheromone to put
     * @param lifetime The lifetime for the pheromone to put
     */
    public void putAndPropagate(int x, int y, int lifetime) {
        for (int i = x - 1; i <= x + 1; i++) {
            for (int j = y - 1; j <= y + 1; j++) {
                try {
                    if (i == x && j == y) {
                        if (getItem(i, j) == null) {
                            putItem(new Pheromone(getEnvironment(), i, j, lifetime));
                        } else {
                            getItem(i, j).reinforce(lifetime);
                        }
                    } else {
                        if (getItem(i, j) == null) {
                            putItem(new Pheromone(getEnvironment(), i, j, lifetime / 10));
                        } else {
                            getItem(i, j).reinforce(lifetime / 10);
                        }
                    }
                } catch (IndexOutOfBoundsException exc) {
                    // (i, j) is no coordinate in this world
                    //No-op
                }
            }
        }
    }


    /**
     * Adds Pheromones to this PheromoneWorld.
     *
     * @param pheromones The array of pheromones to put in this world
     */
    @Override
    public void placeItems(Collection<Pheromone> pheromones) {
        pheromones.forEach(this::placeItem);
    }

    /**
     * Adds a Pheromone to this PheromoneWorld.
     *
     * @param pheromone The pheromone to put in this world
     */
    @Override
    public void placeItem(Pheromone pheromone) {
        try {
            this.putItem(pheromone);
        } catch (ClassCastException exc) {
            this.logger.severe("Can only place a Pheromone in PheromoneWorld.");
        }
    }
}
