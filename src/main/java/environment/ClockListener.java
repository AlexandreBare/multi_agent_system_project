package environment;

/**
 * An interface for clock listeners, Items that listen to the Clock.
 */
public interface ClockListener {

    void onClockEvent(ClockEvent event);

}
