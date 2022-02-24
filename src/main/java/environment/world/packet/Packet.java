package environment.world.packet;

import java.awt.Color;
import java.util.logging.Logger;

import environment.Item;
import gui.video.Drawer;
import util.MyColor;

/**
 * A class for packets with a certain color.
 */

public class Packet extends Item<PacketRep> {

    // The color of the packet
    private final Color color;

    private final Logger logger = Logger.getLogger(Packet.class.getName());


    /**
     * Initializes a new Packet instance
     *
     * @param  x    X-coordinate of the Packet
     * @param  y    Y-coordinate of the Packet
     * @param  col  The Packet's color
     */
    public Packet(int x, int y, Color col) {
        super(x, y);
        this.color = col;
        this.logger.fine(String.format("Packet created at %d %d", x, y));
    }

    /**
     * Initializes a new Packet instance
     *
     * @param  x        X-coordinate of the Packet
     * @param  y        Y-coordinate of the Packet
     * @param  colorStr The packet's color represented by a String
     */
    public Packet(int x, int y, String colorStr) {
        this(x, y, MyColor.getColor(colorStr));
    }

    /**
     * Gets the color of this Packet
     * @return This packet's color
     */
    public Color getColor() {
        return this.color;
    }

    /**
     * Returns a representation of this Packet
     *
     * @return A packet-representation
     */
    @Override
    public PacketRep getRepresentation() {
        return new PacketRep(getX(), getY(), getColor());
    }

    /**
     * Draws this Packet on the GUI.
     *
     * @param drawer The visiting drawer
     */
    public void draw(Drawer drawer) {
        drawer.drawPacket(this);
    }


    public String generateEnvironmentString() {
        return String.format("%s\n", this.getClass().getName()) +
                String.format("nbArgs %d\n", 3) +
                String.format("Integer %d\n", this.getX()) +
                String.format("Integer %d\n", this.getY()) +
                String.format("String \"%s\"\n", MyColor.getName(this.getColor()));
    }

}
