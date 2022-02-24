package support;

import java.util.logging.Logger;

import environment.ActiveItemID;
import environment.Environment;
import environment.world.agent.Agent;
import environment.world.packet.Packet;
import environment.world.packet.PacketWorld;
import util.event.AgentActionEvent;

/**
 * A class for influences for passing packet from one agent to another.
 */
public class InfStealPacket extends Influence {

    private final Logger logger = Logger.getLogger(InfStealPacket.class.getName());
    
    /**
     * Initializes a new InfPickPacket object
     * Cfr. super
     */
    public InfStealPacket(Environment environment, int agentStolenFromXCoordinate, int agentStolenFromYCoordinate, ActiveItemID stealingAgent) {
        super(environment, agentStolenFromXCoordinate, agentStolenFromYCoordinate, stealingAgent, null);
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
        return stealPacket(getX(), getY(), getID());
    }

    protected synchronized AgentActionEvent stealPacket(int tx, int ty, ActiveItemID agentID) {
        Agent srcAgent = getEnvironment().getAgentWorld().getItem(tx, ty);
        Agent dstAgent = getAreaOfEffect().getAgent(agentID);
        if (srcAgent == null) {
            this.logger.severe("Source agent of passing is null!");
            return null;
        }
        if (dstAgent == null) {
            this.logger.severe("Destination agent of passing is null!");
            return null;
        }
        Packet packet = srcAgent.getCarry().orElse(null);
        if (packet == null) {
            return null;
        }

        dstAgent.consume(packet);
        srcAgent.consume(null);
        packet.moveTo(tx, ty);
        
        AgentActionEvent event = new AgentActionEvent(this);
        event.setAction(AgentActionEvent.STEAL_PACKET);
        event.setFrom(tx, ty);
        event.setPacket(packet);
        event.setAgent(dstAgent);
        return event;
    }

}
