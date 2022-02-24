package gui.video;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Stroke;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.UnaryOperator;
import java.util.logging.Logger;

import environment.CellPerception;
import environment.Coordinate;
import environment.EnergyValues;
import environment.Environment;
import environment.world.agent.Agent;
import environment.world.conveyor.Conveyor;
import environment.world.crumb.Crumb;
import environment.world.destination.Destination;
import environment.world.energystation.EnergyStation;
import environment.world.flag.Flag;
import environment.world.generator.PacketGenerator;
import environment.world.gradient.Gradient;
import environment.world.packet.Packet;
import environment.world.pheromone.DirPheromone;
import environment.world.pheromone.Pheromone;
import environment.world.wall.GlassWall;
import environment.world.wall.SolidWall;
import environment.world.wall.Wall;
import environment.world.wall.WallWorld;
import util.Direction;
import util.Pair;

/**
 * Visitor for the drawing of items in the video panel.
 */

@SuppressWarnings("FieldCanBeLocal")
public class ItemDrawer extends Drawer {


    /**
     * The horizontal offset for the drawing area.
     */
    private final int horizontalOffset = 20;

    /**
     * The vertical offset for the drawing area.
     */
    private final int verticalOffset = 20;

    /**
     * The width & height for the drawing area.
     */
    private int w;

    /**
     * The width & height of a cell in the drawing area.
     */
    private int cellWidth;

    /**
     * Draw hair on the agent's head
     */
    private final boolean hairyMode = true;

    private final Logger logger = Logger.getLogger(ItemDrawer.class.getName());
    private Environment environment = null;



    public void init(Graphics g, int width, int height, int envWidth,
                     int envHeight, Color bgColor) {
        super.init(g, width, height, envWidth, envHeight, bgColor);
        int fw = Math.min(getWidth(), getHeight()); //fw = FrameWidth
        int s = Math.max(this.envWidth, this.envHeight);
        w = fw - 2 * horizontalOffset;
        cellWidth = w / s;
    }

    public void setEnvironment(Environment env) {
        this.environment = env;
    }

    public void clear() {
        for (int i = 0; i < envWidth; i++) {
            for (int j = 0; j < envHeight; j++) {
                clearItem(i, j);
            }
        }
    }

    public void clearItem(int i, int j) {
        g.setColor(bgColor);
        g.fillRect(i * cellWidth + horizontalOffset + 1, j * cellWidth + verticalOffset + 1, cellWidth - 2, cellWidth - 2);
    }

    public void drawGrid() {
        g.setColor(Color.black);
        for (int i = 0; i <= envHeight; i++) {
            g.drawLine(horizontalOffset, verticalOffset + cellWidth * i, horizontalOffset + envWidth * cellWidth, verticalOffset + cellWidth * i);
        }
        for (int j = 0; j <= envWidth; j++) {
            g.drawLine(horizontalOffset + cellWidth * j, verticalOffset, horizontalOffset + cellWidth * j, verticalOffset + envHeight * cellWidth);
        }
    }

    public void drawPacket(Packet packet) {
        int i = packet.getX();
        int j = packet.getY();
        Color pc = packet.getColor();

        g.setColor(pc);
        g.fillRect(i * cellWidth + horizontalOffset + cellWidth / 4, j * cellWidth + verticalOffset + cellWidth / 4, cellWidth / 2,
                   cellWidth / 2);
        this.logger.fine("packet drawn");
    }


    private int[] generateXPointsOctagon(double distanceCenter, double center) {
        double[] points = {center - distanceCenter / 2.0, center + distanceCenter / 2.0,
            center + distanceCenter, center + distanceCenter,
            center + distanceCenter / 2.0, center - distanceCenter / 2.0,
            center - distanceCenter, center - distanceCenter};
        return Arrays.stream(points).mapToInt(d -> (int) d).toArray();
    }

    private int[] generateYPointsOctagon(double distanceCenter, double center) {
        double[] points = {center - distanceCenter, center - distanceCenter,
            center - distanceCenter / 2.0, center + distanceCenter / 2.0,
            center + distanceCenter, center + distanceCenter,
            center + distanceCenter / 2.0, center - distanceCenter / 2.0};
        return Arrays.stream(points).mapToInt(d -> (int) d).toArray();
    } 


