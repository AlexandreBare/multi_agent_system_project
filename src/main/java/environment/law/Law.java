package environment.law;

import environment.Environment;
import support.Influence;

/**
 * An interface for laws, being objects used by the reactor to
 * validate certain influences.
 */
public interface Law {

    /**
     * Check if this Law is applicable to the given Influence 'inf'
     */
    boolean applicable(Influence inf);

    /**
     * Check if the given Influence 'inf' passes validation by this Law.
     */
    boolean apply(Influence inf);

    /**
     * Sets the environment of this Law
     */
    void setEnvironment(Environment env);
}
