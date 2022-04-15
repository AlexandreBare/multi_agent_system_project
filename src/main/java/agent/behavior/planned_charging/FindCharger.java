package agent.behavior.planned_charging;

import agent.AgentAction;
import agent.AgentCommunication;
import agent.AgentState;
import agent.behavior.Behavior;
import agent.utils.MovementManager;
import environment.Coordinate;

import java.util.List;
import java.util.Random;


public class FindCharger extends Behavior {
    Random rand = new Random(42);


    @Override
    public void communicate(AgentState agentState, AgentCommunication agentCommunication) {

    }


    @Override
    public void act(AgentState agentState, AgentAction agentAction) {
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
}
