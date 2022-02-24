package support;

import environment.ActiveItemID;

/**
 * A class representing the outcome of an Agent's action-phase.
 */
public class ActionOutcome extends Outcome {

    private final Influence influence;

    /**
     * Initializes a new ActionOutcome instance
     *  @param agent        the ID of the agent that acted
     * @param acted        whether 'agent' has acted or not
     * @param syncSet      the synchronisation set 'agent' belongs to
     * @param influence    the Influence 'agent' has produced
     */
    public ActionOutcome(ActiveItemID agent, boolean acted, ActiveItemID[] syncSet,
                         Influence influence) {
        super(agent, acted, syncSet);
        this.influence = influence;
        setCorrespondingHandler("Reactor");
    }

    /**
     * Gets the influence of this ActionOutcome
     * @return This ActionOutcome's influence
     */
    public Influence getInfluence() {
        return influence;
    }

    /**
     * Gets the type of this ActionOutcome
     * @return This ActionOutcome's type
     */
    public String getType() {
        return "action";
    }
}
