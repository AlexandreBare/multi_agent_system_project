package environment.world.energystation;

import java.util.logging.Logger;

import environment.ActiveImp;
import environment.ActiveItem;
import environment.ActiveItemID;
import environment.ActiveItemID.ActionPriority;
import environment.Environment;
import gui.video.Drawer;
import synchronizer.Synchronization;

/**
 * A class for energyStations, items representing a station where agents can
 * charge energy in the EnergyStationsWorld.
 */

public class EnergyStation extends ActiveItem<EnergyStationRep> {
    

    private final Logger logger = Logger.getLogger(EnergyStation.class.getName());
    
    /**
     * Initializes a new EnergyStation instance.
     *
     * @param x   x-coordinate of the energyStation
     * @param y   y-coordinate of the energyStation
     * @param id  the ID of the energyStation
     */
    public EnergyStation(int x, int y, int id) {
        this(x, y, new ActiveItemID(id, ActionPriority.ENERGYSTATION));
    }


    /**
     * Initializes a new EnergyStation instance.
     *
     * @param x   x-coordinate of the energyStation
     * @param y   y-coordinate of the energyStation
     * @param ID  the ID of the energyStation
     */
    public EnergyStation(int x, int y, ActiveItemID ID) {
        super(x, y, RANGE, ID);
        this.logger.fine(String.format("Energy Station created at %d %d", x, y));
    }


    /**
     * Returns a representation of this energy station.
     *
     * @return The representation of this energy station
     */
    @Override
    public EnergyStationRep getRepresentation() {
        return new EnergyStationRep(getX(), getY());
    }

    /**
     * Draws this EnergyStation on the GUI.
     *
     * @param drawer The visiting drawer
     */
    public void draw(Drawer drawer) {
        drawer.drawEnergyStation(this);
    }


    
    public String generateEnvironmentString() {
        return String.format("%s\n", this.getClass().getName()) +
                String.format("nbArgs %d\n", 3) +
                String.format("Integer %d\n", this.getX()) +
                String.format("Integer %d\n", this.getY()) +
                String.format("Integer %d\n", this.getID().getID());
    }

    /**
     * The range in which this energyStation has an influence, i.e. can
     * load the battery of agents. This is also the region in which the
     * energyStation eventually has to synchronize with the active items or
     * agents present.
     * The energyStation can be detected from outside this range because of the
     * gradient implementation of the energyStation. The detection range is
     * equal to the power (see gradient) of the energyStation.
     */
    public static final int RANGE = 1;

    @Override
    public ActiveImp generateImplementation(Environment env, Synchronization synchronizer) {
        EnergyStationImp imp = new EnergyStationImp(this.getID(), this.getX(), this.getY());
        imp.setEnvironment(env);
        imp.setSynchronizer(synchronizer);
        return imp;
    }
}
