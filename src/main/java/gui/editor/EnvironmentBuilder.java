package gui.editor;

import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.logging.Logger;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;

import com.google.common.eventbus.EventBus;

import environment.ActiveItemID;
import environment.ApplicationRunner;
import environment.CollisionMatrix;
import environment.Environment;
import environment.World;
import environment.world.agent.Agent;
import environment.world.conveyor.Conveyor;
import environment.world.destination.Destination;
import environment.world.energystation.EnergyStation;
import environment.world.generator.PacketGenerator;
import environment.world.packet.Packet;
import environment.world.wall.GlassWall;
import environment.world.wall.SolidWall;
import gui.video.VideoPanel;
import util.AsciiReader;
import util.Direction;
import util.MyColor;
import util.TxtFileFilter;
import util.Variables;

/**
 * A graphical editor for editing environment files.
 */
public class EnvironmentBuilder extends JFrame {

    private JPanel panelWorld = new JPanel();
    private final JPanel panelManipulate = new JPanel();
    private final JPanel panelNorth = new JPanel(new BorderLayout());
    private final JButton btnPacket = new JButton();
    private final JButton btnDest = new JButton();
    private final JButton btnAgent = new JButton();
    private final JButton btnBattery = new JButton();
    private final JButton btnWall = new JButton();
    private final JButton btnGlassWall = new JButton();
    private final JButton btnGenerator = new JButton();
    private final JButton btnConveyor = new JButton();
    private final JButton btnRemove = new JButton();

    private final JLabel lblWidth = new JLabel();
    private final JLabel lblHeight = new JLabel();
    private final JLabel lblView = new JLabel();
    private final JLabel lblColor = new JLabel();
    private final JLabel lblDirection = new JLabel();
    private final JComboBox<String> jComboBoxWidth = new JComboBox<>();
    private final JComboBox<String> jComboBoxHeight = new JComboBox<>();
    private final JComboBox<String> jComboBoxView = new JComboBox<>();
    private final JComboBox<String> jComboBoxColor = new JComboBox<>();
    private final JComboBox<String> jComboBoxDirection = new JComboBox<>();
    private final JMenuBar menuBar = new JMenuBar();
    private JFileChooser fileChooser;
    private final Color DEFAULT_COLOR = Color.gray;
    private final String WINDOW_TITLE = "Environment Creator";

    private File curFile = null;
    private boolean changed = false;

    private Environment env;
    private int view;
    private final List<Class<? extends World<?>>> WORLDS = ApplicationRunner.getDefaultWorlds();
    private final VideoPanel videoPanel = new VideoPanel();
    private Selection selected = Selection.None;

    // Keep track of the next ID to issue for active items
    private int nextActiveItemID;

    private final static int MAX_SIZE = 100;
    private final static int MAX_VIEW = 100;

    private final Logger logger = Logger.getLogger(EnvironmentBuilder.class.getName());


    public static void main(String[] arg) {
        EnvironmentBuilder builder = new EnvironmentBuilder();
        builder.init();
        builder.setVisible(true);
    }

    public void initColorList(boolean includeColorless) {
        jComboBoxColor.removeAllItems();

        if (includeColorless) {
            jComboBoxColor.addItem("None");
        }

        MyColor.getColors().stream()
            .map(MyColor::getName)
            .forEach(jComboBoxColor::addItem);
    }
    public void initDirectionList() {
        jComboBoxDirection.removeAllItems();
        Arrays.stream(Direction.values())
            .map(Direction::toString)
            .map(s -> s.substring(0, 1).toUpperCase() + s.substring(1).toLowerCase())
            .forEach(jComboBoxDirection::addItem);
    }
    /**
     * Initializes the application. Calls initialize() and does some more init
     */
    public void init() {
        this.panelWorld = videoPanel;

        this.initialize();
        this.initColorList(false);
        this.initDirectionList();

        for (int i = 4; i <= MAX_SIZE; i++) {
            jComboBoxWidth.addItem(Integer.toString(i));
            jComboBoxHeight.addItem(Integer.toString(i));
        }
        for (int i = 1; i <= MAX_VIEW; i++) {
            jComboBoxView.addItem(Integer.toString(i));
        }
        jComboBoxWidth.addActionListener(this::sizeActionPerformed);
        jComboBoxHeight.addActionListener(this::sizeActionPerformed);
        jComboBoxView.addActionListener(this::sizeActionPerformed);

        this.newFile();
    }

