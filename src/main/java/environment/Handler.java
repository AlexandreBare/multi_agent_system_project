package environment;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;

public abstract class Handler<T extends ToHandle> implements Runnable {

    protected final Queue<T> inbox;
    protected int nbInInBox;
    protected final Object dummy = new Object();
    protected boolean suspendRequested;
    protected boolean running;

    public Handler() {
        inbox = new LinkedList<>();
        nbInInBox = 0;
        Thread t = new Thread(this);
        suspendRequested = true;
        t.start();
    }

    synchronized void deposit(T toBeHandled) {
        inbox.add(toBeHandled);
        nbInInBox++;
        if (suspendRequested) {
            requestResume();
        }
    }

    public void run() {
        running = true;
        while (running) {
            checkSuspended();
            if (running) {
                monitorIncomingToBeHandled();
            }
        }
    }

    protected void checkSuspended() {
        try {
            synchronized (dummy) {
                while (suspendRequested) {
                    dummy.wait();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void requestSuspend() {
        suspendRequested = true;
    }

    void requestResume() {
        suspendRequested = false;
        synchronized (dummy) {
            dummy.notify();
        }
    }

    public synchronized void monitorIncomingToBeHandled() {
        if (inbox.isEmpty()) {
            requestSuspend();
        } else {
            nbInInBox--;
            process(inbox.poll());
        }
    }

    /**
     * This is the method to be implemented by each of the three handlers
     */
    protected abstract void process(T toBeHandled);

    // a random number generator method transported from the old ControllerImp
    protected int nextActive(boolean[] turns) {
        boolean hit = false;
        int next = 0;
        boolean candidate = false;
        while (!candidate & next < turns.length) {
            candidate = !turns[next++];
        }
        if (!candidate) {
            Arrays.fill(turns, false);
        }
        while (!hit) {
            next = (int) (Math.random() * turns.length);
            hit = !turns[next];
        }
        turns[next] = true;
        return next;
        //return (active + 1) % implementations.length;
    }

    public void finish() {
        running = false;
        requestResume();

    }

}