    public void drawPacketGenerator(PacketGenerator packetGenerator) {
        int startX = packetGenerator.getX() * cellWidth + horizontalOffset;
        int startY = packetGenerator.getY() * cellWidth + verticalOffset;

        Graphics2D g2 = (Graphics2D) g;
        Stroke oldStroke = g2.getStroke();

        g.setColor(Color.black);
        g2.setStroke(new BasicStroke(Math.max(1, cellWidth / 6)));
        g.drawPolygon(generateXPointsOctagon(cellWidth * 15 / 48.0, startX + cellWidth / 2.0),
                generateYPointsOctagon(cellWidth * 15 / 48.0, startY + cellWidth / 2.0), 8);
            
        g2.setStroke(new BasicStroke(Math.max(1, cellWidth / 20)));
        g.setColor(Color.DARK_GRAY);
        if (!packetGenerator.hasHitThreshold() || packetGenerator.getAmtPacketsInBuffer() > 0) {
            g.setColor(packetGenerator.getColor().darker().darker());
        }
        g.drawPolygon(generateXPointsOctagon(cellWidth * 15 / 48.0, startX + cellWidth / 2.0),
                generateYPointsOctagon(cellWidth * 15 / 48.0, startY + cellWidth / 2.0), 8);
            
        g.setColor(Color.black);
        g2.setStroke(oldStroke);
    }

