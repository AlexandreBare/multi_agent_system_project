package support;

import environment.ActiveItemID;

/**
 * A class representing the results (outcomes) of an Agent's perception-phase.
 */
public class PerceptionOutcome extends Outcome {

    /**
     * Initializes a new PerceptionOutcome object
     *
     * @param agent    The ID of the Agent involved
     * @param acted    Whether the agent has acted
     * @param syncSet  The synchronization set 'agent' belongs to
     */
    public PerceptionOutcome(ActiveItemID agent, boolean acted, ActiveItemID[] syncSet) {
        super(agent, acted, syncSet);
        setCorrespondingHandler("EOPHandler");
    }

    /**
     * Gets the type of this PerceptionOutcome
     * @return This PerceptionOutcome's type
     */
    public String getType() {
        return "perception";
    }
}
