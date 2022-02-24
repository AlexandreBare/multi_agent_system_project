package environment.law;

import environment.CollisionMatrix;
import environment.Environment;
import support.InfPutPacket;
import support.Influence;

/**
 * A law controlling the putting down of packets.
 */
public class LawPutPacket implements Law {

    private Environment env;

    /**
     * Initializes a new LawPutPacket instance
     */
    public LawPutPacket() {}

    /**
     * Check if this Law is applicable to the given Influence 'inf'
     *
     * @param inf The influence to check
     * @return    'true' if 'inf' is an instance of the InfPutPacket class
     */
    public boolean applicable(Influence inf) {
        return inf instanceof InfPutPacket;
    }

    /**
     * Check if the given Influence 'inf' passes validation by this Law.
     *
     * @param inf The influence to check
     * @return    'true' if the coordinates given in 'inf' hold a Free Item on
     *            each World ('env.getAllWorlds()') or if there is a Destination
     *            on the given coordinates
     */
    public boolean apply(Influence inf) {
        var agent = inf.getEnvironment().getAgentWorld().getAgent(inf.getID());
        return CollisionMatrix.packetCanStandOn(env, inf.getX(), inf.getY())
                && Environment.chebyshevDistance(inf.getX(), inf.getY(), agent.getX(), agent.getY()) <= 1;
    }

    /**
     * Sets the environment of this LawPutPacket
     * @param env The new environment value
     */
    public void setEnvironment(Environment env) {
        this.env = env;
    }
}
