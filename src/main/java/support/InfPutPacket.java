package support;

import environment.ActiveItemID;
import environment.Environment;
import environment.world.packet.Packet;
import environment.world.packet.PacketWorld;
import util.event.AgentActionEvent;

/**
 * A class for influences for putting down packets.
 */
public class InfPutPacket extends Influence {

    /**
     * Initializes a new InfPutPacket object
     * Cfr. super
     */
    public InfPutPacket(Environment environment, int x, int y, ActiveItemID agent) {
        super(environment, x, y, agent, null);
    }

    /**
     * Gets the area of effect (the World it wants to effect) for this Influence
     *
     * @return The PacketWorld
     */
    @Override
    public PacketWorld getAreaOfEffect() {
        return getEnvironment().getPacketWorld();
    }

    @Override
    public AgentActionEvent effectuateEvent() {
        return putPacket(getX(), getY(), getID());
    }

    /**
     * Make a given Agent in this AgentWorld put the packet it is carrying at the given
     * coordinates
     *
     * @param  tx       x coordinate
     * @param  ty       y coordinate
     * @param  agent    The agent that is putting a packet
     * @pre    The agent must be carrying a packet
     *          agent.carry() != null
     * @post   The packet 'agent' was carrying is positioned on coordinate tx,ty
     * @post   The ID of the agent no longer carries a packet
     * @post   The putting down of the packet is shown on the gui
     * @return An agent action event containing information about the packet being put, or null if the packet could not be put.
     */
    protected synchronized AgentActionEvent putPacket(int tx, int ty, ActiveItemID agent) {
        Packet packet = getAreaOfEffect().getAgent(agent).getCarry().orElse(null);
        if (packet == null) {
            return null;
        }
        Packet oldTo =  getAreaOfEffect().getItem(tx, ty);
        AgentActionEvent event = new AgentActionEvent(this);

        if (getEnvironment().getDestinationWorld().getItem(tx, ty) != null) {
            getEnvironment().getPacketWorld().deliverPacket(); // there's one packet less in the world
            event.setAction(AgentActionEvent.DELIVER_PACKET);
        } else {
            packet.moveTo(tx, ty); // make the packet consume these coordinates
            getAreaOfEffect().putItem(tx, ty, packet); // place it
            event.setAction(AgentActionEvent.PUT_PACKET);
        }
        getAreaOfEffect().getAgent(agent).consume(null); //not holding a packet anymore

        event.setOldTo(oldTo);
        event.setTo(tx, ty);
        event.setPacket(null);
        event.setAgent(getAreaOfEffect().getAgent(agent));
        return event;

    }

}
