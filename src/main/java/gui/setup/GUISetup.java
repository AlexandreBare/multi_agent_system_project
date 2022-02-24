package gui.setup;

import javax.swing.JFrame;

import environment.ApplicationRunner;
import gui.video.BehaviorWatch;
import gui.video.EventHistoryMonitor;
import gui.video.UserPanel;
import gui.video.VideoFrame;
import gui.video.VideoPanel;

public class GUISetup {
    
    private UserPanel userPanel;
    private VideoPanel videoPanel;
    private EventHistoryMonitor ehm = null;
    public boolean guiOutput = true;

    private final ApplicationRunner applicationRunner;

    

    public GUISetup() {        
        this.applicationRunner = new ApplicationRunner();
    }


    /**
     * Start the gui for this application
     *
     * @post A VideoFrame containing the grid and it's contents is created and shown.
     */
    public void startGui() {
        var eventBus = this.applicationRunner.getEventBus();

        BehaviorWatch bw = new BehaviorWatch(this.applicationRunner.getActiveItemContainer());
        bw.initialize();
        bw.setVisible(true);
        eventBus.register(bw);

        this.ehm = new EventHistoryMonitor(this.applicationRunner);
        ehm.initialize();
        ehm.setVisible(true);

        videoPanel = new VideoPanel();
        videoPanel.setEnvironment(this.applicationRunner.getEnvironment());
        eventBus.register(videoPanel);

        userPanel = new UserPanel(this, videoPanel);

        VideoFrame videoFrame = new VideoFrame(videoPanel, userPanel);
        videoFrame.setVisible(true);
    }



    public void selectSettings() {
        JFrame worldConfigurationFrame = new WorldConfigurationFrame(this);
        worldConfigurationFrame.setVisible(true);
    }


    public void reset() {
        applicationRunner.reset();
        videoPanel.setEnvironment(applicationRunner.getEnvironment());
        
        if (this.ehm != null) {
            this.ehm.reset();
        }
        if (this.userPanel != null) {
            this.userPanel.reset();
        }
    }



    public ApplicationRunner getApplicationRunner() {
        return this.applicationRunner;
    }
}
