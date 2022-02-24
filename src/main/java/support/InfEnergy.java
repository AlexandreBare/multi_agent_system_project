package support;

import environment.ActiveItemID;
import environment.Environment;
import environment.world.agent.AgentWorld;
import util.event.AgentActionEvent;

/**
 * A class for influences that EnergyStations exercise on the Environment.
 */
public class InfEnergy extends Influence {


    /**
     * The amount of energy that gets transferred to the effect area of this
     * influence
     */
    private final int strength;



    /**
     * Initializes a new InfEnergy object
     * @see Influence
     *
     * @param strength The amount of energy this influence carries
     */
    public InfEnergy(Environment environment, int x, int y, ActiveItemID ID, int strength) {
        super(environment, x, y, ID, null);
        this.strength = strength;
    }

    /**
     * Gets the area of effect (the World it wants to effect) for this Influence
     * @return The AgentWorld
     */
    @Override
    public AgentWorld getAreaOfEffect() {
        return getEnvironment().getAgentWorld();
    }

    @Override
    public AgentActionEvent effectuateEvent() {
        return loadEnergy(getX(), getY(), getLoadAmount());
    }
    protected synchronized AgentActionEvent loadEnergy(int x, int y, int loadAmount) {
        if (getAreaOfEffect().inBounds(x, y) && getAreaOfEffect().getItem(x, y) != null) {
            AgentActionEvent event = new AgentActionEvent(this);
            event.setAction(AgentActionEvent.LOAD_ENERGY);
            event.setAgent(getAreaOfEffect().getAgent(getAreaOfEffect().getItem(x, y).getID()));
            event.setValue(loadAmount);
            return event;
        }
        return null;
    }


    /**
     * Returns the amount of energy that is transferred by this influence
     * @return The strength of this influence
     */
    public int getLoadAmount() {
        return strength;
    }
}
