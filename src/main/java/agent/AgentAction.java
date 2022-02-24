package agent;

import java.awt.Color;

import environment.CellPerception;



public interface AgentAction {
    
    /**
     * Do nothing, skip this turn.
     */
    void skip();


    /**
     * Take a step to the give coordinate.
     *
     * @param x  The x coordinate of the area to step to.
     * @param y  The y coordinate of the area to step to.
     */
    void step(int x, int y);


    /**
     * Put a packet down on the given Coordinate. If the cell contains
     * a destination, the packet will be delivered. If the cell is empty,
     * the packet will be put down.
     *
     * @param x  The x coordinate of the target area.
     * @param y  The y coordinate of the target area.
     */
    void putPacket(int x, int y);


    /**
     * Pick up a packet from the given Coordinate.
     *
     * @param x  The x coordinate of the area of the packet.
     * @param y  The y coordinate of the area of the packet.
     * @throws RuntimeException  if no packet is present on the specified coordinate.
     */
    void pickPacket(int x, int y);


    /**
     * Steal a packet from another agent at the given coordinate.
     *
     * @param x  The x coordinate of the carrier of the packet.
     * @param y  The y coordinate of the carrier of the packet.
     * @throws RuntimeException  if no packet is present on the specified coordinate.
     */
    void stealPacket(int x, int y);


    /**
     * Put a pheromone in the environment at the given coordinate and lifetime.
     *
     * @param x         The x coordinate of the target area.
     * @param y         The y coordinate of the target area.
     * @param lifetime  The lifetime for the pheromone (maximum lifetime can be found at {@link environment.world.pheromone.Pheromone#MAX_LIFETIME}).
     */
    void putPheromone(int x, int y, int lifetime);


    /**
     * Put a directed pheromone in the environment at the given coordinate, lifetime and target cell.
     *
     * @param x         The x coordinate of the target area.
     * @param y         The y coordinate of the target area.
     * @param lifetime  The lifetime for the pheromone.
     * @param target    The area the directed pheromone has to point to.
     */
    void putDirectedPheromone(int x, int y, int lifetime, CellPerception target);


    /**
     * Remove a pheromone from the environment at the given coordinate.
     *
     * @param x  The x coordinate of the target area.
     * @param y  The y coordinate of the target area.
     */
    void removePheromone(int x, int y);


    /**
     * Put a flag with the specified Color in the environment on the given coordinate.
     *
     * @param x      The x coordinate of the target area.
     * @param y      The y coordinate of the target area.
     * @param color  The color of the new flag.
     */
    void putFlag(int x, int y, Color color);


    /**
     * Put a colorless flag in the environment at the given coordinate.
     *
     * @param x  the x coordinate of the target area.
     * @param y  the y coordinate of the target area.
     */
    void putFlag(int x, int y);


    /**
     * Put a specified number of crumbs in the environment at the given coordinate.
     *
     * @param x       The x coordinate of the target area.
     * @param y       The y coordinate of the target area.
     * @param number  The number of crumbs to put on target area.
     */
    void putCrumb(int x, int y, int number);


    /**
     * Pick up a specified number of crumbs in the environment from the given coordinate. 
     *
     * @param x       The x coordinate of the target area.
     * @param y       The y coordinate of the target area.
     * @param number  The number of crumbs to get from target area.
     */
    void pickCrumb(int x, int y, int number);
}
