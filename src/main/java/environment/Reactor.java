package environment;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Properties;
import java.util.logging.Logger;

import com.google.common.eventbus.EventBus;

import environment.law.Law;
import support.InfSkip;
import support.Influence;
import util.Variables;
import util.event.GameOverEvent;
import util.event.WorldProcessedEvent;

/**
 * A class for reactors. A reactor processes influenceBuffers.
 * A reactor is a kind of handler (see Handler class).
 */
public class Reactor extends Handler<InfluenceSet> {

    private Environment env;
    private final ApplicationRunner applicationRunner;
    private final List<Law> laws;
    private final EventBus eventBus;

    private final Logger logger = Logger.getLogger(Reactor.class.getName());

    /**
     * Initializes a new Reactor object
     *
     * @param environ  the environment which this Reactor is part of
     */
    public Reactor(Environment environ, ApplicationRunner applicationRunner, EventBus eventBus) {
        super();
        setEnvironment(environ);
        this.applicationRunner = applicationRunner;
        this.eventBus = eventBus;
        this.laws = new ArrayList<>();
        loadLaws();
    }

    /**
     * Instantiate all Laws listed in the 'lawsoftheuniverse.properties' configurationFile and add them
     * to the 'laws' list.
     * The configurationFile is read, and we try to instantiate each class listed there and
     * add it to the list 'laws'.
     */
    public void loadLaws() {
        Properties universeLaws = new Properties();
        // open configuration file
        try {
            FileInputStream sf = new FileInputStream(Variables.LAWS_PROPERTIES_FILE);
            universeLaws.load(sf);
        } catch (Exception e) {
            this.logger.severe(String.format("error with lawsoftheuniverse properties file: %s", e));
        }
        int nbFound = 0;
        boolean stop = false;
        // list for the strings we find in the configuration file
        List<String> found = new ArrayList<>();
        for (int i = 0; !stop; i++) {
            try {
                // add all worlds listed in the configuration file to 'found'
                found.add(universeLaws.getProperty( (Integer.valueOf(i + 1)).
                    toString(), "default"));
                if (! ( ( found.get(i)).equals("default"))) {
                    nbFound++;
                    this.logger.fine(String.format("found universe law: %s", found.get(i)));
                } else {
                    stop = true;
                }
            } catch (Exception e) {
                this.logger.fine(String.format("EOF (%s)", e));
            }
        }
        // instantiate all the classes in 'found' and add them to 'laws'
        for (int i = 0; i < nbFound; i++) {
            try {
                Law law = (Law) Class.forName(found.get(i)).getDeclaredConstructor()
                    .newInstance();

                law.setEnvironment(env);
                laws.add(law);
            } catch (Exception e) {
                this.logger.severe(String.format("Error setting laws of the universe %s", e));
            }
        }
    }

    /**
     * Sets the environment of this Reactor
     *
     * @param environ  The new environment value
     */
    private void setEnvironment(Environment environ) {
        this.env = environ;
    }

    /**
     * Processes a ToHandle.
     * In this case we're talking about InfluenceSets. The reactor processes
     * each Influence in the InfluenceSet one by one.
     *
     * @post    each Influence in toBeHandled is processed
     * @post    the clock is increased
     * @post    the sphere that sent this InfluenceSet gets notified of it's being processed.
     */
    protected void process(InfluenceSet toBeHandled) {
        this.logger.fine("Reactor has received an InfluenceSet ------------------------------------");

        var s = toBeHandled.getInfluenceSet();
        Arrays.sort(s, Comparator.comparingInt(Influence::getPriority));

        for (Influence inf : s) {
            if (inf != null) {
                process(inf); // process each influence
            }
        }


        //End added


        this.logger.fine("Reactor processed the InfluenceSet --------------------------------------");
        env.getClock().incrClock();
        update();
        if (running) {
            toBeHandled.getSendingSphere().setHandled(toBeHandled.getNbCorrespondingOutcomes());
        }
    }

    /**
     * Process a given Influence 'inf'.
     * First 'inf' will be validated according to all known laws.
     * If validation is successful it will be effectuated in the World it
     * affects
     *
     * @param  inf  The influence that has to be processed.
     * @post    If validate(inf) returned true 'inf' is
     *          effectuated in its area of effect.
     */
    private void process(Influence inf) {
        this.logger.fine(String.format("Reactor is processing %s from agent %d", inf.toString(), inf.getID().getID()));
        boolean valid = validate(inf);
        try {
            if (valid) {
                inf.effectuate();

            } else {
                InfSkip skippedInf = new InfSkip(inf.getEnvironment(), inf.getID());
                skippedInf.effectuate();
            }
        } catch (NullPointerException exc) {
            this.logger.severe(String.format("Failed to effectuate influence: %s", exc));
        }
    }

    /**
     * Validate a given Influence 'inf'
     * 'inf' will be passed to all known laws, which are applied to it, when applicable
     *
     * @param  inf The influence that has to be validated.
     * @return 'true' if every applicable law applies successfully
     */
    private boolean validate(Influence inf) {
        for (var law : laws) {
            if (law.applicable(inf) && !law.apply(inf)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Repaints the GUI and updates any data that needs to be updated
     * Puts the program thread to sleep for a period specified by the user
     *
     * @post GUI is repainted
     */
    void update() {
        this.eventBus.post(new WorldProcessedEvent(this));

        if (env.getPacketWorld().getNbPackets() == 0 && 
                env.getPacketGeneratorWorld().getItems().stream()
                    .flatMap(List::stream)
                    .filter(Objects::nonNull)
                    .allMatch(g -> g.hasHitThreshold() && g.getAmtPacketsInBuffer() == 0)) {
            this.eventBus.post(new GameOverEvent(this));
        }
        applicationRunner.checkSuspended();
    }

}
