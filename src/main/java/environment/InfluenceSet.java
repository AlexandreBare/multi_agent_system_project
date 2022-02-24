package environment;

import support.Influence;

/**
 * A class for facilitating communication between the Spheres and the Reactor.
 */
public class InfluenceSet extends ToHandle {

    private Influence[] set;

    public InfluenceSet(Sphere sender) {
        super(sender);
        set = new Influence[0];
    }

    public Influence[] getInfluenceSet() {
        return set;
    }

    public void addInfluenceToSet(Influence influence) {
        Influence[] temp = new Influence[set.length + 1];
        System.arraycopy(set, 0, temp, 0, set.length);
        temp[set.length] = influence;
        set = temp;
    }
}
