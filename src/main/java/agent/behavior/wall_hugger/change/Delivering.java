package agent.behavior.wall_hugger.change;

import agent.behavior.BehaviorChange;

public class Delivering extends BehaviorChange {
    public boolean destPacketPair;
    public boolean isCarrying;
    @Override
    public void updateChange() {
        destPacketPair = matchingPacketAndDestination();
        isCarrying = getAgentState().hasCarry();
    }

    @Override
    public boolean isSatisfied() {
        return destPacketPair && isCarrying;
    }
}
