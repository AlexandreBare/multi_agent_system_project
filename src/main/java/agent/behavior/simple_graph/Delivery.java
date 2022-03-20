package agent.behavior.simple_graph;

import agent.AgentAction;
import agent.AgentCommunication;
import agent.AgentState;
import agent.behavior.Behavior;

public class Delivery extends Behavior {

    @Override
    public void communicate(AgentState agentState, AgentCommunication agentCommunication) {
        // no comunication
    }

    @Override
    public void act(AgentState agentState, AgentAction agentAction) {
        // lookAround()

        // for each neighbour
            // contains right destination?
                // dropOffPacket()
                // return

        // walkToTarget() (see Explorer)
    }

    private void lookAround(){
        // for each visible tile
            // contains packet/destination/generator?
                // add to list
    }

    private void dropOffPacket(){
        // any targets left?
            // set target (the nearest packet)
        // drop of target
    }
}
