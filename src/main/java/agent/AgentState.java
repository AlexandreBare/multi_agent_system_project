package agent;

import java.awt.Color;
import java.util.Optional;
import java.util.Set;

import agent.behavior.Behavior;
import agent.behavior.BehaviorState;
import environment.CellPerception;
import environment.Coordinate;
import environment.Perception;
import environment.world.packet.Packet;


public interface AgentState {

    /**
     * Get the perception of this agent.
     */
    Perception getPerception();


    /**
     * Returns a CellPerception of the previous area this agent stood on.
     */
    CellPerception getPerceptionLastCell();

    /**
     * Check to see if an agent can see a destination with the specified color.
     *
     * @return {@code true} if this agent sees such a destination, {@code false} otherwise.
     */
    boolean seesDestination(Color color);

    
    /**
     * Check to see if an agent can see any destination.
     *
     * @return {@code true} if this agent sees such a destination, {@code false} otherwise.
     */
    boolean seesDestination();


    /**
     * Find a neighbouring cell that contains a destination of a specific color
     *
     * @param color     The color of the destination we are looking for
     *
     * @return          A cell that contains the matching destination,
     *                  null if no neighbouring cell contains a matching destination
     */
    CellPerception getNeighbouringCellWithDestination(Color color);


    /**
     * Find cells in the agent's perception area that contain a destination of a specific color
     *
     * @param color     The color of the destination we are looking for
     *
     * @return          A set of cells that contain the matching destinations,
     *                  An empty set if no perceivable cell contains a matching destination
     */
    Set<CellPerception> getPerceivableCellsWithDestination(Color color);

    /**
     * Check to see if this agent can see a packet with the specified color.
     *
     * @return {@code true} if this agent can see such a packet, {@code false} otherwise.
     */
    boolean seesPacket(Color color);


    /**
     * Check to see if this agent can see any packet.
     *
     * @return {@code true} if this agent can see such a packet, {@code false} otherwise.
     */
    boolean seesPacket();

    /**
     * Find a neighbouring cell that contains a packet
     *
     * @return          A cell that contains a packet,
     *                  null if no neighbouring cell contains a packet
     */
    CellPerception getNeighbouringCellWithPacket();

    /**
     * Find the set of neighbouring cells that contain a packet
     *
     * @return          A set of neighbouring cells that contain a packet,
     *                  null if no neighbouring cell contains a packet
     */
    Set<CellPerception> getNeighbouringCellsWithPacket();

    /**
     * Find cells in the agent's perception area that contain a packet
     *
     * @return          A set of cells that contain a packet,
     *                  An empty set if no neighbouring cell contains a packet
     */
    Set<CellPerception> getPerceivableCellsWithPacket();

    /**
     * Check if the agent can walk at some specific coordinates
     *
     * @param x     x-coordinate
     * @param y     y-coordinate
     *
     * @return {@code true} if this agent can walk at these coordinates, {@code false} otherwise.
     */
    boolean canWalk(int x, int y);



    
    /**
     * Returns the optional packet this agent is carrying.
     * @return An optional of the packet the agent carries, or an empty optional otherwise.
     */
    Optional<Packet> getCarry();
    

    /**
     * Check if the agent is carrying something.
     * @return {@code true} if the agent carries a packet, {@code false} otherwise.
     */
    boolean hasCarry();


    /**
     * Get the X coordinate of this agent.
     */
    int getX();

    /**
     * Get the Y coordinate of this agent.
     */
    int getY();

    /**
     * Get the coordinates of this agent.
     */
    Coordinate getCoordinates();


    /**
     * Get the name of this agent.
     */
    String getName();


    /**
     * Get the optional color of this agent itself.
     */
    Optional<Color> getColor();


    /**
     * Get the battery state of the agent.
     * @return  The battery state of the agent (from {@link environment.EnergyValues#BATTERY_MIN} to {@link environment.EnergyValues#BATTERY_MAX}).
     */
    int getBatteryState();


    /**
     * Get the current Behavior.
     */
    Behavior getCurrentBehavior();




    /**
     * Print the world as known by the agent (from his memory).
     */
    void prettyPrintWorld();

    /**
     * Adds a memory fragment to this agent (if its memory is not full).
     *
     * @param key     The key associated with the memory fragment
     * @param data    The memory fragment itself
     */
    void addMemoryFragment(String key, String data);

