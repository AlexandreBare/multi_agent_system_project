package environment;

public class ActiveItemID implements Comparable<ActiveItemID> {
    
    private final int id;
    private final ActionPriority actionPriority;


    public ActiveItemID(int id, ActionPriority priority) {
        this.id = id;
        this.actionPriority = priority;
    }


    public int getID() {
        return this.id;
    }

    public ActionPriority getActionPriority() {
        return this.actionPriority;
    }

    @Override
    public int compareTo(ActiveItemID other) {
        return this.getID() - other.getID();
    }


    public enum ActionPriority {
        // Priorities listed from higher priority (value 0) to lower priority (1)
        
        AGENT(0),
        ENERGYSTATION(1),
        GENERATOR(2),
        CONVEYOR(3),
        OTHER(4);


        ActionPriority(int priority) {
            this.priority = priority;
        }

        public int getPriority() {
            return this.priority;
        }

        private final int priority;
    }

}
