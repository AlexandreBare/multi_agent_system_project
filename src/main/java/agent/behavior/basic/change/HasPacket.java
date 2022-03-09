package agent.behavior.basic.change;

import agent.behavior.BehaviorChange;

public class HasPacket extends BehaviorChange {
    private boolean hasPacket = false;

    @Override
    public void updateChange() {
        this.hasPacket = this.getAgentState().hasCarry();
    }

    @Override
    public boolean isSatisfied() {
        // Decides when the Behavior change is triggered, i.e.,
        // if the agent has a packet, it will change to Deliver Behaviour
        return this.hasPacket;
    }
}