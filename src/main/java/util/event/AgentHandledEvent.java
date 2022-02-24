package util.event;

import agent.AgentImp;
import environment.ActiveItemID;

public class AgentHandledEvent extends Event {

    private ActiveItemID ID;
    private AgentImp agent;

    
    public AgentHandledEvent(Object thrower) {
        super(thrower);
    }

    public ActiveItemID getID() {
        return ID;
    }

    protected void setID(ActiveItemID ID) {
        this.ID = ID;
    }

    public AgentImp getAgent() {
        return agent;
    }

    public void setAgent(AgentImp agent) {
        this.agent = agent;
        setID(agent.getActiveItemID());
    }
}
