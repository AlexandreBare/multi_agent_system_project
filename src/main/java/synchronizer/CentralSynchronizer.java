package synchronizer;

import java.util.List;
import java.util.logging.Logger;

import environment.ActiveItemContainer;
import environment.ActiveItemID;

/**
 * A class for objects synchronizing all agents in the system.
 */

public class CentralSynchronizer implements Synchronizer {

    /**
     * A container for id's of agents having this CentralSynchronizer instance as their synchronizer.
     * @invar for each i, j in 0..agents.length-1: getSynchronizer(i) == getSynchronizer(j)
     */
    private final ActiveItemID[] agents;


    private final Logger logger = Logger.getLogger(CentralSynchronizer.class.getName());

    /**
     *Initialize a central synchronizer, i.e. a synchronizer, connected to all the agents.
     *@post new.agents == getEnvironment().getAllAgentID()
     */
    public CentralSynchronizer(ActiveItemContainer agentImps) {
        agents = agentImps.getAllActiveItemIDs().toArray(new ActiveItemID[0]);
        //begin{TEST}
        this.logger.fine("Synchronizer initialized on the following agent set:");
        for (ActiveItemID agent : agents) {
            this.logger.fine(agent.getID() + "  ");
        }
        //end{TEST}
    }

    /**
     * Return the syncSet of the agent with the name id. Under central synchronization, the returned sync set consists of the id's of
     *  all other agents active in the system.
     * @param agent The name of the agent requesting its sync set.
     * @return The set of id's of agents with which the requesting agent is bound to synchronize.
     *         !(agent in getSyncSet(agent)) &&
     *         for each i in agents[i]: if agents[i] != agent, then agents[i] in getSyncSet(agent)
     */
    public synchronized ActiveItemID[] getSyncSet(ActiveItemID agent) {
        ActiveItemID[] sS = new ActiveItemID[agents.length - 1];
        int j = 0;
        for (ActiveItemID k : agents) {
            if (k != agent) {
                sS[j] = k;
                j++;
            }
        }
        return sS;
    }

    /**
     * Form the sync set for the agent with name agent, based on the set of candidates with which to synchronize and agent's syncTime
     *  Under central synchronization, this method has no effect.
     * 
     * @param agent The id of the agent requesting formation of its sync set.
     * @param setOfCandidates The set of agent-id's that are candidate-members of the sync set to be formed.
     * @param time The syncTime of the requesting agent.
     * @post  new getSyncSet(agent)==getSyncSet(agent)
     */
    public void synchronize(ActiveItemID agent, List<ActiveItemID> setOfCandidates, int time) {
        // NO-OP;
    }

}
