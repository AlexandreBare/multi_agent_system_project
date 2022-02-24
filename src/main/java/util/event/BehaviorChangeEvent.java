package util.event;

import agent.behavior.BehaviorChange;
import environment.ActiveItemID;

public class BehaviorChangeEvent extends Event {
    private ActiveItemID agent;
    private String behaviorName;

    
    public BehaviorChangeEvent(Object thrower) {
        super(thrower);
    }

    public ActiveItemID getAgent() {
        return agent;
    }

    public void setAgent(ActiveItemID agent) {
        this.agent = agent;
    }

    public String getBehaviorName() {
        return behaviorName;
    }

    public void setBehaviorName(String behaviorName) {
        this.behaviorName = behaviorName;
    }

    public BehaviorChange getChange() {
        return (BehaviorChange) getThrower();
    }
}
