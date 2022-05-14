package agent.behavior.task_coordination;

import agent.AgentAction;
import agent.AgentCommunication;
import agent.AgentState;
import agent.behavior.Behavior;
import agent.utils.MovementManager;
import agent.utils.PathFinder;
import agent.utils.VirtualEnvironment;
import environment.CellPerception;
import environment.Coordinate;
import environment.world.destination.DestinationRep;
import environment.world.packet.Packet;
import environment.world.packet.PacketRep;
import util.MyColor;

import java.awt.*;
import java.util.List;
import java.util.*;


public class Gather extends Behavior {
    Random rand = new Random(42);
    int GATHERING_RADIUS = 2;

    @Override
    public void communicate(AgentState agentState, AgentCommunication agentCommunication) {

    }


    @Override
    public void act(AgentState agentState, AgentAction agentAction) {
        ///////////// Memorize all representations that the agent can see in his perception area /////////////
        agentState.memorizeAllPerceivableRepresentations();

        //        // Print the state of the world as seen and memorized by the agent until now
//        agentState.prettyPrintWorld();


        ///////////// Check whether the agent can put down his packet because he is close enough /////////////
        ///////////// from the gathering place                                                   /////////////

        // if the agent knows a path to a gathering place
        if(agentState.getMemoryFragmentKeys().contains("ShortestPath2Gather")) {
            List<Coordinate> path2Gather = Coordinate.string2Coordinates(agentState.getMemoryFragment("ShortestPath2Gather"));
            Coordinate gatheringCoordinates = path2Gather.get(path2Gather.size() - 1);
            Coordinate agentCoordinates = agentState.getCoordinates();
            int distAgentGathering = agentCoordinates.distanceFrom(gatheringCoordinates);
            // if the agent is close enough from the gathering place to put down his packet
            if (distAgentGathering <= GATHERING_RADIUS) { // + 1
                // Sort the delivering directions to put down the packet in priority to the cells which are the closest
                // from the gathering place
                Coordinate deliverDirection = gatheringCoordinates.diff(agentCoordinates);
                MovementManager movementManager = new MovementManager();
                movementManager.sort(deliverDirection, "ManhattanDistance");
                List<Coordinate> deliverDirections = movementManager.getMoves();
                // For each delivering direction
                for(Coordinate deliverRelPos: deliverDirections) {
                    CellPerception deliverCell = agentState.getPerception().getCellPerceptionOnRelPos(deliverRelPos.getX(), deliverRelPos.getY());
                    // if the target cell to deliver is free
                    if (deliverCell != null && deliverCell.isFree()) {
                        // Remove the path to the gathering place from memory
                        agentState.removeMemoryFragment("ShortestPath2Gather");
                        agentAction.putPacket(deliverCell.getX(), deliverCell.getY()); // Put the packet down
                        return;
                    }
                }
            }
        }

        ///////////// Forget all representations that are not present anymore                       /////////////
        ///////////// in the agent's perception area (because a packet was picked up for example)   /////////////
        agentState.forgetAllUnperceivableRepresentations();


        ///////////// If a packet can not be put down, the agent moves instead /////////////

        ///////////// Criterion 1: Let's not go backwards after moving forward /////////////
        // Retrieve the positions that the agent has already been to
        MovementManager movementManager = new MovementManager();
        CellPerception agentLastCell = agentState.getPerceptionLastCell();
        if (agentLastCell != null) {
            Coordinate agentLastCoordinates = agentLastCell.getCoordinates();
            Coordinate agentCurrentCoordinates = new Coordinate(agentState.getX(), agentState.getY());
            // Compute the last move that the agent has done to go from agentLastCoordinates to agentCurrentCoordinates
            Coordinate lastMove = agentCurrentCoordinates.diff(agentLastCoordinates);
            // Compute the opposite move to this last move
//            Coordinate backwardMove = new Coordinate(0, 0).diff(lastMove);
            // Remove the opposite move so that the agent can not go backwards.
            // It forces the agent not to stick to the same region and instead "explore" the environment
//            movementManager.remove(backwardMove);
            // Pre-sort the remaining available moves by their manhattan distance to the last move.
            // It pushes the agent to continue moving in a direction as close as possible to the last one
            // but does not strictly force him to do so if it happens not to be possible to go this way.
            movementManager.sort(lastMove, "ManhattanDistance");
        }


        ///////////// Criterion 2: Let's follow the closest path to a destination that matches /////////////
        ///////////// the packet color.                                                        /////////////

        // If a path to the closest packet does not exist in memory
        // Run A* to find an optimal path to the closest destination of the right color if no path has been computed yet
        if (!agentState.getMemoryFragmentKeys().contains("ShortestPath2Gather")){
            // Convert all stored cell information to a list of cells
            Set<CellPerception> cells = agentState.memory2Cells();
            // Create a fictive environment with these cells and the object that manages moves
            VirtualEnvironment virtualEnvironment = new VirtualEnvironment(cells, movementManager);
            // Create a PathFinder object that can search the shortest paths to specific destinations in the fictive
            // environment
            PathFinder pathFinder = new PathFinder(virtualEnvironment);

            // Current cell the agent stands on
            CellPerception agentCell = agentState.getPerception().getCellPerceptionOnRelPos(0, 0);

            // Retrieve the memory key where packets of the same color as the agent are stored
            String key = AgentState.rep2MemoryKey(PacketRep.class.toString(), MyColor.getName(agentState.getColor().orElse(Color.BLACK)));
            String memoryFragment = agentState.getMemoryFragment(key);
            if (memoryFragment != null) {
                List<Coordinate> packetCoordinatesList = Coordinate.string2Coordinates(memoryFragment);

                // The gathering coordinates correspond to the average position of all known packets of the same colors
                Coordinate gatheringCoordinates = new Coordinate(0, 0);
                for (Coordinate otherPacketCoordinates : packetCoordinatesList) {
                    gatheringCoordinates = gatheringCoordinates.add(otherPacketCoordinates);
                }
                gatheringCoordinates = gatheringCoordinates.divideBy(packetCoordinatesList.size());
                Set<CellPerception[]> destinationCells = new HashSet<>();
                // Get the fictive cell from the fictive environment of the gathering place we computed
                destinationCells.add(new CellPerception[]{
                        virtualEnvironment.getCell(gatheringCoordinates)
                });

                // Run A* to find the shortest path from the agent's current cell to the gathering place
                List<List<Coordinate>> shortestPaths = pathFinder.astar(agentCell, destinationCells);
                // If a path exist to the gathering place and is not empty
                if (!shortestPaths.isEmpty() && !shortestPaths.get(0).isEmpty()) {
                    agentState.addMemoryFragment("ShortestPath2Gather", Coordinate.coordinates2String(shortestPaths.get(0)));
                }
            }
        }

        // If still no path to a gathering place exists
        if (!agentState.getMemoryFragmentKeys().contains("ShortestPath2Gather")){
            // Let's put down the packet anywhere at random
            Coordinate agentCoordinates = agentState.getCoordinates();
            MovementManager movementManager2 = new MovementManager();
            movementManager2.shuffle(rand);
            List<Coordinate> moves = movementManager2.getMoves();
            for(Coordinate move: moves) {
                CellPerception deliverCell = agentState.getPerception().getCellPerceptionOnRelPos(move.getX(), move.getY());
                if (deliverCell.isFree()) {
                    agentAction.putPacket(agentCoordinates.getX() + move.getX(),
                            agentCoordinates.getY() + move.getY());
                    return;
                }
            }
        }

        // If now a path to the gathering place exists in memory
        if (agentState.getMemoryFragmentKeys().contains("ShortestPath2Gather")){
            ///////////// Sort the moves by following the shortest path to the gathering place /////////////

            // Retrieve the current path the agent has to follow to get to the gathering place
            String memoryFragment = agentState.getMemoryFragment("ShortestPath2Gather");
            List<Coordinate> path2Gather = Coordinate.string2Coordinates(memoryFragment);
            // Retrieve and remove from memory the next cell we should go to in this path
            Coordinate nextCoordinatesInPath = path2Gather.get(0);
            agentState.removeFromMemory(nextCoordinatesInPath, "ShortestPath2Gather");

            // Sort the moves to best match the direction to the next cell in the path.
            // It allows the agent not to be forced to follow exactly the optimal path if for example another
            // agent appears in his way
            Coordinate agentCoordinates = agentState.getCoordinates();
            Coordinate direction2Gather = nextCoordinatesInPath.diff(agentCoordinates);
            movementManager.sort(direction2Gather);
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

