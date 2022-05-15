package agent.behavior.task_coordination;

import agent.AgentAction;
import agent.AgentCommunication;
import agent.AgentState;
import agent.behavior.Behavior;
import agent.utils.MovementManager;
import agent.utils.PathFinder;
import agent.utils.VirtualEnvironment;
import com.google.common.collect.Table;
import environment.CellPerception;
import environment.Coordinate;
import environment.Mail;
import environment.Perception;
import environment.world.agent.AgentRep;
import environment.world.destination.DestinationRep;
import environment.world.packet.PacketRep;
import util.MyColor;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class Pickup extends Behavior {
    Random rand = new Random(42);
    float RANDOM_EXPLORATION_RATE = 0.2f;
    int GATHERING_RADIUS = 2;


    @Override
    public void act(AgentState agentState, AgentAction agentAction) {
        ///////////// Memorize all representations that the agent can see in his perception area /////////////
        agentState.memorizeAllPerceivableRepresentations();

        // Print the state of the world as seen and memorized by the agent until now
//        agentState.prettyPrintWorld();


        ///////////// Check whether the agent can pick up a neighbouring packet      /////////////
        ///////////// from which it knows a matching destination or gathering place  /////////////

        // Check for a neighbouring packet to pick up
        Set<CellPerception> packetCells = agentState.getNeighbouringCellsWithPacket();
        for(CellPerception packetCell: packetCells) {
            Color packetColor = packetCell.getRepOfType(PacketRep.class).getColor();
            // If the agent has no specific preference (i.e. he is of color black and thus returns an empty Optional object)
            // or if the agent's color match the packet color, he will be able to pick it up
            if (agentState.getColor().orElse(null) == null || agentState.getColor().orElse(Color.BLACK).equals(packetColor)) {
                // Build the key to find a destination in memory where the current packet could be later delivered
                String packetColorName = MyColor.getName(packetCell.getRepOfType(PacketRep.class).getColor());
                String destinationKey = AgentState.rep2MemoryKey(DestinationRep.class.toString(), packetColorName);

                // If the agent knows a destination that can accept the current packet (i.e. same color)
                if (agentState.getMemoryFragmentKeys().contains(destinationKey)) {
                    // if the agent knows a path to a destination or gathering place for a packet
                    if (agentState.getMemoryFragmentKeys().contains("ShortestPath2Destination")
                            || agentState.getMemoryFragmentKeys().contains("ShortestPath2Gather")) {
                        boolean canPickup = true;
                        // if the agent knows a path to a gathering place (rather than a destination) for a packet
                        if (agentState.getMemoryFragmentKeys().contains("ShortestPath2Gather")) {
                            // we check if the packet is already close enough to the gathering place, in which case the
                            // agent does not need to transport it closer
                            List<Coordinate> shortestPath2Gather = Coordinate.string2Coordinates(agentState.getMemoryFragment("ShortestPath2Gather"));
                            Coordinate gatheringCoordinates = shortestPath2Gather.get(shortestPath2Gather.size() - 1);
                            if (packetCell.getCoordinates().distanceFrom(gatheringCoordinates) <= GATHERING_RADIUS) {
                                canPickup = false;
                                agentState.removeMemoryFragment("ShortestPath2Gather"); // Remove the path to the packet from memory
                            }
                            agentState.removeMemoryFragment("ShortestPath2Packet"); // Remove the path to the packet from memory
                        }

                        // if the agent can pick up the packet
                        if (canPickup) {
                            agentState.removeFromMemory(packetCell); // Remove the packet from memory as we will pick it up
                            agentState.removeMemoryFragment("ShortestPath2Packet"); // Remove the path to the packet from memory
                            agentAction.pickPacket(packetCell.getX(), packetCell.getY()); // Pick the packet at hand
                            return;
                        }
                    }
                }
            }
        }


        ///////////// Forget all representations that are not present anymore                       /////////////
        ///////////// in the agent's perception area (because a packet was picked up for example)   /////////////
        agentState.forgetAllUnperceivableRepresentations();


        ///////////// If a packet can not be picked, the agent moves instead /////////////

        ///////////// Criterion 1: Let's not go backwards after moving forward /////////////
        // Retrieve the positions that the agent has already been to
        MovementManager movementManager = new MovementManager();
        if (rand.nextFloat() < RANDOM_EXPLORATION_RATE){
            // Shuffle the moves to add some randomness to the process. It helps to break infinite loop
            movementManager.shuffle(rand);
        }else {
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
                // but does not strictly force him to do so if it is not possible to go this way.
                movementManager.sort(lastMove, "ManhattanDistance");
            }
        }

        // Prioritise high impact packets
        if (!priorityCoordinates.isEmpty()) {
            // Pathfinding setup
            Set<CellPerception> environmentKnowledge = agentState.memory2Cells();
            VirtualEnvironment virtualEnvironment = new VirtualEnvironment(environmentKnowledge, new MovementManager());
            PathFinder pathFinder = new PathFinder(virtualEnvironment);
            Set<CellPerception[]> agentDestinationCells = new HashSet<>();
            CellPerception agentCell = agentState.getPerception().getCellPerceptionOnRelPos(0, 0);

            for (var coordinate: priorityCoordinates) {
                agentDestinationCells.add(new CellPerception[]{
                   virtualEnvironment.getCell(coordinate)
                });
            }

            List<List<Coordinate>> shortestPaths = pathFinder.astar(agentCell, agentDestinationCells);
            if (!shortestPaths.isEmpty()) {
                List<Coordinate> shortestPath2Packet = shortestPaths.get(0);
                if(!shortestPath2Packet.isEmpty()) {
                    agentState.addMemoryFragment("ShortestPath2Packet", Coordinate.coordinates2String(shortestPath2Packet));
                }
            }
        }

        // Run A* to find one of the shortest paths to the closest packet of which a destination is known if no path has
        // been computed yet.
        if (!agentState.getMemoryFragmentKeys().contains("ShortestPath2Packet")){
            ///////////// Criterion 2: Let's target the closest packet that the agent has memorized /////////////
            ///////////// and its known destination (there may be more than one)                    /////////////
            ///////////// Find the shortest path to the get to one of the known packets and then    /////////////
            ///////////// to one of its corresponding destinations. The problem is solved for each  /////////////
            ///////////// pair of potential packet and corresponding destination                    /////////////

            Map<String, List<Coordinate>> coloredPacketCoordinatesMap = new HashMap<>();
            Map<String, List<Coordinate>> coloredDestinationCoordinatesMap = new HashMap<>();

            // If the agent is not black, retrieve all memory keys where packets matching the agent's color are stored
            // Otherwise, retrieve all memory keys where packets are stored
            String subkey = PacketRep.class.toString();
            Color agentColor = agentState.getColor().orElse(null);
            if (agentColor != null){
                subkey = AgentState.rep2MemoryKey(subkey, MyColor.getName(agentColor));
            }
            Set<String> packetKeys = agentState.getMemoryFragmentKeysContaining(subkey);
            // For each of the selected keys where there are matching packets stored in memory
            for (String packetKey : packetKeys) {
                String packetColor = AgentState.memoryKey2Rep(packetKey)[1];
                String destinationKey = AgentState.rep2MemoryKey(DestinationRep.class.toString(), packetColor);
                // If the agent knows a destination that can accept the current packet (i.e. same color)
                if (agentState.getMemoryFragmentKeys().contains(destinationKey)) {
                    // we will only keep the coordinates of the packets whose destination is known by the agent
                    String memoryFragment = agentState.getMemoryFragment(packetKey);
                    List<Coordinate> coordinatesList = Coordinate.string2Coordinates(memoryFragment);
                    coloredPacketCoordinatesMap.put(packetColor, coordinatesList);

                    memoryFragment = agentState.getMemoryFragment(destinationKey);
                    coordinatesList = Coordinate.string2Coordinates(memoryFragment);
                    coloredDestinationCoordinatesMap.put(packetColor, coordinatesList);
                }
            }

          /*  // If the agent is not black, retrieve all memory keys where destinations that match the agent's color are stored
            // Otherwise, retrieve all memory keys where destinations are stored
            subkey = DestinationRep.class.toString();
            if (agentColor != null){
                subkey = AgentState.rep2MemoryKey(subkey, MyColor.getName(agentColor));
            }
            Set<String> destinationKeys = agentState.getMemoryFragmentKeysContaining(subkey);
            // For each key where there are destinations stored in memory
            for (String destinationKey : destinationKeys) {
                // Build up a dictionary of destination coordinates where the keys are the destinations color
                String destinationColor = AgentState.memoryKey2Rep(destinationKey)[1];
                String memoryFragment = agentState.getMemoryFragment(destinationKey);
                List<Coordinate> coordinatesList = Coordinate.string2Coordinates(memoryFragment);
                coloredDestinationCoordinatesMap.put(destinationColor, coordinatesList);
            }
*/

            // Run A* to find one of the shortest paths to the closest packet of which a destination is known
            // Convert all stored cell information to a list of cells
            Set<CellPerception> cells = agentState.memory2Cells();

            // Create a fictive environment with these cells and the object that manages moves
            VirtualEnvironment virtualEnvironment = new VirtualEnvironment(cells, new MovementManager());// movementManager);
            // Create a PathFinder object that can search the shortest paths linking specified ordered destinations
            // in the fictive environment
            PathFinder pathFinder = new PathFinder(virtualEnvironment);

            // Current cell the agent stands on
            CellPerception agentCell = agentState.getPerception().getCellPerceptionOnRelPos(0, 0);

            // Define the destination cells the agent must go to (typically the target packet's cell and its closest
            // delivering destination)
            Set<CellPerception[]> agentDestinationCells = new HashSet<>();
            for(var packetEntry: coloredPacketCoordinatesMap.entrySet()){
                String color = packetEntry.getKey();
                List<Coordinate> destinationCoordinatesList = coloredDestinationCoordinatesMap.get(color);
                for (Coordinate packetCoordinates: packetEntry.getValue()){
                    for(Coordinate destinationCoordinates: destinationCoordinatesList){
                        // Get the fictive cells from the fictive environment of the packets we know
                        agentDestinationCells.add(new CellPerception[]{
                                virtualEnvironment.getCell(packetCoordinates),
                                virtualEnvironment.getCell(destinationCoordinates)
                        });
                    }
                }
            }

            // if there exists a packet that the agent could transport to a known destination
            if (!agentDestinationCells.isEmpty()) {
                // Run A* to find the shortest path from the agent's current cell to one of the possible packet cells
                // (of which we know the destination)
                List<List<Coordinate>> shortestPaths = pathFinder.astar(agentCell, agentDestinationCells);
                // if a path exists to the destinations
                if (!shortestPaths.isEmpty()){
                    // Store to memory the shortest path to the closest packet
                    List<Coordinate> shortestPath2Packet = shortestPaths.get(0);
                    List<Coordinate> shortestPath2Destination = shortestPaths.get(1);
                    if(!shortestPath2Packet.isEmpty()) { // only a non empty path is interesting
                        agentState.addMemoryFragment("ShortestPath2Packet", Coordinate.coordinates2String(shortestPaths.get(0)));
                    }
                    if(!shortestPath2Destination.isEmpty()) { // only a non empty path is interesting
                        agentState.removeMemoryFragment("ShortestPath2Gather");
                        agentState.addMemoryFragment("ShortestPath2Destination", Coordinate.coordinates2String(shortestPaths.get(1)));
                    }
                }
                else{
                    if(agentColor != null && coloredDestinationCoordinatesMap.get(MyColor.getName(agentColor)) != null ) {
                        crucialPacketsBlockingDelivery(agentState, coloredDestinationCoordinatesMap.get(MyColor.getName(agentColor)));
                    }
                }
            }
        }


        // if the agent still does not have a path to follow to a packet
        if (!agentState.getMemoryFragmentKeys().contains("ShortestPath2Packet")){

            ///////////// Criterion 3: Let's target the closest packet that the agent has memorized /////////////
            ///////////// Find the shortest path at the same time to get to one of the known        /////////////
            ///////////// packets and bring it to a "gathering" place. The gathering place is the   /////////////
            ///////////// cell which is at the center of all known packets of the same color        /////////////

            // Retrieve the locations where packets of the same color as the agent are stored
            String subkey = PacketRep.class.toString();
            Color agentColor = agentState.getColor().orElse(null);
            if (agentColor != null){
                subkey = AgentState.rep2MemoryKey(subkey, MyColor.getName(agentState.getColor().orElse(Color.BLACK)));
            }
            Set<String> packetKeys = agentState.getMemoryFragmentKeysContaining(subkey);
            List<Coordinate> packetCoordinatesList = new ArrayList<Coordinate>();
            // For each of the selected keys where there are packets of which a matching destination is known,
            // retrieve the packet location
            for (String packetKey : packetKeys) {
                String packetColor = AgentState.memoryKey2Rep(packetKey)[1];
                String destinationKey = AgentState.rep2MemoryKey(DestinationRep.class.toString(), packetColor);
                // If the agent knows a destination that can accept the current packet (i.e. same color)
                if (agentState.getMemoryFragmentKeys().contains(destinationKey)) {
                    String memoryFragment = agentState.getMemoryFragment(packetKey);
                    List<Coordinate> newPacketCoordinatesList = Coordinate.string2Coordinates(memoryFragment);
                    packetCoordinatesList = Stream.concat(packetCoordinatesList.stream(), newPacketCoordinatesList.stream())
                            .collect(Collectors.toList());
                }
            }

            // Run A* to find one of the shortest paths to the closest packet

            // Convert all stored cell information to a list of cells
            Set<CellPerception> cells = agentState.memory2Cells();
            // Create a fictive environment with these cells and the object that manages moves
            VirtualEnvironment virtualEnvironment = new VirtualEnvironment(cells, new MovementManager());//movementManager);
            // Create a PathFinder object that can search the shortest paths linking specified ordered destinations
            // in the fictive environment
            PathFinder pathFinder = new PathFinder(virtualEnvironment);

            // Current cell the agent stands on
            CellPerception agentCell = agentState.getPerception().getCellPerceptionOnRelPos(0, 0);

            Set<CellPerception[]> agentDestinationCells = new HashSet<>();

            for (Coordinate packetCoordinates: packetCoordinatesList){
                // Compute the gathering place as the average of all packets coordinates (of the same color)
                Coordinate gatheringCoordinates = new Coordinate(0, 0);
                for(Coordinate otherPacketCoordinates: packetCoordinatesList) {
                    gatheringCoordinates = gatheringCoordinates.add(otherPacketCoordinates);
                }
                gatheringCoordinates = gatheringCoordinates.divideBy(packetCoordinatesList.size());
                // We only plan on moving a packet closer to the gathering place if it is not already close enough
                if(gatheringCoordinates.distanceFrom(packetCoordinates) > GATHERING_RADIUS) {
                    // Get the fictive cells from the fictive environment of the packets we know
                    agentDestinationCells.add(new CellPerception[]{
                            virtualEnvironment.getCell(packetCoordinates),
                            virtualEnvironment.getCell(gatheringCoordinates)
                    });
                }
            }

            // If there exists at least a gathering place where a packet should be moved
            if (!agentDestinationCells.isEmpty()) {
                // Run A* to find the shortest path from the agent's current cell to one of the possible packet cells
                // (of which we know the destination)
                List<List<Coordinate>> shortestPaths = pathFinder.astar(agentCell, agentDestinationCells);
                // If a path in at least 2 consequent phases exist
                if (!shortestPaths.isEmpty() && shortestPaths.size() >= 2){
                    List<Coordinate> shortestPath2Packet = shortestPaths.get(0);
                    List<Coordinate> shortestPath2Gather = shortestPaths.get(1);

                    // Store to memory the shortest path to the closest packet
                    if(!shortestPath2Packet.isEmpty()) {
                        agentState.addMemoryFragment("ShortestPath2Packet", Coordinate.coordinates2String(shortestPaths.get(0)));
                    }
                    // Store to memory the shortest path from the packet to the closest gathering place
                    if(!shortestPath2Gather.isEmpty()) {
                        agentState.removeMemoryFragment("ShortestPath2Destination");
                        agentState.addMemoryFragment("ShortestPath2Gather", Coordinate.coordinates2String(shortestPaths.get(1)));
                    }
                }
            }
        }

        // If now a path to the closest packet exists in memory
        if (agentState.getMemoryFragmentKeys().contains("ShortestPath2Packet")){
            ///////////// Sort the moves by following the closest packet path /////////////

            // Retrieve the current path the agent has to follow to get to the closest packet
            String memoryFragment = agentState.getMemoryFragment("ShortestPath2Packet");
            List<Coordinate> path2Packet = Coordinate.string2Coordinates(memoryFragment);
            // Retrieve and remove from memory the next cell we should go to in this path
            Coordinate nextCoordinatesInPath = path2Packet.get(0);
            agentState.removeFromMemory(nextCoordinatesInPath, "ShortestPath2Packet");

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


    private void crucialPacketsBlockingDelivery(AgentState agentState, List<Coordinate> destinationCoordinatesList){
        // make environment without packets
        System.out.println("something: " + destinationCoordinatesList);
        Set<CellPerception> cells = agentState.memory2CellsWithoutPackets();
        // replace packages by empty cells
        List<Coordinate> packets = getPackets(agentState);
        Set<CellPerception> additionalEmptyCells = new HashSet<>();
        for (Coordinate packet : packets){
            additionalEmptyCells.add(new CellPerception(packet));
        }
        cells.addAll(additionalEmptyCells);
        VirtualEnvironment virtualEnvironment = new VirtualEnvironment(cells,new MovementManager());


        // setup pathfinder
        // create pathfinder
        PathFinder pathFinder = new PathFinder(virtualEnvironment);
        // get cellPerception of agent cell
        CellPerception agentCell = agentState.getPerception().getCellPerceptionOnRelPos(0,0);
        // get list of cellPerceptions of possible destinations
        Set<CellPerception[]> agentDestinationCells = new HashSet<>();
        for (Coordinate destination: destinationCoordinatesList){
            agentDestinationCells.add(new CellPerception[]{virtualEnvironment.getCell(destination)});
        }

        // calculate path to the shortest destination
        List<List<Coordinate>> shortestPath = pathFinder.astar(agentCell, agentDestinationCells);

        if (shortestPath.isEmpty()){
            return;
        }
             //TODO: what moet je doen als er geen path naar de destination bestaat en het niet de fout is van de packets

        // check path for packets
        List<Coordinate> packetsInShortestPath = new ArrayList<Coordinate>();

        for (Coordinate pathCell : shortestPath.get(0)){
            if (packets.contains(pathCell))
                packetsInShortestPath.add(pathCell);
        }
        System.out.println(packetsInShortestPath);

        // check each packet if it is blocking
        for (Coordinate packetInPath: packetsInShortestPath){
            if (checkIfPacketIsBlocking(agentState, packetInPath, packetsInShortestPath, agentDestinationCells, agentCell)){
                // putt blocking packets in memory
                if(!agentState.memoryFragmentContains(crucialCoordinateMemory, packetInPath) &&
                        !agentState.memoryFragmentContains(sendCrucialCoordinates, packetInPath))
                agentState.append2Memory(crucialCoordinateMemory,packetInPath.toString());
            }
        }

    }

    private boolean checkIfPacketIsBlocking(AgentState agentState, Coordinate packet, List<Coordinate> packets,
                                            Set<CellPerception[]> agentDestinationCells, CellPerception agentCell){

        // make environment without packets, but with packet;
        List<Coordinate> packetsExcludingPacket = new ArrayList<>();
        packetsExcludingPacket.addAll(packets);
        packetsExcludingPacket.remove(packet);
        List<Coordinate> allPackets = getPackets(agentState);
        Set<CellPerception> additionalEmptyCells = new HashSet<>();
        for (Coordinate packetCoordinate : allPackets) {
            if (packetsExcludingPacket.contains(packetCoordinate))
                additionalEmptyCells.add(new CellPerception(packetCoordinate));
        }
        Set<CellPerception> cells = agentState.memory2CellsExcludingCoordinates(packetsExcludingPacket);
        cells.addAll(additionalEmptyCells);
        VirtualEnvironment virtualEnvironment = new VirtualEnvironment(cells,new MovementManager());

        // setup pathfinder
        // create pathfinder
        PathFinder pathFinder = new PathFinder(virtualEnvironment);

        // calculate path to the shortest destination
        List<List<Coordinate>> shortestPath = pathFinder.astar(agentCell, agentDestinationCells);

        // return path.isEmpty()

        return shortestPath.isEmpty();
    }

    private List<Coordinate> getPackets(AgentState agentState){
        List<Coordinate> packets = new ArrayList<>();
        Set<String> packetKeys = agentState.getMemoryFragmentKeysContaining(PacketRep.class.toString());
        for (String key : packetKeys){
            String data = agentState.getMemoryFragment(key);
            packets.addAll(Coordinate.string2Coordinates(data));
        }
        return packets;
    }
}