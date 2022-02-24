package support;

import java.awt.Color;

import environment.ActiveItemID;
import environment.Environment;
import environment.world.generator.PacketGeneratorWorld;
import environment.world.packet.Packet;
import util.event.AgentActionEvent;

/**
 * A class for influences for picking up packets from a Packet Generator.
 */
public class InfPickGeneratorPacket extends Influence {

    /**
     * Initializes a new InfPickGeneratorPacket object
     * Cfr. super
     */
    public InfPickGeneratorPacket(Environment environment, int x, int y, ActiveItemID agent, Color color) {
        super(environment, x, y, agent, color);
    }

    /**
     * Gets the area of effect (the World it wants to effect) for this Influence
     * @return The PacketGeneratorWorld
     */
    @Override
    public PacketGeneratorWorld getAreaOfEffect() {
        return getEnvironment().getPacketGeneratorWorld();
    }

    @Override
    public AgentActionEvent effectuateEvent() {
        Packet packet = getAreaOfEffect().getItem(getX(), getY()).getFirstAvailablePacket();
        if (packet != null) {
            getEnvironment().getPacketWorld().placeItem(packet);
        }
        AgentActionEvent event = new AgentActionEvent(this);
        event.setAction(AgentActionEvent.PICK_GENERATOR);
        return event;
    }

}