    public void drawAgent(Agent agent) {
        int x = agent.getX();
        int y = agent.getY();
        int hr = horizontalOffset + x * cellWidth;
        int vr = verticalOffset + y * cellWidth;

        agent.getCarry().ifPresent(packet -> {
            Color pc = packet.getColor();
            g.setColor(pc);
            g.fillRect(hr + 8 * cellWidth / 15 + 1, vr + 7 * cellWidth / 15 + 1, 4 * cellWidth / 15 - 1, 4 * cellWidth / 15 - 1);
            g.setColor(Color.black);
            g.drawRect(hr + 8 * cellWidth / 15, vr + 7 * cellWidth / 15, 4 * cellWidth / 15, 4 * cellWidth / 15);
        });

        String aName = agent.getName();

        g.setColor(Color.blue);
        List<LinePoints> edgeList = agent.getActualView();
        g.setColor(Color.blue);

        Graphics2D g2d = (Graphics2D) g;
        var oldStroke = g2d.getStroke();
        g2d.setStroke(new BasicStroke(2));
        for (LinePoints edges : edgeList ) {
            g2d.drawLine( horizontalOffset + cellWidth * ItemDrawer.clamp(edges.start().getX(), 0, envWidth), 
                verticalOffset + cellWidth * ItemDrawer.clamp(edges.start().getY(), 0, envHeight),  
                horizontalOffset + cellWidth * ItemDrawer.clamp(edges.end().getX(), 0, envWidth), 
                verticalOffset + cellWidth * ItemDrawer.clamp(edges.end().getY(), 0, envHeight));
        }

        g2d.setStroke(oldStroke);


        if (EnergyValues.ENERGY_ENABLED) {

            var startBattery = new Pair<>(hr + (9 * cellWidth / 15), vr + (12 * cellWidth / 15));
            var endBattery = new Pair<>(hr + (14 * cellWidth / 15), vr + (14 * cellWidth / 15));

            var batteryPercentage =  agent.getBatteryState() / (float) EnergyValues.BATTERY_MAX;
            var filledBatteryX = (int) ((1 - batteryPercentage) * (endBattery.first - startBattery.first) + startBattery.first);

            // Battery fill (depending on battery percentage of agent)
            float hue = batteryPercentage * 0.34f;
            g.setColor(Color.getHSBColor(hue, 1.0f, 0.8f));
            g.fillRect(filledBatteryX, startBattery.second, endBattery.first - filledBatteryX, endBattery.second - startBattery.second);
            g.setColor(Color.black);

            // Battery hull
            g.drawRect(startBattery.first, startBattery.second, endBattery.first - startBattery.first, endBattery.second - startBattery.second);

            // Top of battery
            var batteryNudge = new Pair<>(hr + (17 * cellWidth / 30), vr + (25 * cellWidth / 30));
            g.fillRect(batteryNudge.first, batteryNudge.second, startBattery.first - batteryNudge.first, cellWidth / 15);

            // Vertical battery 'slices'
            final int SLICES = 3;
            UnaryOperator<Integer> xPos = (Integer i) -> startBattery.first + (int) (i * (endBattery.first - startBattery.first) / ((double) SLICES));
            for (int i = 1; i <= SLICES; i++) {
                g.drawLine(xPos.apply(i),  startBattery.second, xPos.apply(i), endBattery.second);
            }
        }




        // Color of an agent specified if it is restricted in terms of packets it can pick up
        g.setColor(agent.getColor().orElse(Color.black));


        //draw head of agent
	    g.fillOval(hr + 3 * cellWidth / 15, vr + cellWidth / 15, 4 * cellWidth / 15, 4 * cellWidth / 15);

        //draw body of agent
        g.drawRect(hr + 3 * cellWidth / 15, vr + 5 * cellWidth / 15, 4 * cellWidth / 15,
                   5 * cellWidth / 15);
        //fill body of agent                   
        g.fillRect(hr + 3 * cellWidth / 15, vr + 5 * cellWidth / 15, 4 * cellWidth / 15,
                   5 * cellWidth / 15);
 
        //draw right arm of agent
        Polygon ra = new Polygon();
        ra.addPoint(hr + 7 * cellWidth / 15, vr + 5625 * cellWidth / 15000);
        ra.addPoint(hr + 9375 * cellWidth / 15000, vr + 7 * cellWidth / 15);
        ra.addPoint(hr + 8625 * cellWidth / 15000, vr + 7 * cellWidth / 15);
        ra.addPoint(hr + 7 * cellWidth / 15, vr + 6375 * cellWidth / 15000);
        g.fillPolygon (ra);          
                   
        //draw left arm of agent
        Polygon la = new Polygon();
        la.addPoint(hr + 3 * cellWidth / 15, vr + 5625 * cellWidth / 15000);
        la.addPoint(hr + 8 * cellWidth / 15, vr + 8625 * cellWidth / 15000);
        la.addPoint(hr + 8 * cellWidth / 15, vr + 9375 * cellWidth / 15000);
        la.addPoint(hr + 3 * cellWidth / 15, vr + 6375 * cellWidth / 15000);
        g.fillPolygon (la);
                         
        //draw right leg of agent
	    g.drawRect(hr + 55 * cellWidth / 150, vr + 10 * cellWidth / 15, cellWidth / 15, 375 * cellWidth / 1500);
	    g.fillRect(hr + 55 * cellWidth / 150, vr + 10 * cellWidth / 15, cellWidth / 15, 375 * cellWidth / 1500);
	    
	    //draw right foot of agent
        g.drawRect(hr + 55 * cellWidth / 150, vr + 135 * cellWidth / 150, 2 * cellWidth / 15, 5 * cellWidth / 150);
	    g.fillRect(hr + 55 * cellWidth / 150, vr + 135 * cellWidth / 150, 2 * cellWidth / 15, 5 * cellWidth / 150);
                   
        //draw left leg of agent
        g.fillRect(hr + 35 * cellWidth / 150, vr + 10 * cellWidth / 15, cellWidth / 15, 375 * cellWidth / 1500);
        g.drawRect(hr + 35 * cellWidth / 150, vr + 10 * cellWidth / 15, cellWidth / 15, 375 * cellWidth / 1500);
        
        //draw left foot of agent
        g.drawRect(hr + 25 * cellWidth / 150, vr + 135 * cellWidth / 150, 2 * cellWidth / 15, 5 * cellWidth / 150);
        g.fillRect(hr + 25 * cellWidth / 150, vr + 135 * cellWidth / 150, 2 * cellWidth / 15, 5 * cellWidth / 150);
             
        //draw neck of agent
        g.fillRect(hr + 425 * cellWidth / 1500, vr + 45 * cellWidth / 150, 15 * cellWidth / 150, 5 * cellWidth / 150);
        g.drawRect(hr + 425 * cellWidth / 1500, vr + 45 * cellWidth / 150, 15 * cellWidth / 150, 5 * cellWidth / 150);
                
        //draw hair of agent
        if (hairyMode) {
            g.drawLine(hr + 5 * cellWidth / 15, vr + 3 * cellWidth / 15, hr + 3 * cellWidth / 15, vr + cellWidth / 15);
            g.drawLine(hr + 5 * cellWidth / 15, vr + 3 * cellWidth / 15, hr + 4 * cellWidth / 15, vr + 5 * cellWidth / 150);
            g.drawLine(hr + 5 * cellWidth / 15, vr + 3 * cellWidth / 15, hr + 5 * cellWidth / 15, vr + 25 * cellWidth / 1500);
            g.drawLine(hr + 5 * cellWidth / 15, vr + 3 * cellWidth / 15, hr + 6 * cellWidth / 15, vr + 5 * cellWidth / 150);
            g.drawLine(hr + 5 * cellWidth / 15, vr + 3 * cellWidth / 15, hr + 7 * cellWidth / 15, vr + cellWidth / 15);
        }

        g.setColor(Color.black);
        int fontSize = cellWidth / (2 + (int) (aName.length() * 1.25));
        if (fontSize > 30) {
            fontSize = 30;
        } else if (fontSize < 8) {
            fontSize = 8;
        }

        Font font = new Font("Courier", Font.BOLD, fontSize);
        g.setFont(font);
        g.drawString(aName, hr + 9 * cellWidth / 15, vr + 6 * cellWidth / 15);

        this.logger.fine("agent drawn");
    }

    public void drawDestination(Destination dest) {
        int i = dest.getX();
        int j = dest.getY();
        Color dc = dest.getColor();

        g.setColor(dc);
        g.fillOval(i * cellWidth + horizontalOffset + 1 + cellWidth / 8, 
            j * cellWidth + verticalOffset + 1 + cellWidth / 8, 
            1 + 3 * cellWidth / 4, 
            1 + 3 * cellWidth / 4);
        this.logger.fine("destination drawn");
    }