    /**
     * Append at the beginning of a memory fragment from this agent
     * if the memory fragment corresponding to a given key already exists.
     * Otherwise, adds a memory fragment to this agent (if its memory is not full).
     *
     * @param key     The key associated with the memory fragment
     * @param data    The data to append to the memory fragment
     */
    void append2Memory(String key, String data);

    /**
     * Generate a key for the representation present on a given cell (used to standardize memory fragment storage).
     *
     * @param cell      The cell from which the representation key must be generated.
     *
     * @return String   The representation key generated
     */
    String rep2MemoryKey(CellPerception cell);

    /**
     * Generate a memory key for a given representation and a given color (used to standardize memory fragment storage).
     *
     * @param rep       A string of the representation.
     *                  Either DestinationRep.class.toString() or PacketRep.class.toString()
     * @param color     The color of the representation
     *
     * @return String   The representation key generated.
     */
    static String rep2MemoryKey(String rep, String color){
        String key = rep + "_" + color;
        return key;
    }

    /**
     * Retrieve the representation and color from a given memory key (used to standardize memory fragment storage).
     *
     * @param key       The memory key with which a representation is stored
     *
     * @return          An array of 2 string elements: the representation and color corresponding to the key
     */
    static String[] memoryKey2Rep(String key){
        String[] representationAndColor = key.split("_");
        return representationAndColor;
    }

    /**
     * Adds the representation on a given cell to this agent's memory (if its memory is not full).
     *
     * @param cell    The cell from which the representation must be memorized
     */
    void addRep2Memory(CellPerception cell);

    /**
     * Memorizes all representations that the agent sees in his perception area
     */
    void memorizeAllPerceivableRepresentations();

    /**
     * Forget all representations that are not present anymore in the agent's perception area
     * (because a packet was picked up by another agent for example)
     *
     * Note: for now, only remove packets from memory has it is the only movable representation
     */
    void forgetAllUnperceivableRepresentations();

    /**
     * Removes a memory fragment with given key from this agent's memory.
     * @param key  The key of the memory fragment to remove.
     */
    void removeMemoryFragment(String key);

    /**
     * Removes the representation on a given cell from this agent's memory.
     *
     * @param cell    The cell from which the representation must be removed.
     */
    void removeFromMemory(CellPerception cell);

    /**
     * Removes specific coordinates stored at a subkey from this agent's memory.
     *
     * @param cellCoordinates    The cell coordinates from which the representation must be removed.
     * @param subkey             The substring of a key to remove from memory.
     */
    void removeFromMemory(Coordinate cellCoordinates, String subkey);

    /**
     * Get a memory fragment with given key from this agent's memory.
     * @param key  The key of the memory fragment to retrieve.
     */
    String getMemoryFragment(String key);

    /**
     * Get all the keys of stored memory fragments in this agent's memory.
     */
    Set<String> getMemoryFragmentKeys();

    /**
     * Get all the keys of stored memory fragments containing a given substring in this agent's memory.
     *
     * @param subkey    The substring of the memory fragment keys that we are looking for
     *
     * @return          A set of memory fragment keys matching the subkey
     */
    Set<String> getMemoryFragmentKeysContaining(String subkey);

    /**
     * Retrieve the representations and potential colors from all memory fragments that store them
     * and create cell perception instances out of them.
     *
     * @return          A set of cell perceptions build out of their fragments stored in memory
     */
    Set<CellPerception> memory2Cells();

    Set<CellPerception> memory2CellsWithoutPackets();
    /**
     * Retrieve the representations and potential colors from a given memory key and create cell perception instances
     * out of them.
     *
     * @param key       The memory key with which a representation is stored
     *
     * @return          A set of cell perceptions build out from their fragments stored in memory
     */
    Set<CellPerception> memoryKey2Cells(String key);

    /**
     * Get the current number of memory fragments in memory of this agent.
     */
    int getNbMemoryFragments();

    /**
     * Get the maximum number of memory fragments for this agent.
     */
    int getMaxNbMemoryFragments();



    /**
     * Set the behavior state of this agent. This method should, generally speaking, not be used by developers.
     * @param state The behavior state to switch to.
     */
    void setCurrentBehaviorState(BehaviorState state);
}
