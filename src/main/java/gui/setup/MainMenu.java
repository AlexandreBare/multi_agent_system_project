package gui.setup;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import gui.editor.EnvironmentBuilder;
import gui.video.BatchMAS;

/**
 * Starting frame to select the application mode to start.
 */
public class MainMenu extends JFrame {

    //--------------------------------------------------------------------------
    //		CONSTRUCTOR
    //--------------------------------------------------------------------------

    /**
     * Initializes a new MainMenu instance
     */
    public MainMenu() {

        // Set the window title
        setTitle("PacketWorld - main menu");

        // Set the toolkit
        Toolkit tk = Toolkit.getDefaultToolkit();

        // Set size & location
        Dimension d = tk.getScreenSize();
        int height = d.height;
        int width = d.width;
        setLocation(width / 8, height / 8);
        setSize(width / 4, height / 3);

        // What has to happen when the user wants to close the window
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });

        // Create the main panel
        JPanel menuPanel = new JPanel(new GridLayout(4, 1, 0, 10));
        getContentPane().setLayout(new BorderLayout(25, 25));
        getContentPane().add(new JLabel(), BorderLayout.NORTH);
        getContentPane().add(new JLabel(), BorderLayout.SOUTH);
        getContentPane().add(new JLabel(), BorderLayout.EAST);
        getContentPane().add(new JLabel(), BorderLayout.WEST);

        getContentPane().add(menuPanel, BorderLayout.CENTER);

        // Make parts and add them to the menu panel.
        JLabel title = new JLabel("Select the program mode to launch :\n", SwingConstants.CENTER);
        menuPanel.add(title);
        JButton guiButton = new JButton("Normal mode");
        guiButton.addActionListener(evt -> {
            GUISetup guiSetup = new GUISetup();
            guiSetup.selectSettings();
            setVisible(false);
        });
        menuPanel.add(guiButton);


        JButton batchButton = new JButton("Batch mode");
        batchButton.addActionListener(evt -> {
            BatchMAS batchMAS = new BatchMAS();
            batchMAS.setVisible(true);
            setVisible(false);
        });
        menuPanel.add(batchButton);

        JButton editorButton = new JButton("World Editor");
        editorButton.addActionListener(evt -> {
            EnvironmentBuilder builder = new EnvironmentBuilder();
            builder.init();
            builder.setVisible(true);
            setVisible(false);
        });
        menuPanel.add(editorButton);
    }
}
