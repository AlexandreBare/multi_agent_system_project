package support;

import environment.ActiveItemID;
import environment.Environment;
import environment.world.agent.AgentWorld;
import util.event.AgentActionEvent;

/**
 * A class for influences for skipping a turn.
 */
public class InfSkip extends Influence {

    /**
     * Initializes a new InfSkip object
     * Cfr. super
     */
    public InfSkip(Environment environment, ActiveItemID id) {
        super(environment, 0, 0, id, null);
    }

    public InfSkip(Environment environment) {
        this(environment, null);
    }

    /**
     * Gets the area of effect (the World it wants to effect) for this
     * Influence. We return AgentWorld but in fact this influence doesn't
     * affect any world. Nothing will happen at all.
     * @return The AgentWorld
     */
    @Override
    public AgentWorld getAreaOfEffect() {
        return getEnvironment().getAgentWorld();
    }


    @Override
    public AgentActionEvent effectuateEvent() {
        return idleEnergy(getID());
    }

    protected synchronized AgentActionEvent idleEnergy(ActiveItemID agent) {

        AgentActionEvent event = new AgentActionEvent(this);
        event.setAction(AgentActionEvent.IDLE_ENERGY);
        event.setAgent(getAreaOfEffect().getAgent(agent));
        return event;
    }
}
