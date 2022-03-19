package environment;

import agent.AgentState;

import java.util.*;
import java.util.stream.Collectors;

public class MovementManager {
    List<Coordinate> moves = new ArrayList<>(List.of(
            new Coordinate(1, 1), new Coordinate(1, -1),
            new Coordinate(-1, -1), new Coordinate(-1, 1),
            new Coordinate(1, 0), new Coordinate(0, 1),
            new Coordinate(-1, 0), new Coordinate(0, -1)
    ));

    public List<Coordinate> getMoves(){
        return moves;
    }

    /**
     * Shuffle the list of moves
     *
     * @param rand      The pseudo-random generator to abide by when shuffling the moves
     */
    public void shuffle(Random rand){
    Collections.shuffle(moves, rand);
}

    /**
     * Removes a given move from the list of moves
     *
     * @param moveToRemove      The move to remove from the list of moves
     */
    public void remove(Coordinate moveToRemove) { moves.remove(moveToRemove); }

    /**
     * Sorts the available moves according to a "maximum coordinate distance" to a given direction
     *
     * @param direction2Destination     The direction that should be followed by the move
     *                                  (moves that do not diverge too much from this direction will have a low score)
     */
    public void sort(Coordinate direction2Destination) {
        sort(direction2Destination, "MaxCoordinateDistance");
    }

    /**
     * Sort the available moves according to some measure of distance to a given direction
     *
     * @param direction2Destination     The direction that should be followed by the move
     *                                  (moves that do not diverge too much from this direction will have a low score)
     * @param method                    Distance method
     *                                  "MaxCoordinateDistance": the highest coordinate difference in absolute value
     *                                  "ManhattanDistance": the sum of the coordinates difference in absolute values
     */
    public void sort(Coordinate direction2Destination, String method) {
        // Map all moves to their scores via a scoring function
        List<Integer> scores = moves.stream().map(c -> this.score(c, direction2Destination, method)).toList();

        // Create a list of indexes of the moves
        List<Integer> indexes = new ArrayList<>();
        for (int i = 0; i < moves.size(); i++) {
            indexes.add(i);
        }

        // Sort the indexes in the crescent order of the scores of the moves they index
        indexes.sort((i1, i2) -> Float.compare(scores.get(i1), scores.get(i2)));

        // Sort the moves following this new order of indexes
        moves = indexes.stream().map(moves::get).collect(Collectors.toList());
    }

    /**
     * A method that scores the moves (here according to some measure of distance)
     * The lower the score, the more desirable is the move.
     *
     * @param move                      The move to score
     * @param direction2Destination     The direction that should be followed by the move
     *                                  (moves that do not diverge too much from this direction will have a low score)
     * @param method                    Distance method
     *                                  "MaxCoordinateDistance": the highest coordinate difference in absolute value
     *                                  "ManhattanDistance": the sum of the coordinates difference in absolute values
     *
     * @return                          The score of a given move
     */
    private int score(Coordinate move, Coordinate direction2Destination, String method){
        int distance = direction2Destination.distanceFrom(move, method);
        return distance;
    }

//    private int score(Coordinate move, Coordinate direction2Destination, AgentState agentState, String method){
//        String data = agentState.getMemoryFragment("Positions");
//        int recencyPenalty = 0;
//        if (data != null) {
//            List<Coordinate> previousPositions = Coordinate.string2Coordinates(data);
//            Coordinate newPosition = new Coordinate(agentState.getX() + move.getX(), agentState.getY() + move.getY());
//
//            for (int i = 0; i < previousPositions.size(); i++) {
//
//                if (previousPositions.get(i).equals(newPosition)) {
//                    recencyPenalty = 20 - i;
//                    break;
//                }
//            }
//        }
//
//        int distance = direction2Destination.distanceFrom(move, method);
//        return distance;
//    }
}

