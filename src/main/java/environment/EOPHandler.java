package environment;

public class EOPHandler extends Handler<EOPSet> {

    public EOPHandler() {
        super();
    }

    protected void process(EOPSet toBeHandled) {
        // NO-OP: the environment does not react to tokens of agents that they have ended perception; this class is merely there
        // for elegance (parallelism in structure).
        toBeHandled.getSendingSphere().setHandled(toBeHandled.getNbCorrespondingOutcomes());
    }
}