    /**
     * Creates all components
     */
    protected void initialize() {

        //getContentPane().add(panel3, BorderLayout.SOUTH);
        this.getContentPane().add(panelNorth, BorderLayout.NORTH);
        this.getContentPane().add(panelWorld, BorderLayout.CENTER);
        panelManipulate.setLayout(new GridBagLayout());
        panelManipulate.setSize(new Dimension(800, 60));
        panelManipulate.add(lblWidth,
            new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                GridBagConstraints.WEST,
                GridBagConstraints.NONE,
                new Insets(5, 25, 5, 10), 0, 0));
        panelManipulate.add(jComboBoxWidth,
            new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
                GridBagConstraints.WEST,
                GridBagConstraints.NONE,
                new Insets(5, 10, 5, 10), 0, 0));
        panelManipulate.add(lblHeight,
            new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0,
                GridBagConstraints.WEST,
                GridBagConstraints.NONE,
                new Insets(5, 10, 5, 10), 0, 0));
        panelManipulate.add(jComboBoxHeight,
            new GridBagConstraints(3, 0, 1, 1, 0.0, 0.0,
                GridBagConstraints.WEST,
                GridBagConstraints.NONE,
                new Insets(5, 10, 5, 10), 0, 0));
        panelManipulate.add(lblView,
            new GridBagConstraints(4, 0, 1, 1, 0.0, 0.0,
                GridBagConstraints.WEST,
                GridBagConstraints.NONE,
                new Insets(5, 10, 5, 10), 0, 0));
        panelManipulate.add(jComboBoxView,
            new GridBagConstraints(5, 0, 1, 1, 0.0, 0.0,
                GridBagConstraints.WEST,
                GridBagConstraints.NONE,
                new Insets(5, 10, 5, 10), 0, 0));

        panelManipulate.add(lblColor,
            new GridBagConstraints(6, 0, 1, 1, 0.0, 0.0,
                GridBagConstraints.WEST,
                GridBagConstraints.NONE,
                new Insets(5, 5, 5, 5), 0, 0));
        panelManipulate.add(jComboBoxColor,
            new GridBagConstraints(7, 0, 1, 1, 0.0, 0.0,
                GridBagConstraints.WEST,
                GridBagConstraints.NONE,
                new Insets(5, 10, 5, 10), 0, 0));
        panelManipulate.add(lblDirection,
            new GridBagConstraints(8, 0, 1, 1, 0.0, 0.0,
                GridBagConstraints.WEST,
                GridBagConstraints.NONE,
                new Insets(5, 5, 5, 5), 0, 0));
        panelManipulate.add(jComboBoxDirection,
            new GridBagConstraints(9, 0, 1, 1, 0.0, 0.0,
                    GridBagConstraints.WEST,
                    GridBagConstraints.NONE,
                    new Insets(5, 5, 5, 5), 0, 0));

        // panelManipulate.add(Box.createHorizontalStrut(250), 
        //     new GridBagConstraints(10, 0, 7, 1, 0.0, 0.0,
        //         GridBagConstraints.WEST,
        //         GridBagConstraints.NONE,
        //         new Insets(5, 5, 5, 5), 0, 0));


        panelManipulate.add(btnPacket,
            new GridBagConstraints(0, 1, 2, 1, 0.0, 0.0,
                GridBagConstraints.WEST,
                GridBagConstraints.HORIZONTAL,
                new Insets(5, 25, 5, 5), 0, 0));
        panelManipulate.add(btnDest,
            new GridBagConstraints(2, 1, 2, 1, 0.0, 0.0,
                GridBagConstraints.WEST,
                GridBagConstraints.HORIZONTAL,
                new Insets(5, 5, 5, 5), 0, 0));
        panelManipulate.add(btnAgent,
            new GridBagConstraints(4, 1, 2, 1, 0.0, 0.0,
                GridBagConstraints.WEST,
                GridBagConstraints.HORIZONTAL,
                new Insets(5, 5, 5, 5), 0, 0));
        panelManipulate.add(btnGenerator,
            new GridBagConstraints(6, 1, 2, 1, 0.0, 0.0,
                GridBagConstraints.WEST,
                GridBagConstraints.HORIZONTAL,
                new Insets(5, 5, 5, 5), 0, 0));
        panelManipulate.add(btnConveyor,
            new GridBagConstraints(8, 1, 2, 1, 0.0, 0.0,
                    GridBagConstraints.WEST,
                    GridBagConstraints.HORIZONTAL,
                    new Insets(5, 5, 5, 5), 0, 0));


        panelManipulate.add(btnBattery,
            new GridBagConstraints(0, 2, 2, 1, 0.0, 0.0,
                GridBagConstraints.WEST,
                GridBagConstraints.HORIZONTAL,
                new Insets(5, 25, 5, 5), 0, 0));
        panelManipulate.add(btnWall,
            new GridBagConstraints(2, 2, 2, 1, 0.0, 0.0,
                GridBagConstraints.WEST,
                GridBagConstraints.HORIZONTAL,
                new Insets(5, 5, 5, 5), 0, 0));
        panelManipulate.add(btnGlassWall,
            new GridBagConstraints(4, 2, 2, 1, 0.0, 0.0,
                GridBagConstraints.WEST,
                GridBagConstraints.HORIZONTAL,
                new Insets(5, 5, 5, 5), 0, 0));
        panelManipulate.add(btnRemove,
            new GridBagConstraints(6, 2, 2, 1, 0.0, 0.0,
                GridBagConstraints.WEST,
                GridBagConstraints.HORIZONTAL,
                new Insets(5, 5, 5, 5), 0, 0));

                
        lblWidth.setText("Width");
        lblHeight.setText("Height");
        lblView.setText("View");
        lblColor.setText("Color");
        lblDirection.setText("Direction");

        jComboBoxWidth.setPreferredSize(new Dimension(50, 25));

        jComboBoxHeight.setPreferredSize(new Dimension(50, 25));

        jComboBoxView.setMinimumSize(new Dimension(50, 25));
        jComboBoxView.setSize(new Dimension(30, 25));
        jComboBoxView.setPreferredSize(new Dimension(50, 25));

        jComboBoxColor.setSize(new Dimension(60, 21));
        jComboBoxColor.setEnabled(false);

        jComboBoxDirection.setSize(new Dimension(60, 21));
        jComboBoxDirection.setEnabled(false);


        var preferredButtonSize = new Dimension(100, 25);

        btnPacket.setText("Packet");
        btnPacket.setPreferredSize(preferredButtonSize);
        btnPacket.addActionListener(this::packetButtonActionPerformed);

        btnDest.setText("Destination");
        btnDest.setPreferredSize(preferredButtonSize);
        btnDest.addActionListener(this::destButtonActionPerformed);

        btnAgent.setText("Agent");
        btnAgent.setPreferredSize(preferredButtonSize);
        btnAgent.addActionListener(this::agentButtonActionPerformed);

        btnBattery.setText("Battery");
        btnBattery.setPreferredSize(preferredButtonSize);
        btnBattery.addActionListener(this::batteryButtonActionPerformed);

        btnWall.setText("Wall");
        btnWall.setPreferredSize(preferredButtonSize);
        btnWall.addActionListener(this::wallButtonActionPerformed);

        btnGlassWall.setText("Glass wall");
        btnGlassWall.setPreferredSize(preferredButtonSize);
        btnGlassWall.addActionListener(this::glassWallButtonActionPerformed);

        btnGenerator.setText("Generator");
        btnGenerator.setPreferredSize(preferredButtonSize);
        btnGenerator.addActionListener(this::generatorButtonActionPerformed);

        btnConveyor.setText("Conveyor");
        btnConveyor.setPreferredSize(preferredButtonSize);
        btnConveyor.addActionListener(this::conveyorButtonActionPerformed);

        btnRemove.setText("Remove");
        btnRemove.setPreferredSize(new Dimension(80, 25));
        btnRemove.addActionListener(this::removeButtonActionPerformed);

        this.setResizable(true);
        this.setDefaultCloseOperation(javax.swing.JFrame.DO_NOTHING_ON_CLOSE);
        this.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                exitActionPerformed(e);
                System.exit(0);
            }
        });

        this.setTitle(WINDOW_TITLE);
        this.setSize(new Dimension(800, 800));
        this.setPreferredSize(new Dimension(800, 800));
        panelWorld.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                panelMouseClicked(e);
            }
        });

        panelNorth.add(panelManipulate, BorderLayout.WEST);


        //Menubar
        this.setJMenuBar(menuBar);

        //Menu File
        JMenu fileMenu = new JMenu("File");
        fileMenu.setMnemonic(KeyEvent.VK_F);
        fileMenu.getAccessibleContext().setAccessibleDescription(
                "Opens the file menu");
        menuBar.add(fileMenu);

        JMenuItem menuItem;
        //File > New
        menuItem = new JMenuItem("New");
        menuItem.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_N, InputEvent.CTRL_DOWN_MASK));
        menuItem.getAccessibleContext().setAccessibleDescription(
                "Creates a new environment file");
        menuItem.addActionListener(this::newActionPerformed);
        fileMenu.add(menuItem);
        //File > Load
        menuItem = new JMenuItem("Open...");
        menuItem.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_O, InputEvent.CTRL_DOWN_MASK));
        menuItem.getAccessibleContext().setAccessibleDescription(
                "Loads an environment file");
        menuItem.addActionListener(this::loadActionPerformed);
        fileMenu.add(menuItem);
        //File > Save
        menuItem = new JMenuItem("Save");
        menuItem.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK));
        menuItem.getAccessibleContext().setAccessibleDescription(
                "Saves the active environment file");
        menuItem.addActionListener(this::saveActionPerformed);
        fileMenu.add(menuItem);
        //File > Save as
        menuItem = new JMenuItem("Save as...");
        menuItem.getAccessibleContext().setAccessibleDescription(
                "Saves the active environment file with a new name");
        menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx() | InputEvent.SHIFT_DOWN_MASK));
        menuItem.addActionListener(this::saveAsActionPerformed);
        fileMenu.add(menuItem);

        fileMenu.addSeparator();
        //File > Exit
        menuItem = new JMenuItem("Exit");
        menuItem.getAccessibleContext().setAccessibleDescription(
                "Quits the editor");
        menuItem.addActionListener(this::exitActionPerformed);
        fileMenu.add(menuItem);
    }

    /**
     * Routine for pressing packet button
     */
    public void packetButtonActionPerformed(ActionEvent e) {
        buttonClear();
        btnPacket.setBackground(DEFAULT_COLOR);
        // btnPacket.setBackground(selectedColor);
        this.initColorList(false);
        jComboBoxColor.setEnabled(true);
        selected = Selection.Packet;
    }

    /**
     * Routine for pressing destination button
     */
    public void destButtonActionPerformed(ActionEvent e) {
        buttonClear();
        btnDest.setBackground(DEFAULT_COLOR);
        // btnDest.setBackground(selectedColor);
        this.initColorList(false);
        jComboBoxColor.setEnabled(true);
        selected = Selection.Destination;
    }

    /**
     * Routine for pressing agent button
     */
    public void agentButtonActionPerformed(ActionEvent e) {
        buttonClear();
        btnAgent.setBackground(DEFAULT_COLOR);
        this.initColorList(true);
        jComboBoxColor.setEnabled(true);
        selected = Selection.Agent;
    }

    /**
     * Routine for pressing battery button
     */
    public void batteryButtonActionPerformed(ActionEvent e) {
        buttonClear();
        btnBattery.setBackground(DEFAULT_COLOR);
        selected = Selection.Battery;
    }

    public void wallButtonActionPerformed(ActionEvent e) {
        buttonClear();
        btnWall.setBackground(DEFAULT_COLOR);
        selected = Selection.Wall;
    }


    public void glassWallButtonActionPerformed(ActionEvent e) {
        buttonClear();
        btnGlassWall.setBackground(DEFAULT_COLOR);
        selected = Selection.GlassWall;
    }


    public void generatorButtonActionPerformed(ActionEvent e) {
        buttonClear();
        btnGenerator.setBackground(DEFAULT_COLOR);
        this.initColorList(false);
        jComboBoxColor.setEnabled(true);
        selected = Selection.Generator;
    }

    public void conveyorButtonActionPerformed(ActionEvent e) {
        buttonClear();
        btnConveyor.setBackground(DEFAULT_COLOR);
        jComboBoxDirection.setEnabled(true);
        selected = Selection.ConveyorBelt;
    }

    
    /**
     * Routine for pressing remove button
     */
    public void removeButtonActionPerformed(ActionEvent e) {
        buttonClear();
        btnRemove.setBackground(DEFAULT_COLOR);
        selected = Selection.Remove;
    }

    /**
     * Resets a buttons
     */
    private void buttonClear() {
        jComboBoxColor.setEnabled(false);
        jComboBoxDirection.setEnabled(false);
        btnPacket.setBackground(null);
        btnDest.setBackground(null);
        btnAgent.setBackground(null);
        btnRemove.setBackground(null);
        btnBattery.setBackground(null);
        btnWall.setBackground(null);
        btnGlassWall.setBackground(null);
        btnGenerator.setBackground(null);
        btnConveyor.setBackground(null);
    }

    /**
     * Routine for adjusting size combo
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public void sizeActionPerformed(ActionEvent e) {
        int width = Integer.parseInt( (String) jComboBoxWidth.getSelectedItem());
        int height = Integer.parseInt( (String) jComboBoxHeight.getSelectedItem());
        int newView = Integer.parseInt( (String) jComboBoxView.getSelectedItem());
        setView(newView);
        Environment newEnv = new Environment(width, height);

        for (Class<? extends World<?>> worldClass : WORLDS) {
            try {
                World<?> w = worldClass.getDeclaredConstructor(EventBus.class).newInstance((Object) null);
                w.initialize(width, height, newEnv);

                newEnv.addWorld(w);
            } catch (Exception exc) {
                this.logger.severe(String.format("Error setting worlds: %s", exc));
                throw new RuntimeException(exc);
            }
        }

        // The environment does not need an ApplicationRunner or EventBus in this case
        // since we are not planning on actually running it, but merely
        // constructing it
        newEnv.createEnvironment(null, null);

        if (getEnvironment() != null) {
            for (Class<? extends World<?>> worldClass : WORLDS) {
                World oldWorld = env.getWorld(worldClass);
                World newWorld = newEnv.getWorld(worldClass);

                for (int i = 0; i < width; i++) {
                    for (int j = 0; j < height; j++) {
                        if (i < getEnvironment().getWidth() && j < getEnvironment().getHeight()) {
                            if (oldWorld.getItem(i, j) != null) {
                                newWorld.placeItem(oldWorld.getItem(i, j));
                                if (oldWorld.getItem(i, j) instanceof Agent) {
                                    ((Agent) newWorld.getItem(i, j)).setEnvironment(newEnv);
                                    ((Agent) newWorld.getItem(i, j)).setView(newView);
                                }
                            }
                        }
                    }
                }
            }
        }
        changed = true;
        setEnvironment(newEnv);

        videoPanel.setEnvironment(getEnvironment());
        videoPanel.initiate();
    }


    /**
     * Routine for clicking in the environment area
     */
    public void panelMouseClicked(MouseEvent e) {

        int x = e.getPoint().x;
        int y = e.getPoint().y;
        int fw = Math.min(panelWorld.getWidth(), panelWorld.getHeight());
        int s = Math.max(getEnvironment().getWidth(),
                         getEnvironment().getHeight());
        int o = 20;
        int w = fw - 2 * o;
        int cs = w / s;
        int i = (x - o) / cs;
        int j = (y - o) / cs;
        if (i >= getEnvironment().getWidth() || j >= getEnvironment().getHeight()) {
            //Clicked outside grid
            //Could cause NullPointerException
            //No-op
            return;
        }

        selected.executeTrigger(this, i, j);

        videoPanel.refresh();
    }

    public void newActionPerformed(ActionEvent e) {
        if (changed) {
            int sel = JOptionPane.showConfirmDialog(this,
                "Environment has been modified. Do you want to save changes?",
                "Save modified file?",
                JOptionPane.YES_NO_CANCEL_OPTION,
                JOptionPane.WARNING_MESSAGE);
            if (sel == JOptionPane.YES_OPTION) {
                saveDialog();
                newFile();
            } else if (sel == JOptionPane.NO_OPTION) {
                newFile();
            }
            //else: CANCEL_OPTION --> No-op
        }
        newFile();
    }

    public void loadActionPerformed(ActionEvent e) {
        if (changed) {
            int sel = JOptionPane.showConfirmDialog(this,
                "Environment has been modified. Do you want to save changes?",
                "Save modified file?",
                JOptionPane.YES_NO_CANCEL_OPTION,
                JOptionPane.WARNING_MESSAGE);
            if (sel == JOptionPane.YES_OPTION) {
                saveDialog();
                if (loadDialog()) changed = false;
            } else if (sel == JOptionPane.NO_OPTION) {
                if (loadDialog()) changed = false;
            }
            //else: CANCEL_OPTION --> No-op
        } else {
            if (loadDialog()) changed = false;
        }
    }

    /**
     * Routine for pressing save button
     */
    public void saveActionPerformed(ActionEvent e) {
        if (changed) {
            if (curFile != null) {
                if (writeFile(curFile)) {
                    changed = false;
                } else {
                    this.videoPanel.warning("Could not write to output file.");
                }
            } else {
                if (saveDialog()) {
                    changed = false;
                }
            }
        }
    }

    public void saveAsActionPerformed(ActionEvent e) {
        if (saveDialog()) {
            changed = false;
        }
    }

    public void exitActionPerformed(AWTEvent e) {
        if (changed) {
            int sel = JOptionPane.showConfirmDialog(this,
                "Environment has been modified. Do you want to save changes?",
                "Save modified file?",
                JOptionPane.YES_NO_CANCEL_OPTION,
                JOptionPane.WARNING_MESSAGE);
            if (sel == JOptionPane.YES_OPTION) {
                if (saveDialog()) {
                    System.exit(0);
                }
            } else if (sel == JOptionPane.NO_OPTION) {
                System.exit(0);
            }
            //Else: cancel --> No-op
        } else {
            System.exit(0);
        }
    }

    /**
     * Shows a save file dialog. Returns <code>true</code> if the loading has
     * succeeded, <code>false</code> otherwise.
     */
    public boolean loadDialog() {
        fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new TxtFileFilter());
        fileChooser.setAcceptAllFileFilterUsed(false);
        fileChooser.setCurrentDirectory(new File(Variables.ENVIRONMENTS_PATH));
        int returnVal = fileChooser.showOpenDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            if (file != null) {
                readFile(file);
                setCurrentFile(file);
                return true;
            }
        }
        return false;
    }

    /**
     * Shows a save file dialog. Returns <code>true</code> if the saving has
     * succeeded, <code>false</code> otherwise.
     */
    public boolean saveDialog() {
        boolean valid = false;
        File file = new File("");
        while (!valid) {
            fileChooser = new JFileChooser();
            fileChooser.setFileFilter(new TxtFileFilter());
            fileChooser.setAcceptAllFileFilterUsed(false);
            fileChooser.setCurrentDirectory(new File(Variables.ENVIRONMENTS_PATH));
            int returnVal = fileChooser.showSaveDialog(this);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                file = fileChooser.getSelectedFile();
                if (!file.getAbsolutePath().endsWith(".txt")) {
                    file = new File(file.getAbsolutePath() + ".txt");
                }
                if (file.isFile()) {
                    int sel = JOptionPane.showConfirmDialog(this,
                        "File already exists! Do you want to overwrite it?",
                        "Overwrite file?",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE);
                    if (sel == JOptionPane.YES_OPTION) {
                        valid = true;
                    }
                } else {
                    valid = true;
                }
            } else {
                return false;
            }
        }
        if (!writeFile(file)) {
            this.videoPanel.warning("Could not write to output file.");
            return false;
        } else {
            setCurrentFile(file);
            return true;
        }
    }

    private void newFile() {
        setEnvironment(null);
        sizeActionPerformed(null);
        selected = Selection.None;
        changed = false;
    }

    /**
     * For more information, see {@link environment.ApplicationRunner#createEnvFromFile(String, EventBus)}.
     * @param file The environment file to read.
     */
    private void readFile(File file) {
        String configFile = file.getAbsolutePath();

        //Sets the values of the width and height combo boxes
        try {
            AsciiReader reader = new AsciiReader(configFile);
            reader.check("width");
            int worldWidth = reader.readInt();
            reader.check("height");
            int worldHeight = reader.readInt();

            jComboBoxWidth.setSelectedItem(String.valueOf(worldWidth));
            jComboBoxHeight.setSelectedItem(String.valueOf(worldHeight));

            setEnvironment(ApplicationRunner.createEnvFromFile(configFile, null));
            List<Agent> agents = getEnvironment().getAgentWorld().getAgents();
            try {
                jComboBoxView.setSelectedItem(String.valueOf(agents.get(0).getView()));
            } catch (Exception exc) {
                jComboBoxView.setSelectedIndex(0);
            }
            videoPanel.setEnvironment(getEnvironment());
            videoPanel.initiate();
            changed = false;
            this.nextActiveItemID = this.getEnvironment().getActiveItemIDs().stream().mapToInt(ActiveItemID::getID)
                .max()
                .orElse(0) + 1;
        } catch (FileNotFoundException e) {
            this.logger.severe(String.format("Environment config file not found: %s\n%s", configFile, e.getMessage()));
        } catch (IOException e) {
            this.logger.severe(String.format("Something went wrong while reading the environment file: %s\n%s", 
                    configFile, e.getMessage()));
        }
    }

    /**
     * Writes configuration file
     */
    private boolean writeFile(File file) {
        String configFile = file.getAbsolutePath();
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(configFile));
            writer.write("width " + getEnvironment().getWidth() + "\n");
            writer.write("height " + getEnvironment().getHeight() + "\n");

            writer.write("\n");
            var agents = getEnvironment().getAgentWorld().getAgents();
            writer.write("nbAgents " + agents.size() + "\n");
            for (var agent : agents) {
                writer.write(agent.generateEnvironmentString());
            }
            
            var packets = getEnvironment().getPacketWorld().getItems().stream()
                    .flatMap(List::stream)
                    .filter(Objects::nonNull)
                    .toList();
            writer.write("\nnbPackets " + packets.size() + "\n");
            for (var packet : packets) {
                writer.write(packet.generateEnvironmentString());
            }

            var destinations = getEnvironment().getDestinationWorld().getItems().stream()
                    .flatMap(List::stream)
                    .filter(Objects::nonNull)
                    .toList();
            writer.write("\nnbDestinations " + destinations.size() + "\n");
            for (var destination : destinations) {
                writer.write(destination.generateEnvironmentString());
            }

            var walls = getEnvironment().getWallWorld().getItems().stream()
                    .flatMap(List::stream)
                    .filter(Objects::nonNull)
                    .toList();
            writer.write("\nnbWalls " + walls.size() + "\n");
            for (var wall : walls) {
                writer.write(wall.generateEnvironmentString());
            }

            var energyStations = getEnvironment().getEnergyStationWorld().getItems().stream()
                    .flatMap(List::stream)
                    .filter(Objects::nonNull)
                    .toList();
            writer.write("\nnbEnergyStations " + energyStations.size() + "\n");
            for (var energyStation : energyStations) {
                writer.write(energyStation.generateEnvironmentString());
            }

            var packetGenerators = getEnvironment().getPacketGeneratorWorld().getItems().stream()
                    .flatMap(List::stream)
                    .filter(Objects::nonNull)
                    .toList();
            writer.write("\nnbPacketGenerators " + packetGenerators.size() + "\n");
            for (var packetGenerator : packetGenerators) {
                writer.write(packetGenerator.generateEnvironmentString());
            }


            var conveyors = getEnvironment().getConveyorWorld().getItems().stream()
                    .flatMap(List::stream)
                    .filter(Objects::nonNull)
                    .toList();
            writer.write("\nnbConveyors " + conveyors.size() + "\n");
            for (var conveyor : conveyors) {
                writer.write(conveyor.generateEnvironmentString());
            }

            writer.close();
            return true;
        } catch (IOException ioe) {
            return false;
        }
    }


    //--------------------------------------------------------------------------
    //		GETTERS & SETTERS
    //--------------------------------------------------------------------------

    private Environment getEnvironment() {
        return env;
    }

    private void setEnvironment(Environment env) {
        this.env = env;
    }

    private int getView() {
        return view;
    }

    private void setView(int view) {
        this.view = view;
    }

    private void setCurrentFile(File file) {
        curFile = file;
        setTitle(WINDOW_TITLE + " - " + file.getAbsolutePath());
    }

    private String getSelectedColor() {
        return (String) jComboBoxColor.getSelectedItem();
    }

    private String getSelectedDirection() {
        return (String) jComboBoxDirection.getSelectedItem();
    }


    private void setChanged(boolean value) {
        this.changed = value;
    }

    private ActiveItemID generateActiveItemID(ActiveItemID.ActionPriority priority) {
        int id = this.nextActiveItemID++;
        return new ActiveItemID(id, priority);
    }




    public enum Selection { 
        None {
            public void executeTrigger(EnvironmentBuilder builder, int i, int j) {}
        },
        Packet {    
            public void executeTrigger(EnvironmentBuilder builder, int i, int j) {
                Environment env = builder.getEnvironment();
                Packet p = new Packet(i, j, builder.getSelectedColor());

                if (!CollisionMatrix.packetCanStandOn(env, i, j, p.getColor())) {
                    env.remove(i, j);
                }
                env.getPacketWorld().placeItem(p);
                builder.setChanged(true);
            }
        },
        Destination {
            public void executeTrigger(EnvironmentBuilder builder, int i, int j) {
                Environment env = builder.getEnvironment();
                Destination d = new Destination(i, j, builder.getSelectedColor());

                if (!CollisionMatrix.destinationCanStandOn(env, i, j)) {
                    env.remove(i, j);
                }
                env.getDestinationWorld().placeItem(d);
                builder.setChanged(true);
            }
        },
        Agent {
            public void executeTrigger(EnvironmentBuilder builder, int i, int j) {
                Environment env = builder.getEnvironment();
                String color = builder.getSelectedColor();
                ActiveItemID newId = builder.generateActiveItemID(ActiveItemID.ActionPriority.AGENT);

                String name = JOptionPane.showInputDialog("Enter a name for your agent: ");
                if (name == null || name.equals("")) {
                    return;
                }

                Agent a = new Agent(i, j, env, builder.getView(), 
                                    newId,
                                    name, 
                                    color.equals("None") ? null : MyColor.getColor(color));

                if (!CollisionMatrix.agentCanStandOn(env, i, j)) {
                    env.remove(i, j);
                }
                env.getAgentWorld().placeItem(a);
                builder.setChanged(true);
            }
        },
        Battery {
            public void executeTrigger(EnvironmentBuilder builder, int i, int j) {
                Environment env = builder.getEnvironment();
                EnergyStation es = new EnergyStation(i, j, 
                    builder.generateActiveItemID(ActiveItemID.ActionPriority.ENERGYSTATION));

                if (!CollisionMatrix.energyStationCanStandOn(env, i, j)) {
                    env.remove(i, j);
                }
                env.getEnergyStationWorld().placeItem(es);
                builder.setChanged(true);
            }
        },
        Wall {
            public void executeTrigger(EnvironmentBuilder builder, int i, int j) {
                Environment env = builder.getEnvironment();
                SolidWall wa = new SolidWall(i, j);

                if (!CollisionMatrix.wallCanStandOn(env, i, j)) {
                    env.remove(i, j);
                }
                env.getWallWorld().placeItem(wa);
                builder.setChanged(true);
            }
        }, 
        GlassWall {
            public void executeTrigger(EnvironmentBuilder builder, int i, int j) {
                Environment env = builder.getEnvironment();
                GlassWall wall = new GlassWall(i, j);

                if (!CollisionMatrix.glassWallCanStandOn(env, i, j)) {
                    env.remove(i, j);
                }
                env.getWallWorld().placeItem(wall);
                builder.setChanged(true);
            }
        },
        Generator {
            public void executeTrigger(EnvironmentBuilder builder, int i, int j) {
                Environment env = builder.getEnvironment();
                String color = builder.getSelectedColor();

                ActiveItemID newId = builder.generateActiveItemID(ActiveItemID.ActionPriority.GENERATOR);

                EnvironmentBuilder.getUserInputGenerator().ifPresent(config -> {
                    PacketGenerator gen;

                    if (config.type.equals(GeneratorType.Fixed)) {
                        gen = new PacketGenerator(i, j, newId, color.equals("None") ? 
                            null : color, Integer.parseInt(config.data), Integer.parseInt(config.threshold));
                    } else if (config.type.equals(GeneratorType.Probability)) {
                        gen = new PacketGenerator(i, j, newId, color.equals("None") ? 
                            null : color, Double.parseDouble(config.data), Integer.parseInt(config.threshold));
                    } else {
                        return;
                    }
                    
                    
                    if (!CollisionMatrix.generatorCanStandOn(env, i, j)) {
                        env.remove(i, j);
                    }
                    env.getPacketGeneratorWorld().placeItem(gen);
                    builder.setChanged(true);
                });
            }
        },
        ConveyorBelt {
            public void executeTrigger(EnvironmentBuilder builder, int i, int j) {
                Environment env = builder.getEnvironment();
                String direction = builder.getSelectedDirection();

                ActiveItemID newId = builder.generateActiveItemID(ActiveItemID.ActionPriority.CONVEYOR);

                Conveyor conv = new Conveyor(i, j, newId, direction.equals("None") ?
                    0 : Direction.valueOfByName(direction).getId());


                if (!CollisionMatrix.conveyorCanStandOn(env, i, j)) {
                    env.remove(i, j);
                }
                env.getConveyorWorld().placeItem(conv);
                builder.setChanged(true);
            }
        },
        Remove {
            public void executeTrigger(EnvironmentBuilder builder, int i, int j) {
                builder.getEnvironment().remove(i, j);

                builder.setChanged(true);
            }
        };
        
        public abstract void executeTrigger(EnvironmentBuilder builder, int i, int j);
    }


    public static Optional<GeneratorConfig> getUserInputGenerator() {
        JTextField data = new JTextField(30);
        data.setPreferredSize(new Dimension(100, 20));
        data.setMinimumSize(new Dimension(100, 20));
        data.setMaximumSize(new Dimension(100, 20));
        JTextField threshold = new JTextField(30);
        threshold.setPreferredSize(new Dimension(100, 20));
        threshold.setMinimumSize(new Dimension(100, 20));
        threshold.setMaximumSize(new Dimension(100, 20));

        JRadioButton fixedButton = new JRadioButton(GeneratorType.Fixed.name());
        fixedButton.setSelected(true);
        fixedButton.setHorizontalAlignment(SwingConstants.CENTER);
        fixedButton.setVerticalTextPosition(JRadioButton.TOP);
        fixedButton.setToolTipText(GeneratorType.Fixed.getDescription());
        // fixedButton.setName(GeneratorType.Fixed.name());
    
        JRadioButton probabilityButton = new JRadioButton(GeneratorType.Probability.name());
        probabilityButton.setHorizontalAlignment(SwingConstants.CENTER);
        probabilityButton.setVerticalTextPosition(JRadioButton.TOP);
        probabilityButton.setToolTipText(GeneratorType.Probability.getDescription());
        // fixedButton.setName(GeneratorType.Probability.name());

        ButtonGroup group = new ButtonGroup();
        group.add(fixedButton);
        group.add(probabilityButton);

        

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setMaximumSize(new Dimension(450, 160));
        panel.setPreferredSize(new Dimension(450, 160));
        panel.setMinimumSize(new Dimension(450, 160));
        
        

        var labelGenerator = new JLabel("<html>Type of generator:<br/>(hover for tooltip)</html>");
        labelGenerator.setPreferredSize(new Dimension(250, 30)); 
        labelGenerator.setMinimumSize(new Dimension(250, 30)); 
        labelGenerator.setMaximumSize(new Dimension(250, 30)); 
        
        panel.add(labelGenerator,
            new GridBagConstraints(0, 0, 2, 1, 0.0, 0.0,
            GridBagConstraints.WEST,
            GridBagConstraints.HORIZONTAL,
            new Insets(5, 5, 5, 5), 0, 0));


        JPanel panelButtons = new JPanel(new GridBagLayout());
        panelButtons.add(fixedButton,
            new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
            GridBagConstraints.CENTER,
            GridBagConstraints.NONE,
            new Insets(5, 5, 5, 5), 0, 0));
        panelButtons.add(probabilityButton,
            new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
            GridBagConstraints.CENTER,
            GridBagConstraints.NONE,
            new Insets(5, 5, 5, 5), 0, 0));
        panel.add(panelButtons,
            new GridBagConstraints(2, 0, 3, 1, 0.0, 0.0,
            GridBagConstraints.EAST,
            GridBagConstraints.HORIZONTAL,
            new Insets(5, 20, 5, 5), 0, 0));

        panel.add(new JLabel("Generation frequency:"),
            new GridBagConstraints(0, 1, 3, 1, 0.0, 0.0,
            GridBagConstraints.WEST,
            GridBagConstraints.HORIZONTAL,
            new Insets(5, 5, 5, 5), 0, 0));
        panel.add(data,
            new GridBagConstraints(3, 1, 2, 1, 0.0, 0.0,
            GridBagConstraints.CENTER,
            GridBagConstraints.NONE,
            new Insets(5, 20, 5, 5), 0, 0));

        panel.add(new JLabel("<html>Generation threshold<br/>(max nb. of packets generated):</html>"),
            new GridBagConstraints(0, 2, 3, 1, 0.0, 0.0,
            GridBagConstraints.WEST,
            GridBagConstraints.HORIZONTAL,
            new Insets(5, 5, 5, 5), 0, 0));
        panel.add(threshold,
            new GridBagConstraints(3, 2, 2, 1, 0.0, 0.0,
            GridBagConstraints.CENTER,
            GridBagConstraints.NONE,
            new Insets(5, 20, 5, 5), 0, 0));
        


        int result = JOptionPane.showConfirmDialog(null, panel, "Packet generator configuration", JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {
            String dataText = data.getText();
            String thresholdText = threshold.getText();
            

            if (!dataText.isEmpty() && !thresholdText.isEmpty()) {
                // Do it like this for now
                GeneratorType selected = null;
                if (fixedButton.isSelected()) {
                    selected = GeneratorType.Fixed;
                } else if (probabilityButton.isSelected()) {
                    selected = GeneratorType.Probability;
                }
                
                return Optional.of(new GeneratorConfig(selected, dataText, thresholdText));
            }
        }
        return Optional.empty();
    }

    private record GeneratorConfig(GeneratorType type, String data, String threshold) {}

    private enum GeneratorType {
        Fixed("Generate every {x} cycles"), 
        Probability("{x} probability to generate packet / cycle");

        private final String description;

        GeneratorType(String description) {
            this.description = description;
        }

        public String getDescription() {
            return this.description;
        }
    }
}
