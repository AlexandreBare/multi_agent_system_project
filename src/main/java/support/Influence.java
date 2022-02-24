package support;

import java.awt.Color;

import environment.ActiveItemID;
import environment.Environment;
import environment.World;
import util.event.AgentActionEvent;

/**
 * A class for influences.
 */
public abstract class Influence {

    private final int x;
    private final int y;
    private final ActiveItemID ID;
    private final Color color;
    private final Environment env;

    /**
     * Initializes a new Influence object
     *
     * @param environment  The Environment instance
     * @param x            An X-coordinate for this influence
     * @param y            A Y-coordinate for this influence
     * @param id           The ID of the agent or object this influence
     *                     originates from
     * @param c            A color for this influence
     */
    public Influence(Environment environment, int x, int y, ActiveItemID id, Color c) {
        this.x = x;
        this.y = y;
        this.ID = id;
        this.color = c;
        this.env = environment;
    }

    /**
     * Gets the x attribute of the Influence object
     * @return The x value
     */
    public int getX() {
        return x;
    }

    /**
     * Gets the y attribute of the Influence object
     * @return The y value
     */
    public int getY() {
        return y;
    }

    /**
     * Gets the ID attribute of the Influence object
     * @return The ID value
     */
    public ActiveItemID getID() {
        return ID;
    }

    /**
     * Gets the color attribute of the Influence object
     * @return The color value
     */
    public Color getColor() {
        return color;
    }

    /**
     * Returns the environment
     * @return The environment
     */
    public Environment getEnvironment() {
        return env;
    }

    /**
     * Gets the area of effect (the World it wants to effect) for this Influence
     * @return This Influence's areaOfEffect
     */
    public abstract World<?> getAreaOfEffect();


    /**
     * effectuates the influence
     */
    public void effectuate() {
        AgentActionEvent event = effectuateEvent();
        if (event != null) {
            getAreaOfEffect().getEventBus().post(event);
        }
    }

    public abstract AgentActionEvent effectuateEvent();

    public int getPriority() {
        return ID.getActionPriority().getPriority();
    }

}
