package environment.law;

import java.util.logging.Logger;

import environment.CollisionMatrix;
import environment.Environment;
import environment.world.agent.Agent;
import support.InfStep;
import support.Influence;

/**
 * A law controlling the movement of agents.
 */
public class LawStep implements Law {

    private Environment env;

    private final Logger logger = Logger.getLogger(LawStep.class.getName());

    /**
     * Initializes a new LawStep instance
     */
    public LawStep() {}

    /**
     * Check if this Law is applicable to the given Influence 'inf'
     *
     * @param inf The influence to check
     * @return    'true' if 'inf' is an instance of the InfStep class
     */
    public boolean applicable(Influence inf) {
        return inf instanceof InfStep;
    }

    /**
     * Check if the given Influence 'inf' passes validation by this Law.
     *
     * @param inf The influence to check
     * @return    'true' if the coordinates given in 'inf' hold a Free Item on each World ('env.getAllWorlds()')
     */
    public boolean apply(Influence inf) {
        Agent agent = env.getAgentWorld().getAgent(inf.getID());
        int dist = Environment.chebyshevDistance(agent.getX(), agent.getY(), inf.getX(), inf.getY());

        if (dist != 1) {
            this.logger.severe(String.format("Illegal action for this influence. No move allowed from (%d, %d) to (%d, %d).",
                agent.getX(), agent.getY(), inf.getX(), inf.getY()));
            return false;
        }

        return CollisionMatrix.agentCanStandOn(env, inf.getX(), inf.getY());
    }

    /**
     * Sets the environment of this LawStep
     * @param env The new environment value
     */
    public void setEnvironment(Environment env) {
        this.env = env;
    }
}