    @Override
    public void drawConveyor(Conveyor conv) {
        int i = conv.getX();
        int j = conv.getY();
        int dirId = conv.getDirection().getId();
        int hr1 = horizontalOffset + i * cellWidth + cellWidth / 4;
        int vr1 = verticalOffset + j * cellWidth + cellWidth / 4;
        int hr2 = (i + 1) * cellWidth + horizontalOffset - cellWidth / 4;
        int vr2 = (j + 1) * cellWidth + verticalOffset - cellWidth / 4;
        int hr3 = horizontalOffset + i * cellWidth + cellWidth / 2;
        int vr3 = verticalOffset + j * cellWidth + cellWidth / 2;
        int squareVert = verticalOffset + j * cellWidth + cellWidth / 8;
        int squareHor = horizontalOffset + i * cellWidth + cellWidth / 8;


        Map<Integer, int[]> x = new HashMap<>();
        Map<Integer, int[]> y = new HashMap<>();

        x.put(Direction.NORTH_ID, new int[]{hr1, hr3, hr2});
        y.put(Direction.NORTH_ID, new int[]{vr2, vr1, vr2});

        x.put(Direction.EAST_ID, new int[]{hr1, hr2, hr1});
        y.put(Direction.EAST_ID, new int[]{vr1, vr3, vr2});

        x.put(Direction.SOUTH_ID, new int[]{hr1, hr3, hr2});
        y.put(Direction.SOUTH_ID, new int[]{vr1, vr2, vr1});

        x.put(Direction.WEST_ID, new int[]{hr2, hr1, hr2});
        y.put(Direction.WEST_ID, new int[]{vr1, vr3, vr2});
        
        g.setColor(Color.black);
        g.fillPolygon(x.get(dirId), y.get(dirId), 3);
        g.setColor(Color.red);
        g.drawRect(squareHor, squareVert, cellWidth * 6 / 8, cellWidth * 6 / 8);
    }

    public void drawFlag(Flag flag) {
        int i = flag.getX();
        int j = flag.getY();
        int hr = horizontalOffset + i * cellWidth;
        int vr = verticalOffset + j * cellWidth;
        Color dc = flag.getColor();
        g.setColor(dc);
        g.drawLine(hr + 6 * cellWidth / 15, vr + 2 * cellWidth / 15, hr + 6 * cellWidth / 15, vr + 14 * cellWidth / 15);
        g.fillRect(hr + 6 * cellWidth / 15, vr + 2 * cellWidth / 15, 4 * cellWidth / 10, 4 * cellWidth / 10);
    }

    public void drawPheromone(Pheromone pheromone) {
        int i = pheromone.getX();
        int j = pheromone.getY();
        int range = bgColor.getGreen();
        int plus = range * pheromone.getLifetime() / 2000;
        if (plus > 255) plus = 255;

        int hr = horizontalOffset + i * cellWidth;
        int vr = verticalOffset + j * cellWidth;
	    g.setColor(new Color(bgColor.getRed(), range - plus, bgColor.getBlue()));
 
        g.fillRect(hr + 1, vr + 1, cellWidth - 2, cellWidth - 2);
    }

    public void drawDirPheromone(DirPheromone pheromone) {
        int i = pheromone.getX();
        int j = pheromone.getY();
        int plus = 5 + pheromone.getLifetime() / 500;
        int newRed =   bgColor.getRed()   + plus / 2;
        if (newRed > 255) newRed = 255;
        int newGreen = bgColor.getGreen() - plus * 2;
        if (newGreen < 0) newGreen = 0;
        int newBlue =  bgColor.getBlue()  - plus * 2;
        if (newBlue < 0) newBlue = 0;

        int hr = horizontalOffset + i * cellWidth;
        int vr = verticalOffset + j * cellWidth;
        g.setColor(new Color(newRed, newGreen, newBlue));
        g.fillRect(hr + 1, vr + 1, cellWidth - 2, cellWidth - 2);

        CellPerception target = pheromone.getTarget();
        if (target != null) {
            int toI = target.getX();
            int toJ = target.getY();
            g.setColor(Color.black);
            g.drawLine(hr + cellWidth / 2, vr + cellWidth / 2, hr + cellWidth * (toI - i + 1) / 2,
                       vr + cellWidth * (toJ - j + 1) / 2);
        }
    }


