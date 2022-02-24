package environment.world.generator;

import java.awt.Color;

import environment.Representation;

public class PacketGeneratorRep extends Representation {
    
    private final Color color;
    private final int packetsInBuffer;


    protected PacketGeneratorRep(int x, int y, Color color, int bufferAmt) {
        super(x, y);
        this.color = color;
        this.packetsInBuffer = bufferAmt;
    }

    public int getPacketsInBuffer() {
        return this.packetsInBuffer;
    }


    public Color getColor() {
        return this.color;
    }


    @Override
    public char getTypeChar() {
        return 'P';
    }

    @Override
    public boolean isWalkable() {
        return false;
    }
}
