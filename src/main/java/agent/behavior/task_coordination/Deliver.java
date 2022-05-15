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
import util.MyColor;

import java.awt.*;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;


public class Deliver extends Behavior {
    Random rand = new Random(42);



    @Override
    public void act(AgentState agentState, AgentAction agentAction) {

        ///////////// Memorize all representations that the agent can see in his perception area /////////////
        agentState.memorizeAllPerceivableRepresentations();

//        // Print the state of the world as seen and memorized by the agent until now
//        agentState.prettyPrintWorld();

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
            agentState.removeMemoryFragment("ShortestPath2Destination"); // Remove path to destination from memory
            agentAction.putPacket(destinationCell.getX(), destinationCell.getY()); // Deliver the packet
            return;
        }


        ///////////// If a packet can not be dropped, the agent moves instead /////////////

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
            //Coordinate backwardMove = new Coordinate(0, 0).diff(lastMove);
            // Remove the opposite move so that the agent can not go backwards.
            // It forces the agent not to stick to the same region and instead "explore" the environment
            //movementManager.remove(backwardMove);
            // Pre-sort the remaining available moves by their manhattan distance to the last move.
            // It pushes the agent to continue moving in a direction as close as possible to the last one
            // but does not strictly force him to do so if it happens not to be possible to go this way.
            movementManager.sort(lastMove, "ManhattanDistance");
        }


        ///////////// Criterion 2: Let's follow the closest path to a destination that matches /////////////
        ///////////// the packet color.                                                        /////////////

        // Run A* to find an optimal path to the closest destination of the right color if no path has been computed yet
        if (!agentState.getMemoryFragmentKeys().contains("ShortestPath2Destination")){
            // Convert all stored cell information to a list of cells
            Set<CellPerception> cells = agentState.memory2Cells();
            // Create a fictive environment with these cells and the object that manages moves
            VirtualEnvironment virtualEnvironment = new VirtualEnvironment(cells, movementManager);
            // Create a PathFinder object that can search the shortest paths to specific destinations in the fictive
            // environment
            PathFinder pathFinder = new PathFinder(virtualEnvironment);

            // Current cell the agent stands on
            CellPerception agentCell = agentState.getPerception().getCellPerceptionOnRelPos(0, 0);
            // Find the potential destinations stored in memory where the packet could be delivered
            String rep = DestinationRep.class.toString();
            String color = MyColor.getName(packet.getColor());
            String memoryKey = AgentState.rep2MemoryKey(rep, color);
            String memoryFragment = agentState.getMemoryFragment(memoryKey);
            List<Coordinate> coordinatesList = Coordinate.string2Coordinates(memoryFragment);
            // Get the fictive cells from the fictive environment of the destination we know
            Set<CellPerception[]> destinationCells = new HashSet<>();
            for(Coordinate destinationCoordinates: coordinatesList){
                destinationCells.add(new CellPerception[]{virtualEnvironment.getCell(destinationCoordinates)});
            }

            // Run A* to find the shortest path from the agent's current cell to one of the possible terminal cells
            List<List<Coordinate>> shortestPaths = pathFinder.astar(agentCell, destinationCells);
            if (!shortestPaths.isEmpty() && !shortestPaths.get(0).isEmpty()) {
                // Store to memory the shortest path to the closest destination
                agentState.addMemoryFragment("ShortestPath2Destination", Coordinate.coordinates2String(shortestPaths.get(0)));
            }
        }


        // If now a path to the closest destination exists in memory
        if(agentState.getMemoryFragmentKeys().contains("ShortestPath2Destination")) {
            ///////////// Sort the moves by following the closest destination path /////////////

            // Retrieve the current path the agent has to follow to get to his destination
            String memoryFragment = agentState.getMemoryFragment("ShortestPath2Destination");
            List<Coordinate> path2Destination = Coordinate.string2Coordinates(memoryFragment);

            // Retrieve and remove from memory the next cell we should go to in this path
            Coordinate nextCoordinatesInPath = path2Destination.get(0);
            agentState.removeFromMemory(nextCoordinatesInPath, "ShortestPath2Destination");

            // Sort the moves to best match the direction to the next cell in the path.
            // It allows the agent not to be forced to follow exactly the optimal path if for example another
            // agent appears in his way
            Coordinate agentCoordinates = new Coordinate(agentState.getX(), agentState.getY());
            Coordinate direction2Destination = nextCoordinatesInPath.diff(agentCoordinates);
            movementManager.sort(direction2Destination);
        }


        ///////////// Let's move the agent /////////////

        // Retrieve the available moves in the order they were sorted
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
