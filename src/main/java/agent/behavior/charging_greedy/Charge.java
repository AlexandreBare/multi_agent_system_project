package agent.behavior.charging_greedy;

import agent.AgentAction;
import agent.AgentCommunication;
import agent.AgentState;
import agent.behavior.Behavior;
import agent.utils.MovementManager;
import environment.Coordinate;

import java.util.List;
import java.util.Random;


public class Charge extends Behavior {
    Random rand = new Random(42);


    @Override
    public void communicate(AgentState agentState, AgentCommunication agentCommunication) {

    }


    @Override
    public void act(AgentState agentState, AgentAction agentAction) {
        // Skip turn above an energy station to refill the battery
        agentAction.skip();
    }
}
