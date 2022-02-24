package agent.behavior;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.TreeSet;
import java.util.logging.Logger;

/**
 * This class acts a [GoF]FlyWeight pattern for the Behavior class. It enables
 * big Behaviors to appear a lot in behavior graphs.
 */
public class BehaviorState {

    private static final Random rnd = new Random();

    private final Logger logger = Logger.getLogger(BehaviorState.class.getName());


    /**
     * A map with keys representing the weights of behavior changes, mapped to a list of
     * one or more BehaviorChange classes that can be evaluated and possible taken from this behavior state.
     */
    private Map<Integer, List<BehaviorChange>> behaviorChanges;

    // The behavior of this behavior state
    private Behavior behavior;

    // Signals if this state is already closing.
    private boolean closing = false;


    
    
    /**
     * Creates a new BehaviorState for the given Behavior
     * @param b: the Behavior to point
     */
    public BehaviorState(Behavior b) {
        behaviorChanges = new HashMap<>();
        behavior = b;
    }

    /**
     * Returns the real Behavior
     */
    public Behavior getBehavior() {
        return behavior;
    }

    /**
     * Evaluates all succeeding BehaviorChanges in random order. When the
     * post condition of the current Behavior is false, no updates or changes
     * are done.
     * If a BehaviorChange fires then the current Behavior State of the owner
     * Agent will change to the one pointed to by the last tested
     * 'positive/open' Change.
     */
    public void testBehaviorChanges() {
        if (behaviorChanges.size() == 0 || !behavior.postCondition()) {
            return;
        }

        for (var changes : behaviorChanges.values()) {
            changes.forEach(BehaviorChange::updateChange);
        }

        TreeSet<Integer> priorities = new TreeSet<>(Comparator.reverseOrder());
        priorities.addAll(behaviorChanges.keySet());

        for (int key : priorities) {
            var changes = behaviorChanges.get(key);
            int[] rand = createRandom(changes.size());

            // Randomly try changes within the same priority level
            for (int i = 0; i < changes.size(); i++) {
                if (changes.get(rand[i]).testChange()) {
                    this.logger.fine(String.format("changed with %s", changes.get(rand[i]).toString()));
                    return;
                }
            }

        }
    }


    /**
     * Adds a BehaviorChange with a given priority level (higher priority values implies a higher priority)
     */
    public void addChange(BehaviorChange bc, int priority) {
        if (!behaviorChanges.containsKey(priority)) {
            behaviorChanges.put(priority, new ArrayList<>());
        }

        behaviorChanges.get(priority).add(bc);
    }

    private int[] createRandom(int length) {
        int[] result = new int[length];
        for (int i = 0; i < length; i++) {
            result[i] = -1;
        }
        int count = 0;
        int index;
        while (count < length) {
            index = rnd.nextInt(length);
            if (result[index] == -1) {
                result[index] = count;
                count++;
            }
        }
        return result;
    }

    /**
     * DESTRUCTOR. Cleans up this BehaviorState
     */
    public void finish() {
        if (closing) {
            return;
        }
        closing = true;
        behavior.finish();
        behavior = null;

        for (var changes : behaviorChanges.values()) {
            changes.forEach(BehaviorChange::finish);
        }
        behaviorChanges.clear();
        behaviorChanges = null;
    }

}
