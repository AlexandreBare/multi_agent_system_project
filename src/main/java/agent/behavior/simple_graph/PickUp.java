package agent.behavior.simple_graph;

import agent.AgentAction;
import agent.AgentCommunication;
import agent.AgentState;
import agent.behavior.Behavior;

public class PickUp extends Behavior {
    @Override
    public void communicate(AgentState agentState, AgentCommunication agentCommunication) {
        // no communication
    }

    @Override
    public void act(AgentState agentState, AgentAction agentAction) {
        // lookAround() (see delivery)

        // for each neighbour
            // contains packet?
                // picUpPacket()
                // return

        // walkToTarget() (see Explorer)
    }

    private void pickUpPacket(){
        // remove packet from packets (in memory)
        // set target (choose the best destination)
        // pick up packet
    }
}
