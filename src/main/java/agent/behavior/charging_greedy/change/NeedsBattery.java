package agent.behavior.charging_greedy.change;

import agent.AgentState;
import agent.behavior.BehaviorChange;
import environment.CellPerception;
import environment.EnergyValues;
import environment.world.gradient.GradientRep;

import java.util.Random;

public class NeedsBattery extends BehaviorChange {
    private boolean needsBattery = false;
    private Random rand = new Random(42);

    @Override
    public void updateChange() {
        AgentState agentState = this.getAgentState();
        CellPerception agentCell = agentState.getPerception().getCellPerceptionOnRelPos(0, 0);
        GradientRep gradientRep = agentCell.getGradientRepresentation().orElse(null);
        if (gradientRep != null) {
            this.needsBattery = agentState.getBatteryState() <= EnergyValues.BATTERY_SAFE_MIN +
                    (gradientRep.getValue() + rand.nextInt(10, 25)) * EnergyValues.BATTERY_DECAY_STEP_WITH_CARRY;
        }

    }

    @Override
    public boolean isSatisfied() {
        // Decides when the Behavior change is triggered, i.e.,
        // if the agent has not enough battery, it will change to FindCharger Behaviour
        return this.needsBattery;
    }
}