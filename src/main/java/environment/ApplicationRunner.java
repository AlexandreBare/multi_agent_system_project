package environment;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Properties;
import java.util.logging.Logger;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

import environment.world.agent.Agent;
import environment.world.conveyor.Conveyor;
import environment.world.destination.Destination;
import environment.world.energystation.EnergyStation;
import environment.world.generator.PacketGenerator;
import environment.world.packet.Packet;
import environment.world.wall.Wall;
import synchronizer.CentralSynchronization;
import synchronizer.Synchronization;
import util.AsciiReader;
import util.Variables;
import util.event.GameOverEvent;

/**
 * This is the main class for the GUI application.
 * It allows for the setup and initialisation of the application.
 */
@SuppressWarnings("FieldCanBeLocal")
public class ApplicationRunner {


    private final String DEFAULT_SYNCMODE = "Central synchronization";
    private final int DEFAULT_WORLD_WIDTH = 16;
    private final int DEFAULT_WORLD_HEIGHT = 16;
    private final int DEFAULT_NB_AGENTS = 4;
    private final int DEFAULT_NB_PACKET_KINDS = 4;
    private final int DEFAULT_NB_PACKETS_PER_KIND = 5;
    private final int DEFAULT_VIEW = 3;

    private String implementation;
    private String envFile;
    private String syncMode = DEFAULT_SYNCMODE;
    private int nbPacketKinds;
    private int nbPacketsPerKind;
    private int nbAgents;
    private int view;
    private boolean custom;

    private Environment env = null;
    private ActiveItemContainer ais = null;

    private final Object dummy = new Object();
    private int steps = 0;
    private boolean pause = true;
    private boolean stepMode = false;
    private boolean stopped = false;
    private int playSpeed = 100;
    private final EventBus eventBus;



    private static final Logger logger = Logger.getLogger(ApplicationRunner.class.getName());

    

    //--------------------------------------------------------------------------
    //		CONSTRUCTOR
    //--------------------------------------------------------------------------

    /**
     * Initializes a new Setup object
     */
    public ApplicationRunner() {
        this.eventBus = new EventBus();
        this.eventBus.register(this);

    }


    @Subscribe
    private void handleGameOverEvent(GameOverEvent event) {
        this.stop();
    }


    //--------------------------------------------------------------------------
    //		INSPECTORS
    //--------------------------------------------------------------------------

    public boolean isCustom() {
        return custom;
    }

    public synchronized void checkSuspended() {
        if (steps > 0) {
            steps--;
        }

        try {
            if (!stepMode) {
                // Only sleep when in play mode, no need to stall execution when manually
                // walking through step by step
                Thread.sleep(playSpeed);
            }
        } catch (InterruptedException ignored) {}
        try {
            synchronized (dummy) {
                while (paused() || (stepMode && steps <= 0)) {
                    dummy.wait();
                }
            }
        } catch (InterruptedException ignored) {}
    }

    //--------------------------------------------------------------------------
    //		MUTATORS
    //--------------------------------------------------------------------------


    public void prepareActiveItems() {
        ais.startAllActiveImps();
    }



    public void play() {
        if (stopped) {
            return;
        }
        stepMode = false;
        pause = false;
        synchronized (dummy) {
            dummy.notify();
        }
    }

    public void stop() {
        setPaused();
        stopped = true;
    }

    public void reset() {
        stopped = false;
        pause = false;

        synchronized (dummy) {
            dummy.notify();
        }
        env.finish();
        ais.finish();
        env = null;
        ais = null;
        make(isCustom());
    }

    public boolean paused() {
        return pause;
    }

    public void setPaused() {
        this.pause = true;
    }

    public void step() {
        if (stopped) {
            return;
        }
        stepMode = true;
        pause = false;
        steps++;
        synchronized (dummy) {
            dummy.notify();
        }
    }


