package support;

import environment.ActiveItemID;
import environment.Environment;
import environment.world.agent.AgentWorld;
import util.event.AgentActionEvent;

public class InfNOP extends Influence {

    public InfNOP(Environment environment, ActiveItemID id) {
        super(environment, -1, -1, id, null);
    }

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
