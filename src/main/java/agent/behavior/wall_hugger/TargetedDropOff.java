package agent.behavior.wall_hugger;

import agent.AgentAction;
import agent.AgentCommunication;
import agent.AgentState;
import agent.behavior.Behavior;
import environment.CellPerception;
import environment.Coordinate;
import environment.world.destination.DestinationRep;
import environment.world.packet.Packet;
import environment.world.packet.PacketRep;

import java.awt.*;

public class TargetedDropOff extends Behavior {
    @Override
    public void communicate(AgentState agentState, AgentCommunication agentCommunication) {
        // no communication
    }

    @Override
    public void act(AgentState agentState, AgentAction agentAction) {
        // -------------------- look around -------------------------
        lookAround(agentState);

        // --------------------- (drop off) -------------------------

        // Check for a neighbouring destination for the packet
        Packet packet = agentState.getCarry().orElse(null);
        Color packet_color = null;
        if(packet != null)
            packet_color = packet.getColor();

        CellPerception neighbouringDestination = agentState.getNeighbouringCellWithDestination(packet_color);
        if (neighbouringDestination != null){
            agentAction.putPacket(neighbouringDestination.getX(),neighbouringDestination.getY());
            return;
        }
            // drop off and return;

        // -------------------- walk to target ----------------------
        //find the closest drop off
        Coordinate target = popCoordinateFromMemory(agentState,targetsKey);
        if (target != null){
            walkToTarget(agentState,agentAction,target);
            return;
        }

        //get target from target list
        target = getClosestTargetFromMemory(DestinationRep.class.toString() + "_" + packet_color,agentState);
        // walk to target
        setPathToTarget(agentState,target);
        // retry walking
        target = popCoordinateFromMemory(agentState,targetsKey);

        if (target == null){
            agentAction.skip();
            return; // and panic
        }

        walkToTarget(agentState,agentAction,target);
        walkToTarget(agentState,agentAction,target);
    }
}