    public void drawSolidWall(SolidWall solidWall) {
        int i = solidWall.getX();
        int j = solidWall.getY();

        // North-West North-East South-East South-West
        boolean[] connected = new boolean[4];

        if (this.environment == null) {
            // Should not reach here, but just in case
            this.logger.warning("The environment field in the ItemDrawer is null.");
            connected[0] = true;
            connected[1] = true;
            connected[2] = true;
            connected[3] = true;
        } else {
            WallWorld w = this.environment.getWallWorld();
            if (!w.inBounds(i, j - 1) || w.getItem(i, j - 1) != null) {
                // Out of border or wall to the north --> both top corners are connected
                connected[0] = true;
                connected[1] = true;
            }

            if (!w.inBounds(i + 1, j) || w.getItem(i + 1, j) != null) {
                // similar, but east side
                connected[1] = true;
                connected[2] = true;
            }

            if (!w.inBounds(i, j + 1) || w.getItem(i, j + 1) != null) {
                // similar, but south side
                connected[2] = true;
                connected[3] = true;
            }

            if (!w.inBounds(i - 1, j) || w.getItem(i - 1, j) != null) {
                // similar, but west side
                connected[3] = true;
                connected[0] = true;
            }
        }
        

        int hr = horizontalOffset + i * cellWidth;
        int vr = verticalOffset + j * cellWidth;

        g.setColor(new Color(200, 20, 0));
        g.fillRoundRect(hr + 1, vr + 1, cellWidth - 1, cellWidth - 1, cellWidth / 2, cellWidth / 2);
        
        if (connected[0]) {
            g.fillRect(hr + 1, vr + 1, (int) Math.floor((cellWidth - 1) / 2.0), (int) Math.floor((cellWidth - 1) / 2.0));
        }
        if (connected[1]) {
            g.fillRect(hr + 1 + (int) Math.floor((cellWidth - 1) / 2.0), vr + 1, (int) Math.ceil((cellWidth - 1) / 2.0), (int) Math.floor((cellWidth - 1) / 2.0));
        }
        if (connected[2]) {
            g.fillRect(hr + 1 + (int) Math.floor((cellWidth - 1) / 2.0), vr + 1 + (int) Math.floor((cellWidth - 1) / 2.0), (int) Math.ceil((cellWidth - 1) / 2.0), (int) Math.ceil((cellWidth - 1) / 2.0) );
        }
        if (connected[3]) {
            g.fillRect(hr + 1, vr + 1 + (int) Math.floor((cellWidth - 1) / 2.0), (int) Math.floor((cellWidth - 1) / 2.0), (int) Math.ceil((cellWidth - 1) / 2.0));
        }
        

        g.setColor(Color.white);
        g.drawLine(hr + 1, vr + cellWidth / 2, hr + cellWidth - 1, vr + cellWidth / 2);
        g.drawLine(hr + cellWidth / 3, vr + cellWidth / 2, hr + cellWidth / 3, vr + cellWidth - 1);
        g.drawLine(hr + cellWidth * 2 / 3, vr + 1, hr + cellWidth * 2 / 3, vr + cellWidth / 2);
    }



    // Helper method to determine neighbors of a glass wall object
    private List<Optional<Wall>> getNeighborGlassWalls(int x, int y, WallWorld w) {
        // Aware that returning a list of optionals is not optimal
        List<Optional<Wall>> neighbors = new ArrayList<>();

        // Start north, go clockwise
        neighbors.add(w.inBounds(x, y - 1) ? Optional.ofNullable(w.getItem(x, y - 1)) : null);
        neighbors.add(w.inBounds(x + 1, y - 1) ? Optional.ofNullable(w.getItem(x + 1, y - 1)) : null);
        neighbors.add(w.inBounds(x + 1, y) ? Optional.ofNullable(w.getItem(x + 1, y)) : null);
        neighbors.add(w.inBounds(x + 1, y + 1) ? Optional.ofNullable(w.getItem(x + 1, y + 1)) : null);
        neighbors.add(w.inBounds(x, y + 1) ? Optional.ofNullable(w.getItem(x, y + 1)) : null);
        neighbors.add(w.inBounds(x - 1, y + 1) ? Optional.ofNullable(w.getItem(x - 1, y + 1)) : null);
        neighbors.add(w.inBounds(x - 1, y) ? Optional.ofNullable(w.getItem(x - 1, y)) : null);
        neighbors.add(w.inBounds(x - 1, y - 1) ? Optional.ofNullable(w.getItem(x - 1, y - 1)) : null);

        return neighbors;
    }

