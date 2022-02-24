package environment.world.energystation;

import environment.ActiveItemID;
import environment.ActiveItemImp;
import support.ActionOutcome;
import support.InfEnergy;
import support.Influence;

/**
 * This class represents the active "behavior" of an EnergyStation.
 * It sends every turn an EnergyInfluence to the Environment.
 */
public class EnergyStationImp extends ActiveItemImp {
    public EnergyStationImp(ActiveItemID id, int x, int y) {
        super(id);
        this.x = x;
        this.y = y;
    }

    protected void action() {
        Influence influence = new InfEnergy(getEnvironment(), getX(), getY() - 1, getActiveItemID(), LOAD);
        ActionOutcome outcome = new ActionOutcome(getActiveItemID(), true, getSyncSet(), influence);
        concludePhaseWith(outcome);
    }

    protected int getX() {
        return x;
    }

    protected int getY() {
        return y;
    }

    private final int x;
    private final int y;

    /**
     * The energy that is transferred per cycle to an agent.
     */
    public static final int LOAD = 100;

}
