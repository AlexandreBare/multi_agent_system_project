package environment;

import util.event.AgentActionEvent;

public class EnergyValues {
    
    public static final int BATTERY_MIN = 0;
    public static final int BATTERY_MAX = 1000;
    public static final int BATTERY_SAFE_MIN = 10;
    public static final int BATTERY_SAFE_MAX = 950;
    public static final int BATTERY_START = BATTERY_MAX;

    public static final int BATTERY_DECAY_STEP = 10;
    public static final int BATTERY_DECAY_STEP_WITH_CARRY = 20;
    public static final int BATTERY_DECAY_SKIP = 5;
    public static boolean ENERGY_ENABLED = true;


    public static int calculateEnergyCost(AgentActionEvent event, boolean includeCharging) {
        switch (event.getAction()) {
            case AgentActionEvent.STEP:
                if (event.getAgent().hasCarry()) {
                    return EnergyValues.BATTERY_DECAY_STEP_WITH_CARRY;
                } else {
                    return EnergyValues.BATTERY_DECAY_STEP;
                }
            case AgentActionEvent.LOAD_ENERGY:
                // Negative cost since the agent is gaining energy with this event
                return includeCharging ? -event.getValue() : 0;
            default:
                return EnergyValues.BATTERY_DECAY_SKIP;
        }
    }

    public static int calculateEnergyCost(AgentActionEvent event) {
        return EnergyValues.calculateEnergyCost(event, true);
    }
}
