
package environment.world.generator;

import java.awt.Color;
import java.util.LinkedList;
import java.util.Queue;

import environment.ActiveImp;
import environment.ActiveItem;
import environment.ActiveItemID;
import environment.Environment;
import environment.world.packet.Packet;
import gui.video.Drawer;
import synchronizer.Synchronization;
import util.FixedRateGenerator;
import util.Generator;
import util.MyColor;
import util.RandomGenerator;

public class PacketGenerator extends ActiveItem<PacketGeneratorRep> {

    private final Color color;
    private final Queue<Packet> packetQueue;
    private final Generator generator;
    private int threshold;
    

    public PacketGenerator(int x, int y, ActiveItemID id, Color color, Generator g, int threshold) {
        super(x, y, 0, id);
        this.color = color;
        this.packetQueue = new LinkedList<>();
        this.generator = g;
        this.threshold = threshold;
    }


    /**
     * Constructor intended for environment configuration files.
     * @param x The x coordinate
     * @param y The y coordinate
     * @param id The ID of the PacketGenerator.
     * @param color The color of the PacketGenerator represented as a String.
     * @param frequency The frequency with which the PacketGenerator should generate packets
     *                  (denoted in amount of cycles).
     */
    public PacketGenerator(int x, int y, int id, String color, int frequency, int threshold) {
        this(x, y, new ActiveItemID(id, ActiveItemID.ActionPriority.GENERATOR), 
            MyColor.getColor(color), new FixedRateGenerator(frequency), threshold);
    }

    /**
     * Constructor intended for environment configuration files.
     * @param x The x coordinate
     * @param y The y coordinate
     * @param id The ID of the PacketGenerator.
     * @param color The color of the PacketGenerator represented as a String.
     * @param chance The chance with which the PacketGenerator should generate packets
     *                  (denoted in a probability to generate).
     */
    public PacketGenerator(int x, int y, int id, String color, double chance, int threshold) {
        this(x, y, new ActiveItemID(id, ActiveItemID.ActionPriority.GENERATOR), 
            color, chance, threshold);
    }



    /**
     * Constructor intended for environment configuration files.
     * @param x The x coordinate
     * @param y The y coordinate
     * @param id The ID of the PacketGenerator.
     * @param color The color of the PacketGenerator represented as a String.
     * @param frequency The frequency with which the PacketGenerator should generate packets
     *                  (denoted in amount of cycles).
     */
    public PacketGenerator(int x, int y, ActiveItemID id, String color, int frequency, int threshold) {
        this(x, y, id, MyColor.getColor(color), new FixedRateGenerator(frequency), threshold);
    }

    /**
     * Constructor intended for environment configuration files.
     * @param x The x coordinate
     * @param y The y coordinate
     * @param id The ID of the PacketGenerator.
     * @param color The color of the PacketGenerator represented as a String.
     * @param chance The chance that the PacketGenerator should generate a packet each cycle.
     */
    public PacketGenerator(int x, int y, ActiveItemID id, String color, double chance, int threshold) {
        this(x, y, id, MyColor.getColor(color), new RandomGenerator(chance), threshold);
    }

    @Override
    public PacketGeneratorRep getRepresentation() {
        return new PacketGeneratorRep(this.getX(), this.getY(), color, packetQueue.size());
    }

    @Override
    public void draw(Drawer drawer) {
        drawer.drawPacketGenerator(this);        
    }


    public synchronized int getAmtPacketsInBuffer() {
        return this.packetQueue.size();
    }

    public synchronized Packet getFirstAvailablePacket() {
        return this.packetQueue.poll();
    }


    public void generatePacket() {
        if (this.threshold == 0) {
            throw new IllegalStateException("The packet generator cannot generate more packets than the initially specified threshold");
        }

        packetQueue.add(new Packet(this.getX(), this.getY(), color));
        this.threshold--;
    }

    public Color getColor() {
        return color;
    }

    public boolean shouldGenerate() {
        return this.threshold > 0 && this.generator.shouldGenerate();
    }

    public boolean hasHitThreshold() {
        return this.threshold == 0;
    }

    public int getPacketsLeftToGenerate() {
        return this.threshold;
    }
    


    @Override
    public ActiveImp generateImplementation(Environment env, Synchronization synchronizer) {
        PacketGeneratorImp imp = new PacketGeneratorImp(this.getID(), this, env);
        imp.setEnvironment(env);
        imp.setSynchronizer(synchronizer);
        return imp;
    }

    public String generateEnvironmentString() {
        return String.format("%s\n", this.getClass().getName()) +
                String.format("nbArgs %d\n", 6) +
                String.format("Integer %d\n", this.getX()) +
                String.format("Integer %d\n", this.getY()) +
                String.format("Integer %d\n", this.getID().getID()) +
                String.format("String \"%s\"\n", MyColor.getName(this.color)) +
                String.format("%s\n", this.generator.generateEnvironmentString()) +
                String.format("Integer %d\n", this.threshold);
    }
}
