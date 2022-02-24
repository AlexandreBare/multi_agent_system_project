package synchronizer;

import java.util.List;

import environment.ActiveItemID;
import environment.MailBox;

@SuppressWarnings({"FieldCanBeLocal", "unused"})
public class PersonalSynchronizer implements Synchronizer {

    private final int myId;
    private final MailBox inbox;
    private SyncElement[] syncSet;

    private boolean ini = false;
    private boolean reqR = false;
    private boolean reqS = false;
    private boolean ackS = false;
    private boolean ackR = false;
    private boolean comR = false;
    private boolean comS = false;
    private boolean sync = false;

    PersonalSynchronizer(int id) {
        myId = id;
        inbox = new MailBox();
    }

    public ActiveItemID[] getSyncSet(ActiveItemID id) {
        return new ActiveItemID[0];
    }

    public void synchronize(ActiveItemID agent_id, List<ActiveItemID> setOfCandidates, int time) {}

    private String getState() {
        if (syncSet.length == 0) {
            return "sync";
        }
        String state = "";
        ini = false;
        reqR = false;
        reqS = false;
        ackR = false;
        ackS = false;
        comR = false;
        comS = false;
        sync = false;
        for (SyncElement syncElement : syncSet) {
            switch (syncElement.getState()) {
                case "ini" -> ini = true;
                case "reqR" -> reqR = true;
                case "reqS" -> reqS = true;
                case "ackR" -> ackR = true;
                case "ackS" -> ackS = true;
                case "comR" -> comR = true;
                case "comS" -> comS = true;
                case "sync" -> sync = true;
            }
        }
        if (ini) {
            state = "ini";
        } else if (reqR) {  // !ini
            state = "reqR";
        } else if (reqS) {  // !ini & !reqR
            state = "reqS";
        } else if (ackR) {  //!ini & !reqR & !reqS
            state = "ackR";
        } else if (ackS) {  // !ini & !reqR & !reqS & !ackR
            state = "ackS";
        } else if (comR) {  // !ini & !reqR & !reqS & !ackR & !ackS
            state = "comR";
        } else if (comS) {  // !ini & !reqR & !reqS & !ackR & !ackS & !comR
            state = "comS";
        } else if (sync) {  // !ini & !reqR & !reqS & !ackR & !ackS & !comR & !comS
            state = "sync";
        }
        return state;
    }
}
