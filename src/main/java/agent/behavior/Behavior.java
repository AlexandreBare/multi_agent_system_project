package agent.behavior;

import agent.AgentAction;
import agent.AgentCommunication;
import agent.AgentImp;
import agent.AgentState;
import environment.CellPerception;
import environment.Coordinate;
import environment.Mail;
import environment.Perception;
import environment.world.agent.AgentRep;
import environment.world.packet.PacketRep;
import util.MyColor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * This class represents a role for an agent. It contains the actions the agent
 * does when playing this role.
 */
abstract public class Behavior {

    protected boolean hasHandled;
    private String description;
    private boolean closing = false;

    

    protected Behavior() {}

    public String crucialCoordinateMemory = "crucialCoordinates";
    public String sendCrucialCoordinates = "send";
    public List<Coordinate> priorityCoordinates = new ArrayList<Coordinate>();

    /**
     * This method handles the actions of the behavior that are communication
     * related. This does not include answering messages, only sending them.
     */
    public void communicate(AgentState agentState, AgentCommunication agentCommunication) {
        if(!agentState.getMemoryFragmentKeys().contains(crucialCoordinateMemory)){
            return;
        }
        List<AgentRep> agentsInRange = getAgentsInRange(agentState);

        for(AgentRep agent: agentsInRange){
            List<Coordinate> coordinatesToSend = getCoordinatesToSend(agent, agentState, agentCommunication);
            for(Coordinate coordinate: coordinatesToSend){
                agentCommunication.sendMessage(agent, coordinate.toString());
            }
            updateMemoryWithSendCoordinates(coordinatesToSend, agentState, true);

            List<Coordinate> coordinatesToReturn = getCoordinatesToReturn(agent, agentState, agentCommunication);
            for(Coordinate coordinate: coordinatesToReturn){
                agentCommunication.sendMessage(agent, coordinate.toString());
            }
            updateMemoryWithSendCoordinates(coordinatesToSend, agentState, false);
        }

        setPriorityCoordinates(agentState, agentCommunication);
        removePacketFromPerception(agentState, agentCommunication);

    }

    /**
     * Gets all other agents present in the perception of this agent.
     * @param agentState
     * @return A list containing all visible other agents.
     */
    private List<AgentRep> getAgentsInRange(AgentState agentState){
        List<AgentRep> agentsInRange = new ArrayList<AgentRep>();
        Perception perception = agentState.getPerception();
        for(int i = 0; i < perception.getWidth(); i++){
            for(int j = 0; j < perception.getHeight(); j++) {
                CellPerception cell = perception.getCellAt(i, j);
                if(cell.containsAgent()){
                    agentsInRange.add(cell.getAgentRepresentation().get());
                }
            }
        }
        return agentsInRange;
    }

    private List<Coordinate> getCoordinatesToSend(AgentRep agent, AgentState agentState, AgentCommunication agentCommunication){
        List<Coordinate> coordinatesToSend = new ArrayList<Coordinate>();
        String crucialCoordinatesString = agentState.getMemoryFragment(crucialCoordinateMemory);
        List<Coordinate> crucialCoordinates = Coordinate.string2Coordinates(crucialCoordinatesString);
        List<Coordinate> receivedCoordinates = getCoordinatesInMessages(agentState, agentCommunication);
        crucialCoordinates.removeAll(receivedCoordinates);
        if(agent.getColor().isPresent()) {
            String key = PacketRep.class + "_" + MyColor.getName(agent.getColor().get());
            if(agentState.getMemoryFragmentKeys().contains(key)) {
                List<Coordinate> packagesWithAgentColor = Coordinate.string2Coordinates(agentState.getMemoryFragment(key));
                for(Coordinate coordinate: crucialCoordinates){
                    if(packagesWithAgentColor.contains(coordinate)){
                        coordinatesToSend.add(coordinate);
                    }
                }
            }
        }
        return coordinatesToSend;
    }

