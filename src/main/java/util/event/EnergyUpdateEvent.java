package util.event;

import environment.world.agent.Agent;

public class EnergyUpdateEvent extends Event {

    private Agent agent;
    private int percentage;
    private boolean isIncreased;


    public EnergyUpdateEvent(Object throwingObject) {
        super(throwingObject);
    }

    public void setAgent(Agent agent) {
        this.agent = agent;
    }

    public void setEnergyPercentage(int percentage) {
        this.percentage = percentage;
    }

    public void setIncreased(boolean increased) {
        this.isIncreased = increased;
    }


    public Agent getAgent() {
        return this.agent;
    }

    public int getEnergyPercentage() {
        return percentage;
    }

    public boolean isIncreased() {
        return this.isIncreased;
    }
}
