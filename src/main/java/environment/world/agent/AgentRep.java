package environment.world.agent;

import java.awt.Color;
import java.util.Optional;

import environment.ActiveItemID;
import environment.Representation;
import environment.world.packet.PacketRep;

/**
 * A class for representations of Agents.
 */
public class AgentRep extends Representation {

    private final ActiveItemID id;
    private final String name;
    private PacketRep carry;
    private final Color color;

    /**
     * Initializes a new AgentRep object
     * @param  x      X-coordinate of the agent this representation represents
     * @param  y      Y-coordinate of the agent this representation represents
     * @param  aID    the ID of the agent this representation represents
     * @param  aName  the name of the agent this representation represents
     * @param  color  the color of the agent this representation represents
     */
    protected AgentRep(int x, int y, ActiveItemID aID, String aName, Color color) {
        super(x, y);
        this.id = aID;
        this.name = aName;
        this.color = color;
    }

    /**
     * Gets the ID of the Agent this AgentRep represents
     * @return This AgentRep's ID
     */
    public ActiveItemID getId() {
        return id;
    }

    /**
     * Gets the name of the Agent this AgentRep represents
     * @return This AgentRep's name
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the representation of the Packet that the Agent this AgentRep represents is carrying
     * @return This AgentRep's carry
     */
    public PacketRep getCarry() {
        return carry;
    }

    /**
     * Returns 'true' if the agent this AgentRep represents is carrying a Packet
     */
    public boolean carriesPacket() {
        return getCarry() != null;
    }


    /**
     * Sets the Packet this AgentRep is carrying
     * @param aCarry The new carry value
     */
    protected void setCarry(PacketRep aCarry) {
        carry = aCarry;
    }

    public char getTypeChar() {
        return ('A');
    }

    @Override
    public boolean isWalkable() {
        return false;
    }

    public Optional<Color> getColor() {
        return Optional.ofNullable(this.color);
    }
}
