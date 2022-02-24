package environment;

import environment.world.agent.AgentWorld;
import environment.world.conveyor.ConveyorWorld;
import environment.world.crumb.CrumbWorld;
import environment.world.destination.DestinationWorld;
import environment.world.energystation.EnergyStationWorld;
import environment.world.flag.FlagWorld;
import environment.world.generator.PacketGeneratorWorld;
import environment.world.gradient.Gradient;
import environment.world.packet.PacketWorld;
import environment.world.pheromone.Pheromone;

/**
 * A class that keeps track of which (kind of) Items can stand together on one
 * area and which cannot.
 */
public class CollisionMatrix {

    public static boolean agentCanStandOn(Environment env, int x, int y) {
        for (World<?> w : env.getWorlds()) {
            if (w.getItem(x, y) != null &&
                    !(w instanceof CrumbWorld) &&
                    !(w instanceof FlagWorld) &&
                    !(w.getItem(x, y) instanceof Pheromone) &&
                    !(w.getItem(x, y) instanceof Gradient)

            ) {
                return false;
            }
        }
        return true;
    }

    public static boolean areaValueCanStandOn(Environment env, int x, int y) {
        for (World<?> w : env.getWorlds()) {
            if (w.getItem(x, y) != null &&
                    !(w instanceof AgentWorld) &&
                    !(w instanceof CrumbWorld) &&
                    !(w instanceof FlagWorld) &&
                    !(w instanceof EnergyStationWorld) &&
                    !(w instanceof PacketWorld) &&
                    !(w instanceof PacketGeneratorWorld) &&
                    !(w.getItem(x, y) instanceof Pheromone)
            ) {
                return false;
            }
        }
        return true;
    }

    public static boolean crumbCanStandOn(Environment env, int x, int y) {
        for (World<?> w : env.getWorlds()) {
            if (w.getItem(x, y) != null &&
                    !(w instanceof AgentWorld) &&
                    !(w instanceof FlagWorld) &&
                    !(w.getItem(x, y) instanceof Pheromone) &&
                    !(w.getItem(x, y) instanceof Gradient)
            ) {
                return false;
            }
        }
        return true;
    }

    public static boolean destinationCanStandOn(Environment env, int x, int y) {
        for (World<?> w : env.getWorlds()) {
            if (w.getItem(x, y) != null &&
                    !(w.getItem(x, y) instanceof Gradient)
            ) {
                return false;
            }
        }
        return true;
    }

    public static boolean energyStationCanStandOn(Environment env, int x, int y) {
        return destinationCanStandOn(env, x, y);
    }

    public static boolean flagCanStandOn(Environment env, int x, int y) {
        for (World<?> w : env.getWorlds()) {
            if (w.getItem(x, y) != null &&
                    !(w instanceof AgentWorld) &&
                    !(w instanceof CrumbWorld) &&
                    !(w.getItem(x, y) instanceof Pheromone) &&
                    !(w.getItem(x, y) instanceof Gradient)
            ) {
                return false;
            }
        }
        return true;
    }

    public static boolean gradientCanStandOn(Environment env, int x, int y) {
        for (World<?> w : env.getWorlds()) {
            if (w.getItem(x, y) != null &&
                    !(w instanceof AgentWorld) &&
                    !(w instanceof CrumbWorld) &&
                    !(w instanceof FlagWorld) &&
                    !(w instanceof PacketWorld) &&
                    !(w instanceof PacketGeneratorWorld) &&
                    !(w.getItem(x, y) instanceof Pheromone)
            ) {
                return false;
            }
        }
        return true;
    }

    public static boolean packetCanStandOn(Environment env, int x, int y) {
        for (World<?> w : env.getWorlds()) {
            if (w.getItem(x, y) != null &&
                    !(w instanceof CrumbWorld) &&
                    !(w instanceof DestinationWorld) &&
                    !(w instanceof PacketGeneratorWorld) &&
                    !(w instanceof ConveyorWorld) &&
                    !(w.getItem(x, y) instanceof Pheromone) &&
                    !(w.getItem(x, y) instanceof Gradient)
            ) {
                return false;
            }
        }
        return true;
    }

    public static boolean pheromoneCanStandOn(Environment env, int x, int y) {
        for (World<?> w : env.getWorlds()) {
            if (w.getItem(x, y) != null &&
                    !(w instanceof AgentWorld) &&
                    !(w instanceof CrumbWorld) &&
                    !(w instanceof FlagWorld) &&
                    !(w instanceof PacketWorld) &&
                    !(w.getItem(x, y) instanceof Pheromone) &&
                    !(w.getItem(x, y) instanceof Gradient)
            ) {
                return false;
            }
        }
        return true;
    }


    public static boolean wallCanStandOn(Environment env, int x, int y) {
        for (World<?> w : env.getWorlds()) {
            if (w.getItem(x, y) != null) {
                return false;
            }
        }
        return true;
    }


    public static boolean glassWallCanStandOn(Environment env, int x, int y) {
        for (World<?> w : env.getWorlds()) {
            if (w.getItem(x, y) != null) {
                return false;
            }
        }
        return true;
    }


    public static boolean generatorCanStandOn(Environment env, int x, int y) {
        // Only packets can stand on the same square as the generator
        for (World<?> w : env.getWorlds()) {
            // If an item is present that is not in the PacketWorld --> collision
            if (w.getItem(x, y) != null && !(w instanceof PacketWorld)) {
                return false;
            }
        }
        return true;
    }

    
    public static boolean conveyorCanStandOn(Environment env, int x, int y) {
        // Only packets can stand on a conveyor belt
        for (World<?> w : env.getWorlds()) {
            // If an item is present that is not in the PacketWorld --> collision
            if (w.getItem(x, y) != null && !(w instanceof PacketWorld)) {
                return false;
            }
        }
        return true;
    }
}