    private List<Coordinate> getCoordinatesToReturn(AgentRep agent, AgentState agentState, AgentCommunication agentCommunication){
        List<Coordinate> coordinatesToReturn = new ArrayList<Coordinate>();
        String crucialCoordinatesString = agentState.getMemoryFragment(crucialCoordinateMemory);
        List<Coordinate> crucialCoordinates = Coordinate.string2Coordinates(crucialCoordinatesString);
        for(int i = 0; i < agentCommunication.getNbMessages(); i++){
            Mail message = agentCommunication.getMessage(i);
            if(message.getFrom().equals(agent.getName())){
                List<Coordinate> receivedCoordinates = Coordinate.string2Coordinates(message.getMessage());
                for(Coordinate coordinate: receivedCoordinates){
                    if (crucialCoordinates.contains(coordinate)){
                        coordinatesToReturn.add(coordinate);
                        agentCommunication.removeMessage(i);
                    }
                }
            }
        }
        return coordinatesToReturn;
    }

    private void updateMemoryWithSendCoordinates(List<Coordinate> sentCoordinates, AgentState agentState, boolean addToSent){
        List<Coordinate> crucialCoordinates = Coordinate.string2Coordinates(agentState.getMemoryFragment(crucialCoordinateMemory));
        agentState.removeMemoryFragment(crucialCoordinateMemory);
        crucialCoordinates.removeAll(sentCoordinates);
        agentState.addMemoryFragment(crucialCoordinateMemory, Coordinate.coordinates2String(crucialCoordinates));

        if(addToSent) {
            List<Coordinate> memorySendCoordinates = Coordinate.string2Coordinates(agentState.getMemoryFragment(sendCrucialCoordinates));
            agentState.removeMemoryFragment(sendCrucialCoordinates);
            memorySendCoordinates.addAll(sentCoordinates);
            agentState.addMemoryFragment(sendCrucialCoordinates, Coordinate.coordinates2String(memorySendCoordinates));
        }
    }

    private List<Coordinate> getCoordinatesInMessages(AgentState agentState, AgentCommunication agentCommunication){
        Collection<Mail> messages = agentCommunication.getMessages();
        List<Coordinate> receivedCoordinates = new ArrayList<Coordinate>();
        for(Mail message: messages){
            List<Coordinate> coordinates = Coordinate.string2Coordinates(message.getMessage());
            receivedCoordinates.addAll(coordinates);
        }
        return receivedCoordinates;
    }

    private void setPriorityCoordinates(AgentState agentState, AgentCommunication agentCommunication){
        List<Coordinate> receivedCoordinates = getCoordinatesInMessages(agentState, agentCommunication);
        List<Coordinate> crucialCoordinates = Coordinate.string2Coordinates(agentState.getMemoryFragment(crucialCoordinateMemory));
        receivedCoordinates.removeAll(crucialCoordinates);
        this.priorityCoordinates = receivedCoordinates;
    }

    private void removePacketFromPerception(AgentState agentState, AgentCommunication agentCommunication){
        List<Coordinate> receivedCoordinates = getCoordinatesInMessages(agentState, agentCommunication);
        List<Coordinate> sentCoordinates = Coordinate.string2Coordinates(agentState.getMemoryFragment(sendCrucialCoordinates));
        List<Coordinate> coordinatesToRemoveFromPerception = new ArrayList<Coordinate>();
        for(Coordinate coordinate: sentCoordinates){
            if(receivedCoordinates.contains(coordinate)){
                coordinatesToRemoveFromPerception.add(coordinate);
            }
        }

        Set<String> keys = agentState.getMemoryFragmentKeysContaining(PacketRep.class + "_");
        for(String key: keys){
            List<Coordinate> coordinates = Coordinate.string2Coordinates(agentState.getMemoryFragment(key));
            coordinates.removeAll(coordinatesToRemoveFromPerception);
            agentState.removeMemoryFragment(key);
            agentState.addMemoryFragment(key, Coordinate.coordinates2String(coordinates));
        }
    }

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
}
