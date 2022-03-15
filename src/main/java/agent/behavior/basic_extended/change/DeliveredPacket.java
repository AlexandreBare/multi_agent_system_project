package agent.behavior.basic_extended.change;

import agent.behavior.BehaviorChange;

public class DeliveredPacket extends BehaviorChange {
    @Override
    public void updateChange() {

    }

    @Override
    public boolean isSatisfied() {
        return !this.getAgentState().hasCarry();
    }
}
