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
            if (distAgentGathering <= GATHERING_RADIUS + 1) {
                // Sort the delivering directions to put down the packet in priority to the cells which are the closest
                // from the gathering place
                Coordinate deliverDirection = gatheringCoordinates.diff(agentCoordinates);
                MovementManager movementManager = new MovementManager();
                movementManager.sort(deliverDirection);
                List<Coordinate> deliverDirections = movementManager.getMoves();
                // For each delivering direction
                for(Coordinate deliverRelPos: deliverDirections) {
//                Coordinate deliverDirection = movementManager.getMoves().get(0);
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

//        // Run A* to find one of the shortest paths to the closest packet of which a destination is known if no path has
//        // been computed yet.
//        if (!agentState.getMemoryFragmentKeys().contains("ShortestPath2Packet")){
//            Map<String, List<Coordinate>> coloredPacketCoordinatesMap = new HashMap<>();
//            Map<String, List<Coordinate>> coloredDestinationCoordinatesMap = new HashMap<>();
//
//            ///////////// Criterion 2: Let's target the closest packet that the agent has memorized /////////////
//            ///////////// and its known destination (there may be more than one)                    /////////////
//            ///////////// Find the shortest path to the get to one of the known packets and then    /////////////
//            ///////////// to one of its corresponding destinations. The problem is solved for each  /////////////
//            ///////////// pair of potential packet and corresponding destination                    /////////////
//
//            // Retrieve all memory keys where packets of the same color as the agent are stored
//            String subkey = AgentState.rep2MemoryKey(PacketRep.class.toString(), agentState.getColor().toString());
//            Set<String> packetKeys = agentState.getMemoryFragmentKeysContaining(subkey);
//            // For each key where there are packets stored in memory
//            for (String packetKey : packetKeys) {
//                String packetColor = AgentState.memoryKey2Rep(packetKey)[1];
//                String destinationKey = AgentState.rep2MemoryKey(DestinationRep.class.toString(), packetColor);
//                // If the agent knows a destination that can accept the current packet (i.e. same color)
//                if (agentState.getMemoryFragmentKeys().contains(destinationKey)) {
//                    // we will only keep the coordinates of the packets whose destination is known by the agent
//                    String memoryFragment = agentState.getMemoryFragment(packetKey);
//                    List<Coordinate> coordinatesList = Coordinate.string2Coordinates(memoryFragment);
//                    coloredPacketCoordinatesMap.put(packetColor, coordinatesList);
//                }
//            }
//
//            // Retrieve all memory keys where destinations of the same color as the agent are stored
//            subkey = AgentState.rep2MemoryKey(DestinationRep.class.toString(), agentState.getColor().toString());
//            Set<String> destinationKeys = agentState.getMemoryFragmentKeysContaining(subkey);
//            // For each key where there are destinations stored in memory
//            for (String destinationKey : destinationKeys) {
//                // Build up a dictionary of destination coordinates where the keys are the destinations color
//                String destinationColor = AgentState.memoryKey2Rep(destinationKey)[1];
//                String memoryFragment = agentState.getMemoryFragment(destinationKey);
//                List<Coordinate> coordinatesList = Coordinate.string2Coordinates(memoryFragment);
//                coloredDestinationCoordinatesMap.put(destinationColor, coordinatesList);
//            }
//
//
//            // Run A* to find one of the shortest paths to the closest packet of which a destination is known                                                  /////////////
//
//            // Convert all stored cell information to a list of cells
//            Set<CellPerception> cells = agentState.memory2Cells();
//            // Create a fictive environment with these cells and the object that manages moves
//            VirtualEnvironment virtualEnvironment = new VirtualEnvironment(cells, movementManager);
//            // Create a path finder object that can search the shortest paths linking specified ordered destinations
//            // in the fictive environment
//            PathFinder pathFinder = new PathFinder(virtualEnvironment);
//
//            // Current cell the agent stands on
//            CellPerception agentCell = agentState.getPerception().getCellPerceptionOnRelPos(0, 0);
//
////            // For all colors of which we know a destination, concatenate the respective memory fragment of the
////            // packet coordinates
////            String rep = PacketRep.class.toString();
////            String concatenatedMemoryFragments = "";
////            for(String color: coloredPacketCoordinatesMap.keySet()){
////                String memoryKey = AgentState.rep2MemoryKey(rep, color);
////                String memoryFragment = agentState.getMemoryFragment(memoryKey);
////                concatenatedMemoryFragments += memoryFragment;
////            }
////
////            List<Coordinate> coordinatesList = Coordinate.string2Coordinates(concatenatedMemoryFragments);
//            Set<CellPerception[]> agentDestinationCells = new HashSet<>();
//            for(var packetEntry: coloredPacketCoordinatesMap.entrySet()){
//                String color = packetEntry.getKey();
//                for (Coordinate packetCoordinates: packetEntry.getValue()){
//                    List<Coordinate> destinationCoordinatesList = coloredDestinationCoordinatesMap.get(color);
//                    for(Coordinate destinationCoordinates: destinationCoordinatesList){
//                        // Get the fictive cells from the fictive environment of the packets we know
//                        agentDestinationCells.add(new CellPerception[]{
//                                virtualEnvironment.getCell(packetCoordinates),
//                                virtualEnvironment.getCell(destinationCoordinates)
//                        });
//                    }
//                }
//            }
//            if (!agentDestinationCells.isEmpty()) {
//                // Run A* to find the shortest path from the agent's current cell to one of the possible packet cells
//                // (of which we know the destination)
//                List<List<Coordinate>> shortestPaths = pathFinder.astar(agentCell, agentDestinationCells);
////                System.out.println(String.format("Shortest Paths: %s", shortestPaths));
//
////                System.out.println("Pickup - AgentDestinationCells: ");
////                Iterator<CellPerception[]> iterator = agentDestinationCells.iterator();
////                while(iterator.hasNext()) {
////                    for (CellPerception destinationCell : iterator.next()) {
////                        System.out.print(destinationCell.getCoordinates());
////                    }
////                    System.out.print("\n");
////                }
////                System.out.println("Pickup - Shortest path 2 packet: " + Coordinate.coordinates2String(shortestPaths.get(0)));
////                System.out.println("Pickup - Shortest path 2 destination: " + Coordinate.coordinates2String(shortestPaths.get(1)));
//                if (!shortestPaths.isEmpty()){
//                    // Store to memory the shortest path to the closest packet
//                    agentState.addMemoryFragment("ShortestPath2Packet", Coordinate.coordinates2String(shortestPaths.get(0)));
//                    agentState.addMemoryFragment("ShortestPath2Destination", Coordinate.coordinates2String(shortestPaths.get(1)));
//                }
//            }

//            // if we memorized at least a packet of which we know the destination
//            if(!coordinatesList.isEmpty()) {
//                // Get the fictive cells from the fictive environment of the packets we know
//                Set<CellPerception[]> packetCells = new HashSet<>();
//                for (Coordinate packetCoordinates : coordinatesList) {
//                    packetCells.add(new CellPerception[]{virtualEnvironment.getCell(packetCoordinates)});
//                }
//                // Run A* to find the shortest path from the agent's current cell to one of the possible packet cells
//                // (of which we know the destination)
//                List<Coordinate> path2ClosestPacket = pathFinder.astar(agentCell, packetCells);
//
//                // We can remove the path to the closest destination, we are anyway not following it anymore.
//                // It helps to save a spot in memory.
//                agentState.removeMemoryFragment("ShortestPath");
//
//                // Store to memory the shortest path to the closest packet
//                agentState.addMemoryFragment("ShortestPath", Coordinate.coordinates2String(path2ClosestPacket));
//            }
//        }

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
//                System.out.println("gatheringCoordinates: " + gatheringCoordinates + " _ packetCoordinatesList: " + packetCoordinatesList + " _ size: " + packetCoordinatesList.size());
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
//                    System.out.println(agentState.getName() + " - Gather - Shortest path 2 gather: " + Coordinate.coordinates2String(shortestPaths.get(0)));
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
//            System.out.println(agentState.getName() + " - Gather - Remove first - Shortest path 2 gather: " + Coordinate.coordinates2String(path2Gather));

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

