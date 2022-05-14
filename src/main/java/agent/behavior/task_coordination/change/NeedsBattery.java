package agent.behavior.task_coordination.change;

import agent.AgentState;
import agent.behavior.BehaviorChange;
import environment.CellPerception;
import environment.Coordinate;
import environment.EnergyValues;
import environment.world.gradient.GradientRep;

import java.util.Random;
import java.util.Set;

public class NeedsBattery extends BehaviorChange {
    private boolean needsBattery = false;
    private Random rand = new Random(42);

    @Override
    public void updateChange() {
        AgentState agentState = this.getAgentState();
        CellPerception agentCell = agentState.getPerception().getCellPerceptionOnRelPos(0, 0);
        GradientRep gradientRep = agentCell.getGradientRepresentation().orElse(null);
        if (gradientRep != null) {
            Set<String> memoryKeys = agentState.getMemoryFragmentKeys();
            if (memoryKeys.contains("ShortestPath2Packet") && memoryKeys.contains("ShortestPath2Destination")) {
                int length_path_2_packet = Coordinate.string2Coordinates(agentState.getMemoryFragment("ShortestPath2Packet")).size();
                int length_path_2_destination = Coordinate.string2Coordinates(agentState.getMemoryFragment("ShortestPath2Destination")).size();
                this.needsBattery = agentState.getBatteryState() <= EnergyValues.BATTERY_SAFE_MIN
                        + gradientRep.getValue() * EnergyValues.BATTERY_DECAY_STEP
                        + length_path_2_packet * EnergyValues.BATTERY_DECAY_STEP
                        + length_path_2_destination * EnergyValues.BATTERY_DECAY_STEP_WITH_CARRY
                        + rand.nextInt(5, 50) * EnergyValues.BATTERY_DECAY_STEP;
            }else{
                this.needsBattery = agentState.getBatteryState() <= EnergyValues.BATTERY_SAFE_MIN
                        + gradientRep.getValue() * EnergyValues.BATTERY_DECAY_STEP
                        + rand.nextInt(5, 50) * EnergyValues.BATTERY_DECAY_STEP;

            }
        }

    }

    @Override
    public boolean isSatisfied() {
        // Decides when the Behavior change is triggered, i.e.,
        // if the agent has not enough battery, it will change to FindCharger Behaviour
        return this.needsBattery;
    }
}