package agent.behavior.basic_extended.state;

import agent.AgentAction;
import agent.AgentCommunication;
import agent.AgentState;
import agent.behavior.Behavior;
import environment.Coordinate;
import environment.Perception;
import util.movement.Step;

import java.awt.Color;

public class Deliver extends Behavior {
    @Override
    public void communicate(AgentState agentState, AgentCommunication agentCommunication) {
        // No communication
    }

    @Override
    public void act(AgentState agentState, AgentAction agentAction) {
        var destinationCoordinate = closestDestinationInPerception(agentState);

        if (destinationCoordinate == null) {
            Coordinate move = Step.random(agentState);
            if (move != null) {
                agentAction.step(move.getX(), move.getY());
            } else {
                agentAction.skip();
            }

        } else {
            var difference = destinationCoordinate.diff(agentState.getCoordinate());
            var direction = difference.sign();

            var cellInDirection = agentState.getPerception().getCellPerceptionOnRelPos(direction.getX(), direction.getY());

            Color packetColor = null;

            var carry = agentState.getCarry();

            if (cellInDirection == null || carry.isEmpty()) {
                agentAction.skip();
            } else {
                packetColor = carry.get().getColor();

                if (cellInDirection.containsDestination(packetColor)) {
                    agentAction.putPacket(agentState.getX() + direction.getX(), agentState.getY() + direction.getY());
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

    public Coordinate closestDestinationInPerception(AgentState agentState) {
        Color packetColor = null;

        var carry = agentState.getCarry();
        if (carry.isEmpty()) {
            return null;
        } else {
            packetColor = carry.get().getColor();
        }

        Perception perception = agentState.getPerception();
        Coordinate closestDestinationLocation = null;
        int closestDestinationDistance = Integer.MAX_VALUE;

        for (var cell : perception) {
            if (cell == null) {
                continue;
            }

            if (cell.containsDestination(packetColor)) {
                Coordinate cellCoordinate = cell.getCoordinate();
                int distance = agentState.getCoordinate().distance(cellCoordinate);
                if (distance < closestDestinationDistance) {
                    closestDestinationDistance = distance;
                    closestDestinationLocation = cellCoordinate;
                }
            }
        }

        return closestDestinationLocation;
    }
}
