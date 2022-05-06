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
    private MovementManager movementManager;
    private List<List<Coordinate>> paths; // the paths that the fictive agent has taken to arrive to the
                                          // current cell coordinates
    private VirtualState previousState; // the state before the current one
//    private List<VirtualState> previousStates; // the list of previous states before arriving to the current one

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
        this.movementManager = new MovementManager();
        this.paths = new ArrayList<List<Coordinate>>();
        this.paths.add(new ArrayList<Coordinate>());
        //this.paths.get(this.paths.size()-1).add(this.currentCell.getCoordinates());
        this.previousState = null;
//        this.previousStates = new ArrayList<>();

//        System.out.println("Destination Cells__: ");
//        Iterator<CellPerception[]> iterator = this.destinationCells.iterator();
//        while(iterator.hasNext()) {
//            for (CellPerception destinationCell : iterator.next()) {
//                System.out.print(destinationCell.getCoordinates());
//            }
//            System.out.print("\n");
//        }
    }

    /**
     * Initialize a new fictive agent state as a subsequent state to a previous one
     *
     * @param previousState     the previous state the fictive agent was in
     * @param nextCell          the cell that the fictive agent will arrive at in this new fictive state
     */
    public VirtualState(VirtualState previousState, CellPerception nextCell){
        this.currentCell = nextCell;
        this.movementManager = new MovementManager();
        this.previousState = previousState;
        //System.out.println(previousState.getPaths().size());
        // the path of the new state is retrieved from the one of the previous state
        this.paths = new ArrayList<>();
        for(List<Coordinate> path: previousState.getPaths()) {
            this.paths.add(new ArrayList<>(path));
        }
        //this.paths = new ArrayList<>(previousState.getPaths());
//        System.out.println(this.paths.size());
//        System.out.println(this.paths.get(0).size());

        // The set of lists of destination cells will be updated if the new virtual state has reached the first destination
        // in at least of one of the lists. This intermediate destination can be removed to focus on the remaining
        // destinations of the list.
        Set<CellPerception[]> oldDestinationCells = previousState.getDestinationCells();
        this.destinationCells = new HashSet<>();
        Set<CellPerception[]> newDestinationCells = new HashSet<>();
        int minDestinationCellListSize = Integer.MAX_VALUE;
        for(CellPerception[] oldDestinationCellList: oldDestinationCells){ // we loop over all lists of destinations in the set
            CellPerception[] newDestinationCellList = new CellPerception[]{};
            // if the first destination of the current list has been reached at the previous state
            if(previousState.isCurrentDestination(oldDestinationCellList[0])){
                if(oldDestinationCellList.length > 1){ // if the list contains other destinations
                    // we copy only the last part of this list because as of now the first destination has been reached. It
                    // is therefore not necessary to come back to it anymore.
                    newDestinationCellList = Arrays.copyOfRange(oldDestinationCellList, 1, oldDestinationCellList.length);
                }
            }else {
                // otherwise, we keep the current list as it was
                newDestinationCellList = oldDestinationCellList;
//                // We only add to the current path the current cell if it is not a destination cell because
//                // in practice, the agent will not need to move exactly to a destination cell
//                // (he does not need to walk on the cell where he picked a packet and can not move onto a cell where
//                // there is a destination)
//                this.path.add(this.currentCell.getCoordinates());
            }
//            if(oldDestinationCellList[0].equals(currentCell) && oldDestinationCellList.length > 1){
//                // we copy only the last part of this list because as of now the first destination has been reached. It
//                // is therefore not necessary to come back to it anymore.
//                newDestinationCellList = Arrays.copyOfRange(oldDestinationCellList, 1, oldDestinationCellList.length-1);
//            }else {
//                // otherwise, we keep the current list as it was
//                newDestinationCellList = oldDestinationCellList;
////                // We only add to the current path the current cell if it is not a destination cell because
////                // in practice, the agent will not need to move exactly to a destination cell
////                // (he does not need to walk on the cell where he picked a packet and can not move onto a cell where
////                // there is a destination)
////                this.path.add(this.currentCell.getCoordinates());
//            }
//            this.destinationCells.add(newDestinationCellList); // we update the set of list of destinations
            minDestinationCellListSize = Math.min(minDestinationCellListSize, newDestinationCellList.length);
            newDestinationCells.add(newDestinationCellList); // save each new list of destinations
        }
        // for each updated destination cell list
        for (CellPerception[] newDestinationCellList: newDestinationCells){
            // if its length is not equal to the smallest destination cell list length, we do not save it in the
            // destination cells as it means that the current path of the current state does not aim to go there. It has
            // already decided to start on another list of destination cells
            if (newDestinationCellList.length == minDestinationCellListSize) {
                this.destinationCells.add(newDestinationCellList);
            }
        }
//        System.out.println("Destination Cells: ");
//        Iterator<CellPerception[]> iterator = this.destinationCells.iterator();
//        while(iterator.hasNext()) {
//            for (CellPerception destinationCell : iterator.next()) {
//                System.out.print(destinationCell.getCoordinates());
//            }
//            System.out.print("\n");
//        }
        // If the state corresponds to a non-walkable destination, we now need to build a new path to the next
        // destination. So we add a new sublist for that.
        if(isCurrentDestinationNonWalkable()){
//            System.out.println("NonWalkableDestination");
            this.paths.add(new ArrayList<Coordinate>());
            //this.paths.get(this.paths.size()-1).add(this.currentCell.getCoordinates());
        }else {
            // Otherwise, we simply append this new state coordinates to the current path
            this.paths.get(this.paths.size() - 1).add(this.currentCell.getCoordinates());
        }

//        System.out.println(this.paths);
//        System.out.println("Destination Cells: ");
//        Iterator<CellPerception[]> iterator = this.destinationCells.iterator();
//        while(iterator.hasNext()) {
//            for (CellPerception destinationCell : iterator.next()) {
//                System.out.print(destinationCell.getCoordinates());
//            }
//            System.out.print("\n");
//        }

//        // the path of the new state is retrieved from the one of the previous state
//        this.path = new ArrayList<>(previousState.getPath());
//        this.path.add(this.currentCell.getCoordinates());

//        this.previousStates = new ArrayList<>(previousState.getPreviousStates());
//        this.previousStates.add(previousState);
    }

    /**
     * Whether the current state is a terminal state
     * (i.e. the fictive agent has reached the last cell of one of the lists of destination cells.
     *  This happens when the list has been reduced to the point where there are no destination cells left to reach)
     */
    public boolean isTerminal(){
        for (CellPerception[] destinationCellList: destinationCells){ // For each possible list of destinations
            //isTerminal = isTerminal || (destinationCellList.length == 0); // no destination left to go to
            // If there is only the final destination left in the list and we have reached it
            if (destinationCellList.length == 1 && isCurrentDestination(destinationCellList[0])){
//                System.out.println("Terminal: " + destinationCellList[0].getCoordinates());
                return true;
            }
        }
        return false;
    }

    /**
     * Whether the current state cell is the first destination of one the lists of destinations.
     * (i.e. the fictive agent has reached the first cell of one of the lists of destination cells)
     * It does not necessarily correspond to a terminal state.
     */
    public boolean isCurrentDestination(){
//        boolean isCurrentDestination = false;
        for (CellPerception[] destinationCellList: destinationCells) {
            if (isCurrentDestination(destinationCellList[0])){
                return true;
            }
        }
        return false;
    }

    /**
     * Whether we have reached a destination cell.
     * It does not necessarily correspond to a terminal state.
     */
    public boolean isCurrentDestination(CellPerception destinationCell){
//        Coordinate currentCoordinates = currentCell.getCoordinates();
//        Coordinate currentDestinationCoordinates = destinationCell.getCoordinates();
//        Coordinate neighboringCoordinates;
//        for (Coordinate move: movementManager.getMoves()){
//            neighboringCoordinates = currentDestinationCoordinates.add(move);
//            if (currentCoordinates.equals(neighboringCoordinates)){
//                return true;
//            }
//        }
//        return false;
        return currentCell.getCoordinates().equals(destinationCell.getCoordinates());
//            isCurrentDestination = isCurrentDestination ||
//                    currentCell.getCoordinates().equals(destinationCellList[0].getCoordinates());
//        return false;//isCurrentDestination;
    }

    public boolean isCurrentDestinationNonWalkable(){
        return isCurrentDestination() && !currentCell.isWalkable();
    }

    public int getPathLength(){
        int pathLength = 0;
        for(List<Coordinate> path: this.paths){
            pathLength += path.size();
        }
        return pathLength;
        //return path.size();
    }

    public List<List<Coordinate>> getPaths(){
        return paths;
    }

    public VirtualState getPreviousState(){
        return previousState;//previousStates.get(previousStates.size()-1);
    }

//    public List<VirtualState> getPreviousStates(){
//        return previousStates;
//    }

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
