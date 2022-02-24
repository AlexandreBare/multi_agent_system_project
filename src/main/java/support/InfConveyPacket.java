package support;

import environment.ActiveItemID;
import environment.Environment;
import environment.world.conveyor.Conveyor;
import environment.world.packet.Packet;
import environment.world.packet.PacketWorld;
import util.event.AgentActionEvent;

public class InfConveyPacket extends Influence {
    
    private final Conveyor conveyor;

    
    public InfConveyPacket(Environment environment, int x, int y, ActiveItemID id, Conveyor conveyor) {
        super(environment, x, y, id, null);
        this.conveyor = conveyor;
    }

    @Override

    public PacketWorld getAreaOfEffect () {
        return getEnvironment().getPacketWorld();
    }


    @Override
    public AgentActionEvent effectuateEvent () {
        return conveyPacket(getX(), getY(), getConveyor());
    }

    public Conveyor getConveyor() {
        return conveyor;
    }

    /**
     * Make a given Agent in this AgentWorld put the packet it is carrying at the given
     * coordinates
     *
     * @param  tx       x coordinate
     * @param  ty       y coordinate
     * @param  conveyor    The agent that is putting a packet
     * @pre    The conveyor must contain a packet
     *
     * @post   The packet on 'conveyor' is positioned on coordinate tx,ty
     * @post   The putting down of the packet is shown on the gui
     * @return An agent action event which contains information about the conveyed packet.
     */
    protected synchronized AgentActionEvent conveyPacket(int tx, int ty, Conveyor conveyor) {
        Packet packet = getAreaOfEffect().getItem(conveyor.getX(), conveyor.getY());
        Packet oldTo = getAreaOfEffect().getItem(tx, ty);
        AgentActionEvent event = new AgentActionEvent(this);
        packet.moveTo(tx, ty); // make the packet consume these coordinates
        getAreaOfEffect().putItem(tx, ty, packet); // place it
        getAreaOfEffect().free(conveyor.getX(), conveyor.getY());
        event.setAction(AgentActionEvent.CONVEY_PACKET);

        event.setOldTo(oldTo);
        event.setTo(tx, ty);
        event.setPacket(packet);

        return event;
    }
}
