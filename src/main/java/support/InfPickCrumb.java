package support;

import environment.ActiveItemID;
import environment.Environment;
import environment.world.crumb.CrumbWorld;
import util.event.AgentActionEvent;

/**
 * A class for influences for picking up crumbs.
 */
public class InfPickCrumb extends Influence {

    private final int number;
    

    /**
     * Initializes a new InfPutCrumb object
     * Cfr. super
     */
    public InfPickCrumb(Environment environment, int x, int y, ActiveItemID id, int number) {
        super(environment, x, y, id, null);
        this.number = number;
    }

    /**
     * Gets the area of effect (the World it wants to effect) for this
     * Influence
     * @return The CrumbWorld
     */
    @Override
    public CrumbWorld getAreaOfEffect() {
        return getEnvironment().getCrumbWorld();
    }

    @Override
    public AgentActionEvent effectuateEvent() {
        return pickCrumb(getX(), getY(), getNumber());
    }

    protected synchronized AgentActionEvent pickCrumb(int x, int y, int number) {
        getAreaOfEffect().pick(x, y, number);

        AgentActionEvent event = new AgentActionEvent(this);
        event.setAction(AgentActionEvent.PICK_CRUMB);
        return event;
    }


    public int getNumber() {
        return number;
    }
}
