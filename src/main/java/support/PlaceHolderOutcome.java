package support;

import environment.ActiveItemID;

/**
 * A class for placeholders for outcomes
 */
public class PlaceHolderOutcome extends Outcome {

    public PlaceHolderOutcome(ActiveItemID agent) {
        super(agent, false, null);
        setCorrespondingHandler("");
    }

    public String getType() {
        return "placeholder";
    }
}
