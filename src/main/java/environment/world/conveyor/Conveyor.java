package environment.world.conveyor;

import java.util.logging.Logger;

import environment.ActiveImp;
import environment.ActiveItem;
import environment.ActiveItemID;
import environment.Environment;
import environment.world.destination.Destination;
import gui.video.Drawer;
import synchronizer.Synchronization;
import util.Direction;

public class Conveyor extends ActiveItem<ConveyorRep> {

    private final Direction direction;

    private final Logger logger = Logger.getLogger(Destination.class.getName());

    /**
     * Initializes a new Destination instance
     *
     * @param  x    x-coordinate of the Destination
     * @param  y    y-coordinate of the Destination
     * @param  dir  the Conveyor's direction
     */
    public Conveyor(int x, int y, int view, ActiveItemID ID, int dir) {
        super(x, y, view, ID);
        this.direction = Direction.valueOfById(dir);
        this.logger.fine(String.format("Conveyor created at %d %d", x, y));
    }

    /**
     * Initializes a new Destination instance
     *
     * @param  x    x-coordinate of the Destination
     * @param  y    y-coordinate of the Destination
     * @param  dir  the Conveyor's direction
     */
    public Conveyor(int x, int y, ActiveItemID ID, int dir) {
        this(x, y, 0, ID , dir);
    }

    /**
     * Initializes a new Destination instance
     *
     * @param  x    x-coordinate of the Destination
     * @param  y    y-coordinate of the Destination
     * @param  dir  the Conveyor's direction
     */
    public Conveyor(int x, int y, int id, int dir) {
        this(x, y, 0, new ActiveItemID(id, ActiveItemID.ActionPriority.CONVEYOR) , dir);
    }


    /**
     * Gets the direction of this Conveyor
     *
     * @return    This Conveyor's direction
     */
    public Direction getDirection() {
        return direction;
    }

    /**
     * Returns a representation of this Destination
     *
     * @return  A Representation of this Destination
     */
    @Override
    public ConveyorRep getRepresentation() {
        return new ConveyorRep(getX(), getY(), getDirection());
    }

    /**
     * Draws this Destination on the GUI.
     *
     * @param drawer  The visiting drawer
     */
    public void draw(Drawer drawer) {
        drawer.drawConveyor(this);
    }


    public String generateEnvironmentString() {
        return String.format("%s\n", this.getClass().getName()) +
                String.format("nbArgs %d\n", 4) +
                String.format("Integer %d\n", this.getX()) +
                String.format("Integer %d\n", this.getY()) +
                String.format("Integer %d\n", this.getID().getID()) +
                String.format("Integer %d\n", this.getDirection().getId());
    }

    public int getDestinationX() {
        return getX() + getDirection().getXChange();
    }
    public int getDestinationY() {
        return getY() + getDirection().getYChange();
    }

    @Override
    public ActiveImp generateImplementation(Environment env, Synchronization synchronizer) {
        ConveyorImp imp = new ConveyorImp(this.getID(),  this, env);
        imp.setEnvironment(env);
        imp.setSynchronizer(synchronizer);
        return imp;
    }
}
