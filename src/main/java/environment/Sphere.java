package environment;

import java.util.logging.Logger;

import support.ActionOutcome;
import support.CommunicationOutcome;
import support.Outcome;
import support.PlaceHolderOutcome;

/**
 * A class for grouping Outcomes, i.e. products of action-phases of Agents and for handling a completion of such a grouping. The
 * condition of completion is met when all member outcomes of a sphere have the property of having acted. Sphere completion
 * triggers the sending of a ToHandled object to one or more of the Handlers of this environment, such that the contents of a
 * completed sphere is being dealt with by the environment. If all outcomes are handled by the environment, all AgentImp's involved
 * are called to activate their next action phase and the sphere is cleared (removed from the set of spheres managed by the
 * collector).
 */
public class Sphere {

    private Outcome[] elements;
    private int nbElements;
    private int nbDealtWith;
    private int nbHandled;

    private final Collector collector;
    private final EOPHandler eOPHandler;
    private final PostalService postalService;
    private final ActiveItemContainer agentImplementations;
    private final Reactor reactor;

    private final Logger logger = Logger.getLogger(Sphere.class.getName());

    /**
     * Initialize a new Sphere. At initialization, a new sphere contains no member Outcomes. Also, a sphere holds a reference to
     * the collector by which it is managed, to the interface of the agentImplementations-package and to each of the three
     * handlers of this environment.
     * 
     * @post new.getNbElements()==0
     * @post getNbHandled()==0
     * @post new.getCollector()==collector
     * @post new.getAgentImplementations()==agentImplementations
     * @post new.getEOPHandler()==eOPHandler
     * @post new.getPostalService()==postalService
     * @post new.getReactor()==reactor
     */
    public Sphere(
        Collector collector,
        ActiveItemContainer agentImplementations,
        EOPHandler eOPHandler,
        PostalService postalService,
        Reactor reactor) {
        elements = new Outcome[0];
        setNbElements(0);
        setNbActed(0);
        nbHandled = 0;
        this.collector = collector;
        this.agentImplementations = agentImplementations;
        this.eOPHandler = eOPHandler;
        this.postalService = postalService;
        this.reactor = reactor;
    }

    /**
     * Return the Outcome registered at index <index> of this Sphere.
     * @param index The index of the Outcome to be returned by this method.
     * @return The outcome registered at <index>.
     * @throws IndexOutOfBoundsException index >= getNbElements()
     */
    public Outcome getElementAt(int index) throws IndexOutOfBoundsException {
        return elements[index];
    }

    /**
     * Integrate <outcome> in this Sphere. Integration includes: (i) addition of an outcome of an agent that did not already contribute
     * to this sphere; (ii) substitution of an outcome by an outcome of the same agent, if the former is a placeholder for the
     * latter.
     */
    public void integrate(Outcome outcome) {
        if (!containsOutcomeOf(outcome.getAgentID())) {
            addToSphere(outcome);
        } else { // this clause substitutes placeholders by genuine outcomes
            for (int i = 0; i < getNbElements(); i++) {
                if (outcome.getAgentID() == getElementAt(i).getAgentID()) {
                    elements[i] = outcome;
                    setNbActed(getNbActed() + 1);
                }
            }
        }
        ActiveItemID[] sS = outcome.getSyncSet();
        for (ActiveItemID s : sS) {
            if (!containsOutcomeOf(s)) {
                addToSphere(new PlaceHolderOutcome(s));
            }
        }
    }

    /**
     * Add the Outcome <outcome> to this Sphere.
     * @param outcome The Outcome instance to be added to this Sphere.
     * @post new.getNbElements()==getNbElements()+1
     * @post if outcome.hasActed()
     *       then new.getNbActed()==getNbActed()+1
     */
    public void addToSphere(Outcome outcome) {
        Outcome[] temp = new Outcome[getNbElements() + 1];
        for (int i = 0; i < getNbElements(); i++) {
            temp[i] = getElements()[i];
        }
        temp[getNbElements()] = outcome;
        elements = temp;
        if (outcome.hasActed()) {
            setNbActed(getNbActed() + 1);
        }
        setNbElements(getNbElements() + 1);
    }

    /**
     * Incorporate <other> into this Sphere, i.e. add the outcomes in <other> to this and clear <other>.
     * @param other The Sphere to be incorporated into this sphere.
     * @post Every element formerly in other is now member of this.
     * @post (new other).getNbElements()==0
     */
    public void incorporate(Sphere other) {
        for (int i = 0; i < other.getNbElements(); i++) {
            addToSphere(other.getElements()[i]);
        }
        other.clear();
    }

    /**
     * Check whether an agent with id <agent> has contributed an outcome to this sphere.
     * @param agent The id or the agent under consideration.
     * @return if for some i in 0..getNbElements()-1: getElementAt(i).getAgentID()==agent
     *         then return true
     *         else return false
     */
    public boolean containsOutcomeOf(ActiveItemID agent) {
        boolean found = false;
        for (int i = 0; i < getNbElements(); i++) {
            if (elements[i].getAgentID() == agent) {
                found = true;
                break;
            }
        }
        return found;
    }

