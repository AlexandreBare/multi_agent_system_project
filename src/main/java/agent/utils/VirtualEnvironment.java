package agent.utils;

import environment.CellPerception;
import environment.Coordinate;

import java.util.*;

/**
 * A class to represent a fictive environment of cells. In the sense, that modifications to this environment
 * will not affect the real environment.
 */
public class VirtualEnvironment {
    private Map<Coordinate, CellPerception> coordinates2Cells; // the cells for each coordinate of the environment
    private MovementManager movementManager; // the movement manager that provides the moves an agent could
                                             // make independently of its environment (independently of the existence
                                             // of walls or other representations)

    /**
     * Initialize a new virtual environment
     *
     * @param cells             the set of cells of which the representations on it are known
     * @param movementManager   the movement manager that provides the moves an agent could make independently of
     *                          its environment (independently of the existence of walls or other representations)
     */
    public VirtualEnvironment(Set<CellPerception> cells, MovementManager movementManager){
        // Create a mapping between each coordinates of the environment and its corresponding CellPerception
        coordinates2Cells = new HashMap<>();
        for (CellPerception cell: cells) {
            coordinates2Cells.put(cell.getCoordinates(), cell);
        }
        this.movementManager = movementManager;
    }


    /**
     * Get the cell at given coordinates
     */
    public CellPerception getCell(Coordinate coordinates){
        CellPerception cell = coordinates2Cells.get(coordinates);
//        if(cell==null) { // unperceivable and unknown cells (i.e. null cells) are considered to be solid walls
//            System.out.println(coordinates);
//            cell = new CellPerception(coordinates);
//            cell.addRep(new SolidWallRep(coordinates));
//        }
        return cell;
    }

    /**
     * Get the next available fictive state where an agent could go to
     *
     * @param state     the current fictive state
     */
    public List<VirtualState> getNextStates(VirtualState state){
//        System.out.println("Current state: " + state.getCurrentCell().getCoordinates() + " -- Next States:");
        List<VirtualState> nextStates = new ArrayList<>();
        // The current agent cell is the one of the current state except if it is a non walkable intermediate/final
        // destination cell (i.e. a cell with a packet or a destination). In this case, the agent can not walk
        // on the cell and remains on the cell of the previous state
        VirtualState currentState = state;
        if (state.isCurrentDestinationNonWalkable()){
            currentState = state.getPreviousState();
        }
        // The coordinates of the agent in the current fictive state
        Coordinate currentCoordinates = currentState.getCurrentCell().getCoordinates();

        // For each move the agent could make
        for(var move: movementManager.getMoves()){
            // Compute the next coordinates, cell and state the agent will move to
            Coordinate nextCoordinates = currentCoordinates.add(move);
            CellPerception nextCell = this.getCell(nextCoordinates);
            if (nextCell != null) {
                VirtualState nextState = new VirtualState(state, nextCell);

                // If the cell is walkable or is an intermediate/final destination
                if (nextCell.isWalkable() || nextState.isCurrentDestination()) {
//                    System.out.println("Next State: " + nextState.getCurrentCell().getCoordinates());
                    nextStates.add(nextState); // Add it to the list of next available states
                }
            }
        }
        return nextStates;
    }
}
