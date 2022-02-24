package synchronizer;

import environment.ActiveItemID;

/**
 * A class serving as the interface for the package dealing with distributed synchronization.
 */
public abstract class DistributedSynchronization extends Synchronization {

    /**
     * A container holding references to instances of PersonalSynchronizer.
     * 
     * @invar synchros.length == getAgentImplementations().getNbAgentImp()
     * @invar for each i in 0..synchros.length-1: synchros[i] instanceof PersonalSynchronizer
     * @invar for each i in 0..synchros.length-1: synchros[i]==getSynchronizer(i)
     */
    protected Synchronizer[] synchros;

    /**
     * Return the synchronizer handling the requests of the agent with name id.
     * @param id The id of the agent issuing a request.
     * @return The synchronizer responsible for handling requests of the agent with name id.
     */
    protected Synchronizer getSynchronizer(ActiveItemID id) {
        return synchros[id.getID()];
    }

    /**
     * Create the instances of classes of the synchronizer package needed at startup time.
     */
    public abstract void createSynchroPackage();
}