    /**
     * Initializes the core modules of the application
     *
     * @param isCustom whether to create a custom world or not
     * @post    an Environment instance is created and the contents of the
     *          environment package are set up
     * @post    a Synchronization instance is created and the contents of the
     *          synchronization package are set up
     * @post    an AgentImplementations instance is created and the contents
     *          of the AgentImplementations package are set up
     * @post    A gui is built and shown containing the play grid and related
     *          items
     */
    public void make(boolean isCustom) {
        setCustom(isCustom);
        Environment env;

        if (isCustom) {
            env = new Environment(DEFAULT_WORLD_WIDTH, DEFAULT_WORLD_HEIGHT);
            createCustomWorlds(getDefaultWorlds(), env, eventBus, DEFAULT_WORLD_WIDTH, DEFAULT_WORLD_HEIGHT, 
                DEFAULT_NB_AGENTS, DEFAULT_VIEW, DEFAULT_NB_PACKET_KINDS, DEFAULT_NB_PACKETS_PER_KIND);
        } else {
            env = initializeEnvironment(Variables.ENVIRONMENTS_PATH + getEnvFile() + ".txt");
        }

        if (env == null) {
            ApplicationRunner.logger.severe("env is null!");
        }
        setEnvironment(env);
        Synchronization sync = null;
        if (getSyncMode().equals("Central synchronization")) {
            sync = new CentralSynchronization();
        }

        if (sync == null) {
            ApplicationRunner.logger.severe("sync is null!");
        }
        ActiveItemContainer ais = new ActiveItemContainer();
        setAgentImplementations(ais);

        sync.setEnvironment(env);
        env.setAgentImplementations(ais);
        ais.setEnvironment(env);
        sync.setAgentImplementations(ais);
        ais.setSynchronizer(sync);
        env.createEnvironment(this, eventBus);
        try {
            var agentIds = env.getAgentIds();
            var otherActiveIds = env.getActiveItemIDs().stream().filter(id -> !agentIds.contains(id)).toList();
            // Workaround, does not work directly when extracting to list from stream
            List<ActiveItem<?>> otherActiveItems = new ArrayList<>();
            otherActiveIds.stream().map(env::getActiveItem).forEach(otherActiveItems::add);
            ais.createAgentImps(agentIds, implementation, eventBus);
            ais.createObjectImps(otherActiveItems);
            sync.createSynchroPackage();
        } catch (Exception e) {
            ApplicationRunner.logger.severe(String.format("Error during setup.make() %s", e.getMessage()));
        }
    }

    /**
     * Instantiates the worlds.
     *
     * @param worldClasses the classes of the worlds to create
     * @param env          the environment
     */
    private static void createWorlds(List<Class<? extends World<?>>> worldClasses, Environment env, EventBus eventBus, int width, int height) {
        for (Class<? extends World<?>> worldClass : worldClasses) {
            try {
                World<?> w = worldClass.getDeclaredConstructor(EventBus.class).newInstance(eventBus);
                w.initialize(width, height, env);

                env.addWorld(w);
            } catch (Exception e) {
                throw new RuntimeException("Could not create all the different worlds.\n" + Arrays.toString(e.getStackTrace()));
            }
        }
    }


    /**
     * Creates the provided worlds and populates the agent, packet and destination worlds
     * randomly according to the provided parameters.
     * 
     * @param worldClasses The classes of all the worlds that should be initialized.
     * @param env The environment in which to create all the worlds and objects.
     * @param width The width of the environment to be created.
     * @param height The height of the environment to be created.
     * @param nbAgents The number of agents to be randomly created in the world.
     * @param view The view of the agents to be created.
     * @param nbPacketKinds The number of types of packets to be added.
     * @param nbPacketsPerKind The number of packets per type to be added to the world.
     */
    private static void createCustomWorlds(List<Class<? extends World<?>>> worldClasses, Environment env, EventBus eventBus,
                                           int width, int height, int nbAgents, int view, int nbPacketKinds, int nbPacketsPerKind) {

        ApplicationRunner.createWorlds(worldClasses, env, eventBus, width, height);
        env.getAgentWorld().createWorld(nbAgents, view);
        env.getPacketWorld().createWorld(nbPacketKinds, nbPacketsPerKind);
        env.getDestinationWorld().createWorld(nbPacketKinds);
    }