    public Outcome[] getElements() {
        return elements;
    }

    public int getNbElements() {
        return nbElements;
    }

    public void setNbElements(int newNb) {
        nbElements = newNb;
    }

    public int getNbActed() {
        return nbDealtWith;
    }

    public void setNbActed(int newNb) {
        nbDealtWith = newNb;
    }

    public boolean allActed() {
        return getNbElements() == getNbActed();
    }

    public int getNbHandled() {
        return nbHandled;
    }

    public void incrNbHandled() {
        nbHandled++;
    }

    public boolean allHandled() {
        return getNbHandled() == getNbElements();
    }

    // new
    protected Collector getCollector() {
        return collector;
    }

    protected PostalService getPostalService() {
        return postalService;
    }

    protected EOPHandler getEOPHandler() {
        return eOPHandler;
    }

    ActiveItemContainer getAgentImplementations() {
        return agentImplementations;
    }

    /**
     * Remove this sphere from the set of spheres managed by the collector.
     * @post for i 0..getCollector().getNbSpheres()-1: new.geSpheres()[i] != this
     */
    public void clear() {
        synchronized (getCollector().getSpheres()) { // mutex with makeNewSphere() !!!!!
            getCollector().removeSphere(this);
        }
    }

    /**
     * Pass the contents of a full sphere to the appropriate handler.
     * @pre allActed()
     */
    void handleFullSphere() {
        EOPSet perceptionSet = new EOPSet(this);
        MailBag mailBag = new MailBag(this);
        InfluenceSet influenceSet = new InfluenceSet(this);
        for (int i = 0; i < getNbElements(); i++) {
            if (getElementAt(i).toBeHandledBy("EOPHandler")) {
                perceptionSet.incrNbCorrespondingOutcomes();
            } else if (getElementAt(i).toBeHandledBy("PostalService")) {
                int amtOfMails = ((CommunicationOutcome) getElementAt(i)).getMailBuffer().getMails().length;

                for (int j = 0; j < amtOfMails; j++) {
                    mailBag.putInBag( ((CommunicationOutcome) getElementAt(i)).getMailBuffer().getMails()[j]);
                }
                mailBag.incrNbCorrespondingOutcomes();
            } else if (getElementAt(i).toBeHandledBy("Reactor")) {
                influenceSet.addInfluenceToSet( ( (ActionOutcome) getElementAt(i)).
                                          getInfluence());
                influenceSet.incrNbCorrespondingOutcomes();
            }
        }

        if (!perceptionSet.isEmpty()) {
            eOPHandler.deposit(perceptionSet);
        }
        if (!mailBag.isEmpty()) {
            postalService.deposit(mailBag);
        }
        if (!influenceSet.isEmpty()) {
            reactor.deposit(influenceSet);
        }
    }

    /**
     * Adjusts the number of outcomes of a full sphere that are handled by the appropriate handlers. If after such an adjustment,
     * all member outcomes have been handled, all agents involved in this sphere (as authors of the member outcomes) are
     * notified to activate the next action-phase and the sphere is deleted from the set of spheres managed by the collector.</p>
     *
     */
    void setHandled(int handledNbOutcomes) {
        nbHandled += handledNbOutcomes;
        if (allHandled()) {
            this.logger.fine("Starting to wake up agents for next phase");
            for (int i = 0; i < getNbElements(); i++) {
                getAgentImplementations().acquireLock(getElementAt(i).
                    getAgentID());
            }
            for (int i = 0; i < getNbElements(); i++) {
                getAgentImplementations().activateNewPhase(getElementAt(i).
                    getAgentID(), givePermissionForNextPhase());
            }
            clear();
            for (int i = 0; i < getNbElements(); i++) {
                getAgentImplementations().releaseLock(getElementAt(i).
                    getAgentID());
            }
        }
    }

    /**
     * A method used to collect votes for activating the next phase in the action cycle of the involved agents. Returns false if
     * at least one of the member outcomes votes against initiating the next phase, and returns true otherwise.
     */
    private boolean givePermissionForNextPhase() {
        boolean next = true;
        for (int i = 0; next && i < getNbElements(); i++) {
            next = next & getElementAt(i).getVoteForContinuingWithNextPhase();
        }
        return next;
    }


    public void printSphere() {
        this.logger.fine(String.format("\t\tNumber of elements: %d", nbElements));
        for (Outcome element : elements) {
            String message = "\t\t" + element.getAgentID().getID();
            if (element.hasActed()) {
                message += "\t\thas acted - type: ";
            } else {
                message += "\t\tnot acted - type: ";
            }
            this.logger.fine(message + element.getType());
        }
    }
}
