package util.event;

import environment.Item;
import environment.world.agent.Agent;
import environment.world.packet.Packet;

public class AgentActionEvent extends Event {

    public final static int PICK_PACKET = 1;
    public final static int PUT_PACKET = 2;
    public final static int DELIVER_PACKET = 3;
    public final static int STEP = 4;
    public final static int SKIP = 5;
    public final static int PUT_FLAG = 6;
    public final static int PUT_PHEROMONE = 7;
    public final static int REMOVE_PHEROMONE = 8;
    public final static int PUT_CRUMB = 9;
    public final static int PICK_CRUMB = 10;
    public final static int PUT_AREA_VALUE = 11;
    public final static int LOAD_ENERGY = 12;
    public final static int IDLE_ENERGY = 13; // Idle action in terms of energy consumption
    public final static int PICK_GENERATOR = 14;
    public final static int GENERATE_PACKET = 15;
    public final static int STEAL_PACKET = 16;
    public final static int CONVEY_PACKET = 20;



    private Agent agent;
    private Packet packet;
    private int toX;
    private int toY;
    private int fromY;
    private int fromX;
    private int action;
    private Item<?> oldTo;
    private int value;



    public AgentActionEvent(Object thrower) {
        super(thrower);
    }

    public Agent getAgent() {
        return agent;
    }

    public void setAgent(Agent agent) {
        this.agent = agent;
    }

    public Packet getPacket() {
        return packet;
    }

    public void setPacket(Packet packet) {
        this.packet = packet;
    }

    public int getToX() {
        return toX;
    }

    public void setToX(int toX) {
        this.toX = toX;
    }

    public int getToY() {
        return toY;
    }

    public void setToY(int toY) {
        this.toY = toY;
    }

    public void setTo(int x, int y) {
        setToX(x);
        setToY(y);
    }

    public int getFromY() {
        return fromY;
    }

    public void setFromY(int fromY) {
        this.fromY = fromY;
    }

    public int getFromX() {
        return fromX;
    }

    public void setFromX(int fromX) {
        this.fromX = fromX;
    }

    public void setFrom(int x, int y) {
        setFromX(x);
        setFromY(y);
    }

    public int getAction() {
        return action;
    }

    public void setAction(int action) {
        this.action = action;
    }

    public Item<?> getOldTo() {
        return oldTo;
    }

    public void setOldTo(Item<?> oldTo) {
        this.oldTo = oldTo;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int val) {
        this.value = val;
    }
}
