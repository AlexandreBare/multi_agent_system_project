package environment.world.pheromone;

import environment.Representation;

/**
 * A class for representations of pheromones.
 */
public class PheromoneRep extends Representation {

    private int lifetime;
    

    /**
     * Initializes a new PheromoneRep instance
     *
     * @param x         X-coordinate of the Pheromone this representation represents
     * @param y         Y-coordinate of the Pheromone this representation represents
     * @param lifetime  The lifetime of the Pheromone this representation represents
     */
    protected PheromoneRep(int x, int y, int lifetime) {
        super(x, y);
        setLifetime(lifetime);
    }

    public char getTypeChar() {
        return ('~');
    }

    public int getLifetime() {
        return lifetime;
    }

    protected void setLifetime(int lifetime) {
        this.lifetime = lifetime;
    }

    @Override
    public boolean isWalkable() {
        return true;
    }
}
