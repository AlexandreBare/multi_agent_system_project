package agent.utils;

import environment.CellPerception;
import environment.Coordinate;

import java.util.*;

/**
 * A class to represent the state a fictive agent is in
 */
public class VirtualState {
    private CellPerception currentCell; // current cell the fictive agent is on
    private Set<CellPerception[]> destinationCells; // the destination cells the agent should go to,
                                                    // there may be more than one cell the agent has to go to on his way
    private List<Coordinate> path; // the path that the fictive agent has taken to arrive to the current cell coordinates

    /**
     * Initialize a new fictive agent state
     *
     * @param cell      the current cell of the state
     * @param destinationCells  the cells that the agent should go to
     *                          (at least he should go to one of these destination cells)
     */
    public VirtualState(CellPerception cell, Set<CellPerception[]> destinationCells){
        this.currentCell = cell;
        this.destinationCells = destinationCells;
        this.path = new ArrayList<>();
    }

    /**
     * Initialize a new fictive agent state as a subsequent state to a previous one
     *
     * @param previousState     the previous state the fictive agent was in
     * @param nextCell          the cell that the fictive agent will arrive at in this new fictive state
     */
    public VirtualState(VirtualState previousState, CellPerception nextCell){
        this.currentCell = nextCell;
        // The set of list of destination cells will be updated if the new virtual state has reached the first destination
        // in at least of one of the lists. This intermediate destination can be removed to focus on the remaining
        // destinations of the list.
        Set<CellPerception[]> oldDestinationCells = previousState.getDestinationCells();
        this.destinationCells = new HashSet<>();
        for(CellPerception[] oldDestinationCellList: oldDestinationCells){ // we loop over all lists of destinations in the set
            CellPerception[] newDestinationCellList;
            if(oldDestinationCellList[0].equals(currentCell)){ // if the first destination of the current list has been reached
                // we copy only the last part of this list because as of now the first destination has been reached. It
                // is therefore not necessary to come back to it anymore.
                newDestinationCellList = Arrays.copyOfRange(oldDestinationCellList, 1, oldDestinationCellList.length-1);
            }else {
                // otherwise, we keep the current list as it was
                newDestinationCellList = oldDestinationCellList;
            }
            this.destinationCells.add(newDestinationCellList); // we update the set of list of destinations
        }

        // the path of the new state is updated from the one of the previous state
        // with the next cell coordinates of this new state
        this.path = new ArrayList<>(previousState.getPath());
        this.path.add(this.currentCell.getCoordinates());
    }

    /**
     * Whether the current state is a terminal state
     * (i.e. the fictive agent has reached the last cell of one of the lists of destination cells.
     *  This happens when the list has been reduced to only one cell and the state of this cell has been reached)
     */
    public boolean isTerminal(){
        boolean isTerminal = false;
        for (CellPerception[] destinationCellList: destinationCells){
            isTerminal = isTerminal ||
                    (destinationCellList.length == 1
                            && currentCell.getCoordinates().equals(destinationCellList[0].getCoordinates()));
        }
        return isTerminal;
    }

    public int getPathLength(){
        return path.size();
    }

    public List<Coordinate> getPath(){
        return path;
    }

    public CellPerception getCurrentCell(){
        return currentCell;
    }

    public Set<CellPerception[]> getDestinationCells(){
        return destinationCells;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VirtualState that = (VirtualState) o;
        return currentCell.equals(that.currentCell) && destinationCells.equals(that.destinationCells);
    }

    @Override
    public int hashCode() {
        return Objects.hash(currentCell, destinationCells);
    }
}
