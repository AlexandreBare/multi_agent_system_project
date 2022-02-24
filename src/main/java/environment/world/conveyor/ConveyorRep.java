package environment.world.conveyor;

import environment.Representation;
import util.Direction;


/**
 * A class for representations of Conveyors.
 */
public class ConveyorRep extends Representation {

    private Direction direction;

    /**
     * Initializes a new DestinationRep instance
     *
     * @param  x      X-coordinate of the Conveyor this representation represents
     * @param  y      Y-coordinate of the Conveyor this representation represents
     * @param  aDirection Direction of the Conveyor this representation represents
     */
    protected ConveyorRep(int x, int y, Direction aDirection) {
        super(x, y);
        setDirection(aDirection);
    }

    /**
     * Gets the direction of the conveyor this ConveyorRep represents
     * @return This ConveyorRep's direction
     */
    public Direction getDirection() {
        return direction;
    }

    /**
     * Sets the direction of this ConveyorRep
     * @param aDirection The new direction value
     */
    protected void setDirection(Direction aDirection) {
        direction = aDirection;
    }

    public char getTypeChar() {
        return ('D');
    }

    @Override
    public boolean isWalkable() {
        return false;
    }
}
