package environment.world.agent;

import java.awt.Color;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

import environment.ActiveImp;
import environment.ActiveItem;
import environment.ActiveItemID;
import environment.ActiveItemID.ActionPriority;
import environment.EnergyValues;
import environment.Environment;
import environment.world.packet.Packet;
import gui.video.Drawer;
import gui.video.ItemDrawer.LinePoints;
import synchronizer.Synchronization;
import util.MyColor;

/**
 * A class for agents, items that represent the body of agents in the
 * environment.
 */

public class Agent extends ActiveItem<AgentRep> {

    /**
     * The x-coordinate of the previous position.
     */
    private int lastX = -1;

    /**
     * The y-coordinate of the previous position.
     */
    private int lastY = -1;


    /**
     * The number of power units in the battery of this agent. This is an
     * additional property of the agent, it doesn't have to be used. Only
     * some strategies make use of it.
     */
    private int batteryState;


    // The name of the agent
    private final String name;

    // A potential packet the agent is carrying (null otherwise)
    private Packet carry;

    // The environment in which the agent operates
    private Environment env;

    // The color associated with this agent. A color of null implies that the agent can pick up any colored packet.
    private final Color color;

    private final Logger logger = Logger.getLogger(Agent.class.getName());
    

    /**
     * Initializes a new Agent instance.
     *
     * @param  x        x-coordinate of the agent
     * @param  y        y-coordinate of the agent
     * @param  environ  the environment in which this agent is situated
     * @param  view     this agent's view range
     * @param  id       this agent's ID
     * @param  name     this agent's name
     */
    public Agent(int x, int y, Environment environ, int view, ActiveItemID id, String name, Color color) {
        super(x, y, view, id);
        this.name = name;
        this.env = environ;
        this.batteryState = EnergyValues.BATTERY_START;
        this.color = color;
        this.logger.fine(String.format("Agent created at %d %d", x, y));
    }


    public Agent(int x, int y, Environment environ, int view, int id, String name, Color color) {
        this(x, y, environ, view, new ActiveItemID(id, ActionPriority.AGENT), name, color);
    }



    /**
     * Initializes a new Agent instance.
     *
     * @param  x        x-coordinate of the agent
     * @param  y        y-coordinate of the agent
     * @param  view     this agent's view range
     * @param  id       this agent's ID
     * @param  name     this agent's name
     */
    public Agent(int x, int y, int view, int id, String name) {
        this(x, y, null, view, id, name, null);
    }

    /**
     * Initializes a new Agent instance.
     *
     * @param  x        x-coordinate of the agent
     * @param  y        y-coordinate of the agent
     * @param  view     this agent's view range
     * @param  id       this agent's ID
     * @param  name     this agent's name
     */
    public Agent(int x, int y, int view, int id, String name, String color) {
        this(x, y, null, view, id, name, MyColor.getColor(color));
    }

    /**
     * Gets the Environment of this Agent.
     *
     * @return    This agent's environment
     */
    public Environment getEnvironment() {
        return env;
    }

    /**
     * Sets the Environment of this Agent.
     *
     * @param  environ  The new environment value
     */
    public void setEnvironment(Environment environ) {
        this.env = environ;
    }

    /**
     * Lets this agent consume a given Packet.
     *
     * @param  p    the Packet to consume
     * @post        new.carry() == p
     */
    public void consume(Packet p) {
        carry = p;
        //when dropped packet, forget orientation (last visited area)
        if (p == null) {
            setLastX(-1);
            setLastY(-1);
        }
    }

    /**
     * Lets this agent consume a new X-coordinate.
     *
     * @param  nx   x-coordinate
     * @post        new.getX() = nx
     */
    protected void consumeX(int nx) {
        setLastX(getX());
        setX(nx);
    }

    /**
     * Lets this agent consume a new Y-coordinate.
     *
     * @param  ny   y-coordinate
     * @post        new.getY() = ny
     */
    protected void consumeY(int ny) {
        setLastY(getY());
        setY(ny);
    }

    /**
     * Returns the packet this agent is carrying. If the agent isn't carrying
     * any packet, it returns an empty Optional.
     *
     * @return the value of carry
     */
    public Optional<Packet> getCarry() {
        return Optional.ofNullable(this.carry);
    }


    /**
     * Check if the agent is carrying something.
     *
     * @return True if the agent carries something, false otherwise.
     */
    public boolean hasCarry() {
        return this.getCarry().isPresent();
    }


    /**
     * Gets the name of this Agent.
     *
     * @return The name value
     */
    public String getName() {
        return name;
    }


    /**
     * Get the color this Agent is limited to.
     *
     * @return The color this agent is limited to, Optional.empty() if the agent has no color restriction.
     */
    public Optional<Color> getColor() {
        return Optional.ofNullable(this.color);
    }


    /**
     * Returns a representation of this Agent.
     *
     * @return the representation of this agent with a representation of anything it carries
     */
    @Override
    public AgentRep getRepresentation() {
        AgentRep aRep = new AgentRep(getX(), getY(), getID(), getName(), this.color);
        this.getCarry().ifPresent(c -> aRep.setCarry(c.getRepresentation()));
        return aRep;
    }

    /**
     * Returns the x-coordinate of the previous position this Agent stood on.
     *
     * @return the previous x-coordinate
     */
    public int getLastX() {
        return lastX;
    }

    /**
     * Returns the y-coordinate of the previous position this Agent stood on.
     *
     * @return the previous y-coordinate
     */
    public int getLastY() {
        return lastY;
    }

    /**
     * Sets the last x-coordinate to the specified coordinate.
     *
     * @param x the new previous x-coordinate.
     */
    private void setLastX(int x) {
        lastX = x;
    }

    /**
     * Sets the last y-coordinate to the specified coordinate.
     *
     * @param y the new previous y-coordinate.
     */
    private void setLastY(int y) {
        lastY = y;
    }

    /**
     * Draws this Agent on the GUI.
     *
     * @param drawer The visiting drawer
     */
    public void draw(Drawer drawer) {
        drawer.drawAgent(this);
    }


    public int getBatteryState() {
        return batteryState;
    }


    public void updateBatteryState(int load) {
        batteryState = Math.max(Math.min(batteryState + load, EnergyValues.BATTERY_MAX), EnergyValues.BATTERY_MIN);
    }



    public String generateEnvironmentString() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%s\n", this.getClass().getName()));
        sb.append(String.format("nbArgs %d\n", this.getColor().isPresent() ? 6 : 5));
        sb.append(String.format("Integer %d\n", this.getX()));
        sb.append(String.format("Integer %d\n", this.getY()));
        sb.append(String.format("Integer %d\n", this.getView()));
        sb.append(String.format("Integer %d\n", this.getID().getID()));
        sb.append(String.format("String \"%s\"\n", this.getName()));

        this.getColor().ifPresent(c -> sb.append(String.format("String \"%s\"\n", MyColor.getName(c))));

        return sb.toString();
    }


    @Override
    public ActiveImp generateImplementation(Environment env, Synchronization synchronizer) {
        // Exceptional case, not used
        throw new RuntimeException("Should not be used.");
    }


    public List<LinePoints> getActualView() {
        return getEnvironment().getPerception(getID()).getShape();
    }
}
