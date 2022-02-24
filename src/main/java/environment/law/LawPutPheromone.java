package environment.law;

import environment.CollisionMatrix;
import environment.Environment;
import support.InfPutDirPheromone;
import support.InfPutPheromone;
import support.Influence;

/**
 * A law controlling the putting down of Pheromones.
 */
public class LawPutPheromone implements Law {

    private Environment env;

    /**
     * Initializes a new LawPutPheromone instance
     */
    public LawPutPheromone() {}

    /**
     * Check if this Law is applicable to the given Influence 'inf'
     *
     * @param inf The influence to check
     * @return    'true' if 'inf' is an instance of the InfPutPheromone class
     */
    public boolean applicable(Influence inf) {
        return inf instanceof InfPutPheromone || inf instanceof InfPutDirPheromone;
    }

    /**
     * Check if the given Influence 'inf' passes validation by this Law.
     *
     * @param inf The influence to check
     * @return    'true' if the coordinate given in 'inf' can contain a Pheromone
     */
    public boolean apply(Influence inf) {
        var agent = inf.getEnvironment().getAgentWorld().getAgent(inf.getID());
        return CollisionMatrix.pheromoneCanStandOn(env, inf.getX(), inf.getY())
                && Environment.chebyshevDistance(inf.getX(), inf.getY(), agent.getX(), agent.getY()) <= 1;
    }

    /**
     * Sets the environment of this LawPutPheromone
     * @param env The new environment value
     */
    public void setEnvironment(Environment env) {
        this.env = env;
    }
}
