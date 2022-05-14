package agent.behavior.task_coordination.change;

import agent.AgentState;
import agent.behavior.BehaviorChange;

public class HasPacketToDeliver extends BehaviorChange {
    private boolean hasPacketToDeliver = false;

    @Override
    public void updateChange() {
        AgentState agentState = this.getAgentState();
        this.hasPacketToDeliver = agentState.hasCarry()
                && agentState.getMemoryFragmentKeys().contains("ShortestPath2Destination");
    }

    @Override
    public boolean isSatisfied() {
        // Decides when the Behavior change is triggered, i.e.,
        // if the agent has a packet, it will change to Deliver Behaviour
        return this.hasPacketToDeliver;
    }
}