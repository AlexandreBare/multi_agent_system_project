package environment;

/**
 * A generic class for facilitating communication between Spheres and Handlers. A ToHandle keeps e reference to the sending sphere,
 * together with the number of outcomes that are dealt with when the ToHandle has been handled.
 */
public abstract class ToHandle {

    private final Sphere sender;
    private int handledNbOutcomes;

    /**
     * Initialize a new ToHandle with <sphere> as the sender of this ToHandle. At initialization, a ToHandle instance corresponds
     * to zero outcomes in <sphere>.
     */
    public ToHandle(Sphere sphere) {
        sender = sphere;
        handledNbOutcomes = 0;
    }

    /**
     * Return the sphere sending this ToHandle.
     */
    public Sphere getSendingSphere() {
        return sender;
    }

    /**
     * Increase the number of outcomes in getSendingSphere() the handling of which this ToHandle is responsible for.
     */
    public void incrNbCorrespondingOutcomes() {
        handledNbOutcomes++;
    }

    /**
     * Return the number of outcomes in getSendingSphere() that are dealt with when this ToHandle has been handled.
     */
    public int getNbCorrespondingOutcomes() {
        return handledNbOutcomes;
    }

    /**
     * Check whether this ToHandle corresponds to zero outcomes in getSendingSphere().
     */
    public boolean isEmpty() {
        return handledNbOutcomes == 0;
    }

}
