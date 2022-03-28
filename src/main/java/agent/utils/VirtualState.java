package agent.utils;

import environment.CellPerception;
import environment.Coordinate;

import java.util.*;

/**
 * A class to represent the state a fictive agent is in
 */
public class VirtualState {
    private CellPerception currentCell; // current cell the fictive agent is on
    private Set<CellPerception> destinationCells; // the destination cells the agent should go to
    private List<Coordinate> path; // the path that the fictive agent has taken to arrive to the current cell coordinates

    /**
     * Initialize a new fictive agent state
     *
     * @param cell      the current cell of the state
     * @param destinationCells  the cells that the agent should go to
     *                          (at least he should go to one of these destination cells)
     */
    public VirtualState(CellPerception cell, Set<CellPerception> destinationCells){
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
        this.destinationCells = previousState.getDestinationCells();

        // the path of the new state is updated from the one of the previous state
        // with the next cell coordinates of this new state
        this.path = new ArrayList<>(previousState.getPath());
        this.path.add(this.currentCell.getCoordinates());
    }

    /**
     * Whether the current state is a terminal state (i.e. the fictive agent has reached one of the destination cells)
     */
    public boolean isTerminal(){
        boolean isTerminal = false;
        for (CellPerception destinationCell: destinationCells){
            isTerminal = isTerminal || (currentCell.getCoordinates().equals(destinationCell.getCoordinates()));
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

    public Set<CellPerception> getDestinationCells(){
        return destinationCells;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VirtualState that = (VirtualState) o;
        return Objects.equals(currentCell, that.currentCell) && Objects.equals(destinationCells, that.destinationCells);
    }

    @Override
    public int hashCode() {
        return Objects.hash(currentCell, destinationCells);
    }
}