    /**
     * Initialize the environment and ApplicationRunner according to the provided
     * environment configuration file.
     * 
     * @param configFile The configuration file containing the environment.
     * @return The environment created according to the provided configuration file.
     */
    public Environment initializeEnvironment(String configFile) {
        Environment env = ApplicationRunner.createEnvFromFile(configFile, this.eventBus);

        this.setEnvironment(env);

        this.setNbAgents(env.getNbAgents());
            
        return env;
    }

    /**
     * Creates an environment from a file.
     *
     * @param  configFile A text file with a configuration of an environment
     * @param  eventBus   The event bus on which to publish events in the application.
     * @return            A new environment created from the description in
     *                    the specified file
     */
    public static Environment createEnvFromFile(String configFile, EventBus eventBus) {
        try {
            AsciiReader reader = new AsciiReader(configFile);
            // Initiate Worlds

            reader.check("width");
            int width = reader.readInt();
            reader.check("height");
            int height = reader.readInt();

            Environment env = new Environment(width, height);

            createWorlds(getDefaultWorlds(), env, eventBus, width, height);

            // Initiate Items

            // Agents
            reader.check("nbAgents");

            int nbAgents = reader.readInt();

            Collection<Agent> agents = new ArrayList<>();
            for (int i = 0; i < nbAgents; i++) {
                Agent agent = (Agent) reader.readClassConstructor();
                agent.setEnvironment(env);
                agents.add(agent);
            }


            // Packets
            reader.check("nbPackets");
            int nbPackets = reader.readInt();

            Collection<Packet> packets = new ArrayList<>();
            for (int i = 0; i < nbPackets; i++) {
                packets.add((Packet) reader.readClassConstructor());
            }


            // Destinations
            reader.check("nbDestinations");
            int nbDest = reader.readInt();

            Collection<Destination> destinations = new ArrayList<>();
            for (int i = 0; i < nbDest; i++) {
                destinations.add((Destination) reader.readClassConstructor());
            }

            // Walls
            reader.check("nbWalls");
            int nbWalls = reader.readInt();

            Collection<Wall> walls = new ArrayList<>();
            for (int i = 0; i < nbWalls; i++) {
                walls.add((Wall) reader.readClassConstructor());
            }

            // EnergyStations
            reader.check("nbEnergyStations");
            int nbBatteries = reader.readInt();

            Collection<EnergyStation> batteries = new ArrayList<>();
            for (int i = 0; i < nbBatteries; i++) {
                EnergyStation energyStation = (EnergyStation) reader.readClassConstructor();
                batteries.add(energyStation);
            }

            // If no energy stations in the world -> do not keep agent's battery in mind during execution of actions
            EnergyValues.ENERGY_ENABLED = nbBatteries != 0;



            // Optional elements at this point, in specific order
            String nextToken = reader.readNext();
            
            // PacketGenerators
            Collection<PacketGenerator> packetGenerators = new ArrayList<>();

            // Conveyors
            Collection<Conveyor> conveyors = new ArrayList<>();


            while (nextToken != null) {
                switch (nextToken) {
                    case "nbPacketGenerators" -> {
                        int nbPacketGenerators = reader.readInt();
                        for (int i = 0; i < nbPacketGenerators; ++i) {
                            packetGenerators.add((PacketGenerator) reader.readClassConstructor());
                        }
                    } 
                    case "nbConveyors" -> {
                        int nbConveyors = reader.readInt();
                        for (int i = 0; i < nbConveyors; i++) {
                            conveyors.add((Conveyor) reader.readClassConstructor());
                        }
                    }
                }

                nextToken = reader.readNext();
            }

            env.getAgentWorld().placeItems(agents);
            env.getPacketWorld().placeItems(packets);
            env.getDestinationWorld().placeItems(destinations);
            env.getWallWorld().placeItems(walls);
            env.getEnergyStationWorld().placeItems(batteries);
            env.getPacketGeneratorWorld().placeItems(packetGenerators);
            env.getConveyorWorld().placeItems(conveyors);
            return env;
        } catch (FileNotFoundException e) {
            ApplicationRunner.logger.severe(String.format("Environment config file not found: %s\n%s", configFile, e.getMessage()));
        } catch (IOException e) {
            ApplicationRunner.logger.severe(String.format("Something went wrong while reading: %s\n%s", configFile, e.getMessage()));
        } catch (NullPointerException e) {
            ApplicationRunner.logger.severe("Something went wrong while creating the environment. See the thrown exception for more details.");
            throw e;
        }

        throw new RuntimeException(String.format("Something went wrong while creating the environment from the specified configuration file: %s", configFile));
    }


