package agent.behavior.basic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import java.awt.Color;

import agent.AgentAction;
import agent.AgentCommunication;
import agent.AgentState;
import agent.behavior.Behavior;
import environment.Coordinate;
import environment.world.packet.Packet;


public class Basic extends Behavior {
    Random rand = new Random(42);

    @Override
    public void communicate(AgentState agentState, AgentCommunication agentCommunication) {
        // No communication
    }


    @Override
    public void act(AgentState agentState, AgentAction agentAction) {
        // Potential moves an agent can make (radius of 1 around the agent)
        List<Coordinate> moves = new ArrayList<>(List.of(
                new Coordinate(1, 1), new Coordinate(-1, -1),
                new Coordinate(1, 0), new Coordinate(-1, 0),
                new Coordinate(0, 1), new Coordinate(0, -1),
                new Coordinate(1, -1), new Coordinate(-1, 1)
        ));

        // Shuffle moves randomly
        Collections.shuffle(moves, rand);

        var perception = agentState.getPerception();

        if (agentState.hasCarry()){
            // Check for a neighbouring destination for packet
            Packet packet = agentState.getCarry().orElse(null);
            Color packet_color = null;
            if(packet != null){
                packet_color = packet.getColor();
            }

            for (var move : moves) {

                int x = move.getX() + agentState.getX();
                int y = move.getY() + agentState.getY();
                if (perception.getCellPerceptionOnAbsPos(x, y) != null
                        && perception.getCellPerceptionOnAbsPos(x, y).containsDestination(packet_color)){
                    agentAction.putPacket(x, y);
                    return;
                }
            }
        }else{
            // Check for a neighbouring packet
            for (var move : moves) {
                int x = move.getX() + agentState.getX();
                int y = move.getY() + agentState.getY();
                if (perception.getCellPerceptionOnAbsPos(x, y) != null
                        && perception.getCellPerceptionOnAbsPos(x, y).containsPacket()){
                    agentAction.pickPacket(x, y);
                    return;
                }
            }
        }

        // Check for viable moves
        for (var move : moves) {
            int x = move.getX() + agentState.getX();
            int y = move.getY() + agentState.getY();

            // If the area is null, it is outside the bounds of the environment
            //  (when the agent is at any edge for example some moves are not possible)
            if (perception.getCellPerceptionOnAbsPos(x, y) != null
                    && perception.getCellPerceptionOnAbsPos(x, y).isWalkable()) {
                agentAction.step(x, y);
                return;
            }
        }

        // No viable moves, skip turn
        agentAction.skip();
    }
}
