package util.movement;

import agent.AgentState;
import environment.Coordinate;
import environment.Perception;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Step {

    public static Coordinate random(AgentState agentState) {
        // Potential moves an agent can make (radius of 1 around the agent)
        List<Coordinate> moves = new ArrayList<>(List.of(
                new Coordinate(1, 1), new Coordinate(-1, -1),
                new Coordinate(1, 0), new Coordinate(-1, 0),
                new Coordinate(0, 1), new Coordinate(0, -1),
                new Coordinate(1, -1), new Coordinate(-1, 1)
        ));

        // Shuffle moves randomly
        Collections.shuffle(moves);

        Perception perception = agentState.getPerception();

        for (Coordinate move : moves) {
            int x = move.getX();
            int y = move.getY();

            // If the area is null, it is outside the bounds of the environment
            //  (when the agent is at any edge for example some moves are not possible)
            if (perception.getCellPerceptionOnRelPos(x, y) != null && perception.getCellPerceptionOnRelPos(x, y).isWalkable()) {
                return new Coordinate(agentState.getX() + x, agentState.getY() + y);
            }
        }

        return null;
    }
}
