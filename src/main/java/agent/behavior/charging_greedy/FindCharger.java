package agent.behavior.charging_greedy;

import agent.AgentAction;
import agent.AgentCommunication;
import agent.AgentState;
import agent.behavior.Behavior;
import agent.utils.MovementManager;
import agent.utils.PathFinder;
import agent.utils.VirtualEnvironment;
import environment.CellPerception;
import environment.Coordinate;
import environment.world.agent.AgentRep;
import environment.world.destination.DestinationRep;
import environment.world.packet.Packet;
import util.MyColor;

import java.awt.*;
import java.util.*;
import java.util.List;


public class FindCharger extends Behavior {
    Random rand = new Random(42);
    List<Coordinate> blockedCoordinates = new ArrayList<Coordinate>();
    boolean moveOutOfTheWay;

    @Override
    public void communicate(AgentState agentState, AgentCommunication agentCommunication) {
/*            // send messages to the other agents in view
            Set<CellPerception> agents = agentState.getPerceivableCellsWithAgents();
            String message = agentState.getName() +": " + agentState.getBatteryState();
            for (CellPerception agent : agents){
                Optional<AgentRep> agentRep = agent.getAgentRepresentation();

                if (!agentRep.isPresent() || agentRep.get().getName().equals(agentState.getName()))
                    continue;

                System.out.println(agentRep.get().getName());
                agentCommunication.sendMessage(agentRep.get(),message);

            }
*/
        // set move out of the way
        moveOutOfTheWay = false;

        // receive incoming messages
        if(agentCommunication.getNbMessages() != 0)
            System.out.println("---------- incomming messages for: " + agentState.getName()+ " ----------");
        for (int i = 0; i <  agentCommunication.getNbMessages(); i++){
            // pop the first message
            String message =agentCommunication.getMessage(0).getMessage();
            System.out.println(agentState.getName() + ", " + i + ": " + message);
            agentCommunication.removeMessage(0);

            // get the coordinate at which the calling agent is stuck
            Coordinate stuckAgent = Coordinate.string2Coordinates(message).get(0);
            blockedCoordinates.add(stuckAgent);
            moveOutOfTheWay = true;
        }

        if (!blockedCoordinates.isEmpty())
            dealWithBeingMaybeStuck(agentState, agentCommunication, blockedCoordinates);
    }


    @Override
    public void act(AgentState agentState, AgentAction agentAction) {
        if (agentState.getMemoryFragment(requiredMoveKey) != null){
            tryForceMove(agentState,agentAction);
            return;
        }

        if (moveOutOfTheWay) {
            moveOutOfTheWay(agentState,agentAction,blockedCoordinates);
            return;
        }


        MovementManager movementManager = new MovementManager();

        ///////////// Criterion 1 : Sort the agent's moves by decreasing gradient value /////////////
        ///////////// on the respective cells they lead to                              /////////////
        // The agent will move closer the nearest energy station by following the lowest gradient around him
        movementManager.sort(agentState, "DecreasingGradientValue");

        // Retrieve the available moves in the order they were sorted
        List<Coordinate> moves = movementManager.getMoves();


        ///////////// Criterion 2 : The agent can only move where on walkable cells /////////////

        // Check for walkable moves
        for (var move : moves) {
            int x = move.getX() + agentState.getX();
            int y = move.getY() + agentState.getY();

            // If the agent can not walk at these coordinates
            if (!agentState.canWalk(x, y)) {
                movementManager.remove(move);
            }
        }


//        if (movementManager.getNbMoves() == 0) {
//            // Retrieve the available moves
//            movementManager = new MovementManager();
//            List<Coordinate> moves = movementManager.getMoves();
//
//            // Check for viable moves
//            for (var move : moves) {
//                int x = move.getX() + agentState.getX();
//                int y = move.getY() + agentState.getY();
//
//                // If the agent can not walk at these coordinates
//                if (!agentState.canWalk(x, y)) {
//
//                }
//            }
//        }


        if (movementManager.getNbMoves() > 0) { // if there are viable moves

            ///////////// Let's move the agent /////////////

            Coordinate move = movementManager.getMoves().get(0);
            int x = move.getX() + agentState.getX();
            int y = move.getY() + agentState.getY();

            // Move the agent
            agentAction.step(x, y);
            return;
        }


        ///////////// No viable or available moves, let's skip this agent's turn /////////////

        // Skip turn
        agentAction.skip();
    }

    private void moveOutOfTheWay(AgentState agentState, AgentAction agentAction, List<Coordinate> blackList) {
        CellPerception[] neighbours = agentState.getPerception().getNeighbours();

        for( CellPerception neighbour: neighbours){
            if (neighbour == null || !neighbour.isWalkable() || blackList.contains(neighbour.getCoordinates()))
                continue;

            int x = neighbour.getX();
            int y = neighbour.getY();

            agentAction.step(x,y);
            return;
        }

        agentAction.skip();
    }
}
