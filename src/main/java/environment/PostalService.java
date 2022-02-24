package environment;

import java.util.logging.Logger;

import com.google.common.eventbus.EventBus;

import util.event.MsgSentEvent;

/**
 * A class for handling communicationOutcomes in a completed sphere. This class is given an own thread of execution. A postal-
 * Service is woken up by an incoming bag of mails and is responsible for instant delivery of these mails into the personal
 * mailboxes of the addressees.After this has been done, a postalService notifies the sphere that delivered the set.</p>
 */
public class PostalService extends Handler<MailBag> {

    private final ActiveItemContainer agentImplementations;
    private final EventBus eventBus;

    private final Logger logger = Logger.getLogger(PostalService.class.getName());

    /**
     * Initialize a new PostalService. At initialization, a postalService controls an empty set of MailBags and holds a reference
     * to the interface of the agentImplementations-package.
     * @param agentImps A reference to the interface of the agentImplementations-package.
     */
    public PostalService(ActiveItemContainer agentImps, EventBus eventBus) {
        super();
        agentImplementations = agentImps;
        this.eventBus = eventBus;
    }

    protected void process(MailBag toBeHandled) {
        Mail[] mailSet = toBeHandled.getMailSet();
        boolean[] turns = new boolean[mailSet.length];
        for (int i = 0; i < mailSet.length; i++) {
            Mail toDeliver = mailSet[nextActive(turns)];
            String from = toDeliver.getFrom();
            String to = toDeliver.getTo();
            try {
                getAgentImplementations().sendMessage(
                    getAgentImplementations().getAgentID(to),
                    toDeliver);
            } catch (IllegalArgumentException e) {
                this.logger.severe(e.getMessage());
            }
            this.logger.fine(String.format("Mail from %s to %s has been delivered.", from, to));
            MsgSentEvent se = new MsgSentEvent(this);
            se.setMsg(toDeliver);
            this.eventBus.post(se);
        }
        this.logger.fine(String.format("%d mails have been delivered.", mailSet.length));
        toBeHandled.getSendingSphere().setHandled(toBeHandled.getNbCorrespondingOutcomes());
    }

    ActiveItemContainer getAgentImplementations() {
        return agentImplementations;
    }

}
