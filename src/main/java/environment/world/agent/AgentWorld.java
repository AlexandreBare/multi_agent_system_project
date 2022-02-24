package environment.world.agent;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Logger;

import javax.annotation.Nullable;

import com.google.common.eventbus.EventBus;

import environment.ActiveItemID;
import environment.Coordinate;
import environment.World;
import util.event.AgentActionEvent;

/**
 * A class for an AgentWorld, being a layer of the total world, that contains
 * Agents.
 */

public class AgentWorld extends World<Agent> {

    private final List<Agent> agents;

    private final Logger logger = Logger.getLogger(AgentWorld.class.getName());
    

    //--------------------------------------------------------------------------
    //		CONSTRUCTOR
    //--------------------------------------------------------------------------

    /**
     * Initializes a new AgentWorld instance
     */
    public AgentWorld(EventBus eventBus) {
        super(eventBus);
        this.agents = new ArrayList<>();
    }

    //--------------------------------------------------------------------------
    //		INSPECTORS
    //--------------------------------------------------------------------------

    /**
     * Gets an array containing the Agents that are in this AgentWorld
     * @return This AgentWorld's agents
     */
    public List<Agent> getAgents() {
        return this.agents;
    }

    /**
     * Gets the total amount of agents that are in this AgentWorld
     * @return This AgentWorld's nbAgents
     */
    public int getNbAgents() {
        return this.agents.size();
    }

    public String toString() {
        return "AgentWorld";
    }

    //--------------------------------------------------------------------------
    //		MUTATORS
    //--------------------------------------------------------------------------

    //--------------------------------------------------------------------------


    protected synchronized void loadEnergy(int x, int y, int loadAmount) {
        if (inBounds(x, y) && getItem(x, y) != null) {
            AgentActionEvent event = new AgentActionEvent(this);
            event.setAction(AgentActionEvent.LOAD_ENERGY);
            event.setAgent(getAgent(getItem(x, y).getID()));
            event.setValue(loadAmount);
            this.getEventBus().post(event);
        }
    }


    /**
     * Move a given Agent from and to the given coordinates
     *
     * @param   fromX    X coordinate to be moved from
     * @param   fromY    Y coordinate to be moved from
     * @param   toX      X coordinate to be moved to
     * @param   toY      Y coordinate to be moved to
     * @param   agent    The agent that is to be moved
     * @post    The position where the agent stood is free (A Free Item)
     * @post    The position where the agent moved to is taken up by the agent
     * @post    If the agent were carrying a packet, that packet gets notified
     *          of its new coordinates.
     */
    public void moveAgent(int fromX, int fromY, int toX, int toY,
                          Agent agent) {
        putItem(toX, toY, getItem(fromX, fromY)); // move the agent
        super.free(fromX, fromY); // make its origin free
        agent.getCarry().ifPresent(c -> c.moveTo(toX, toY));
        agent.consumeX(toX);
        agent.consumeY(toY);
    }

    /**
     * Gets the agent with the given 'ID'
     *
     * @param ID the number we gave to the agent we are looking for
     * @return   The agent with the given ID in this AgentWorld.
     *           Or, if there is no agent with that ID, we return null
     */
    @Nullable
    public Agent getAgent(ActiveItemID ID) {
        return this.agents.stream().filter(a -> a.getID() == ID)
            .findFirst()
            .orElse(null);
    }

    /**
     * Adds a number of Agents randomly to this AgentWorld.
     *
     * @param nbAgents the number of agents to add to this world
     * @param view     the view range for the agents to add
     */
    public void createWorld(int nbAgents, int view) {
        for (int i = 0; i < nbAgents; i++) {
            boolean ok = false;
            while (!ok) {
                Coordinate c = getRandomCoordinate(getEnvironment().getWidth(),
                                                   getEnvironment().getHeight());
                if (getEnvironment().isFreePos(c.getX(), c.getY())) {
                    Agent agent = new Agent(c.getX(), c.getY(), getEnvironment(), view,
                                            i + 1, String.valueOf(i + 1), null);
                    placeItem(agent);
                    ok = true;
                }
            }
        }
    }

    /**
     * Adds Agents to this AgentWorld.
     *
     * @param agents  a collection containing the agents to place in this world
     */
    @Override
    public void placeItems(Collection<Agent> agents) {
        agents.forEach(this::placeItem);
    }

    /**
     * Adds an Agent to this AgentWorld.
     *
     * @param agent  the agent to place in this world
     */
    @Override
    public void placeItem(Agent agent) {
        try {
            putItem(agent);
            getEnvironment().addActiveItem(agent);
            agents.add(agent);
        } catch (ClassCastException exc) {
            this.logger.severe("Can only place an Agent in AgentWorld.");
        }
    }

    @Override
    public void free(int x, int y) {
        super.free(x, y);
        List<Agent> agentsToRemove = new ArrayList<>();
        
        for (Agent agent : this.agents) {
            if (agent.getX() == x && agent.getY() == y) {
                agentsToRemove.add(agent);
            }
        }

        agentsToRemove.forEach(this.agents::remove);
    }


}
