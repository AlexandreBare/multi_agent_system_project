package environment;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.annotation.Nullable;

import environment.world.agent.AgentRep;
import environment.world.crumb.CrumbRep;
import environment.world.destination.DestinationRep;
import environment.world.energystation.EnergyStationRep;
import environment.world.flag.FlagRep;
import environment.world.gradient.GradientRep;
import environment.world.packet.PacketRep;
import environment.world.pheromone.PheromoneRep;
import environment.world.wall.WallRep;

/**
 * A class of representations of positions in a Perception.
 * A CellPerception is part of a Perception and has a list of all
 * Representations of items on that coordinate.
 */

public class CellPerception {
    
    /**
     * The x and y coordinate (wrt. the environment) of this cell.
     */
    private final int x;
    private final int y;

    /**
     * The list of Representations of Items on (x, y)
     */
    private final List<Representation> reps;



    /**
     * Constructor.
     *
     * @param x The (absolute) x-coordinate of this CellPerception
     * @param y The (absolute) y-coordinate of this CellPerception
     */
    public CellPerception(int x, int y) {
        this.x = x;
        this.y = y;
        this.reps = new ArrayList<>();
    }

    /**
     * Adds a Representation to this CellPerception.
     *
     * @param rep The Representation to be added.
     */
    public void addRep(Representation rep) {
        this.reps.add(rep);
    }

    public void clear() {
        this.reps.clear();
    }

    /**
     * Returns the Representation of a given type in this CellPerception.
     * If no such Representation is found, null is returned.
     *
     * @param clazz The class one wants the Representation of
     */
    @SuppressWarnings("unchecked")
    @Nullable
    public <T extends Representation> T getRepOfType(Class<T> clazz) {
        for (Representation representation : this.reps) {
            if (clazz.isInstance(representation)) {
                return (T) representation;
            }
        }
        return null;
    }


    /**
     * Check if this cell perception has a packet in it.
     * @return True if a packet is present in this cell perception, false otherwise.
     */
    public boolean containsPacket() {
        return this.getRepOfType(PacketRep.class) != null;
    }

    /**
     * Check if this cell perception has a wall in it.
     * @return True if a wall is present in this cell perception, false otherwise.
     */
    public boolean containsWall() {
        return this.getRepOfType(WallRep.class) != null;
    }

    /**
     * Check if a destination of any color is present in this cell perception.
     * @return True is a destination is present, false otherwise.
     */
    public boolean containsAnyDestination() {
        return this.getRepOfType(DestinationRep.class) != null;
    }

    /**
     * Check if an agent is present in this cell perception.
     * @return True is an agent is present, false otherwise.
     */
    public boolean containsAgent() {
        return this.getRepOfType(AgentRep.class) != null;
    }

    /**
     * Retrieve the Agent in this cell perception (if present).
     * @return The Agent representation if present, Optional.empty() otherwise.
     */
    public Optional<AgentRep> getAgentRepresentation() {
        return Optional.ofNullable(this.getRepOfType(AgentRep.class));
    }
    /**
     * Check if a destination with the specified color is present in this cell perception.
     * @param color The color to check.
     * @return True if a destination of the given color is present, false otherwise.
     */
    public boolean containsDestination(Color color) {
        var destination = this.getRepOfType(DestinationRep.class);
        return destination != null && destination.getColor().equals(color);
    }

    /**
     * Check if this cell perception contains an energy station.
     * @return True if an energy station representation is present, false otherwise.
     */
    public boolean containsEnergyStation() {
        return this.getRepOfType(EnergyStationRep.class) != null;
    }

    /**
     * Check if this cell perception contains a gradient.
     * @return True if a gradient is present, false otherwise.
     */
    public boolean containsGradient() {
        return this.getGradientRepresentation().isPresent();
    }

    /**
     * Retrieve the Gradient in this cell perception (if present).
     * @return The Gradient representation if present, Optional.empty() otherwise.
     */
    public Optional<GradientRep> getGradientRepresentation() {
        return Optional.ofNullable(this.getRepOfType(GradientRep.class));
    }

    /**
     * Check if this cell perception contains a flag.
     * @return True if a flag is present, false otherwise.
     */
    public boolean containsFlag() {
        return this.getFlagRepresentation().isPresent();
    }

    /**
     * Check if this cell perception contains a flag.
     * @return True if a flag is present, false otherwise.
     */
    public boolean containsFlagWithColor(Color color) {
        return this.getFlagRepresentation()
            .map(f -> f.getColor() == color)
            .orElse(false);
    }


    /**
     * Retrieve the flag in this cell perception (if present).
     * @return The flag representation if present, Optional.empty() otherwise.
     */
    public Optional<FlagRep> getFlagRepresentation() {
        return Optional.ofNullable(this.getRepOfType(FlagRep.class));
    }

    /**
     * Check if this cell perception contains a pheromone.
     * @return True if a pheromone is present, false otherwise.
     */
    public boolean containsPheromone() {
        return this.getPheromoneRepresentation().isPresent();
    }

    /**
     * Retrieve the pheromone in this cell perception (if present).
     * @return The pheromone representation if present, Optional.empty() otherwise.
     */
    public Optional<PheromoneRep> getPheromoneRepresentation() {
        return Optional.ofNullable(this.getRepOfType(PheromoneRep.class));
    }


    /**
     * Check if this cell perception contains a crumb.
     * @return True if a crumb is present, false otherwise.
     */
    public boolean containsCrumb() {
        return this.getCrumbRepresentation().isPresent();
    }

    /**
     * Retrieve the crumb in this cell perception (if present).
     * @return The crumb representation if present, Optional.empty() otherwise.
     */
    public Optional<CrumbRep> getCrumbRepresentation() {
        return Optional.ofNullable(this.getRepOfType(CrumbRep.class));
    }


    /**
     * Returns the number of Representations on this CellPerception.
     */
    public int getNbReps() {
        return this.reps.size();
    }

    /**
     * Checks whether there are any Representations on this CellPerception.
     */
    public boolean isFree() {
        return this.reps.size() == 0;
    }

    public boolean isWalkable() {
        return this.reps.stream().allMatch(Representation::isWalkable);
    }


    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }
}