    @SuppressWarnings("OptionalAssignedToNull")
    @Override
    public void drawGlassWall(GlassWall glassWall) {
        int x = glassWall.getX();
        int y = glassWall.getY();

        int baseX = x * cellWidth + horizontalOffset;
        int baseY = y * cellWidth + verticalOffset;


        // The corners of the glass wall
        double margin = 0.1;
        
        // North-west corner
        Coordinate cornerNW = new Coordinate((int) (baseX + margin * cellWidth), (int) (baseY + margin * cellWidth));
        Coordinate cornerSE = new Coordinate((int) (baseX + (1.0 - margin) * cellWidth), (int) (baseY + (1.0 - margin) * cellWidth));
        // Coordinate[] corners = new Coordinate[4];

        // corners[0] = new Coordinate((int) (baseX + margin * cellWidth), (int) (baseY + margin * cellWidth));
        // corners[1] = new Coordinate((int) (baseX + (1.0 - margin) * cellWidth), (int) (baseY + margin * cellWidth));
        // corners[2] = new Coordinate((int) (baseX + (1.0 - margin) * cellWidth), (int) (baseY + (1.0 - margin) * cellWidth));
        // corners[3] = new Coordinate((int) (baseX + margin * cellWidth), (int) (baseY + (1.0 - margin) * cellWidth));


        if (this.environment == null) {
            // Should not reach here, but just in case
            this.logger.warning("The environment field in the ItemDrawer is null.");

            g.setColor(new Color(201, 229, 245, 150));
            g.fillRect(cornerNW.getX(), cornerNW.getY(), cornerSE.getX() - cornerNW.getX(), cornerSE.getY() - cornerNW.getY());

            g.setColor(Color.black);
            Graphics2D g2 = (Graphics2D) g;
            g2.setStroke(new BasicStroke(2));
            g.drawRect(cornerNW.getX(), cornerNW.getY(), cornerSE.getX() - cornerNW.getX(), cornerSE.getY() - cornerNW.getY());

        } else {
            WallWorld w = this.environment.getWallWorld();

            // Determine if corners should be 'solid' (clockwise, start in NW)
            boolean[] solid = new boolean[4];
            
            var neighbors = this.getNeighborGlassWalls(x, y, w);
            
            solid[0] = (neighbors.get(0) == null || neighbors.get(0).isPresent()) 
                    && (neighbors.get(6) == null || neighbors.get(6).isPresent())
                    && (neighbors.get(7) == null || neighbors.get(7).isPresent());

            solid[1] = (neighbors.get(0) == null || neighbors.get(0).isPresent()) 
                    && (neighbors.get(1) == null || neighbors.get(1).isPresent())
                    && (neighbors.get(2) == null || neighbors.get(2).isPresent());

            solid[2] = (neighbors.get(2) == null || neighbors.get(2).isPresent()) 
                    && (neighbors.get(3) == null || neighbors.get(3).isPresent())
                    && (neighbors.get(4) == null || neighbors.get(4).isPresent());

            solid[3] = (neighbors.get(4) == null || neighbors.get(4).isPresent()) 
                    && (neighbors.get(5) == null || neighbors.get(5).isPresent())
                    && (neighbors.get(6) == null || neighbors.get(6).isPresent());


            g.setColor(new Color(201, 229, 245, 150));

            List<Coordinate> pointsFill = new ArrayList<>();
            List<Pair<Coordinate, Coordinate>> borderLines = new ArrayList<>();

            if (solid[0]) {
                pointsFill.add(new Coordinate(baseX + 1, baseY + 1));
            } else {
                Coordinate first = new Coordinate(baseX + 1, (int) (baseY + margin * cellWidth));
                Coordinate middle = cornerNW;
                Coordinate last = new Coordinate((int) (baseX + margin * cellWidth), baseY + 1);
                // West neighbor
                if (neighbors.get(6) == null || neighbors.get(6).isPresent()) {
                    pointsFill.add(first);

                    borderLines.add(new Pair<>(first, middle));
                }

                pointsFill.add(middle);

                // North neighbor
                if (neighbors.get(0) == null || neighbors.get(0).isPresent()) {
                    pointsFill.add(last);

                    borderLines.add(new Pair<>(middle, last));
                } else {
                    borderLines.add(new Pair<>(middle, new Coordinate(cornerSE.getX(), cornerNW.getY())));
                }
            }

            if (solid[1]) {
                pointsFill.add(new Coordinate(baseX + cellWidth - 1, baseY + 1));
            } else {
                Coordinate first = new Coordinate((int) (baseX + (1.0 - margin) * cellWidth), baseY + 1);
                Coordinate middle = new Coordinate(cornerSE.getX(), cornerNW.getY());
                Coordinate last = new Coordinate(baseX + cellWidth - 1, (int) (baseY + margin * cellWidth));
                // North neighbor
                if (neighbors.get(0) == null || neighbors.get(0).isPresent()) {
                    pointsFill.add(first);

                    borderLines.add(new Pair<>(first, middle));
                }

                pointsFill.add(middle);

                // East neighbor
                if (neighbors.get(2) == null || neighbors.get(2).isPresent()) {
                    pointsFill.add(last);

                    borderLines.add(new Pair<>(middle, last));
                } else {
                    borderLines.add(new Pair<>(middle, cornerSE));
                }
            }

            if (solid[2]) {
                pointsFill.add(new Coordinate(baseX + cellWidth - 1, baseY + cellWidth - 1));
            } else {
                Coordinate first = new Coordinate(baseX + cellWidth - 1, (int) (baseY + (1.0 - margin) * cellWidth));
                Coordinate middle = cornerSE;
                Coordinate last = new Coordinate((int) (baseX + (1.0 - margin) * cellWidth), baseY + cellWidth - 1);
                // East neighbor
                if (neighbors.get(2) == null || neighbors.get(2).isPresent()) {
                    pointsFill.add(first);

                    borderLines.add(new Pair<>(first, middle));
                }

                pointsFill.add(middle);

                // South neighbor
                if (neighbors.get(4) == null || neighbors.get(4).isPresent()) {
                    pointsFill.add(last);

                    borderLines.add(new Pair<>(middle, last));
                } else {
                    borderLines.add(new Pair<>(middle, new Coordinate(cornerNW.getX(), cornerSE.getY())));
                }
            }

            if (solid[3]) {
                pointsFill.add(new Coordinate(baseX + 1, baseY + cellWidth - 1));
            } else {
                Coordinate first = new Coordinate((int) (baseX + margin * cellWidth), baseY + cellWidth - 1);
                Coordinate middle = new Coordinate(cornerNW.getX(), cornerSE.getY());
                Coordinate last = new Coordinate(baseX + 1, (int) (baseY + (1.0 - margin) * cellWidth));

                // South neighbor
                if (neighbors.get(4) == null || neighbors.get(4).isPresent()) {
                    pointsFill.add(first);

                    borderLines.add(new Pair<>(first, middle));
                }

                pointsFill.add(middle);

                // West neighbor
                if (neighbors.get(6) == null || neighbors.get(6).isPresent()) {
                    pointsFill.add(last);
                    borderLines.add(new Pair<>(middle, last));
                } else {
                    borderLines.add(new Pair<>(middle, cornerNW));
                }
            }
            g.fillPolygon(pointsFill.stream().mapToInt(Coordinate::getFirst).toArray(), 
                    pointsFill.stream().mapToInt(Coordinate::getSecond).toArray(), 
                    pointsFill.size());

            
            
            g.setColor(Color.black);
            Graphics2D g2 = (Graphics2D) g;
            var oldStroke = g2.getStroke();
            g2.setStroke(new BasicStroke(2));
            borderLines.forEach(l -> g.drawLine(l.first.getX(), l.first.getY(), l.second.getX(), l.second.getY()));
            // g.drawPolygon(pointsFill.stream().mapToInt(Coordinate::getFirst).toArray(), pointsFill.stream().mapToInt(Coordinate::getSecond).toArray(), pointsFill.size());

            g2.setStroke(oldStroke);
            
            // if (!w.inBounds(x, y - 1) || w.getItem(x, y - 1) != null) {
            //     // Out of border or wall to the north --> both top corners are connected
            //     cornerNW = new Coordinate(cornerNW.getX(), baseY + 1);
            // }

            // if (!w.inBounds(x + 1, y) || w.getItem(x + 1, y) != null) {
            //     // similar, but east side
            //     cornerSE = new Coordinate(baseX + cellWidth - 1, cornerSE.getY());
            // }

            // if (!w.inBounds(x, y + 1) || w.getItem(x, y + 1) != null) {
            //     // similar, but south side
            //     cornerSE = new Coordinate(cornerSE.getX(), baseY + cellWidth - 1);
            // }
            
            // if (!w.inBounds(x - 1, y) || w.getItem(x - 1, y) != null) {
            //     // similar, but west side
            //     cornerNW = new Coordinate(baseX + 1, cornerNW.getY());
            // }


            // g.fillRect(cornerNW.getX(), cornerNW.getY(), cornerSE.getX() - cornerNW.getX(), cornerSE.getY() - cornerNW.getY());
            
            // g.setColor(Color.black);
            // Graphics2D g2 = (Graphics2D) g;
            // var oldStroke = g2.getStroke();
            // g2.setStroke(new BasicStroke(2));

            // Little redundant to do checks here again, could optimize in future
            // if (!w.inBounds(x, y - 1) || w.getItem(x, y - 1) == null) {
            //     g.drawLine(cornerNW.getX(), cornerNW.getY(), cornerSE.getX(), cornerNW.getY());
            // }
            // if (!w.inBounds(x + 1, y) || w.getItem(x + 1, y) == null) {
            //     g.drawLine(cornerSE.getX(), cornerNW.getY(), cornerSE.getX(), cornerSE.getY());
            // }
            // if (!w.inBounds(x, y + 1) || w.getItem(x, y + 1) == null) {
            //     g.drawLine(cornerSE.getX(), cornerSE.getY(), cornerNW.getX(), cornerSE.getY());
            // }
            // if (!w.inBounds(x - 1, y) || w.getItem(x - 1, y) == null) {
            //     g.drawLine(cornerNW.getX(), cornerSE.getY(), cornerNW.getX(), cornerNW.getY());
            // }
            // // g.drawRect(cornerNW.getX(), cornerNW.getY(), cornerSE.getX() - cornerNW.getX(), cornerSE.getY() - cornerNW.getY());
            // g2.setStroke(oldStroke);
            

            g.setColor(Color.gray);
            // draw diagonal lines
            for (var quadrant : List.of(new Pair<>(0.0, 0.0), new Pair<>(0.0, 1.0), new Pair<>(1.0, 0.0), new Pair<>(1.0, 1.0), new Pair<>(0.5, 0.5))) {
                int startX = (int) (baseX + quadrant.getFirst() * cellWidth / 2.0);
                int startY = (int) (baseY + quadrant.getSecond() * cellWidth / 2.0);

                // middle line
                g.drawLine(startX + (int) (cellWidth * (1.0 / 6.0)), startY + (int) (cellWidth * (2.0 / 6.0)), 
                        startX + (int) (cellWidth * (2.0 / 6.0)), startY + (int) (cellWidth * (1.0 / 6.0)));
                
                // sidelines
                g.drawLine(startX + (int) (cellWidth * (2.0 / 10.0) - cellWidth / 18.0),
                        startY + (int) (cellWidth * (3.0 / 10.0) - cellWidth / 18.0), 
                        startX + (int) (cellWidth * (3.0 / 10.0) - cellWidth / 18.0),
                        startY + (int) (cellWidth * (2.0 / 10.0) - cellWidth / 18.0));
                g.drawLine(startX + (int) (cellWidth * (2.0 / 10.0) + cellWidth / 18.0),
                        startY + (int) (cellWidth * (3.0 / 10.0) + cellWidth / 18.0), 
                        startX + (int) (cellWidth * (3.0 / 10.0) + cellWidth / 18.0),
                        startY + (int) (cellWidth * (2.0 / 10.0) + cellWidth / 18.0));
            }
        }
    }

