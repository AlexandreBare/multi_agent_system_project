package environment;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.stream.Stream;

import com.google.common.eventbus.EventBus;

import agent.AgentImp;
import agent.FileAgentImp;
import synchronizer.Synchronization;
import util.Variables;

/**
 * This class is a container for AgentImp. It acts as an interface
 * to the Environment and Synchronizer packages
 */
public class ActiveItemContainer {


    /**
     * The Implementations (ID of an agent mapped to its implementation)
     */
    protected final Map<ActiveItemID, AgentImp> agents;
    /**
     * Similarly keep track of implementations of other active items
     */
    protected final Map<ActiveItemID, ActiveImp> otherActiveItems;

    private Synchronization synchronizer;
    private Environment environment;


    private final Logger logger = Logger.getLogger(ActiveItemContainer.class.getName());


    /**
     * Creates a AgentImplementations container
     */
    public ActiveItemContainer() {
        this.agents = new HashMap<>();
        this.otherActiveItems = new HashMap<>();
    }

    //CONSTRUCTION

    /**
     * Creates the agentImps and related objects
     * post condition: getNbAgents = count(all id: getAgentImp(id) <> null)
     */
    public void createAgentImps(List<ActiveItemID> agentIds, String behavior, EventBus eventBus) {
        try {
            this.logger.fine(String.format("Nb AgentImps = %d", agentIds.size()));

            String behaviorFile = Variables.IMPLEMENTATIONS_PATH + behavior + ".txt";

            this.logger.fine(String.format("Behavior file %s opened.", behaviorFile));
            for (ActiveItemID id : agentIds) {
                AgentImp imp = new FileAgentImp(id, behaviorFile, eventBus);

                imp.setEnvironment(getEnvironment());
                imp.setSynchronizer(getSynchronizer());
                imp.createBehavior();
                agents.put(imp.getActiveItemID(), imp);
            }
        } catch (Exception e) {
            this.logger.severe(e.getMessage());
        }
    }

    public void createObjectImps(Collection<ActiveItem<?>> aObjects) {
        for (ActiveItem<?> item : aObjects) {
            var newImp = item.generateImplementation(getEnvironment(), getSynchronizer());
            otherActiveItems.put(newImp.getActiveItemID(), newImp);
        }
    }

    //AGENT IMP METHODS


    /**
     *Starts all AgentImps and ActiveObjectImps.
     */
    public void startAllActiveImps() {
        this.getActiveObjects().forEach(ActiveImp::awake);
    }

    /**
     * Grants permission to the AgentImp with id <code>agentID</code> to execute the next phase in its action cycle.
     *
     * @param agentID The id of the AgentImp whose next phase should be executed.
     * @param next Whether environment allows the AgentImp involved to execute the next phase in line, or rather execute
     *             the same phase again.
     */
    public void activateNewPhase(ActiveItemID agentID, boolean next) {
        getActiveImp(agentID).activateNewPhase(next);
    }

    /**
     * Sends given message to the AgentImp with given ID
     * precondition: getAgentImp(agentID) <> null
     * @param agentID: the ID of the agent to send the message to
     * @param msg: the Mail to send
     */
    public void sendMessage(ActiveItemID agentID, Mail msg) {
        try {
            this.getAgentImp(agentID).receiveMessage(msg);
        } catch (NullPointerException e) {
            this.logger.severe(String.format("Message '%s' not delivered.", msg.toString()));
            this.logger.severe(String.format("AgentID %d does not exist.", agentID.getID()));
        } catch (ClassCastException e) {
            this.logger.severe("Only Agents can receive messages.");
        }
    }

    /**
     * Stops the AgentImp with given ID
     * precondition: getAgentImp(agentID) <> null
     * @param agentID: the ID of the agent to stop
     */
    public void finish(ActiveItemID agentID) {
        getActiveImp(agentID).finish();
    }

    /**
     * Stops all agents
     */
    public void finish() {
        // finish all agents
        getActiveObjects().forEach(ActiveImp::finish);
        agents.clear();
        otherActiveItems.clear();
    }

    /**
     * Needed for Synchronization
     */
    public void acquireLock(ActiveItemID agentID) {
        getActiveImp(agentID).getLock().acquireLock();
    }

    /**
     * Needed for Synchronization
     */
    public void releaseLock(ActiveItemID agentID) {
        try {
            getActiveImp(agentID).getLock().releaseLock();
        } catch (Exception exc) {
            //Normally NullPointerException or ArrayIndexOutOfBoundsException
            //NO-OP
            //This is not allowed to happen. However, this sometimes happens
            //on an abort in the BatchMAS, where it is allowed.
            //Need to find a clean solution, however.
            this.logger.severe("Cannot release lock for agent " + agentID.getID() +
                        "; there is no agent with such an ID");
        }
    }

    //GET AND SETTERS


    private Stream<ActiveImp> getActiveObjects() {
        return Stream.concat(agents.values().stream(), otherActiveItems.values().stream());
    }

    /**
     * Returns the agent or object with given ID
     *
     * @param ID the ID of the item to be retrieved
     * @return the Agent or ActiveObject that has the specified ID, or
     *         null if no such Agent of ActiveObject found
     */
    protected ActiveImp getActiveImp(ActiveItemID ID) {
        return getActiveObjects().filter(o -> o.getActiveItemID() == ID)
                .findFirst().orElse(null);
    }


    /**
     * Return the agent imp corresponding to the given ID.
     */
    protected AgentImp getAgentImp(ActiveItemID ID) {
        return agents.get(ID);
    }


    /**
     * Returns a sorted list with the ID of all agents.
     */
    public List<ActiveItemID> getAllAgentIDs() {
        return this.agents.keySet().stream().sorted().toList();
    }

    /**
     * Returns a sorted list with the ID of all active objects.
     */

    public List<ActiveItemID> getAllActiveItemIDs() {
        return this.getActiveObjects().map(ActiveImp::getActiveItemID).sorted().toList();
    }

    /**
     * Returns the number of AgentImps contained in this AgentImplementations
     */
    public int getNbAgents() {
        return agents.size();
    }

    /**
     * Returns the ID of the agentImp with the name <name>
     *
     * @param  name The name of the agent whose ID is being requested
     * @return the ID of the AgentImp with name <name>
     * @throws java.lang.IllegalArgumentException
     *         None of the AgentImps in lnkAgentImps carries the name <name>
     */
    public ActiveItemID getAgentID(String name) throws IllegalArgumentException {
        for (var entry : agents.entrySet()) {
            if (entry.getValue().getName().equals(name)) {
                return entry.getKey();
            }
        }

        throw new IllegalArgumentException("No agentId found matching the name " + name);
    }

    public Synchronization getSynchronizer() {
        return synchronizer;
    }

    public void setSynchronizer(Synchronization synchronizer) {
        this.synchronizer = synchronizer;
    }

    public Environment getEnvironment() {
        return environment;
    }

    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }
}
