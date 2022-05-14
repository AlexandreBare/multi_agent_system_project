package agent.behavior.task_coordination.change;

import agent.AgentState;
import agent.behavior.BehaviorChange;

public class HasPacketToGather extends BehaviorChange {
    private boolean hasPacketToGather = false;

    @Override
    public void updateChange() {
        AgentState agentState = this.getAgentState();
        this.hasPacketToGather = agentState.hasCarry()
                && agentState.getMemoryFragmentKeys().contains("ShortestPath2Gather");
    }

    @Override
    public boolean isSatisfied() {
        // Decides when the Behavior change is triggered, i.e.,
        // if the agent has a packet, it will change to Gather Behaviour
        return this.hasPacketToGather;
    }
}