package synchronizer;

import environment.ActiveItemID;

/**
 * A class serving as the interface for the package of classes responsible for central synchronization; also this class assumes
 * responsibility over the creation of instances of al classes of the package needed at startup time.
 */
public class CentralSynchronization extends Synchronization {

    /**
     * A variable holding the reference to the central synchronizer.
     * @invar synchro instanceof CentralSynchronizer
     */
    private Synchronizer synchro;

    /**
     * Initialize a new CentralSynchronization-interface.
     */
    public CentralSynchronization() {
        super();
    }

    /**
     * Return the synchronizer handling the requests of the agent with name id.
     * @param id The id of the agent issuing a request.
     * @return The synchronizer responsible for handling requests of the agent with name id.
     */
    protected Synchronizer getSynchronizer(ActiveItemID id) {
        return synchro;
    }

    /**
     * Create the instances of classes of the synchronizer package needed at startup time.
     * @post A new instance of CentralSynchronizer is created.
     */
    public void createSynchroPackage() {
        synchro = new CentralSynchronizer(getActiveItemContainer());
    }
}
