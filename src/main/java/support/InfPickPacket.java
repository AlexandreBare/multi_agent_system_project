package support;

import java.awt.Color;

import environment.ActiveItemID;
import environment.Environment;
import environment.world.packet.Packet;
import environment.world.packet.PacketWorld;
import util.event.AgentActionEvent;

/**
 * A class for influences for picking up packages.
 */
public class InfPickPacket extends Influence {

    /**
     * Initializes a new InfPickPacket object
     * Cfr. super
     */
    public InfPickPacket(Environment environment, int x, int y, ActiveItemID agent, Color color) {
        super(environment, x, y, agent, color);
    }

    /**
     * Gets the area of effect (the World it wants to effect) for this Influence
     * @return The PacketWorld
     */
    @Override
    public PacketWorld getAreaOfEffect() {
        return getEnvironment().getPacketWorld();
    }

    @Override
    public AgentActionEvent effectuateEvent() {
        return pickPacket(getX(), getY(), getID());
    }

    /**
     * Make a given Agent in this AgentWorld pick the packet at the given coordinates
     *
     * @param  fx     X coordinate of the packet that is to be picked up
     * @param  fy     Y coordinate of the packet that is to be picked up
     * @param  agent  The ID of the agent that is picking up a packet
     * @post          The position that held the picked up packet is free
     * @post          The agent holds the packet that was positioned on coordinate fx,fy
     * @post          The packet that is picked up is informed of its new coordinates,
     *                being the agent's coordinates
     * @post          The picking up of the packet is shown on the gui
     */
    protected synchronized AgentActionEvent pickPacket(int fx, int fy, ActiveItemID agent) {
        Packet from = getAreaOfEffect().getItem(fx, fy);
        getAreaOfEffect().getAgent(agent).consume(from);
        getAreaOfEffect().free(fx, fy);
        from.moveTo(getAreaOfEffect().getAgent(agent).getX(), getAreaOfEffect().getAgent(agent).getY());
        AgentActionEvent event = new AgentActionEvent(this);
        event.setAction(AgentActionEvent.PICK_PACKET);
        event.setFrom(fx, fy);
        event.setPacket(from);
        event.setAgent(getAreaOfEffect().getAgent(agent));
        return event;
    }

}
