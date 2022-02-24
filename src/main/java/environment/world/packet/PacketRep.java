package environment.world.packet;

import java.awt.Color;

import environment.Representation;

/**
 *  A class for representations of packets.
 */
public class PacketRep extends Representation {

    private Color color;

    /**
     * Initializes a new PacketRep instance
     *
     * @param x      X-coordinate of the Packet this representation represents
     * @param y      Y-coordinate of the Packet this representation represents
     * @param aColor Color of the Packet this representation represents
     */
    protected PacketRep(int x, int y, Color aColor) {
        super(x, y);
        setColor(aColor);
    }

    /**
     * Gets the color of the Packet this PacketRep represents
     *
     * @return This PacketRep's color
     */
    public Color getColor() {
        return color;
    }

    /**
     * Sets the color of this PacketRep
     *
     * @param aColor The new color value
     */
    protected void setColor(Color aColor) {
        color = aColor;
    }

    public char getTypeChar() {
        return ('p');
    }

    @Override
    public boolean isWalkable() {
        return false;
    }
}
