package environment;

/**
 * An interface for a representable, being items that are able to return a
 * representation.
 */
public interface Representable<T extends Representation> {

    /**
     * Make and return a representation
     */
    T getRepresentation();

}
