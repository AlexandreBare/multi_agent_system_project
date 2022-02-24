package environment.law;

import environment.Environment;
import environment.world.packet.Packet;
import support.InfStealPacket;
import support.Influence;

/**
 * A law controlling the passing of packets.
 */
public class LawStealPacket implements Law {

    private Environment env;

    /**
     * Initializes a new LawStealPacket instance
     */
    public LawStealPacket() {}

    /**
     * Check if this Law is applicable to the given Influence 'inf'
     *
     * @param inf The influence to check
     * @return    'true' if 'inf' is an instance of the InfStealPacket class
     */
    public boolean applicable(Influence inf) {
        return inf instanceof InfStealPacket;
    }

    /**
     * Check if the given Influence 'inf' passes validation by this Law.
     *
     * @param inf The influence to check
     * @return    'true' if the coordinates given in 'inf' hold an Agent that
     *            has a carry and the current agent has no carry
     */
    public boolean apply(Influence inf) {
        var agent = env.getAgentWorld().getAgent(inf.getID());
        if (agent == null) {
            return false;
        }
        var agentColor = agent.getColor();

        var other = env.getAgentWorld().getItem(inf.getX(), inf.getY());
        if (other == null) {
            return false;
        }
        Packet packetToSteal = other.getCarry().orElse(null);
        var distance = Environment.chebyshevDistance(inf.getX(), inf.getY(), agent.getX(), agent.getY());

        return packetToSteal != null
                && !agent.hasCarry() 
                && (agentColor.isEmpty() || agentColor.get() == packetToSteal.getColor())
                && distance == 1;
    }

    /**
     * Sets the environment of this LawPassPacket
     *
     * @param env The new environment value
     */
    public void setEnvironment(Environment env) {
        this.env = env;
    }

}
