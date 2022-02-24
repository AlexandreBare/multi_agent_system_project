package environment.law;

import environment.Environment;
import support.InfConveyPacket;

import support.Influence;

public class LawConveyPacket implements Law {

    private Environment env;

    /**
     * Initializes a new LawConveyPacket instance
     */
    public LawConveyPacket() {}

    /**
     * Check if this Law is applicable to the given Influence 'inf'
     *
     * @param inf The influence to check
     * @return    'true' if 'inf' is an instance of the InfConveyPacket class
     */
    public boolean applicable(Influence inf) {
        return inf instanceof InfConveyPacket;
    }

    /**
     * Check if the given Influence 'inf' passes validation by this Law.
     *
     * @param inf The influence to check
     * @return    'true' if there actually is a packet to convey and an empty conveyor to convey it to
     */
    public boolean apply(Influence inf) {
        InfConveyPacket conveyInf = (InfConveyPacket) inf;
        var conveyor = conveyInf.getConveyor();
        var packet = env.getPacketWorld().getItem(conveyor.getX(), conveyor.getY());
        var destPacket = env.getPacketWorld().getItem(inf.getX(), inf.getY());

        var destConveyor = env.getConveyorWorld().getItem(inf.getX(), inf.getY());

        return destConveyor != null
                && packet != null
                && destPacket == null
                && inf.getX() == conveyor.getDestinationX()
                && inf.getY() == conveyor.getDestinationY();
    }

    /**
     * Sets the environment of this LawConveyPacket
     * @param env The new environment value
     */
    public void setEnvironment(Environment env) {
        this.env = env;
    }

}