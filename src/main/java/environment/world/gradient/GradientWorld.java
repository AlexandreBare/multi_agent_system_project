package environment.world.gradient;

import java.util.Collection;

import com.google.common.eventbus.EventBus;

import environment.ActiveItemID;
import environment.CellPerception;
import environment.Coordinate;
import environment.World;
import environment.world.pheromone.Pheromone;

public class GradientWorld extends World<Gradient> {

    /**
     * Initialize the GradientWorld
     */
    public GradientWorld(EventBus eventBus) {
        super(eventBus);
    }


    /**
     * Place a collection of gradients inside the Gradient World.
     *
     * @param gradients The collection of gradients.
     */
    @Override
    public void placeItems(Collection<Gradient> gradients) {
        gradients.forEach(this::placeItem);
    }

    /**
     * Place a single gradient in the Gradient World.
     *
     * @param item The gradient.
     */
    @Override
    public void placeItem(Gradient item) {
        putItem(item);
    }

    /**
     * Add gradients in the Gradient World starting from a specified location
     *
     * @param startLocation     The location of the initial gradient.
     */
    public void addGradientsWithStartLocation(Coordinate startLocation){
        addGradients(startLocation, 0);
    }

    /**
     * Add gradients recursively in the Gradient World by neighbouring location propagation
     * starting from a specified location and value
     *
     * @param location     The location of the current gradient.
     * @param value        The value of the current gradient.
     */
    public void addGradients(Coordinate location, int value){
        int x = location.getX();
        int y = location.getY();
        // get the potential gradient that could already exist at the current location
        Gradient currentGradient = getItem(x, y);
        if (currentGradient != null && currentGradient.getValue() <= value)
            // If there is already a gradient of lower value at the current location,
            // we don't need to replace it
            return;

        // Else, add a gradient with the current value at the current location
        currentGradient = new Gradient(location, value);
        placeItem(currentGradient);

        // For all neighbouring locations
        for (int i = x - 1; i <= x + 1; i++) {
            for (int j = y - 1; j <= y + 1; j++) {
                try{
                    if (getEnvironment().getDestinationWorld().getItem(i, j) == null
                            && getEnvironment().getWallWorld().getItem(i, j) == null){
                        //&& getEnvironment().getEnergyStationWorld().getItem(i, j) == null){
                        // if no destination or wall can be found at the current neighbouring location,
                        // add gradients starting from this new location with an incremented value
                        addGradients(new Coordinate(i, j), value + 1);
                    }
                }catch (IndexOutOfBoundsException e){
                    // catch exceptions in case we move out of the environment limits in terms of location
                    // we won't add a gradient there
                }
            }
        }
    }
}
