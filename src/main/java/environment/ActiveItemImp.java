package environment;

import support.CommunicationOutcome;
import support.PerceptionOutcome;

/**
 * This class represents the implementation of an active object in the MAS.
 * It is an object other than an agent that has an influence in the
 * Environment. It interacts with the Environment for new information of the
 * world by running a separate thread.
 */
abstract public class ActiveItemImp extends ActiveImp {
    public ActiveItemImp(ActiveItemID ID) {
        super(ID);
    }

    protected void cleanup() {}

    /**
     * Implements the execution of a synchronization phase.
     */
    protected void execCurrentPhase() {
        if (perceiving) {
            perceive();
            PerceptionOutcome outcome = new PerceptionOutcome(getActiveItemID(), true, getSyncSet());
            concludePhaseWith(outcome);

            nbTurn++;
            setSyncTime(getEnvironment().getTime());
        } else if (talking) {
            CommunicationOutcome outcome = new CommunicationOutcome(getActiveItemID(), true,
                getSyncSet(), "EOC", new MailBuffer());

            concludePhaseWith(outcome);
        } else if (doing) {
            action();
        }
    }

    protected boolean environmentPermissionNeededForNextPhase() {
        return true;
    }
}
