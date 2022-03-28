package agent.behavior.wall_hugger;


import agent.AgentAction;
import agent.AgentCommunication;
import agent.AgentState;
import agent.behavior.Behavior;
import environment.CellPerception;
import environment.Coordinate;
import environment.Perception;
import environment.world.destination.DestinationRep;
import environment.world.packet.PacketRep;

import java.util.Set;

public class TargetedPickUp extends Behavior{
    //TODO: make target specific color
    @Override
    public void communicate(AgentState agentState, AgentCommunication agentCommunication) {
        // no communication
    }

    @Override
    public void act(AgentState agentState, AgentAction agentAction) {
        // -------------------- look around ---------------------------
        lookAround(agentState);

        // -------------------- (pick up) -----------------------------
        CellPerception[] neighbours = agentState.getPerception().getNeighbours();
        for(CellPerception neighbour: neighbours){
            if(neighbour.containsPacket()){
                PacketRep packetRep = neighbour.getRepOfType(PacketRep.class);
                String correspondingDestination = DestinationRep.class + "_" + packetRep.getColor();
                if (agentState.getMemoryFragment(correspondingDestination) != null){
                    agentAction.pickPacket(neighbour.getX(),neighbour.getY());
                    return;
                }

            }
        }

        // -------------------- walk to target-------------------------
        Coordinate target = popCoordinateFromMemory(agentState,targetsKey);
        if (target != null){
            walkToTarget(agentState,agentAction,target);
            return;
        }

        //get target from target list
        target = getClosestTargetFromMemory(PacketRep.class.toString(),agentState);
        // walk to target
        setPathToTarget(agentState,target);
        // retry walking
        target = popCoordinateFromMemory(agentState,targetsKey);

        if (target == null){
            agentAction.skip();
            return; // and panic
        }

        walkToTarget(agentState,agentAction,target);
    }

}
