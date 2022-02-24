package synchronizer;

import environment.ActiveItemID;

import java.util.List;

/**
 * A common interface for both central and personal synchronizers.
 *  Note: this interface isn't really needed in the new structure and can be dispensed with in a later stage.
 */
public interface Synchronizer {

    ActiveItemID[] getSyncSet(ActiveItemID id);

    void synchronize(ActiveItemID agent_id, List<ActiveItemID> setOfCandidates, int time);

}
