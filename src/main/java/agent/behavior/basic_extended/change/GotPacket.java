package agent.behavior.basic_extended.change;

import agent.behavior.BehaviorChange;

public class GotPacket extends BehaviorChange {
    @Override
    public void updateChange() {

    }

    @Override
    public boolean isSatisfied() {
        return this.getAgentState().hasCarry();
    }
}
