package environment.world.generator;

import java.util.Collection;

import com.google.common.eventbus.EventBus;

import environment.World;

/**
 * A class for a PacketGeneratorWorld, being a layer of the total world that contains
 * PacketGenerators.
 */
public class PacketGeneratorWorld extends World<PacketGenerator> {

    public PacketGeneratorWorld(EventBus eventBus) {
        super(eventBus);
    }

    @Override
    public void placeItems(Collection<PacketGenerator> items) {
        items.forEach(this::placeItem);
    }

    @Override
    public void placeItem(PacketGenerator item) {
        this.putItem(item);
        getEnvironment().addActiveItem(item);
    }
    

    public String toString() {
        return "PacketGeneratorWorld";
    }
}
