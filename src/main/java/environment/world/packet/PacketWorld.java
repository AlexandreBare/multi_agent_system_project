package environment.world.packet;

import java.util.Collection;
import java.util.logging.Logger;

import com.google.common.eventbus.EventBus;

import environment.ActiveItemID;
import environment.Coordinate;
import environment.World;
import environment.world.agent.Agent;
import util.MyColor;

/**
 * A class for a PacketWorld, being a layer of the total world, that contains Packets.
 */
public class PacketWorld extends World<Packet> {


    private int nbPackets;

    private final Logger logger = Logger.getLogger(PacketWorld.class.getName());

    
    /**
     * Initializes a new PacketWorld instance
     */
    public PacketWorld(EventBus eventBus) {
        super(eventBus);
    }

    /**
     * Gets the total amount of packets that are in this AgentWorld
     *
     * @return This AgentWorld's nbPackets
     */
    public int getNbPackets() {
        return nbPackets;
    }

    /**
     * Gets the agent with the given 'ID'
     *
     * @param ID  The number we gave to the agent we are looking for
     * @return    The agent with the given ID in this AgentWorld.
     *            Or, if there is no agent with that ID, we return null
     */
    public Agent getAgent(ActiveItemID ID) {
        return getEnvironment().getAgentWorld().getAgent(ID);
    }

    public String toString() {
        return "PacketWorld";
    }

    private void setNbPackets(int nbPackets) {
        this.nbPackets = nbPackets;
    }

    /**
     * Adds a number of Packets randomly to this PacketWorld.
     *
     * @param nbPacketKinds the number of different packet colors
     * @param nbPacketsPerKind the number of packets to place per color
     */
    public void createWorld(int nbPacketKinds, int nbPacketsPerKind) {
        for (int i = 0; i < nbPacketKinds; i++) {
            for (int j = 0; j < nbPacketsPerKind; j++) {
                boolean ok = false;
                while (!ok) {
                    Coordinate c = getRandomCoordinate(getEnvironment().getWidth(), getEnvironment().getHeight());

                    if (getEnvironment().isFreePos(c.getX(), c.getY())) {
                        placeItem(new Packet(c.getX(), c.getY(), MyColor.getColor(i)));
                        ok = true;
                    }
                }
            }
        }
        setNbPackets(nbPacketKinds * nbPacketsPerKind);
    }

    /**
     * Adds Packets to this PacketWorld.
     *
     * @param  packets  the array of Packets that should be placed
     */
    @Override
    public void placeItems(Collection<Packet> packets) {
        packets.forEach(this::placeItem);
    }

    /**
     * Adds a Packet to this PacketWorld.
     *
     * @param packet the packet to place in this world
     */
    @Override
    public void placeItem(Packet packet) {
        try {
            setNbPackets(getNbPackets() + 1);
            putItem(packet);
        } catch (ClassCastException exc) {
            this.logger.severe("Can only place an Packet in PacketWorld.");
        }
    }

    public void deliverPacket() {
        setNbPackets(getNbPackets() - 1);
    }

}
