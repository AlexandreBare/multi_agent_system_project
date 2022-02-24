package support;

import environment.ActiveItemID;
import environment.Environment;
import environment.world.pheromone.PheromoneWorld;
import util.event.AgentActionEvent;

/**
 * A class for influences for putting down pheromones.
 */
public class InfPutPheromone extends Influence {



    private final int lifetime;


    /**
     * Initializes a new InfPutPheromone object
     * @see Influence
     */
    public InfPutPheromone(Environment environment, int x, int y, ActiveItemID id) {
        super(environment, x, y, id, null);
        lifetime = -1;
    }

    /**
     * Initializes a new InfPutPheromone object
     * @see Influence
     */
    public InfPutPheromone(Environment environment, int x, int y, ActiveItemID id, int time) {
        super(environment, x, y, id, null);
        lifetime = time;
    }

    /**
     * Gets the area of effect (the World to effect) for this Influence
     * @return The PheromoneWorld
     */
    @Override
    public PheromoneWorld getAreaOfEffect() {
        return getEnvironment().getPheromoneWorld();
    }

    @Override
    public AgentActionEvent effectuateEvent() {
        return putPheromone(getX(), getY(), getLifeTime());
    }

    /**
     * Puts a new Pheromone on the given coordinates in this PheromoneWorld
     * with a specified initial lifetime. If there already is a Pheromone
     * on the specified coordinate, reinforces this Pheromone with the
     * specified lifetime amount.
     * Updates the gui to show the pheromone.
     *
     * @param x        X-coordinate of the Pheromone that we want to put down
     * @param y        Y-coordinate of the Pheromone that we want to put down
     * @param lifetime The initial lifetime for the Pheromone
     * @post           A new Pheromone is created with the given coordinates
     *                 and placed at the given location in this PheromoneWorld.
     *                 If a Pheromone already exists at the given location,
     *                 that Pheromone is reinforced by <code>lifetime</code>.
     * @post           The Pheromone is shown on the gui.
     */
    protected synchronized AgentActionEvent putPheromone(int x, int y, int lifetime) {
        if (lifetime <= 0) {
            getAreaOfEffect().put(x, y);
            //putAndPropagate(x, y);
        } else {
            getAreaOfEffect().put(x, y, lifetime);
            //putAndPropagate(x, y, lifetime);
        }

        AgentActionEvent event = new AgentActionEvent(this);
        event.setAction(AgentActionEvent.PUT_PHEROMONE);
        //event.setFrom(fx,fy);
        return event;
    }

    public int getLifeTime() {
        return lifetime;
    }
}
