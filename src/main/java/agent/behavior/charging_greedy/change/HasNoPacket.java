package agent.behavior.charging_greedy.change;

import agent.behavior.BehaviorChange;

public class HasNoPacket extends BehaviorChange {
    private boolean hasPacket = false;

    @Override
    public void updateChange() {
        this.hasPacket = this.getAgentState().hasCarry();
    }

    @Override
    public boolean isSatisfied() {
        // Decides when the Behavior change is triggered, i.e.,
        // if the agent has no packet, it will change to Deliver Behaviour
        return ! this.hasPacket;
    }
}