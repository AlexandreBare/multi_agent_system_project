package environment.world.conveyor;

import environment.ActiveItemID;
import environment.ActiveItemImp;
import environment.Environment;
import support.ActionOutcome;
import support.InfConveyPacket;
import support.InfSkip;
import support.Outcome;

public class ConveyorImp extends ActiveItemImp {

    private final Conveyor conveyor;

    
    public ConveyorImp(ActiveItemID ID, Conveyor conveyor, Environment env) {
        super(ID);
        this.setEnvironment(env);
        this.conveyor = conveyor;
    }


    @Override
    protected void action() {
        if (getEnvironment().getPacketWorld().getItem(conveyor.getX(), conveyor.getY()) != null &&
                getEnvironment().getConveyorWorld().getItem(conveyor.getDestinationX(), conveyor.getDestinationY()) != null &&
                getEnvironment().getPacketWorld().getItem(conveyor.getDestinationX(), conveyor.getDestinationY()) == null) {
            InfConveyPacket inf = new InfConveyPacket(this.getEnvironment(), conveyor.getDestinationX(), conveyor.getDestinationY(), this.getActiveItemID(), conveyor);
            Outcome outcome = new ActionOutcome(getActiveItemID(), true, getSyncSet(), inf);
            concludePhaseWith(outcome);

        } else {
            concludePhaseWith(new ActionOutcome(getActiveItemID(), true, getSyncSet(), new InfSkip(this.getEnvironment(), this.getActiveItemID())));
        }
    }
}
