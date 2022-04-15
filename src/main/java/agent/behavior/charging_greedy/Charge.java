package agent.behavior.charging_greedy;

import agent.AgentAction;
import agent.AgentCommunication;
import agent.AgentState;
import agent.behavior.Behavior;
import agent.utils.MovementManager;
import environment.CellPerception;
import environment.Coordinate;
import environment.world.agent.AgentRep;

import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.Set;


public class Charge extends Behavior {
    Random rand = new Random(42);


    @Override
    public void communicate(AgentState agentState, AgentCommunication agentCommunication) {
/*        // send messages to the other agents in view
        Set<CellPerception> agents = agentState.getPerceivableCellsWithAgents();
        String message = agentState.getName() +": " + agentState.getBatteryState();
        for (CellPerception agent : agents){
            Optional<AgentRep> agentRep = agent.getAgentRepresentation();

            if (!agentRep.isPresent() || agentRep.get().getName().equals(agentState.getName()))
                continue;

            System.out.println(agentRep.get().getName());
            agentCommunication.sendMessage(agentRep.get(),message);

        }
*/
        // recieve incomming messages
        if(agentCommunication.getNbMessages() != 0)
            System.out.println("---------- incomming messages for: " + agentState.getName()+ " while charging ----------");
        for (int i = 0; i <  agentCommunication.getNbMessages(); i++){
            // pop the first message
            System.out.println(agentState.getName() + ", " + i + ": " + agentCommunication.getMessage(0));
            agentCommunication.removeMessage(0);
        }
    }


    @Override
    public void act(AgentState agentState, AgentAction agentAction) {
        // Skip turn above an energy station to refill the battery
        agentAction.skip();
    }
}
