package synchronizer;

import environment.ActiveItemID;

public class NoSynchronization extends Synchronization {

    /**
     * A variable holding the reference to the dummy synchronizer.
     * @invar synchro instanceof DummySynchronizer
     */
    private Synchronizer synchro;

    /**
     * Initialize a new NoSynchronization-interface.
     */
    public NoSynchronization() {
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
     * @post A new instance of DummySynchronizer is created.
     */
    public void createSynchroPackage() {
        synchro = new DummySynchronizer();
    }
}
