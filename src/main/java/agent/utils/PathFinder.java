package agent.utils;

import environment.CellPerception;
import environment.Coordinate;

import java.util.*;

/**
 * A class to find optimal paths in a virtual environment (implements A*)
 */
public class PathFinder {
    VirtualEnvironment virtualEnvironment; // the fictive environment in which we will compute the optimal path


    /**
     * Initialize a new solver that finds optimal paths in a virtual environment
     *
     * @param virtualEnvironment    the fictive environment in which we will compute the optimal path
     */
    public PathFinder(VirtualEnvironment virtualEnvironment) {
        this.virtualEnvironment = virtualEnvironment;
    }

    /**
     * The cost function of A*
     *
     * @param state    the current fictive state
     *
     * @return  the current path length
     */
    public static int costFunction(VirtualState state){
        return state.getPathLength();
    }

    /**
     * The heuristic function of A*
     *
     * @param state    the current fictive state
     *
     * @return  the Chebyshev distance to the closest destination (admissible heuristic -> ensures optimality)
     */
    public static int heuristic(VirtualState state){
        CellPerception cell = state.getCurrentCell();
        Set<CellPerception> destinationCells = state.getDestinationCells();
        List<Integer> distances = new ArrayList<>();
        // Compute the distances between the different possible destinations
        for(CellPerception destinationCell: destinationCells){
            distances.add(cell.getCoordinates().distanceFrom(destinationCell.getCoordinates()));
        }
        return Collections.min(distances); // return the distance to the closest destination
    }

    /**
     * The priority function of A*
     *
     * @param state    the current fictive state
     *
     * @return  the priority score of each state
     */
    public static int priorityFunction(VirtualState state){
        return PathFinder.costFunction(state) + PathFinder.heuristic(state);
    }

    /**
     * Run the A* algorithm to find the shortest path
     * from a single source cell to many possible destination cells
     *
     * @param startingCell          the starting cell
     * @param destinationCells      the possible destination cells
     *
     * @return      the list of coordinates pairs composing the optimal path to the closest destination
     */
    public List<Coordinate> astar(CellPerception startingCell, Set<CellPerception> destinationCells){
        // Starting virtual state
        VirtualState state = new VirtualState(startingCell, destinationCells);
        // the set of already visited cells (avoid cycles when browsing the cells)
        Set<CellPerception> closed = new HashSet<>();
        // a priority queue of the next states to first browse
        PriorityQueue<VirtualState> nextStates = new PriorityQueue<>(10, new Comparator<>() {
            @Override
            public int compare(VirtualState state1, VirtualState state2) {
                return PathFinder.priorityFunction(state1) - PathFinder.priorityFunction(state2);
            }
        });

        nextStates.add(state); // Add first the initial state to the queue of next states to browse

        while(true){
            if (nextStates.isEmpty()) // If no more next states are available for the agent,
                return new ArrayList<>(); // no path was found

            state = nextStates.poll(); // Retrieve and remove the first state in the priority queue

            if (state.isTerminal()) // If the agent is in a terminal state
                return state.getPath(); // return the optimal path found

            closed.add(state.getCurrentCell()); // Add the current cell to the set of already visited cells

            // For every next legal state available
            for(VirtualState nextState: virtualEnvironment.getNextStates(state)){
                // if the cell the agent arrives to was not already visited
                if (!closed.contains(nextState.getCurrentCell())){
//                    TO DO: implement the correct version here under
//                    // if the next state is already in the list of next states
//                    if(nextStates.contains(nextState)) {
//                        // we should replace it by the new one only if its score in the priority queue is smaller,
//                        // i.e. if the length of the current path to get to this state is smaller
//                        oldState =
//                        if(nextStates.comparator(nextState, oldState) < 0) {
//                            nextStates.remove(oldState);
//                            nextState.add(nextState);
//                        }
//                    }

                    // if the state is not already in the priority queue
                    // (This part of the implementation is not exact, we should implement the version in comments above)
                    if(!nextStates.contains(nextState))
                        nextStates.add(nextState); // we add it to the queue of next states to browse
                }
            }
        }
    }
}
