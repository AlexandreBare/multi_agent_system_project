package environment;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import com.google.common.eventbus.EventBus;

import environment.world.agent.Agent;
import environment.world.agent.AgentWorld;
import environment.world.conveyor.ConveyorWorld;
import environment.world.crumb.CrumbWorld;
import environment.world.destination.DestinationWorld;
import environment.world.energystation.EnergyStationWorld;
import environment.world.flag.FlagWorld;
import environment.world.generator.PacketGeneratorWorld;
import environment.world.packet.PacketWorld;
import environment.world.pheromone.PheromoneWorld;
import environment.world.wall.WallWorld;
import support.Outcome;

/**
 * A class that acts as an interface between the environment module and
 * other modules, and as a factory for making all needed objects in the
 * environment module during initialisation of the MAS.
 */

public class Environment {


    /**
     * The interface to the agentImplementations module
     */
    protected ActiveItemContainer agentImpl;

    /**
     * The reactor of this environment
     */
    protected Reactor reactor;

    /**
     * The perceptionReactor of this environment
     */
    protected PerceptionReactor pReactor;

    /**
     * The collector of this environment
     */
    protected Collector collector;

    /**
     * EOPHandler
     */
    protected EOPHandler eopHandler;

    /**
     * PostalService
     */
    protected PostalService postalService;

    /**
     * Clock
     */
    protected Clock clock;

    /**
     * The width of each world in this environment
     */
    protected final int width;

    /**
     * The height of each world in this environment
     */
    protected final int height;


    /**
     * All the worlds this environment contains
     */
    private List<World<?>> worlds;

    /**
     * All the active items this environment contains
     * invar: aItem contains only ActiveItems
     */
    private final List<ActiveItem<?>> aItems;


    private final Logger logger = Logger.getLogger(Environment.class.getName());

    //--------------------------------------------------------------------------
    //		CONSTRUCTOR
    //--------------------------------------------------------------------------

    /**
     * Initializes an Environment with a given width, height and view.
     *
     * @param  widthAmount    the width of each world
     * @param  heightAmount   the height of each world
     */
    public Environment(int widthAmount, int heightAmount) {
        this.width = widthAmount;
        this.height = heightAmount;
        this.worlds = new ArrayList<>();
        this.aItems = new ArrayList<>();
    }

    //--------------------------------------------------------------------------
    //		INSPECTORS
    //--------------------------------------------------------------------------

    /**
     * Returns all worlds the environment had loaded.
     *
     * @return    A collection containing all worlds.
     */
    public Collection<World<?>> getWorlds() {
        return worlds;
    }

    /**
     * Returns a perception for the active item with the given ID, containing
     * representations of the items the object sees.
     *
     * @param  aItemID  the ID of the active item that requires the perception
     * @return          a perception for the ActiveItem with ID <code>aItemID</code>
     */
    public Perception getPerception(ActiveItemID aItemID) {
        for (ActiveItem<?> activeItem : getActiveItems()) {
            if (activeItem.getID() == aItemID) {
                return pReactor.getPerception(activeItem);
            }
        }
        this.logger.severe("No ActiveItem found by that ID.");
        throw new RuntimeException(String.format("No ActiveItem found by ID %d", aItemID.getID()));
    }

    /**
     * Returns a world from the worlds listed in <code>worlds</code>, that is
     * an instance of a class, named <code>worldClass<code>. If no such world
     * exists, we return <code>null</code>.
     *
     * @param worldClass  The class of which we require an instance
     * @return            A member of <code>worlds</code> that is an instance of
     *                    the <code>worldClass</code> class. Returns
     *                    <code>null</code> if no such member is found.
     */
    @SuppressWarnings("unchecked")
    public <T extends World<?>> T getWorld(Class<T> worldClass) {
        for (World<?> w : worlds) {
            if (w.getClass() == worldClass) {
                return (T) w;
            }
        }

        throw new RuntimeException("Could not find world class: " + worldClass.getName());
    }

    /**
     * Gets the amount of agents currently in the agentWorld of this
     * Environment.
     *
     * @return the number of agents currently in agentWorld
     */
    public int getNbAgents() {
        return getAgentWorld().getNbAgents();
    }


    /**
     * Retrieve a list of all agent Ids present in this environment.
     * 
     * @return A list of integers representing the ids of all agents.
     */
    public List<ActiveItemID> getAgentIds() {
        return this.getAgentWorld().getAgents().stream()
            .map(Agent::getID)
            .collect(Collectors.toList());
    }

