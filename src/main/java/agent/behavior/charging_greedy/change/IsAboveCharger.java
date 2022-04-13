package agent.behavior.charging_greedy.change;

import agent.behavior.BehaviorChange;
import environment.CellPerception;

public class IsAboveCharger extends BehaviorChange {
    private boolean isAboveCharger = false;

    @Override
    public void updateChange() {
        CellPerception cellBelowAgent = this.getAgentState().getPerception().getCellPerceptionOnRelPos(0, 1);
        if (cellBelowAgent != null)
            this.isAboveCharger = cellBelowAgent.containsEnergyStation();
    }

    @Override
    public boolean isSatisfied() {
        // Decides when the Behavior change is triggered, i.e.,
        // if the agent is above an energy station, it will change to Charge Behaviour
        return this.isAboveCharger;
    }
}