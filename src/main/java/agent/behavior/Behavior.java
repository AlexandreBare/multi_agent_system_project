package agent.behavior;

import agent.AgentAction;
import agent.AgentCommunication;
import agent.AgentImp;
import agent.AgentState;
import environment.CellPerception;
import environment.Coordinate;
import environment.Perception;
import environment.world.agent.AgentRep;

import java.util.ArrayList;
import java.util.List;

/**
 * This class represents a role for an agent. It contains the actions the agent
 * does when playing this role.
 */
abstract public class Behavior {

    protected boolean hasHandled;
    private String description;
    private boolean closing = false;
    public String requiredMoveKey = "recMove";

    

    protected Behavior() {}


    /**
     * This method handles the actions of the behavior that are communication
     * related. This does not include answering messages, only sending them.
     */
    public abstract void communicate(AgentState agentState, AgentCommunication agentCommunication);

    /**
     * This method handles the actions of the behavior that are action related
     */
    public abstract void act(AgentState agentState, AgentAction agentAction);




    /**
     * Implements the general behavior method each cycle.
     * This method first handles communication in agents, and afterwards executes actions for the agents
     *  (cfr. abstract methods {@link #communicate(AgentState, AgentCommunication)} and {@link #act(AgentState, AgentAction)})
     */
    public final void handle(AgentImp agent) {
        
        if (this.hasHandled()) {
            return;
        }
        if (agent.inCommPhase()) {
            this.communicate(agent, agent);
            agent.closeCommunication();
        } else if (agent.inActionPhase()) {
            agent.resetCommittedAction();
            this.act(agent, agent);

            if (!agent.hasCommittedAction()) {
                throw new RuntimeException(String.format("Agent with ID=%d did not perform any action in its current behavior. Make sure the agent performs exactly one action each turn (taking the skip action into account as well).", agent.getActiveItemID().getID()));
            }
        }
    }


    
    /**
     * An optional precondition that can be defined for this behavior (checked before evaluating any behavior changes).
     * @param agentState The agent's perception with which the precondition is evaluated.
     * @return {@code true} if the precondition is fulfilled, {@code false} otherwise.
     */
    public boolean preCondition(AgentState agentState) {
        return true;
    }

    /**
     * An optional post condition that can be defined for this behavior (checked before taking actual behavior transitions).
     * @return {@code true} if the post condition is fulfilled, {@code false} otherwise.
     */
    public boolean postCondition() {
        return true;
    }



    /**
     * Optional actions to take before leaving this role
     */
    public void leave(AgentState agentState) {}




    /**
     * Returns a description of this role
     */
    public final String getDescription() {
        return description;
    }


    /**
     * Sets a description for this role
     */
    public void setDescription(String description) {
        this.description = description;
    }


    /**
     * Checks if this role has done for this synchronization cycle
     */
    private boolean hasHandled() {
        return this.hasHandled;
    }


    /**
     * DESTRUCTOR. Clears this role.
     */
    public void finish() {
        if (closing) {
            return;
        }
        closing = true;
        description = null;
    }

    /**
     * a function that looks if the given agent is blocked by another agent
     * @return true if the only walkable cell is the previous cell this agent was on and if
     * the agent
     */
    public boolean stuckBecauseOfAgent(AgentState agentState, List<Coordinate> blackList){
        Perception agentPerception  = agentState.getPerception();
        CellPerception[] neighbours = agentPerception.getNeighbours();

        CellPerception preciousCell = agentState.getPerceptionLastCell();
        int agentsAround = 0;
        for(CellPerception neighbour: neighbours){
            if (neighbour == null)
                continue;

            if (neighbour.isWalkable() && !neighbour.equals(preciousCell))
                return false;

            if (neighbour.containsAgent() && !blackList.contains(neighbour.getCoordinates()))
                agentsAround++;
        }

        return agentsAround > 0;
    }

    public void sendMessageToMakePlace(AgentState agentState, AgentCommunication agentCommunication, List<Coordinate> blackList) {
        CellPerception[] neighbours = agentState.getPerception().getNeighbours();

        for (CellPerception neighbour: neighbours){
            if (neighbour == null || !neighbour.containsAgent() || blackList.contains(neighbour.getCoordinates()))
                continue;
            AgentRep blockingAgent = neighbour.getAgentRepresentation().get();

            // compose messages
            Coordinate agentPos = agentState.getCoordinates();
            Coordinate blockedCoordinate = blockingAgent.getCoordinates();
            List<Coordinate> coordinates1  = new ArrayList<Coordinate>();
            List<Coordinate> coordinates2  = new ArrayList<Coordinate>();
            coordinates1.add(agentPos);
            coordinates2.add(blockedCoordinate);
            String message1 = Coordinate.coordinates2String(coordinates1);
            String message2 = Coordinate.coordinates2String(coordinates2);

            // send message
            agentCommunication.sendMessage(blockingAgent,message1);
            agentState.addMemoryFragment(requiredMoveKey,message2);
        }
    }


    public void dealWithBeingMaybeStuck(AgentState agentState, AgentCommunication agentCommunication){
        dealWithBeingMaybeStuck(agentState,agentCommunication,new ArrayList<Coordinate>());
    }

    public void dealWithBeingMaybeStuck(AgentState agentState, AgentCommunication agentCommunication, List<Coordinate> blackList){
        if(stuckBecauseOfAgent(agentState, blackList))
            sendMessageToMakePlace(agentState,agentCommunication, blackList);
    }

    public void tryForceMove(AgentState agentState, AgentAction agentAction) {
        Coordinate possibleNextPosition =
                Coordinate.string2Coordinates(agentState.getMemoryFragment(requiredMoveKey)).get(0);

        CellPerception possibleNextCell = agentState.getPerception().getCellPerceptionOnAbsPos(
                                                                        possibleNextPosition.getX(),
                                                                        possibleNextPosition.getY());
        System.out.println(possibleNextCell.getCoordinates().toString() + ": " + possibleNextCell.isWalkable());
        if (possibleNextCell.isWalkable()){
            agentAction.step(possibleNextPosition.getX(), possibleNextPosition.getY());
            agentState.removeMemoryFragment(requiredMoveKey);
        }
        else
            agentAction.skip();
    }

}
