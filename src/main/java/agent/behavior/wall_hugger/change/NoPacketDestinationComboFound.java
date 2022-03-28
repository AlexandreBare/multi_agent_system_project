package agent.behavior.wall_hugger.change;

import agent.behavior.BehaviorChange;

public class NoPacketDestinationComboFound extends BehaviorChange {
    @Override
    public void updateChange() {}

    @Override
    public boolean isSatisfied() {
        return !matchingPacketAndDestination();
    }
}
