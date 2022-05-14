package agent.behavior.a_star_multi_targets;

import agent.AgentAction;
import agent.AgentCommunication;
import agent.AgentState;
import agent.behavior.Behavior;
import agent.utils.PathFinder;
import agent.utils.VirtualEnvironment;
import environment.CellPerception;
import environment.Coordinate;
import agent.utils.MovementManager;
import environment.world.destination.DestinationRep;
import environment.world.packet.PacketRep;
import util.MyColor;

import java.util.*;


public class Pickup extends Behavior {
    Random rand = new Random(42);

    @Override
    public void communicate(AgentState agentState, AgentCommunication agentCommunication) {

    }


    @Override
    public void act(AgentState agentState, AgentAction agentAction) {
        ///////////// Memorize all representations that the agent can see in his perception area /////////////
        agentState.memorizeAllPerceivableRepresentations();

        //        // Print the state of the world as seen and memorized by the agent until now
//        agentState.prettyPrintWorld();


        ///////////// Check whether the agent can pick up a neighbouring packet /////////////
        ///////////// from which it knows a matching destination                /////////////

        // Check for a neighbouring packet to pick up
        CellPerception packetCell = agentState.getNeighbouringCellWithPacket();
        if (packetCell != null){
            // Build the key to find a destination in memory where the current packet could be later delivered
            String packetColor = MyColor.getName(packetCell.getRepOfType(PacketRep.class).getColor());
            String destinationKey = AgentState.rep2MemoryKey(DestinationRep.class.toString(), packetColor);

            // If the agent knows a destination that can accept the current packet (i.e. same color)
            if (agentState.getMemoryFragmentKeys().contains(destinationKey)) {
                agentState.removeFromMemory(packetCell); // Remove the packet from memory as we will pick it up
                agentAction.pickPacket(packetCell.getX(), packetCell.getY()); // Pick the packet at hand
                return;
            }
        }


        ///////////// Forget all representations that are not present anymore                       /////////////
        ///////////// in the agent's perception area (because a packet was picked up for example)   /////////////
        agentState.forgetAllUnperceivableRepresentations();


        ///////////// If a packet can not be picked, the agent moves instead /////////////

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
            Coordinate backwardMove = new Coordinate(0, 0).diff(lastMove);
            // Remove the opposite move so that the agent can not go backwards.
            // It forces the agent not to stick to the same region and instead "explore" the environment
            movementManager.remove(backwardMove);
            // Pre-sort the remaining available moves by their manhattan distance to the last move.
            // It pushes the agent to continue moving in a direction as close as possible to the last one
            // but does not strictly force him to do so if it happens not to be possible to go this way.
            movementManager.sort(lastMove, "ManhattanDistance");
        }

        // Run A* to find one of the shortest paths to the closest packet of which a destination is known if no path has
        // been computed yet.
        if (!agentState.getMemoryFragmentKeys().contains("ClosestPacketPath")){
            Map<String, List<Coordinate>> coloredPacketCoordinatesMap = new HashMap<>();

            ///////////// Criterion 2: Let's target the closest packet that the agent has memorized /////////////
            ///////////// and of which the destination is known                                     /////////////

            // Retrieve all memory keys where packets are stored
            String subkey = PacketRep.class.toString();
            Set<String> packetKeys = agentState.getMemoryFragmentKeysContaining(subkey);
            //StringBuilder dataConcatenated = new StringBuilder();
            // For each key where there are packets stored in memory
            for (String packetKey : packetKeys) {
                String packetColor = AgentState.memoryKey2Rep(packetKey)[1];
                String destinationKey = AgentState.rep2MemoryKey(DestinationRep.class.toString(), packetColor);
                // If the agent knows a destination that can accept the current packet (i.e. same color)
                if (agentState.getMemoryFragmentKeys().contains(destinationKey)) {
                    // we will only keep the coordinates of the packets whose destination is known by the agent
                    String memoryFragment = agentState.getMemoryFragment(packetKey);
                    List<Coordinate> coordinatesList = Coordinate.string2Coordinates(memoryFragment);
                    coloredPacketCoordinatesMap.put(packetColor, coordinatesList);
                }
            }


            // Run A* to find one of the shortest paths to the closest packet of which a destination is known                                                  /////////////

            // Convert all stored cell information to a list of cells
            Set<CellPerception> cells = agentState.memory2Cells();
            // Create a fictive environment with these cells and the object that manages moves
            VirtualEnvironment virtualEnvironment = new VirtualEnvironment(cells, new MovementManager());
            // Create a path finder object that can search the shortest paths to specific destinations in the fictive
            // environment
            PathFinder pathFinder = new PathFinder(virtualEnvironment);

            // Current cell the agent stands on
            CellPerception agentCell = agentState.getPerception().getCellPerceptionOnRelPos(0, 0);

            // For all colors of which we know a destination, concatenate the respective memory fragment of the
            // packet coordinates
            String rep = PacketRep.class.toString();
            String concatenatedMemoryFragments = "";
            for(String color: coloredPacketCoordinatesMap.keySet()){
                String memoryKey = AgentState.rep2MemoryKey(rep, color);
                String memoryFragment = agentState.getMemoryFragment(memoryKey);
                concatenatedMemoryFragments += memoryFragment;
            }

            List<Coordinate> coordinatesList = Coordinate.string2Coordinates(concatenatedMemoryFragments);
            // if we memorized at least a packet of which we know the destination
            if(!coordinatesList.isEmpty()) {
                // Get the fictive cells from the fictive environment of the packets we know
                Set<CellPerception> packetCells = new HashSet<>();
                for (Coordinate packetCoordinates : coordinatesList) {
                    packetCells.add(virtualEnvironment.getCell(packetCoordinates));
                }
                // Run A* to find the shortest path from the agent's current cell to one of the possible packet cells
                // (of which we know the destination)
                List<Coordinate> path2ClosestPacket = pathFinder.astar(agentCell, packetCells);

                // We can remove the path to the closest destination, we are anyway not following it anymore.
                // It helps to save a spot in memory.
                agentState.removeMemoryFragment("ClosestDestinationPath");

                // Store to memory the shortest path to the closest packet
                agentState.addMemoryFragment("ClosestPacketPath", Coordinate.coordinates2String(path2ClosestPacket));
            }
        }

        // If now a path to the closest packet exists in memory
        if (agentState.getMemoryFragmentKeys().contains("ClosestPacketPath")){
            ///////////// Sort the moves by following the closest packet path /////////////

            // Retrieve the current path the agent has to follow to get to the closest packet
            String memoryFragment = agentState.getMemoryFragment("ClosestPacketPath");
            List<Coordinate> path2ClosestPacket = Coordinate.string2Coordinates(memoryFragment);
            // Retrieve and remove from memory the next cell we should go to in this path
            Coordinate nextCoordinatesInPath = path2ClosestPacket.get(0);
            agentState.removeFromMemory(nextCoordinatesInPath, "ClosestPacketPath");

            // Sort the moves to best match the direction to the next cell in the path.
            // It allows the agent not to be forced to follow exactly the optimal path if for example another
            // agent appears in his way
            Coordinate agentCoordinates = new Coordinate(agentState.getX(), agentState.getY());
            Coordinate direction2Packet = nextCoordinatesInPath.diff(agentCoordinates);
            movementManager.sort(direction2Packet);
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