    /**
     * Returns all items on the (x,y)-position of all worlds.
     *
     * @param x  the x-coordinate
     * @param y  the y-coordinate
     * @return   a list with of Items that stand on the specified coordinate
     */
    public List<Item<?>> getItemsOnPos(int x, int y) {
        if (x < getWidth() && y < getHeight()) {
            return getWorlds().stream().map(w -> w.getItem(x, y)).collect(Collectors.toCollection(ArrayList::new));
        } else {
            this.logger.severe(String.format("Index out of bounds: %d %d.", x, y));
            throw new RuntimeException(String.format("Index out of bounds: %d %d", x, y));
        }
    }

    /**
     * Test whether the (x,y)-coordinate is empty.
     *
     * @param x  the x-coordinate
     * @param y  the y-coordinate
     * @return   (getItemsOnPos(x, y) == null)
     */
    public boolean isFreePos(int x, int y) {
        return getWorlds().stream().allMatch(w -> w.getItem(x, y) == null);
    }

    /**
     * Returns the number of worlds in this Environment.
     *
     * @return  getWorlds().size()
     */
    public int getNbWorlds() {
        return getWorlds().size();
    }

    //--------------------------------------------------------------------------
    //		MUTATORS
    //--------------------------------------------------------------------------

    /**
     * Creates the various components of this environment
     *
     * @post   all selected worlds are loaded
     * @post   a new clock is created
     * @post   a new reactor is created with a reference to the clock
     * @post   a new perceptionReactor is created with a reference to this environment
     * @post   a new postalService is created with a reference to the agentImplementations module
     * @post   a new EOPHandler is created
     * @post   a new collector is created with references to the agentImplementations module,
     *          the EOPHandler, the reactor and the postalService
     */
    public void createEnvironment(ApplicationRunner applicationRunner, EventBus eventBus) {
        this.logger.fine("Start creating the environment");

        clock = new Clock();
        this.logger.fine("clock set");

        reactor = new Reactor(this, applicationRunner, eventBus);
        this.logger.fine("reactor set");

        pReactor = new PerceptionReactor(this);
        this.logger.fine("perceptionReactor set");

        postalService = new PostalService(getAgentImplementations(), eventBus);
        this.logger.fine("postalService set");

        eopHandler = new EOPHandler();
        this.logger.fine("eopHandler set");

        collector = new Collector(getAgentImplementations(), eopHandler,
                                  reactor, postalService);
        this.logger.fine("collector set");
    }

    /**
     * Clears all items on the (x,y)-coordinate.
     *
     * @param x  the x-coordinate
     * @param y  the y-coordinate
     * @post     isFreePos(x, y)
     */
    public void free(int x, int y) {
        getWorlds().stream().filter(w -> w.getItem(x, y) != null)
                .forEach(w -> w.free(x, y));
    }

    /**
     * removes all items on the (x,y)-coordinate.
     *
     * @param x  the x-coordinate
     * @param y  the y-coordinate
     * @post     isFreePos(x, y)
     */
    public void remove(int x, int y) {
        getWorlds().stream().filter(w -> w.getItem(x, y) != null)
                .forEach(w -> w.free(x, y));

        List<ActiveItem<?>> toDelete = new ArrayList<>();

        for (ActiveItem<?> item : this.getActiveItems()) {
            if (item.getX() == x && item.getY() == y) {
                toDelete.add(item);
            }
        }

        toDelete.forEach(this::removeActiveItem);
    }


    /**
     * Adds a world to this Environment.
     *
     * @param world        the world to add
     */
    public <T extends World<?>> void addWorld(T world) {
        worlds.add(world);
    }

    /**
     * Puts an outcome in the collector's in-buffer. If there's anything in
     * that buffer, the collector-thread starts processing it.
     *
     * @param outcome  the outcome to put in the collector's buffer
     */
    public void collectOutcome(Outcome outcome) {
        collector.collectOutcome(outcome);
    }


    /**
     * Tells the collector to print the sphereSet
     */
    public void printSphereSet() { // only for testing
        collector.printSphereSet();
    }

    public void finish() {
        reactor.finish();
        eopHandler.finish();
        collector.finish();
        postalService.finish();
    }

    //--------------------------------------------------------------------------
    //		GETTERS & SETTERS
    //--------------------------------------------------------------------------

    /**
     * Gets the value of agentImpl
     *
     * @return the value of agentImpl
     */
    public ActiveItemContainer getAgentImplementations() {
        return this.agentImpl;
    }

