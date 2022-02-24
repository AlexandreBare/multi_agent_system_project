package support;

import java.awt.Color;

import environment.ActiveItemID;
import environment.Environment;
import environment.world.flag.Flag;
import environment.world.flag.FlagWorld;
import util.event.AgentActionEvent;

/**
 * A class for influences for putting down flags.
 */
public class InfPutFlag extends Influence {

    /**
     * Initializes a new InfPutFlag object
     * Cfr. super
     */
    public InfPutFlag(Environment environment, int x, int y, ActiveItemID id, Color c) {
        super(environment, x, y, id, c);
    }

    /**
     * Gets the area of effect (the World it wants to effect) for this Influence
     * @return The FlagWorld
     */
    @Override
    public FlagWorld getAreaOfEffect() {
        return getEnvironment().getFlagWorld();
    }

    @Override
    public AgentActionEvent effectuateEvent() {
        return putFlag(getX(), getY(), getColor());
    }

    /**
     * Puts a new flag with a given color on the given coordinates in this FlagWorld.
     * Updates the gui to show the flag.
     *
     * @param x    X-coordinate of the Flag that we want to put down
     * @param y    Y-coordinate of the Flag that we want to put down
     * @param c    Color of the Flag that we want to put down
     * @post       A new Flag is created with the given coordinates and color and placed
     *             at the given location in this FlagWorld
     * @post       The flag is shown on the gui.
     */
    protected synchronized AgentActionEvent putFlag(int x, int y, Color c) {
        getAreaOfEffect().putItem(x, y, new Flag(x, y, c));

        AgentActionEvent event = new AgentActionEvent(this);
        event.setAction(AgentActionEvent.PUT_FLAG);
        event.setTo(x, y);
        return event;

    }

}
