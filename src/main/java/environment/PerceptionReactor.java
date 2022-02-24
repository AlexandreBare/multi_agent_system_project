package environment;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Logger;

import environment.law.PerceptionLaw;
import util.Variables;

/**
 *  A class for PerceptionReactors. A PerceptionReactor sends perceptions to
 *  the agents. These perceptions are made according to perceptionLaws, a set
 *  of rules that govern what an agent can see.
 */

public class PerceptionReactor {

    private Environment env;
    private final List<PerceptionLaw> laws;

    private final Logger logger = Logger.getLogger(PerceptionReactor.class.getName());

    /**
     *  Initializes a new PerceptionReactor object
     *
     * @param  environ  The environment this PerceptionReactor is part of.
     */
    public PerceptionReactor(Environment environ) {
        setEnvironment(environ);
        this.laws = new ArrayList<>();
        loadLaws();
    }

    /**
     *   Instantiate all PerceptionLaws listed in the 'perceptionlaws.properties'
     *   configuration file and add them to the 'laws' list.
     *   The configuration file is read, and we try to instantiate each class
     *   listed there and add it to the list 'laws'.
     */
    public void loadLaws() {
        Properties perceptionLaws = new Properties();
        // open configuration file
        try {
            FileInputStream sf = new FileInputStream(Variables.PERCEPTION_LAWS_PROPERTIES_FILE);
            perceptionLaws.load(sf);
        } catch (Exception e) {
            this.logger.severe(String.format("error with perceptionlaws properties file: %s", e));
        }
        int nbFound = 0;
        boolean stop = false;
        // list for the strings we find in the configuration file
        List<String> found = new ArrayList<>();
        for (int i = 0; !stop; i++) {
            try {
                // add all worlds listed in the configuration file to 'found'
                found.add(perceptionLaws.getProperty( (Integer.valueOf(i + 1)).
                    toString(), "default"));
                if (! ( ( found.get(i)).equals("default"))) {
                    nbFound++;
                    this.logger.fine(String.format("found perception law: %s", found.get(i)));
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
                PerceptionLaw pLaw = (PerceptionLaw) Class.forName(found.get(i))
                        .getDeclaredConstructor()
                        .newInstance();
                laws.add(pLaw);
            } catch (Exception e) {
                this.logger.severe("Error setting perceptionlaws");
            }
        }
    }

    /**
     *  Sets the environment of this PerceptionReactor
     *
     * @param  environ  The environment value
     */
    private void setEnvironment(Environment environ) {
        this.env = environ;
    }

    /**
     *  Returns a perception for the given ActiveItem (including agents).
     *
     * @param   item  The perceiving ActiveItem
     * @pre     item != null
     * @return  A perception filled with representations of what 'item' can see
     *          after applying all the perceptionlaws listed in 'laws'
     */
    protected Perception getPerception(ActiveItem<?> item) {
        int view = item.getView();
        int width = env.getWidth();
        int height = env.getHeight();
        // we're talking about absolute coordinates here
        // we calculate the borders of the perception in the worlds
        int ax = item.getX();
        int ay = item.getY();
        int minX = Math.max(0, ax - view);
        int maxX = Math.min(width - 1, ax + view);
        int minY = Math.max(0, ay - view);
        int maxY = Math.min(height - 1, ay + view);
        // we initiate a Perception with the right dimensions
        Perception perception = new Perception(maxX - minX + 1, maxY - minY + 1,
                                         minX, minY);
        perception.setSelfX(ax - minX);
        perception.setSelfY(ay - minY);
        for (World<?> aWorld : env.getWorlds()) {
            for (int j = minX; j <= maxX; j++) {
                for (int k = minY; k <= maxY; k++) {
                    var potentialItem = aWorld.getItem(j, k);
                    if (potentialItem != null) {
                        Representation tempRep = potentialItem.getRepresentation();
                        perception.addRep(j - minX, k - minY, tempRep);

                    }
                }
            }
        }

        // we enforce all known laws on 'perception'
        return enforceLaws(perception);
    }


    /**
     *   Enforce all known laws on a given Perception 'perception'.
     *
     *   @param perception The Perception to enforce all perceptionlaws upon
     *   @return A Perception that is become by enforcing all PerceptionLaws
     *           in 'laws' upon 'perception'
     */
    private Perception enforceLaws(Perception perception) {
        Perception temp = perception;
        for (PerceptionLaw law : laws) {
            temp = law.enforce(temp);
        }
        return temp;
    }
}
