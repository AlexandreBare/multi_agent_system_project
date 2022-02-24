package support;

import environment.ActiveItemID;
import environment.Environment;
import environment.world.pheromone.PheromoneWorld;
import util.event.AgentActionEvent;

/**
 * A class for influences for removing pheromones.
 */
public class InfRemovePheromone extends Influence {

    /**
     * Initializes a new InfPutPheromone object
     * @see Influence
     */
    public InfRemovePheromone(Environment environment, int x, int y, ActiveItemID id) {
        super(environment, x, y, id, null);
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
        return removePheromone(getX(), getY());
    }

    /**
     * Removes a pheromone from a given coordinate.
     *
     * @param x the x-coordinate of the pheromone to be removed
     * @param y the y-coordinate of the pheromone to be removed
     */
    protected synchronized AgentActionEvent removePheromone(int x, int y) {
        getAreaOfEffect().free(x, y);
        AgentActionEvent event = new AgentActionEvent(this);
        event.setAction(AgentActionEvent.REMOVE_PHEROMONE);
        return event;
    }
}
