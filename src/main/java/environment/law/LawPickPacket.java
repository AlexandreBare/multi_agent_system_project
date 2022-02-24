package environment.law;

import environment.Environment;
import support.InfPickPacket;
import support.Influence;

/**
 * A law controlling the picking up of Packets.
 */
public class LawPickPacket implements Law {

    private Environment env;

    /**
     * Initializes a new LawPickPacket instance
     */
    public LawPickPacket() {}

    /**
     * Check if this Law is applicable to the given Influence 'inf'
     *
     * @param inf The influence to check
     * @return    'true' if 'inf' is an instance of the InfPickPacket class
     */
    public boolean applicable(Influence inf) {
        return inf instanceof InfPickPacket;
    }

    /**
     * Check if the given Influence 'inf' passes validation by this Law.
     *
     * @param inf The influence to check
     * @return    'true' if there actually is a packet to pick up
     */
    public boolean apply(Influence inf) {
        var agent = inf.getEnvironment().getAgentWorld().getAgent(inf.getID());
        var agentColor = agent.getColor();

        var packet = env.getPacketWorld().getItem(inf.getX(), inf.getY());
        var distance = Environment.chebyshevDistance(inf.getX(), inf.getY(), agent.getX(), agent.getY());

        return packet != null
            && !agent.hasCarry() 
            && (agentColor.isEmpty() || agentColor.get() == packet.getColor())
            && distance == 1;
    }

    /**
     * Sets the environment of this LawPickPacket
     * @param env The new environment value
     */
    public void setEnvironment(Environment env) {
        this.env = env;
    }
}
