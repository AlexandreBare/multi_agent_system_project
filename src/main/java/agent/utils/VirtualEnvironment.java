package agent.utils;

import environment.CellPerception;
import environment.Coordinate;
import environment.world.wall.SolidWallRep;
import environment.world.wall.WallRep;

import java.util.*;

/**
 * A class to represent a fictive environment of cells. In the sense, that modifications to this environment
 * will not affect the real environment.
 */
public class VirtualEnvironment {
    Map<Coordinate, CellPerception> coordinates2Cells; // the cells for each coordinate of the environment
    MovementManager movementManager; // the movement manager that provides the moves an agent could make independently
                                     // of its environment (independently of the existence of walls
                                     // or other representations)

    /**
     * Initialize a new virtual environment
     *
     * @param cells             the set of cells of which the representations on it are known
     * @param movementManager   the movement manager that provides the moves an agent could make independently of
     *                          its environment (independently of the existence of walls or other representations
     */
    public VirtualEnvironment(Set<CellPerception> cells, MovementManager movementManager){

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
        List<VirtualState> nextStates = new ArrayList<>();
        // The coordinates of the agent in the current fictive state
        Coordinate currentCoordinates = state.getCurrentCell().getCoordinates();
        // For each move the agent could make
        for(var move: movementManager.getMoves()){
            // Compute the next coordinates, cell and state the agent will move to
            Coordinate nextCoordinates = currentCoordinates.add(move);
            CellPerception nextCell = this.getCell(nextCoordinates);
            if (nextCell != null) {
                VirtualState nextState = new VirtualState(state, nextCell);

                // If the cell is walkable or correspond to a terminal state
                if (nextCell.isWalkable() || nextState.isTerminal()) {
                    nextStates.add(nextState); // Add it to the list of next available fictive states
                }
            }
        }
        return nextStates;
    }
}