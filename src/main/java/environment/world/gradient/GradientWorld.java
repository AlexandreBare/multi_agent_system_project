package environment.world.gradient;

import java.util.Collection;

import com.google.common.eventbus.EventBus;

import environment.World;

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
}
