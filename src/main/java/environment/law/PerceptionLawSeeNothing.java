package environment.law;

import environment.Perception;
import environment.world.agent.AgentRep;

/**
 * A PerceptionLaw that doesn't allow an agent to perceive 'any'
 * specific information. When this law is applied agents will
 * perceive every Item as a Representation, which has no informative
 * value for them, except that there is 'something'.
 */
public class PerceptionLawSeeNothing implements PerceptionLaw {

    /**
     * Initializes a new PerceptionLawSeeNothing instance
     */
    public PerceptionLawSeeNothing() {}

    /**
     * Enforces this PerceptionLaw on a given Perception 'perception'.
     * All CellPerceptions in 'perception' will be emptied, thus removing
     * any specific information about items (representations) on them.
     *
     * @param perception The perception on which we will enforce this perception law
     * @return     A perception containing only unspecified 'Representations'.
     *             The only information that will remain in these representations
     *             are the coordinates of the Items they represent, nothing further.
     */
    public Perception enforce(Perception perception) {
        Perception newPerception = new Perception(perception.getWidth(), perception.getHeight(),
                                            perception.getOffsetX(), perception.getOffsetY());
        int aX = perception.getSelfX();
        int aY = perception.getSelfY();

        newPerception.getCellAt(aX, aY).addRep(perception.getCellAt(aX, aY).getRepOfType(AgentRep.class));
        newPerception.setSelfX(aX);
        newPerception.setSelfY(aY);
        return newPerception;
    }
}
