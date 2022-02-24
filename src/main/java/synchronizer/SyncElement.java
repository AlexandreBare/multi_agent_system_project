package synchronizer;

import java.util.logging.Logger;

/**
 * A class for members of sync sets. A syncElement represents for a synchronizer of an agent the other agents with which it is in the
 * process of synchronizing, including their state and syncTime.
 */
public class SyncElement {

    /**
     * A variable holding the name of the agent represented by this SyncElement.
     */
    private final int name;

    /**
     * A reference to the representation of the state of the agent represented by this SyncElement.
     */
    private String state;

    /**
     * A variable for representing the sync time of the agent represented by this syncElement
     */
    private int time;


    private final Logger logger = Logger.getLogger(SyncElement.class.getName());

    /**
     * Initialize a new SyncElement with name n.
     * @param name The name of this new syncElement
     * @post new.getName()==n
     * @post new.getState().equals("ini")
     * @post new.getTime()==0
     */
    public SyncElement(int name) {
        this(name, "ini", 0);
    }

    /**
     * Initialize a new SyncElement with name n, in state s and time t.
     * @param name The name of this new syncElement
     * @param state The state of this new SyncElement instance
     * @param time The syncTime of this new SyncElement instance
     * @post new.getName() == n
     * @post new.getState().equals(s)
     * @post new.getTime() == t
     */
    public SyncElement(int name, String state, int time) {
        this.name = name;
        this.state = state;
        this.time = time;
    }

    /**
     * Return the name of this SyncElement.
     */
    public int getName() {
        return name;
    }

    /**
     * Return the state of this SyncElement.
     */
    public String getState() {
        return state;
    }

    /**
     * Return the syncTime of this SyncElement.
     */
    public int getTime() {
        return time;
    }

    /**
     * Change the state of this SyncElement into st.
     * @param state The new state of this SyncElement.
     * @post new.getState().equals(st)
     */
    public void changeState(String state) {
        this.state = state;
    }

    /**
     * Change the time of this SyncElement into t.
     * @param time The new syncTime for this SyncElement
     * @post new.getTime()==t
     */
    public void changeTime(int time) {
        this.time = time;
    }

    // For testing purposes
    public void printElem() {
        this.logger.fine("  (" + (name + 1) + "," + state + "," + time + ")");
    }

    public boolean isCommittable(int t) {
        return ( ( ( ( (getState().equals("sync")
                        | getState().equals("comS")
                        )
                      | getState().equals("comR")
                      )
                    | (getState().equals("ackS") & getTime() <= t)
                    )
                  | (getState().equals("ackR") & getTime() <= t)
                  )
                | getState().equals("add")
                );
    }

    public boolean isSyncable(int t) {
        return ( (getState().equals("sync")
                  //   |  getState().equals("comS")
                  //  )
                  | getState().equals("comR")
                  )
                | getState().equals("add")
                );
    }

    public boolean possibleBlocked(int t) {
        return ( (getState().equals("comR")
                  | getState().equals("reqS")
                  )
                | getState().equals("add")
                );
    }
}
