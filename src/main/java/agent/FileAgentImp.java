package agent;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Logger;

import com.google.common.eventbus.EventBus;

import agent.behavior.Behavior;
import agent.behavior.BehaviorChange;
import agent.behavior.BehaviorState;
import environment.ActiveItemID;
import util.AsciiReader;

/**
 * This Agent Implementation reads its behavior graph from file
 * @see AgentImp
 */
public class FileAgentImp extends AgentImp {


    // The file which contains the configuration for this agent implementation
    private final String configFile;

    private final Logger logger = Logger.getLogger(FileAgentImp.class.getName());


    /**
     * Creates an agent with a behavior that is specified through a configuration file
     * @pre file != null
     * @pre file != ""
     * @param ID          see superclass
     * @param file        the name of the file having the configuration for this Agent
     * @param eventBus    The eventbus on which events in the PacketWorld are published
     */
    public FileAgentImp(ActiveItemID ID, final String file, EventBus eventBus) {
        super(ID, eventBus);
        this.configFile = file;
    }


    /**
     * Creates this Agent's behavior by loading a behavior graph from file.
     */
    public void createBehavior() {
        try {
            AsciiReader reader = new AsciiReader(this.configFile);
            reader.check("description");
            reader.readNext(); //skipping description
            reader.check("nbStates");
            int nbStates = reader.readInt();
            BehaviorState[] states = new BehaviorState[nbStates];
            for (int i = 0; i < nbStates; i++) {
                reader.check(Integer.toString(i + 1));
                states[i] = new BehaviorState((Behavior) reader.readClassConstructor());
            }
            reader.check("nbChanges");
            int nbChanges = reader.readInt();

            for (int i = 0; i < nbChanges; i++) {
                BehaviorChange change = (BehaviorChange) reader.readClassConstructor();
                change.setAgentState(this);
                reader.check("priority");
                int priority = reader.readInt();

                reader.check("source");
                states[reader.readInt() - 1].addChange(change, priority);
                reader.check("target");
                change.setNextBehavior(states[reader.readInt() - 1]);
            }
            setCurrentBehaviorState(states[0]);
        } catch (FileNotFoundException e) {
            this.logger.severe(String.format("Behavior config file not found: %s\n%s", this.configFile, e.getMessage()));
        } catch (IOException e) {
            this.logger.severe(String.format("Something went wrong while reading the agent configuration file: %s\n%s",
                    this.configFile, e.getMessage()));
        } catch (Exception e) {
            this.logger.severe(String.format("Something went wrong while loading behavior from file %s: \n%s",
                    this.configFile, e.getMessage()));
        }
    }
}
