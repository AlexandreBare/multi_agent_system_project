package support;

import environment.ActiveItemID;
import environment.MailBuffer;

public class CommunicationOutcome extends Outcome {

    private final String token;
    private final MailBuffer mailBuffer;

    public CommunicationOutcome(ActiveItemID agent, boolean acted, ActiveItemID[] syncSet,
                                String token, MailBuffer buffer) {
        super(agent, acted, syncSet);
        this.token = token;
        this.mailBuffer = buffer;
        setCorrespondingHandler("PostalService");
    }

    public String getToken() {
        return token;
    }

    public MailBuffer getMailBuffer() {
        return mailBuffer;
    }

    public boolean getVoteForContinuingWithNextPhase() {
        return !getToken().equals("CC");
    }

    public String getType() {
        return "communication";
    }
}
