package agent.behavior.basic_extended.state;

import agent.AgentAction;
import agent.AgentCommunication;
import agent.AgentState;
import agent.behavior.Behavior;
import environment.Coordinate;
import environment.Perception;
import util.movement.Step;


public class Search extends Behavior {
    @Override
    public void communicate(AgentState agentState, AgentCommunication agentCommunication) {
        // No Communication
    }

    @Override
    public void act(AgentState agentState, AgentAction agentAction) {
        var packetCoordinate = closestPacketInPerception(agentState);

        if (packetCoordinate == null) {
            Coordinate move = Step.random(agentState);
            if (move != null) {
                agentAction.step(move.getX(), move.getY());
            } else {
                agentAction.skip();
            }
        } else {
            var difference = packetCoordinate.diff(agentState.getCoordinate());
            var direction = difference.sign();

            var cellInDirection = agentState.getPerception().getCellPerceptionOnRelPos(direction.getX(), direction.getY());

            if (cellInDirection == null) {
                agentAction.skip();
            } else {
                if (cellInDirection.containsPacket()) {
                    agentAction.pickPacket(agentState.getX() + direction.getX(), agentState.getY() + direction.getY());
                    return;
                }

                if (cellInDirection.isWalkable()) {
                    agentAction.step(agentState.getX() + direction.getX(), agentState.getY() + direction.getY());
                    return;
                }

                agentAction.skip();
            }
        }
    }

    public static Coordinate closestPacketInPerception(AgentState agentState) {
        Perception perception = agentState.getPerception();
        Coordinate closestPacketLocation = null;
        int closestPacketDistance = Integer.MAX_VALUE;

        for (var cell : perception) {
            if (cell == null) {
                continue;
            }

            if (cell.containsPacket()) {
                Coordinate cellCoordinate = cell.getCoordinate();
                int distance = agentState.getCoordinate().distance(cellCoordinate);
                if (distance < closestPacketDistance) {
                    closestPacketDistance = distance;
                    closestPacketLocation = cellCoordinate;
                }
            }
        }

        return closestPacketLocation;
    }
}
