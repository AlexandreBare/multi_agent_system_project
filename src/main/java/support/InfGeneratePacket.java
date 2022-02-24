package support;

import java.awt.Color;

import environment.ActiveItemID;
import environment.Environment;
import environment.world.generator.PacketGeneratorWorld;
import util.event.AgentActionEvent;

public class InfGeneratePacket extends Influence {

    public InfGeneratePacket(Environment environment, int x, int y, ActiveItemID id, Color c) {
        super(environment, x, y, id, c);
    }

    @Override
    public PacketGeneratorWorld getAreaOfEffect() {
        return getEnvironment().getPacketGeneratorWorld();
    }

    @Override
    public AgentActionEvent effectuateEvent() {
        getAreaOfEffect().getItem(getX(), getY()).generatePacket();
        if (getEnvironment().getPacketWorld().getItem(getX(), getY()) == null) {
            getEnvironment().getPacketWorld().placeItem(getAreaOfEffect().getItem(getX(), getY()).getFirstAvailablePacket());
        }
        AgentActionEvent event = new AgentActionEvent(this);
        event.setAction(AgentActionEvent.GENERATE_PACKET);
        return event;
    }

}
