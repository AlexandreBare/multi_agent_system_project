package environment.world.energystation;

import java.util.Collection;
import java.util.Objects;

import com.google.common.eventbus.EventBus;

import environment.World;

/**
 * A class for an EnergyStationWorld, being a layer of the total world that contains
 * EnergyStations.
 */

public class EnergyStationWorld extends World<EnergyStation> {


    /**
     * Initializes a new EnergyStationWorld instance
     */
    public EnergyStationWorld(EventBus eventBus) {
        super(eventBus);
    }

    

    /**
     * Gets the total amount of EnergyStations that are in this EnergyStationWorld
     *
     * @return This EnergyStationWorld's number of EnergyStations
     */
    public int getNbEnergyStations() {
        return (int) this.items.stream()
                .flatMap(Collection::stream)
                .filter(Objects::nonNull)
                .count();
    }

    public String toString() {
        return "EnergyStationWorld";
    }


    /**
     * Adds EnergyStations to this EnergyStationWorld.
     *
     * @param energyStations The energyStations to place in this world
     */
    @Override
    public void placeItems(Collection<EnergyStation> energyStations) {
        energyStations.forEach(this::placeItem);
    }

    /**
     * Adds a EnergyStation to this EnergyStationWorld.
     *
     * @param energyStation The energyStation to place in this world
     */
    @Override
    public void placeItem(EnergyStation energyStation) {
        putItem(energyStation);
        getEnvironment().addActiveItem(energyStation);
    }
}
