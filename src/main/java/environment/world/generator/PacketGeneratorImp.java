package environment.world.generator;

import environment.ActiveItemID;
import environment.ActiveItemImp;
import environment.Environment;
import support.ActionOutcome;
import support.InfGeneratePacket;
import support.InfPopGeneratorPacket;
import support.InfSkip;
import support.Outcome;

public class PacketGeneratorImp extends ActiveItemImp {
    
    private final PacketGenerator packetGenerator;
    

    public PacketGeneratorImp(ActiveItemID ID, PacketGenerator packetGenerator, Environment env) {
        super(ID);
        this.setEnvironment(env);
        this.packetGenerator = packetGenerator;
    }

    @Override
    protected void action() {
        if (this.packetGenerator.shouldGenerate()) {
            InfGeneratePacket inf = new InfGeneratePacket(this.getEnvironment(), packetGenerator.getX(), packetGenerator.getY(), 
                    this.getActiveItemID(), packetGenerator.getColor());
            Outcome outcome = new ActionOutcome(this.getActiveItemID(), true, this.getSyncSet(), inf);
            concludePhaseWith(outcome);
        } else if (this.packetGenerator.getAmtPacketsInBuffer() > 0) {
            InfPopGeneratorPacket inf = new InfPopGeneratorPacket(this.getEnvironment(), packetGenerator.getX(), 
                    packetGenerator.getY(), this.getActiveItemID(), packetGenerator.getColor());
            Outcome outcome = new ActionOutcome(this.getActiveItemID(), true, this.getSyncSet(), inf);
            concludePhaseWith(outcome);
        } else {
            concludePhaseWith(new ActionOutcome(getActiveItemID(), true, getSyncSet(), 
                new InfSkip(this.getEnvironment(), this.getActiveItemID())));
        }
    }
}
