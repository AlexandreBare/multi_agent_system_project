package environment;

import java.io.Serializable;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import support.Outcome;
import synchronizer.Synchronization;
import util.Mutex;

/**
 * This class provides the basics of threading methods for active objects and
 * agents in the Environment.
 */
abstract public class ActiveImp implements Serializable, Runnable {


    private boolean suspendRequested, firstCycle;
    protected boolean running, initialRun, perceiving, talking, doing;
    private final Object dummy = new Object();
    protected Environment environment;
    private Synchronization synchronizer;
    protected Perception perception;
    protected int nbTurn;
    private int syncTime;
    private ActiveItemID[] syncSet;
    protected List<ActiveItemID> synchroCandidates;
    protected final Mutex lock;
    protected final ActiveItemID ID;


    private final Logger logger = Logger.getLogger(ActiveImp.class.getName());


    public ActiveImp(ActiveItemID id) {
        this.ID = id;
        this.running = false;
        this.initialRun = true;
        this.firstCycle = true;
        this.suspendRequested = false;
        this.perceiving = false;
        this.talking = true;
        this.doing = false;
        this.lock = new Mutex();
    }

    //INTERFACE TO SYNCHRONIZER

    abstract protected void cleanup();

    /**
     * Starts this AgentImps execution
     */
    public void awake() {
        nbTurn = 0;
        Thread t = new Thread(this);
        running = true;
        t.start();
    }

    /**
     * Stops this agent.
     */
    public void finish() {
        //stopping thread
        running = false;
        requestResume();
    }

    //protected void cleanup() {
        //finishing up
    //}

    //THREAD CONTROL

    /**
     * Check whether a request for suspension of the thread associated with
     * this AgentImp instance is pending, and, if so, suspend this thread.
     */
    protected void checkSuspended() {
        try {
            synchronized (dummy) {
                while (suspendRequested) {
                    dummy.wait();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Request the suspension of the thread associated with this AgentImp.
     */
    protected void requestSuspend() {
        suspendRequested = true;
    }

    /**
     * Request to wake up the thread associated with this AgentImp, if it is suspended.
     */
    protected void requestResume() {
        suspendRequested = false;
        synchronized (dummy) {
            dummy.notify();
        }
    }

    // INTERFACE TO RUNNING THREAD


    /**
     * The run cycle of the thread associated with this AgentImp.
     */
    public void run() {
        if (initialRun) {
            perceive();
            initialRun = false;
        }
        while (running) {
            checkSuspended();
            if (running) {
                if (checkSynchronize()) {
                    synchronize();
                }
                executeCurrentPhase();
            }
        }
        cleanup();
    }

    /**
     * Ask a perception from Environment and retrieve information for
     * synchronisation from the View
     */
    protected void perceive() {
        nbTurn++;
        setPerception(getEnvironment().getPerception(getActiveItemID()));
        //synchroCandidates = getVisibleActiveItems();
        synchroCandidates = getAllActiveItemIDs();
        setSyncTime(getEnvironment().getTime());
    }

    protected boolean checkSynchronize() {
        boolean result = firstCycle | perceiving;
        if (firstCycle) {
            firstCycle = false;
        }
        return result;
    }


    protected void executeCurrentPhase() {
        getLock().acquireLock();
        execCurrentPhase();
        //getLock().releaseLock();
    }

    /**
     * Implements the execution of a synchronization phase.
     */
    abstract protected void execCurrentPhase();

    protected void setNextPhase() {
        if (perceiving) {
            perceiving = false;
            talking = true;
        } else if (talking) {
            talking = false;
            doing = true;
        } else if (doing) {
            doing = false;
            perceiving = true;
        }
    }

    void activateNewPhase(boolean environmentPermissionForNextPhase) {
        if (environmentPermissionNeededForNextPhase()) {
            if (environmentPermissionForNextPhase) {
                setNextPhase();
            }
        } else {
            setNextPhase();
        }
        requestResume();
    }

    abstract protected boolean environmentPermissionNeededForNextPhase();

    /**
     * Recompute the syncSet for this agentImp's next action-cycle.
     */
    protected void synchronize() {
        getSynchronizer().synchronize(getActiveItemID(), getSynchroCandidates(),
                                      getSyncTime());
        setSyncSet(getSynchronizer().getSyncSet(getActiveItemID()));
        this.logger.fine("SyncSet for " + getActiveItemID() + " recomputed: [");
        for (ActiveItemID j : syncSet) {
            this.logger.fine("" + j.getID());
        }
        this.logger.fine("]");

    }

    /**
     * Sends an Outcome to the Environment. This represents the intention of
     * the agent.
     */
    protected void concludePhaseWith(Outcome outcome) {
        if (outcome != null) {
            getEnvironment().collectOutcome(outcome);
        }
        requestSuspend();
        this.logger.fine("Suspension of agentImp " + getActiveItemID() + " requested");

        getLock().releaseLock();
    }

    abstract protected void action();


    /**
     * Returns the IDs of all the ActiveItems in the Environment.
     *
     * @return An array containing the ID's of all the ActiveItems in the
     *         Environment.
     */
    List<ActiveItemID> getAllActiveItemIDs() {
        return this.getEnvironment().getActiveItemIDs().stream()
            .filter(id -> id != this.getActiveItemID())
            .collect(Collectors.toList());
    }


    // GETTERS & SETTERS

    /**
     * Returns the current local view of the active object
     */
    public Perception getPerception() {
        return perception;
    }

    protected void setPerception(Perception perception) {
        this.perception = perception;
    }


    /**
     * Returns the unique ID number of this active object
     */
    public ActiveItemID getActiveItemID() {
        return ID;
    }
    /**
     * Return the set of agent- and object-id's that candidate to be synchronized with.
     */
    protected List<ActiveItemID> getSynchroCandidates() {
        return synchroCandidates;
    }

    /**
     * Return the syncTime of this AgentImp.
     */
    protected int getSyncTime() {
        return syncTime;
    }

    protected ActiveItemID[] getSyncSet() {
        return syncSet;
    }

    private void setSyncSet(ActiveItemID[] set) {
        syncSet = set;
    }

    public Environment getEnvironment() {
        return environment;
    }

    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    public Synchronization getSynchronizer() {
        return synchronizer;
    }

    public void setSynchronizer(Synchronization synchro) {
        synchronizer = synchro;
    }

    protected void setSyncTime(int time) {
        syncTime = time;
    }

    Mutex getLock() {
        return lock;
    }
}
