package agent.behavior.planned_charging.change;

import agent.AgentState;
import agent.behavior.BehaviorChange;
import environment.EnergyValues;

public class IsFullyCharged extends BehaviorChange {
    private boolean isFullyCharged = false;

    @Override
    public void updateChange() {
        AgentState agentState = this.getAgentState();
        this.isFullyCharged = agentState.getBatteryState() >= EnergyValues.BATTERY_MAX;
    }

    @Override
    public boolean isSatisfied() {
        // Decides when the Behavior change is triggered, i.e.,
        // if the agent has filled its battery to the fullest, it will change to Pickup Behaviour
        return this.isFullyCharged;
    }
}