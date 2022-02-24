package util;

import java.util.logging.Logger;

/**
 * Class needed for Synchronization
 */
public class Mutex {

    private boolean lockTaken;

    private final Logger logger = Logger.getLogger(Mutex.class.getName());

    public Mutex() {
        lockTaken = false;
    }

    public synchronized void acquireLock() {
        this.logger.fine("Requesting lock " + this);
        while (lockTaken) {
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        this.logger.fine("Acquiring lock " + this);
        lockTaken = true;
    }

    public synchronized void releaseLock() {
        lockTaken = false;
        this.logger.fine("Releasing lock " + this);
        notify();
    }

}
