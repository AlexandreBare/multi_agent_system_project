package support;

import environment.ActiveItemID;

/**
 * A class representing the outcome of an action phase. Depending on the kind
 * of activity performed in that phase, the outcome will subclass into a
 * particular kind. The basic idea behind this kind of class is to provide a
 * uniform wrapper for very different sort of outcomes, which allows for a
 * generic collector dealing uniformly with them all.
 */

public abstract class Outcome {

    private final ActiveItemID agent;
    private final boolean acted;
    private final ActiveItemID[] syncSet;
    private String correspondingHandler;

    /**
     * Initializes a new Outcome object
     *
     * @param agent    The ID of the Agent involved
     * @param acted    Whether the agent has acted
     * @param syncSet  The synchronisation set 'agent' belongs to
     */
    public Outcome(ActiveItemID agent, boolean acted, ActiveItemID[] syncSet) {
        this.agent = agent;
        this.syncSet = syncSet;
        this.acted = acted;
    }

    /**
     * Gets the agentID of this Outcome
     * @return This agentID
     */
    public ActiveItemID getAgentID() {
        return agent;
    }

    /**
     * Gets the syncSet of this Outcome
     * @return This syncSet
     */
    public ActiveItemID[] getSyncSet() {
        return syncSet;
    }

    /**
     * Check if this Outcome's agent has acted or not
     * @return 'true' if this agent has acted
     */
    public boolean hasActed() {
        return acted;
    }

    /**
     * Gets the type of this Outcome
     * @return This Outcome's type
     */
    public abstract String getType();

    /**
     * Sets the correspondingHandler of this Outcome
     * @param handler The new correspondingHandler value
     */
    protected void setCorrespondingHandler(String handler) {
        correspondingHandler = handler;
    }

    /**
     * Gets the correspondingHandler of this Outcome
     * @return This Outcome's correspondingHandler
     */
    public String getCorrespondingHandler() {
        return correspondingHandler;
    }

    /**
     * Check if this Outcome is to be handled by the given handler 'handler' (by String)
     *
     * @param handler  The handler for which we are checking if it can handle this Outcome
     * @return         'true' if this Outcome's corresponding handler equals 'handler'
     */
    public boolean toBeHandledBy(String handler) {
        return correspondingHandler.equals(handler);
    }

    /**
     * Gets the vote for continuing with the next phase
     * @return 'true'
     */
    public boolean getVoteForContinuingWithNextPhase() {
        return true;
    }

}
