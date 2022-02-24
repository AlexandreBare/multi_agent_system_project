package gui.video;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.Dictionary;
import java.util.Hashtable;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.plaf.InsetsUIResource;

import com.google.common.eventbus.Subscribe;

import environment.ApplicationRunner;
import environment.EnergyValues;
import gui.setup.GUISetup;
import util.event.AgentActionEvent;
import util.event.MsgSentEvent;
import util.event.WorldProcessedEvent;



public class UserPanel extends JPanel {

    final JLabel scoreLabel;
    final JLabel messagesLabel;
    final JLabel deliveredLabel;
    final JLabel cyclesLabel;

    int score = 0;
    int messages = 0;
    int delivered = 0;
    int nbCycles = 0;

    final JButton playButton;
    final JButton stepButton;
    final JButton pauseButton;
    final JButton restartButton;
    final JSlider playSpeed;

    final GUISetup guiSetup;

    boolean agentsStarted = false;


    
    public UserPanel(GUISetup guiSetup, VideoPanel videoPanel) {
        this.guiSetup = guiSetup;
        ApplicationRunner applicationRunner = guiSetup.getApplicationRunner();

        setLayout(new BorderLayout());
        add(new JLabel(), "North");

        var gridBag = new GridBagLayout();
        JPanel buttonPanel = new JPanel(gridBag);
        GridBagConstraints c = new GridBagConstraints();

        c.fill = GridBagConstraints.BOTH;
        // c.anchor = GridBagConstraints.CENTER;
        c.weightx = 1;

        scoreLabel = new JLabel(" Energy = " + score);
        gridBag.setConstraints(scoreLabel, c);
        messagesLabel = new JLabel("Messages = ");
        gridBag.setConstraints(messagesLabel, c);
        deliveredLabel = new JLabel("Packets delivered = " + delivered);
        gridBag.setConstraints(deliveredLabel, c);

        cyclesLabel = new JLabel(" Cycles = " + nbCycles);
        gridBag.setConstraints(cyclesLabel, c);
        
        c.gridwidth = GridBagConstraints.REMAINDER;
        var speedLabel = new JLabel("Simulation speed");
        speedLabel.setHorizontalAlignment(JLabel.CENTER);
        gridBag.setConstraints(speedLabel, c);

        buttonPanel.add(scoreLabel);
        buttonPanel.add(messagesLabel);
        buttonPanel.add(deliveredLabel);
        buttonPanel.add(cyclesLabel);
        buttonPanel.add(speedLabel); // For spacing


        var defaultButtonSizeX = 200;
        var defaultButtonSizeY = 25;

        c.weightx = 0;
        c.gridheight = 2;

        c.fill = GridBagConstraints.NONE;
        c.gridwidth = GridBagConstraints.BOTH;
        c.insets = new InsetsUIResource(0, 0, 0, 5);
        // c.anchor = GridBagConstraints.CENTER;

        playButton = new JButton("play");
        playButton.setEnabled(true);
        playButton.setPreferredSize(new Dimension(defaultButtonSizeX, defaultButtonSizeY));
        playButton.setMaximumSize(new Dimension(defaultButtonSizeX, defaultButtonSizeY));
        gridBag.setConstraints(playButton, c);
        buttonPanel.add(playButton);


        pauseButton = new JButton("pause");
        pauseButton.setEnabled(false);
        pauseButton.setPreferredSize(new Dimension(defaultButtonSizeX, defaultButtonSizeY));
        pauseButton.setMaximumSize(new Dimension(defaultButtonSizeX, defaultButtonSizeY));
        gridBag.setConstraints(pauseButton, c);
        buttonPanel.add(pauseButton);

        stepButton = new JButton("step");
        stepButton.setEnabled(true);
        stepButton.setPreferredSize(new Dimension(defaultButtonSizeX, defaultButtonSizeY));
        stepButton.setMaximumSize(new Dimension(defaultButtonSizeX, defaultButtonSizeY));
        gridBag.setConstraints(stepButton, c);
        buttonPanel.add(stepButton);

        restartButton = new JButton("reset");
        restartButton.setEnabled(false);
        restartButton.setPreferredSize(new Dimension(defaultButtonSizeX, defaultButtonSizeY));
        restartButton.setMaximumSize(new Dimension(defaultButtonSizeX, defaultButtonSizeY));
        gridBag.setConstraints(restartButton, c);
        buttonPanel.add(restartButton);


        playButton.addActionListener(ev -> {
            if (!agentsStarted) {
                agentsStarted = true;
                applicationRunner.prepareActiveItems();
            }
            applicationRunner.play();
            this.playButton.setEnabled(false);
            this.stepButton.setEnabled(false);
            this.restartButton.setEnabled(true);
            this.pauseButton.setEnabled(true);
        });

        pauseButton.addActionListener(ev -> {
            applicationRunner.setPaused();
            this.playButton.setEnabled(true);
            this.stepButton.setEnabled(true);
            this.pauseButton.setEnabled(false);
        });

        stepButton.addActionListener(ev -> {
            if (!agentsStarted) {
                agentsStarted = true;
                applicationRunner.prepareActiveItems();
            }
            applicationRunner.step();
            this.restartButton.setEnabled(true);
        });

        restartButton.addActionListener(ev -> {
            guiSetup.reset();
            videoPanel.setEnvironment(applicationRunner.getEnvironment());
            videoPanel.repaint();
            agentsStarted = false;
            this.playButton.setEnabled(true);
            this.stepButton.setEnabled(true);
            this.pauseButton.setEnabled(false);
            this.restartButton.setEnabled(false);
        });

        playSpeed = new JSlider(0, 2000, 100);
        playSpeed.setPreferredSize(new Dimension(250, 50));
        playSpeed.setMajorTickSpacing(250);
        playSpeed.setMinorTickSpacing(50);
        playSpeed.setPaintTicks(true);
        playSpeed.setSnapToTicks(true);
        Dictionary<Integer, JLabel> ticks = new Hashtable<>();
        ticks.put(0, new JLabel("Fast"));
        ticks.put(2000, new JLabel("Slow"));
        playSpeed.setLabelTable(ticks);
        playSpeed.setPaintLabels(true);
        
        
        playSpeed.addChangeListener(e -> applicationRunner.setSpeed(playSpeed.getValue()));
        c.gridwidth = GridBagConstraints.REMAINDER;
        c.gridheight = 2;
        c.fill = GridBagConstraints.BOTH;
        gridBag.setConstraints(playSpeed, c);

        buttonPanel.add(playSpeed);
        add(new JLabel(" "), BorderLayout.CENTER);
        add(buttonPanel, "South");


        applicationRunner.getEventBus().register(this);
    }

    

    @Subscribe
    public void handleAgentActionEvent(AgentActionEvent event) {
        score += EnergyValues.calculateEnergyCost(event, false);

        if (event.getAction() == AgentActionEvent.DELIVER_PACKET) {
            delivered++;
        }

        this.repaint();
    }


    @Subscribe
    public void handleMsgSentEvent(MsgSentEvent event) {
        messages++;
        this.repaint();
    }


    @Subscribe
    void handleWorldProcessedEvent(WorldProcessedEvent e) {
        nbCycles++;
        this.repaint();
    }



    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (scoreLabel != null) {
            scoreLabel.setText(" Energy = " + score);
            messagesLabel.setText("Messages = " + messages);
            deliveredLabel.setText("Packets delivered = " + delivered);
            cyclesLabel.setText(" Cycles = " + nbCycles);
        }
    }


    public void reset() {
        this.score = 0;
        this.messages = 0;
        this.delivered = 0;
        this.nbCycles = 0;

        this.repaint();
    }
}
