package util.event;

/**
 * A class for events
 */
public class Event {


    private final Object thrower;

    
    /**
     * Constructs a new event, given the object that throws this event.
     * @param throwingObject the object that throws this event
     */
    public Event(Object throwingObject) {
        thrower = throwingObject;
    }

    /**
     * Returns the object that threw this event.
     */
    public Object getThrower() {
        return thrower;
    }
}
