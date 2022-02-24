package support;

import environment.ActiveItemID;
import environment.CellPerception;
import environment.Environment;
import environment.world.pheromone.PheromoneWorld;
import util.event.AgentActionEvent;

/**
 * A class for influences for putting down directed pheromones.
 */
public class InfPutDirPheromone extends Influence {

    private final int lifetime;
    private final CellPerception target;
    

    /**
     * Initializes a new InfPutDirPheromone object
     * @see Influence
     */
    public InfPutDirPheromone(Environment environment, int x, int y, ActiveItemID id, CellPerception target) {
        super(environment, x, y, id, null);
        lifetime = -1;
        this.target = target;
    }

    /**
     * Initializes a new InfPutPheromone object
     * @see Influence
     */
    public InfPutDirPheromone(Environment environment, int x, int y, ActiveItemID id, int time, CellPerception target) {
        super(environment, x, y, id, null);
        lifetime = time;
        this.target = target;
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
        return putDirPheromone(getX(), getY(), getLifeTime(), getTarget());
    }

    /**
     * Puts a new DirPheromone on the given coordinates in this PheromoneWorld
     * with a specified initial lifetime and target. If there already is a
     * DirPheromone on the specified coordinate, reinforces this DirPheromone
     * with the specified lifetime amount.
     * Updates the gui to show the pheromone.
     *
     * @param x        X-coordinate of the DirPheromone that we want to put down
     * @param y        Y-coordinate of the DirPheromone that we want to put down
     * @param lifetime The initial lifetime for the DirPheromone
     * @param target   The target for the DirPheromone
     * @post           A new DirPheromone is created with the given coordinates
     *                 and placed at the given location in this PheromoneWorld.
     *                 If a DirPheromone already exists at the given location,
     *                 that DirPheromone is reinforced by <code>lifetime</code>.
     * @post           The DirPheromone is shown on the gui.
     */
    protected synchronized AgentActionEvent putDirPheromone(int x, int y, int lifetime, CellPerception target) {
        if (lifetime <= 0) {
            getAreaOfEffect().putDirected(x, y, target);
        } else {
            getAreaOfEffect().putDirected(x, y, lifetime, target);
        }

        AgentActionEvent event = new AgentActionEvent(this);
        event.setAction(AgentActionEvent.PUT_PHEROMONE);
        //event.setFrom(fx,fy);
        return event;
    }

    public int getLifeTime() {
        return lifetime;
    }

    public CellPerception getTarget() {
        return target;
    }
}
