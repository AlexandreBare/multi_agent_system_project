package synchronizer;

import java.util.List;

import environment.ActiveItemContainer;
import environment.ActiveItemID;
import environment.Environment;

/**
 * An abstract superclass for interfaces of the synchronizer-package.
 */
public abstract class Synchronization {

    /**
     * A pointer towards the interface of the environment package.
     */
    private Environment env;

    /**
     * A pointer towards the container with all active items.
     */
    private ActiveItemContainer activeItemContainer;

    /**
     * Return a reference to the interface of the environment package.
     */
    Environment getEnvironment() {
        return env;
    }

    /**
     * Associate this Synchronization instance with environment.
     * 
     * @param environment The interface of the environment-package
     * @pre   environment <> null
     * @post  new.getEnvironment()==environment
     */
    public void setEnvironment(Environment environment) {
        this.env = environment;
    }

    /**
     * Return a reference to the interface of the agentImplementations package.
     */
    ActiveItemContainer getActiveItemContainer() {
        return activeItemContainer;
    }

    /**
     * Associate this Synchronization instance with activeItemContainer.
     * @param activeItemContainer The container containing all active items.
     * @pre activeItemContainer <> null
     * @post new.getAgentImplementations() == activeItemContainer
     */
    public void setAgentImplementations(ActiveItemContainer activeItemContainer) {
        this.activeItemContainer = activeItemContainer;
    }

    /**
     * Return the syncSet of the agent with the name id.
     * @param id The name of the agent requesting its sync set.
     * @return The set of id's of agents with which the requesting agent is bound to synchronize.
     */
    public ActiveItemID[] getSyncSet(ActiveItemID id) {
        return getSynchronizer(id).getSyncSet(id);
    }

    /**
     * Form the sync set of the agent with name id, given the set of candidates with which to synchronize and given
     * syncTime time.
     * @param id The id of the agent requesting formation of its sync set.
     * @param setOfCandidates The set of agent-id's that are candidate-members of the sync set to be formed.
     * @param time The syncTime of the requesting agent.
     */

    public void synchronize(ActiveItemID id, List<ActiveItemID> setOfCandidates, int time) {
        getSynchronizer(id).synchronize(id, setOfCandidates, time);
    }

    /**
     * Return the synchronizer handling the requests of the agent with name id.
     * @param id The id of the agent issuing a request.
     * @return The synchronizer responsible for handling requests of the agent with name id.
     */
    protected abstract Synchronizer getSynchronizer(ActiveItemID id);

    /**
     * Create the instances of classes of the synchronizer package needed at startup time.
     */
    public abstract void createSynchroPackage();

}
