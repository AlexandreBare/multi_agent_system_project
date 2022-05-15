package agent.behavior.task_coordination;

import agent.AgentAction;
import agent.AgentCommunication;
import agent.AgentState;
import agent.behavior.Behavior;

import java.util.Random;


public class Charge extends Behavior {
    Random rand = new Random(42);



    @Override
    public void act(AgentState agentState, AgentAction agentAction) {
        // Skip turn above an energy station to refill the battery
        agentAction.skip();
    }
}
