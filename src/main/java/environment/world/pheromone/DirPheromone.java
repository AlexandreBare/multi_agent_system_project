package environment.world.pheromone;

import environment.CellPerception;
import environment.Environment;
import gui.video.Drawer;

/**
 * A class for pheromones with a direction.
 */
public class DirPheromone extends Pheromone {

    /**
     * The AreaPerception this DirPheromone points at.
     */
    private CellPerception target;
    

    /**
     * Initializes a new DirPheromone instance with a specified lifetime.
     *
     * @param environ  the environment the new DirPheromone is situated in
     * @param x        x-coordinate of the new DirPheromone
     * @param y        y-coordinate of the new DirPheromone
     * @param time     the initial strength of the new DirPheromone
     * @param target   the area this DirPheromone points at
     * @pre   time > 0
     */
    public DirPheromone(Environment environ, int x, int y, int time, CellPerception target) {
        super(environ, x, y, time);
        this.target = target;
    }

    /**
     * Initializes a new DirPheromone instance with a default lifetime.
     *
     * @param  environ  the environment this Pheromone is situated in
     * @param  x    x-coordinate of the Pheromone
     * @param  y    y-coordinate of the Pheromone
     * @param target   the area this DirPheromone points at
     */
    public DirPheromone(Environment environ, int x, int y, CellPerception target) {
        this(environ, x, y, DEFAULT_LIFETIME, target);
    }

    /**
     * Draws this Pheromone on the GUI.
     *
     * @param drawer The visiting drawer
     */
    public void draw(Drawer drawer) {
        drawer.drawDirPheromone(this);
    }

    /**
     * Returns a representation of this Pheromone.
     *
     * @return A Pheromone-representation
     */
    @Override
    public DirPheromoneRep getRepresentation() {
        return new DirPheromoneRep(getX(), getY(), getLifetime(), getTarget());
    }

    /**
     * Returns the CellPerception this DirPheromone points at.
     *
     * @return a CellPerception of the target area
     */
    public CellPerception getTarget() {
        return target;
    }

    public void setTarget(CellPerception target) {
        this.target = target;
    }
}
