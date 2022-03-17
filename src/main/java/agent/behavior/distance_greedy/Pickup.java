package agent.behavior.distance_greedy;

import agent.AgentAction;
import agent.AgentCommunication;
import agent.AgentState;
import agent.behavior.Behavior;
import environment.CellPerception;
import environment.Coordinate;
import environment.MovementManager;
import environment.world.destination.DestinationRep;
import environment.world.packet.PacketRep;
import util.MyColor;

import java.util.*;
import java.util.List;


public class Pickup extends Behavior {
    Random rand = new Random(42);

    @Override
    public void communicate(AgentState agentState, AgentCommunication agentCommunication) {
//        // Send Messages
//        var perception = agentState.getPerception();
//        int vw = perception.getWidth();
//        int vh = perception.getHeight();
//
//        for (int i = 0; i < vw; i++) {
//            for (int j = 0; j < vh; j++) {
//                // Look at each cell in the perception area
//                var cell = perception.getCellAt(i, j);
//                if (cell != null && cell.containsAgent()) {
//                    var agentRep = cell.getAgentRepresentation().orElse(null);
//
//                    // Send list of destinations in memory
//                    String messageDestination;
//                    for (String key: agentState.getMemoryFragmentKeysContaining(DestinationRep.class.toString())){
//                        messageDestination = key + ":" + agentState.getMemoryFragment(key);
//                        agentCommunication.sendMessage(agentRep, messageDestination);
//                    }
//
//                    // Send list of packets in memory
//                    String messagePacket;
//                    for (String key: agentState.getMemoryFragmentKeysContaining(PacketRep.class.toString())){
//                        messagePacket = key + ":" + agentState.getMemoryFragment(key);
//                        agentCommunication.sendMessage(agentRep, messagePacket);
//                    }
//
//                    // Send list of representations removed from memory
//                    String key = "Remove";
//                    String messageRemove = key + ":" + agentState.getMemoryFragment(key);
//                    agentCommunication.sendMessage(agentRep, messageRemove);
//                }
//            }
//        }
//
//
//        // Receives Messages
//        for(var mail: agentCommunication.getMessages()){
//            String message = mail.getMessage();
////            System.out.println(message);
//            String key = message.split(":")[0];//substring(0, message.indexOf("_", message.indexOf("_")+1));
////            System.out.println("Key: " + key);
//            String oldData = agentState.getMemoryFragment(key);
//            String newData;
//            Set oldCoordinatesSet;
//            if(oldData != null) {
//                newData = oldData;
//                List<Coordinate> oldCoordinatesList = Coordinate.string2Coordinates(oldData);
//                oldCoordinatesSet = new HashSet(oldCoordinatesList);
//            }else{
//                newData = "";
//                oldCoordinatesSet = new HashSet<Coordinate>();
//            }
//            String otherData = message.split(":")[1];//.split("_")[2];
////            System.out.println("OtherData: " + otherData);
//            List<Coordinate> otherCoordinatesList = Coordinate.string2Coordinates(otherData);
//            Set otherCoordinatesSet = new HashSet(otherCoordinatesList);
//
//            otherCoordinatesSet.removeAll(oldCoordinatesSet);
//
//
//            for (var coordinates: otherCoordinatesSet){
//                newData += coordinates.toString();
//            }
//            agentState.removeMemoryFragment(key);
////            System.out.println("NewData: " + newData);
//            agentState.addMemoryFragment(key, newData);
//
//            if (key.equals("Remove")){
//                for (var coordinates: otherCoordinatesList) {
//                    agentState.removeRepFromMemory(coordinates, PacketRep.class.toString());
//                }
//            }
//        }
    }


    @Override
    public void act(AgentState agentState, AgentAction agentAction) {
        agentState.prettyPrintWorld();

        ///////////// Memorize all representations that the agent can see in his perception area /////////////
        agentState.memorizeAllPerceivableRepresentations();


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
                agentState.removeRepFromMemory(packetCell); // Remove the packet from memory as we will pick it up
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


        ///////////// Criterion 2: Let's target the closest packet that the agent can see /////////////
        ///////////// and of which the destination is known                               /////////////

        List<Coordinate> packetCoordinatesList = new ArrayList<>();

        // For all cells containing a packet in the agent's perception area
        for (var cell: agentState.getPerceivableCellsWithPacket()){
            String packetColor = MyColor.getName(cell.getRepOfType(PacketRep.class).getColor());
            String destinationKey = AgentState.rep2MemoryKey(DestinationRep.class.toString(), packetColor);
            // If the agent knows a destination that can accept the current packet (i.e. same color)
            if (agentState.getMemoryFragmentKeys().contains(destinationKey)) {
                Coordinate packetCoordinates = new Coordinate(cell.getX(), cell.getY());
                packetCoordinatesList.add(packetCoordinates); // Save the coordinates of the cell
            }
        }
        // Select the coordinates of the cell containing a packet that is the closest to the agent
        Coordinate agentCoordinates = new Coordinate(agentState.getX(), agentState.getY());
        Coordinate closestPacketCoordinates = agentCoordinates.closestCoordinatesTo(packetCoordinatesList);


        ///////////// Criterion 3: Let's target the closest packet that the agent has memorized /////////////
        ///////////// and of which the destination is known                                     /////////////

        if (closestPacketCoordinates == null){
            // If no packets can be seen in the agent's perception area, we will rely instead
            // on the packet locations it has memorised

            // Retrieve all memory keys where packets are stored
            String subkey = PacketRep.class.toString();
            Set<String> packetKeys = agentState.getMemoryFragmentKeysContaining(subkey);
            StringBuilder dataConcatenated = new StringBuilder();
            // For each key where there are packets stored in memory
            for (String packetKey : packetKeys) {
                String packetColor = AgentState.memoryKey2Rep(packetKey)[1];
                String destinationKey = AgentState.rep2MemoryKey(DestinationRep.class.toString(), packetColor);
                // If the agent knows a destination that can accept the current packet (i.e. same color)
                if (agentState.getMemoryFragmentKeys().contains(destinationKey)) {
                    // we will only keep the coordinates of the packets whose destination are known by the agent
                    dataConcatenated.append(agentState.getMemoryFragment(packetKey));
                }
            }

            List<Coordinate> coordinatesList = Coordinate.string2Coordinates(dataConcatenated.toString());
            closestPacketCoordinates = agentCoordinates.closestCoordinatesTo(coordinatesList);
        }


        ///////////// Sort the moves according to the targeted packet /////////////

        // If the agent has a packet location that he can target
        // (either because he sees it in his perception area or because it has memorized it).
        // Note: the agent can target it only if he knows a destination for this packet
        if (closestPacketCoordinates != null) {
            // Sort moves by how close they move the agent towards the target coordinates
            Coordinate direction2Packet = closestPacketCoordinates.diff(agentCoordinates);
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

