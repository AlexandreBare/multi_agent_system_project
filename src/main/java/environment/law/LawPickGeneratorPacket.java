package environment.law;

import environment.Environment;
import support.InfPickGeneratorPacket;
import support.Influence;

/**
 * A law controlling the picking up of Packets.
 */
public class LawPickGeneratorPacket implements Law {

    private Environment env;

    /**
     * Initializes a new LawPickGeneratorPacket instance
     */
    public LawPickGeneratorPacket() {}

    /**
     * Check if this Law is applicable to the given Influence 'inf'
     *
     * @param inf The influence to check
     * @return    'true' if 'inf' is an instance of the InfPickGeneratorPacket class
     */
    public boolean applicable(Influence inf) {
        return inf instanceof InfPickGeneratorPacket;
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

        var packetGenerator = env.getPacketGeneratorWorld().getItem(inf.getX(), inf.getY());
        var distance = Environment.chebyshevDistance(inf.getX(), inf.getY(), agent.getX(), agent.getY());

        return packetGenerator != null
            && !agent.hasCarry() 
            && (agentColor.isEmpty() || agentColor.get() == packetGenerator.getColor())
            && packetGenerator.getAmtPacketsInBuffer() > 0 
            && distance == 1;
    }

    /**
     * Sets the environment of this LawPickGeneratorPacket
     * @param env The new environment value
     */
    public void setEnvironment(Environment env) {
        this.env = env;
    }
}
