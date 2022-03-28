package agent.behavior.wall_hugger;

import java.util.Collections;
import java.util.List;

import agent.AgentAction;
import agent.AgentCommunication;
import agent.AgentState;
import agent.behavior.Behavior;
import environment.Coordinate;

public class Wander extends Behavior {

    @Override
    public void communicate(AgentState agentState, AgentCommunication agentCommunication) {
        // No communication
    }


    @Override
    public void act(AgentState agentState, AgentAction agentAction) {
        // --------------------- look around ------------------------------
        lookAround(agentState);

        // --------------------- movement ----------------------------------
        List<Coordinate> moves = getMoves();


        // Shuffle moves randomly
        Collections.shuffle(moves);

        // Check for viable moves
        for (var move : moves) {
            var perception = agentState.getPerception();
            int x = move.getX();
            int y = move.getY();

            // If the area is null, it is outside the bounds of the environment
            //  (when the agent is at any edge for example some moves are not possible)
            if (perception.getCellPerceptionOnRelPos(x,y) != null &&
                    perception.getCellPerceptionOnRelPos(x,y).isWalkable() &&
                    !isPreviousPosition(agentState,move)) {
                agentAction.step(agentState.getX() + x, agentState.getY() + y);
                return;
            }
        }

        // No viable moves, skip turn
        agentAction.skip();
    }

    private boolean isPreviousPosition (AgentState agentState, Coordinate position){
        int prevX = agentState.getPerceptionLastCell().getX();
        int prevY = agentState.getPerceptionLastCell().getY();

        return prevX == position.getX() && prevY == position.getY();
    }
}
