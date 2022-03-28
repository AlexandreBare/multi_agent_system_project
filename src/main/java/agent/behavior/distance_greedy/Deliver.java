package agent.behavior.distance_greedy;

import agent.AgentAction;
import agent.AgentCommunication;
import agent.AgentState;
import agent.behavior.Behavior;
import environment.CellPerception;
import environment.Coordinate;
import agent.utils.MovementManager;
import environment.world.destination.DestinationRep;
import environment.world.packet.Packet;
import util.MyColor;

import java.awt.*;
import java.util.*;
import java.util.List;


public class Deliver extends Behavior {
    Random rand = new Random(42);


    @Override
    public void communicate(AgentState agentState, AgentCommunication agentCommunication) {

    }


    @Override
    public void act(AgentState agentState, AgentAction agentAction) {
        agentState.prettyPrintWorld();

        ///////////// Memorize all representations that the agent can see in his perception area /////////////
        agentState.memorizeAllPerceivableRepresentations();


        ///////////// Check whether the agent can deliver his packet to a neighbouring destination /////////////

        // Retrieve the color of the packet the agent carries
        Packet packet = agentState.getCarry().orElse(null);
        Color packetColor = null;
        if (packet != null) {
            packetColor = packet.getColor();
        }

        // Check for a neighbouring destination of the right color to deliver the packet
        CellPerception destinationCell = agentState.getNeighbouringCellWithDestination(packetColor);
        if (destinationCell != null) { // If a matching destination is found
            agentAction.putPacket(destinationCell.getX(), destinationCell.getY()); // Deliver the packet
            return;
        }


        ///////////// If a packet can not be dropped, the agent moves instead /////////////

        ///////////// Criterion 1: Let's not go backwards after moving forward /////////////
        // Retrieve the positions that the agent has already been to
        MovementManager movementManager = new MovementManager();
        String positionsString = agentState.getMemoryFragment("Positions");
        if (positionsString != null) {
            Coordinate agentLastCoordinates = Coordinate.string2Coordinates(positionsString).get(0);
            Coordinate agentCurrentCoordinates = new Coordinate(agentState.getX(), agentState.getY());
            // Compute the last move that the agent has done to go from agentLastCoordinates to agentCurrentCoordinates
            Coordinate lastMove = agentCurrentCoordinates.diff(agentLastCoordinates);
            // Compute the opposite move to this last move
            Coordinate backwardMove = new Coordinate(0, 0).diff(lastMove);
            // Remove the opposite move so that the agent can not go backwards.
            // It forces the agent not to stick to the same region and instead "explore" the environment
            movementManager.remove(backwardMove);
            // Pre-sort the remaining available moves by their manhattan distance to the last move.
            // It pushes the agent to continue moving in a direction as close as possible to the last one
            // but does not strictly force him to do so if it happens not to be possible to go this way.
            movementManager.sort(lastMove, "ManhattanDistance");
        }


        ///////////// Criterion 2: Let's go to the closest memorized destination /////////////
        ///////////// that matches the packet the agent carries                  /////////////

        // Select the destination coordinates that we have stored in memory (there may be more than one pair)
        // that can accept the packet the agent has (i.e. they share the same color).
        List<Coordinate> destinationCoordinatesList = null;
        String key = AgentState.rep2MemoryKey(DestinationRep.class.toString(), MyColor.getName(packetColor));
        if (agentState.getMemoryFragmentKeys().contains(key)) {
            String data = agentState.getMemoryFragment(key);
            destinationCoordinatesList = Coordinate.string2Coordinates(data);
        }

        // If at least one matching destination was found in memory
        if (destinationCoordinatesList != null) {
            // Select the closest destination to the agent
            Coordinate agentCoordinates = new Coordinate(agentState.getX(), agentState.getY());
            Coordinate destinationCoordinates = agentCoordinates.closestCoordinatesTo(destinationCoordinatesList);

            // Sort the moves to best match the direction to the destination
            Coordinate direction2Destination = destinationCoordinates.diff(agentCoordinates);
            movementManager.sort(direction2Destination);
        }


        ///////////// Let's move the agent /////////////

        // Retrieve the available moves
        List<Coordinate> moves = movementManager.getMoves();

        // Check for viable moves
        for (var move : moves) {
            int x = move.getX() + agentState.getX();
            int y = move.getY() + agentState.getY();

            // If the agent can walk at these coordinates
            if (agentState.canWalk(x, y)) {

                // Append to memory the old position of the agent
                Coordinate oldPosition = new Coordinate(agentState.getX(), agentState.getY());
                agentState.append2Memory("Positions", oldPosition.toString());

                // Move the agent
                agentAction.step(x, y);
                return;
            }
        }


        ///////////// No viable or available moves, let's skip this agent's turn /////////////

        // Skip turn
        agentAction.skip();
    }
}
