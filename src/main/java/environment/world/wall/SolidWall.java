package environment.world.wall;

import java.util.logging.Logger;

import gui.video.Drawer;

/**
 * A class for walls, being simple Items with a certain position.
 */
public class SolidWall extends Wall {

    private final Logger logger = Logger.getLogger(SolidWall.class.getName());
    
    /**
     * Initializes a new Wall instance
     *
     * @param x  X-coordinate of the Wall
     * @param y  Y-coordinate of the Wall
     */
    public SolidWall(int x, int y) {
        super(x, y);
        this.logger.fine(String.format("Wall created at %d %d", x, y));
    }

    /**
     * Returns a representation of this Wall
     *
     * @return A wall-representation
     */
    @Override
    public SolidWallRep getRepresentation() {
        return new SolidWallRep(getX(), getY());
    }

    /**
     * Draws this Packet on the GUI.
     *
     * @param drawer The visiting drawer
     */
    public void draw(Drawer drawer) {
        drawer.drawSolidWall(this);
    }


    public String generateEnvironmentString() {
        return String.format("%s\n", this.getClass().getName()) +
                String.format("nbArgs %d\n", 2) +
                String.format("Integer %d\n", this.getX()) +
                String.format("Integer %d\n", this.getY());
    }
}
