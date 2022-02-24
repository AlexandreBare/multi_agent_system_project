package gui.video;

import java.awt.Graphics;
import java.util.Collection;
import java.util.List;
import java.util.logging.Logger;

import javax.swing.JOptionPane;
import javax.swing.JPanel;

import com.google.common.eventbus.Subscribe;

import environment.Environment;
import environment.Item;
import environment.World;
import environment.world.agent.Agent;
import environment.world.destination.Destination;
import environment.world.energystation.EnergyStationWorld;
import environment.world.packet.Packet;
import environment.world.pheromone.PheromoneWorld;
import util.event.WorldProcessedEvent;


/**
 * Panel for the video panel containing the grid of the world.
 */
public class VideoPanel extends JPanel {

    private Environment env;
    boolean started;

    /**
     * Visitor for the drawing of items.
     */
    private final ItemDrawer drawer;

    private final Logger logger = Logger.getLogger(VideoPanel.class.getName());
    

    //--------------------------------------------------------------------------
    //		CONSTRUCTOR
    //--------------------------------------------------------------------------

    public VideoPanel() {
        drawer = new ItemDrawer();
    }

    //--------------------------------------------------------------------------
    //		INSPECTORS
    //--------------------------------------------------------------------------

    //--------------------------------------------------------------------------
    //		MUTATORS
    //--------------------------------------------------------------------------


    @Subscribe
    private void handleWorldProcessedEvent(WorldProcessedEvent event) {
        this.repaint();
    }

    public void warning(String message) {
        JOptionPane.showMessageDialog(this, message);
    }


    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (started) {
            getDrawer().init(g, getWidth(), getHeight(),
                             getEnvironment().getWidth(),
                             getEnvironment().getHeight(), getBackground());

            getDrawer().drawGrid();
            getDrawer().clear();

            try {
                //Draw Pheromones (those which fill the whole area),
                Environment env = getEnvironment();

                drawItems(env.getPheromoneWorld().getItemsCopied());
                drawItems(env.getEnergyStationWorld().getItemsCopied());

                Collection<World<?>> worlds = env.getWorlds();
                for (World<?> world : worlds) {
                    if (!(world instanceof PheromoneWorld ||
                            world instanceof EnergyStationWorld)) {
                        drawItems(world.getItemsCopied());
                    }
                }
            } catch (NullPointerException exc) {
                this.logger.severe("Error while painting the components. " +
                            "Probably one of the needed worlds is not defined.\n");
            }
        }
    }

    public void initiate() {
        started = true;
        repaint();
    }

    private <T extends Item<?>> void drawItems(List<List<T>> items) {
        for (int i = 0; i < getEnvironment().getWidth(); i++) {
            for (int j = 0; j < getEnvironment().getHeight(); j++) {
                if (items.get(i).get(j) != null) {
                    items.get(i).get(j).draw(getDrawer());
                }
            }
        }
    }

    private void redrawAgent(Agent agent) {
        int ax = agent.getX();
        int ay = agent.getY();
        getDrawer().clearItem(ax, ay);
        agent.draw(getDrawer());
    }

    public void putPacket(int toX, int toY, Item<?> oldTo, Packet packet, Agent agent) {
        if (! (oldTo instanceof Destination)) {
            packet.draw(getDrawer());
        }
        redrawAgent(agent);
    }

    public void pickPacket(int fromX, int fromY, Packet packet, Agent agent) {
        redrawAgent(agent);
    }

    public void moveAgent(int fromX, int fromY, int toX, int toY, Agent agent) {
        getDrawer().clearItem(fromX, fromY);
        agent.draw(getDrawer());
    }

    public void refresh() {
        repaint();
    }

    public void step(int fx, int fy, int tx, int ty, Agent agent) {
        repaint();
    }

    public void put(int tx, int ty, Packet packet, Agent agent) {
        repaint();
    }

    public void pick(int px, int py, Packet packet, Agent agent) {
        repaint();
    }

    public void step() {
        repaint();
    }

    public void put() {
        repaint();
    }

    public void pick() {
        repaint();
    }


    //--------------------------------------------------------------------------
    //		GETTERS & SETTERS
    //--------------------------------------------------------------------------

    public void setEnvironment(Environment environ) {
        this.env = environ;
        this.drawer.setEnvironment(environ);
    }

    public Environment getEnvironment() {
        return env;
    }
    private ItemDrawer getDrawer() {
        return drawer;
    }
}

