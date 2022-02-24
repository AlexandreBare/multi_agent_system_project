package support;

import environment.ActiveItemID;
import environment.Environment;
import environment.world.crumb.CrumbWorld;
import util.event.AgentActionEvent;

/**
 * A class for influences for putting down crumbs.
 */
public class InfPutCrumb extends Influence {

    private final int number;


    /**
     * Initializes a new InfPutCrumb object
     * Cfr. super
     */
    public InfPutCrumb(Environment environment, int x, int y, ActiveItemID id, int number) {
        super(environment, x, y, id, null);
        this.number = number;
    }

    /**
     * Gets the area of effect (the World it wants to effect) for this Influence
     * @return The CrumbWorld
     */
    @Override
    public CrumbWorld getAreaOfEffect() {
        return getEnvironment().getCrumbWorld();
    }

    @Override
    public AgentActionEvent effectuateEvent() {
        return putCrumb(getX(), getY(), getNumber());
    }

    /**
     * Puts a new Crumb on the given coordinates in this CrumbWorld
     * with a specified initial number of crumbs. If there already is a
     * Crumb on the specified coordinate, adds the value of number to that
     * Crumb.
     * Updates the gui to show the crumb.
     *
     * @param x      x-coordinate of the Crumb that we want to put down
     * @param y      y-coordinate of the Crumb that we want to put down
     * @param number the number of crumbs to be laid
     * @post         A new Crumb is created with the given coordinates
     *               and placed at the given location in this CrumbWorld.
     *               If a Crumb already exists at the given location,
     *               <code>number</code> is added to that Crumb.
     * @post         The Crumb is shown on the gui.
     */
    protected synchronized AgentActionEvent putCrumb(int x, int y, int number) {
        getAreaOfEffect().put(x, y, number);

        AgentActionEvent event = new AgentActionEvent(this);
        event.setAction(AgentActionEvent.PUT_CRUMB);
        return event ;
    }

    public int getNumber() {
        return number;
    }
}
