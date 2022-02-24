package support;

import java.awt.Color;

import environment.ActiveItemID;
import environment.Environment;
import environment.world.generator.PacketGeneratorWorld;
import util.event.AgentActionEvent;

public class InfPopGeneratorPacket extends Influence {

    public InfPopGeneratorPacket(Environment environment, int x, int y, ActiveItemID id, Color c) {
        super(environment, x, y, id, c);
    }

    @Override
    public PacketGeneratorWorld getAreaOfEffect() {
        return this.getEnvironment().getPacketGeneratorWorld();
    }

    @Override
    public AgentActionEvent effectuateEvent() {
        if (this.getEnvironment().getPacketWorld().getItem(this.getX(), this.getY()) == null) {
            this.getEnvironment().getPacketWorld()
                    .placeItem(getAreaOfEffect().getItem(this.getX(), this.getY()).getFirstAvailablePacket());
        }
        return null;
    }
}