    /**
     * Returns the AgentWorld of this Environment.
     */
    public AgentWorld getAgentWorld() {
        return this.getWorld(AgentWorld.class);
    }

    /**
     * Returns the PacketWorld of this Environment.
     */
    public PacketWorld getPacketWorld() {
        return this.getWorld(PacketWorld.class);
    }


    /**
     * Returns the DestinationWorld of this Environment.
     */
    public DestinationWorld getDestinationWorld() {
        return this.getWorld(DestinationWorld.class);
    }
    
    
    /**
     * Returns the WallWorld of this Environment.
     */
    public WallWorld getWallWorld() {
        return this.getWorld(WallWorld.class);
    }



    /**
     * Returns the EnergyStationWorld of this Environment.
     */
    public EnergyStationWorld getEnergyStationWorld() {
        return this.getWorld(EnergyStationWorld.class);
    }

    /**
     * Returns the ConveyorWorld of this Environment.
     */
    public ConveyorWorld getConveyorWorld() {
        return this.getWorld(ConveyorWorld.class);
    }

    /**
     * Returns the FlagWorld of this Environment.
     */
    public FlagWorld getFlagWorld() {
        return this.getWorld(FlagWorld.class);
    }


    /**
     * Returns the CrumbWorld of this Environment.
     */
    public CrumbWorld getCrumbWorld() {
        return this.getWorld(CrumbWorld.class);
    }


    /**
     * Returns the PheromoneWorld of this Environment.
     */
    public PheromoneWorld getPheromoneWorld() {
        return this.getWorld(PheromoneWorld.class);
    }

    /**
     * Returns the PacketGeneratorWorld of this Environment.
     */
    public PacketGeneratorWorld getPacketGeneratorWorld() {
        return this.getWorld(PacketGeneratorWorld.class);
    }

    /**
     * Gets the value of reactor
     * @return the value of reactor
     */
    protected Reactor getReactor() {
        return this.reactor;
    }

    /**
     * Gets the height of each world in this Environment.
     * @return This Environment's size
     */
    public int getHeight() {
        return this.height;
    }

    /**
     * Gets the width of each world in this Environment.
     * @return This Environment's size
     */
    public int getWidth() {
        return this.width;
    }

    /**
     * Gets the time of this Environment's clock
     * @return clock.getTime()
     */
    public int getTime() {
        return clock.getTime();
    }

    /**
     * Gets the clock of this Environment
     * @return This Environment's clock
     */
    public Clock getClock() {
        return this.clock;
    }


    /**
     * Sets the value of agentImpl
     * @param agentImplementations The new agentImplementations value
     */
    public void setAgentImplementations(ActiveItemContainer
                                        agentImplementations) {
        this.agentImpl = agentImplementations;
    }

    /**
     * Sets the value of reactor
     * @param aReactor The new reactor value
     */
    protected void setReactor(Reactor aReactor) {
        this.reactor = aReactor;
    }

    /**
     * Sets the list of all worlds in this Environment
     * @param worlds The new worlds list
     */
    protected void setWorlds(List<World<?>> worlds) {
        this.worlds = worlds;
    }

    /**
     * Add an active item to this Environment
     * @param aItem the ActiveItem to add
     */
    public void addActiveItem(ActiveItem<?> aItem) {
        aItems.add(aItem);
    }

    /**
     * Get all active items in this Environment
     * @return an ArrayList of ActiveItems
     */
    public Collection<ActiveItem<?>> getActiveItems() {
        return new ArrayList<>(aItems);
    }

    /**
     * Returns the IDs of all the Active Items in the Environment.
     * @return An array containing the ID's of all the ActiveItems in this
     *         Environment.
     */
    public List<ActiveItemID> getActiveItemIDs() {
        return this.getActiveItems().stream()
            .map(ActiveItem::getID)
            .collect(Collectors.toList());
    }


    public ActiveItem<?> getActiveItem(ActiveItemID id) {
        return this.getActiveItems().stream()
            .filter(i -> i.getID() == id)
            .findFirst().orElseThrow();
    }

    public void removeActiveItem(ActiveItem<?> aItem) {
        aItems.remove(aItem);
    }

    public static int chebyshevDistance(Coordinate pos1, Coordinate pos2) {
        return Environment.chebyshevDistance(pos1.getX(), pos1.getY(), pos2.getX(), pos2.getY());
    }


    public static int chebyshevDistance(int x1, int y1, int x2, int y2) {
        return Math.max(Math.abs(x2 - x1), Math.abs(y2 - y1));
    }
}
