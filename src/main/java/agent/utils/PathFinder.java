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
     * @param state     the current fictive state
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
        CellPerception currentCell = state.getCurrentCell();
        Set<CellPerception[]> destinationCells = state.getDestinationCells();
        List<Integer> distances = new ArrayList<>();
        // Compute the distances between the different possible destinations
        for(CellPerception[] destinationCellList: destinationCells){
            int dist = 0;
            CellPerception startingCell = currentCell;
            for(CellPerception destinationCell: destinationCellList) {
                dist += startingCell.getCoordinates().distanceFrom(destinationCell.getCoordinates());
                startingCell = destinationCell;
            }
//            dist -= destinationCellList.length - (destinationCellList.length - 1);
            distances.add(dist);
        }
        // return the smallest cumulated distance to go through the list of destinations
        return Collections.min(distances);
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
     * from a single source cell to many possible destination cells with potentially many destination cells to go to
     * before reaching the terminal cell
     *
     * @param startingCell          the starting cell
     * @param destinationCells      the possible set of lists of destination cells
     *
     * @return      the list of coordinates pairs composing the optimal path to the closest destination
     */
    public List<List<Coordinate>> astar(CellPerception startingCell, Set<CellPerception[]> destinationCells){
        // Starting virtual state
        VirtualState state = new VirtualState(startingCell, destinationCells);
        // the set of already visited cells (avoid cycles when browsing the cells)
//        Set<CellPerception> closed = new HashSet<>();
        Set<VirtualState> closed = new HashSet<>();
        // a priority queue of the next states to first browse
        PriorityQueue<VirtualState> nextStates = new PriorityQueue<>(10, new Comparator<>() {
            @Override
            public int compare(VirtualState state1, VirtualState state2) {
                return PathFinder.priorityFunction(state1) - PathFinder.priorityFunction(state2);
            }
        });

        nextStates.add(state); // Add first the initial state to the queue of next states to browse

        while(true){
            if (nextStates.isEmpty()) { // If no more next states are available for the agent,
//                for (CellPerception[] d: destinationCells){
//                    System.out.println("\nDestination Tuple:");
//                    for(CellPerception cell: d){
//                        System.out.print(cell.getCoordinates());
//                    }
//                }
                return new ArrayList<>(); // no path was found
            }

            state = nextStates.poll(); // Retrieve and remove the first state in the priority queue
//            System.out.println(priorityFunction(state));
            if (state.isTerminal()) // If the agent is in a terminal state
                return state.getPaths(); // return the optimal path found

//            closed.add(state.getCurrentCell()); // Add the current cell to the set of already visited cells
            closed.add(state);

            // For every next legal state available
            for(VirtualState nextState: virtualEnvironment.getNextStates(state)){
                // if the cell the agent arrives to was not already visited
                if (!closed.contains(nextState)){ //(!closed.contains(nextState.getCurrentCell())){
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
                    // (This part of the implementation is not exact if the score function is not monotonically
                    // increasing, we should then implement the version in comments above
                    // but in our case we do not have to)
                    if(!nextStates.contains(nextState))
//                        System.out.println("Next State added to priority queue: " + nextState.getCurrentCell().getCoordinates());
                        nextStates.add(nextState); // we add it to the queue of next states to browse
                }
            }
        }
    }
}