    @Override
    public void drawCrumb(Crumb crumb) {}

    @Override
    public void drawGradient(Gradient gradient) {
        int i = gradient.getX();
        int j = gradient.getY();
        int hr = horizontalOffset + i * cellWidth;
        int vr = verticalOffset + j * cellWidth;

        //int fontSize = 8;
        int fontSize = cellWidth / 3;
        if (fontSize > 24) {
            fontSize = 24;
        } else if (fontSize < 8) {
            fontSize = 8;
        }
        Font font = new Font("Courier", Font.PLAIN, fontSize);
        g.setFont(font);
        g.setColor(Color.black);
        g.drawString(String.valueOf(gradient.getValue()), hr + cellWidth / 15, vr + 13 * cellWidth / 15);
    }

    public void drawEnergyStation(EnergyStation station) {
        int x = station.getX();
        int y = station.getY();
        g.setColor(Color.yellow);
        g.fillRect(x * cellWidth + horizontalOffset + cellWidth / 4, y * cellWidth + verticalOffset + cellWidth / 6, cellWidth / 2 - 1, cellWidth / 6);
        g.setColor(Color.black);
        g.drawRect(x * cellWidth + horizontalOffset + cellWidth / 4, y * cellWidth + verticalOffset + cellWidth / 6, cellWidth / 2 - 1, cellWidth / 6);
        g.fillRect(x * cellWidth + horizontalOffset + cellWidth / 4, y * cellWidth + verticalOffset + cellWidth / 3, cellWidth / 2, cellWidth / 2);
        g.fillRect(x * cellWidth + horizontalOffset + cellWidth * 2 / 5, y * cellWidth + verticalOffset + cellWidth / 8, cellWidth / 5, cellWidth / 24 + 1);


        if (y != 0) {
            int minX = horizontalOffset + cellWidth * x;
            int maxX = horizontalOffset + cellWidth * (x + 1);

            int minY = verticalOffset + cellWidth * (y - 1);
            int maxY = verticalOffset + cellWidth * (y);

            g.setColor(Color.getHSBColor(124 / 360.0f, 1.0f, 0.6f));

            Graphics2D g2d = (Graphics2D) g;
            var oldStroke = g2d.getStroke();
            g2d.setStroke(new BasicStroke(3));

            int half_distance = cellWidth / 2;

            g2d.drawLine(minX, minY, minX, minY + half_distance);
            g2d.drawLine(minX, maxY, minX + half_distance, maxY);
            g2d.drawLine(maxX, maxY, maxX, minY + half_distance);
            g2d.drawLine(maxX, minY, minX + half_distance, minY);

            g.setColor(Color.getHSBColor(11 / 360.0f, 1.0f, 0.8f));

            g2d.drawLine(minX, minY + half_distance, minX, maxY);
            g2d.drawLine(minX + half_distance, maxY, maxX, maxY);
            g2d.drawLine(maxX, minY + half_distance, maxX, minY);
            g2d.drawLine(minX + half_distance, minY, minX, minY);

            g2d.setStroke(oldStroke);
        }

        this.logger.fine("energy station drawn");
    }


    private static int clamp(int x, int min, int max) {
        return Math.max(min, Math.min(max, x));
    }


    public record LinePoints(Coordinate start, Coordinate end) {}
}
