package agent.behavior;


import agent.AgentState;


abstract public class BehaviorChange {
    
    // The behavior state this behavior change belongs to.
    private BehaviorState behaviorState;

    // The agent implementation that should be used to evaluate this behavior change.
    private AgentState agentState;

    

    /**
     * Updates this change. The conditions in this change are
     * computed/verified.
     */
    public abstract void updateChange();

    /**
     * Checks if the condition for this BehaviorChange is satisfied
     */
    abstract public boolean isSatisfied();


    /**
     * Updates this change according to the information contained in the Agent.
     * If the update lets this Change fire and the precondition of the
     * target is true, then the owner Agent's currentBehaviorState is set to
     * the next state pointed by this BehaviorChange.
     */
    public final boolean testChange() {
        if (this.isSatisfied() && this.behaviorState.getBehavior().preCondition(this.getAgentState())) {
            this.getAgentState().getCurrentBehavior().leave(this.getAgentState());
            this.getAgentState().setCurrentBehaviorState(behaviorState);
            return true;
        } 
        
        return false;
    }

    /**
     * Sets the target behavior, specified by its flyweight BehaviorState
     */
    public final void setNextBehavior(BehaviorState bs) {
        behaviorState = bs;
    }

    /**
     * Association to the owning AgentState
     */
    public final AgentState getAgentState() {
        return this.agentState;
    }

    /**
     * Sets association to the owning AgentState
     */
    public final void setAgentState(AgentState agentState) {
        this.agentState = agentState;
    }

    /**
     * DESTRUCTOR
     */
    public void finish() {
        this.agentState = null;
        behaviorState.finish();
        behaviorState = null;
    }
}
