package support;

import environment.ActiveItemID;
import environment.Environment;
import environment.world.agent.AgentWorld;
import util.event.AgentActionEvent;

/**
 * A class for influences for stepping to a certain position.
 */
public class InfStep extends Influence {

    /**
     * Initializes a new InfStep object
     * Cfr. super
     */
    public InfStep(Environment environment, int x, int y, ActiveItemID agent) {
        super(environment, x, y, agent, null);
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
        return step(getX(), getY(), getID());
    }

    /**
     * Make a given Agent in this AgentWorld step to the given coordinates
     *
     * @param  tx       x coordinate
     * @param  ty       y coordinate
     * @param  agent    The ID of the agent that is to step
     * @post    'agent' is situated on coordinate tx,ty
     * @post    agent gets informed of its new coordinates
     * @post    The agent's move is shown on the gui
     */
    protected synchronized AgentActionEvent step(int tx, int ty, ActiveItemID agent) {
        AgentActionEvent event = new AgentActionEvent(this);
        event.setAction(AgentActionEvent.STEP);
        event.setFrom(getAreaOfEffect().getAgent(agent).getX(), getAreaOfEffect().getAgent(agent).getY());
        event.setTo(tx, ty);
        event.setPacket(getAreaOfEffect().getAgent(agent).getCarry().orElse(null));
        event.setAgent(getAreaOfEffect().getAgent(agent));

        getAreaOfEffect().moveAgent(getAreaOfEffect().getAgent(agent).getX(), getAreaOfEffect().getAgent(agent).getY(), tx, ty,
                getAreaOfEffect().getAgent(agent));

        return event;
    }

}