    /**
     * Loads a list of properties from a file into an array
     *
     * @param filename the name of the properties file
     * @return an array with the loaded properties
     */
    public static String[] getPropertiesFromFile(String filename) {
        Properties prop = new Properties();
        try {
            FileInputStream sf = new FileInputStream(filename);
            prop.load(sf);
        } catch (Exception e) {
            ApplicationRunner.logger.severe(String.format("Error with properties file %s\n%s", filename, e.getMessage()));
        }
        String[] propStrings = new String[prop.size()];
        for (int i = 1; i <= propStrings.length; i++) {
            propStrings[i - 1] = prop.getProperty(String.valueOf(i));
        }
        return propStrings;
    }

    //--------------------------------------------------------------------------
    //		GETTERS & SETTERS
    //--------------------------------------------------------------------------
    @SuppressWarnings("unchecked")
    public static List<Class<? extends World<?>>> getDefaultWorlds() {
        String[] worldNames = getPropertiesFromFile(Variables.WORLD_PROPERTIES_FILE);
        List<Class<? extends World<?>>> result = new ArrayList<>();

        for (String name : worldNames) {
            try {
                result.add((Class<? extends World<?>>) Class.forName(name));
            } catch (ClassNotFoundException | ClassCastException e) {
                e.printStackTrace();
                throw new RuntimeException("Could not add world with name " + name);
            }
        }

        return result;
    }

    public String getImplementation() {
        return implementation;
    }

    public String getSyncMode() {
        return syncMode;
    }

    public int getNbPacketKinds() {
        return nbPacketKinds;
    }

    public int getNbAgents() {
        return nbAgents;
    }

    public int getNbPacketsPerKind() {
        return nbPacketsPerKind;
    }

    public int getView() {
        return view;
    }

    public int getSpeed() {
        return playSpeed;
    }

    public String getEnvFile() {
        return envFile;
    }

    public Environment getEnvironment() {
        return env;
    }

    public ActiveItemContainer getActiveItemContainer() {
        return ais;
    }

    public EventBus getEventBus() {
        return eventBus;
    }



    /**
     * Sets the implementation of this Setup
     * @param s The new implementation value
     */
    public void setImplementation(String s) {
        implementation = s;
    }

    /**
     * Sets the syncMode of this Setup
     * @param s The new syncMode value
     */
    public void setSyncMode(String s) {
        syncMode = s;
    }

    /**
     * Sets the amount of different kinds of packets
     * @param nbKinds The new nbPacketKinds value
     */
    public void setNbPacketKinds(int nbKinds) {
        nbPacketKinds = nbKinds;
    }

    /**
     * Sets the number of agents
     * @param nbAgents the new nbAgents value
     */
    public void setNbAgents(int nbAgents) {
        this.nbAgents = nbAgents;
    }

    /**
     * Sets the amount of packets per kind of packet
     * @param nb The new nbPacketsPerKind value
     */
    public void setNbPacketsPerKind(int nb) {
        nbPacketsPerKind = nb;
    }


    /**
     * Sets the range of view for agents
     * @param v The new view value
     */
    public void setView(int v) {
        view = v;
    }

    public void setEnvFile(String fileName) {
        envFile = fileName;
    }

    public void setSpeed(int s) {
        playSpeed = s;
    }

    public void setCustom(boolean custom) {
        this.custom = custom;
    }

    public void setEnvironment(Environment env) {
        this.env = env;
    }

    private void setAgentImplementations(ActiveItemContainer ais) {
        this.ais = ais;
    }

}
