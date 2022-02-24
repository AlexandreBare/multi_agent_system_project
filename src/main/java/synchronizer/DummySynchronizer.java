package synchronizer;

import environment.ActiveItemID;

import java.util.List;

public class DummySynchronizer implements Synchronizer {

    public DummySynchronizer() {
    }

    public synchronized ActiveItemID[] getSyncSet(ActiveItemID agent) {
        return new ActiveItemID[0];
    }

    public void synchronize(ActiveItemID agent, List<ActiveItemID> setOfCandidates, int time) {
        // NO-OP;
    }
}
