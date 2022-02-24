package environment.world.pheromone;

import environment.CellPerception;

/**
 * A class for representations of directed pheromones.
 */
public class DirPheromoneRep extends PheromoneRep {

    private CellPerception target;
    

    /**
     * Initializes a new DirPheromoneRep instance
     *
     * @param  x        X-coordinate of the DirPheromone this representation represents
     * @param  y        Y-coordinate of the DirPheromone this representation represents
     * @param  lifetime The strength of the DirPheromone this representation represents
     * @param  target   The target of the DirPheromone this representation represents
     */
    protected DirPheromoneRep(int x, int y, int lifetime, CellPerception target) {
        super(x, y, lifetime);
        setTarget(target);
    }

    public CellPerception getTarget() {
        return target;
    }

    protected void setTarget(CellPerception target) {
        this.target = target;
    }
}
