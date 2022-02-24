package environment.world.energystation;

import environment.Representation;

/**
 * A class for representations of EnergyStations.
 */
public class EnergyStationRep extends Representation {

    /**
     * Initializes a new EnergyStationRep object
     *
     * @param x X-coordinate of the energyStation this representation represents
     * @param y Y-coordinate of the energyStation this representation represents
     */
    protected EnergyStationRep(int x, int y) {
        super(x, y);
    }

    @Override
    public char getTypeChar() {
        return ('E');
    }

    @Override
    public boolean isWalkable() {
        return false;
    }
}
