package gui.video;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;


public class VideoFrame extends JFrame {

    /**
     * Initializes a new VideoFrame object
     *
     * @param videoPanel The video panel of the application
     * @param userPanel  The user panel of the application
     */
    public VideoFrame(VideoPanel videoPanel, UserPanel userPanel) {
        setTitle("Video for the agent application");
        Toolkit tk = Toolkit.getDefaultToolkit();
        Dimension d = tk.getScreenSize();
        int height = d.height;
        int width = d.width;
        setLocation(550, 0);
        setSize(17 * width / 30, 18 * height / 20);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });

        Container contentPane = getContentPane();
        contentPane.add(videoPanel, "Center");
        videoPanel.initiate();
        contentPane.add(userPanel, "South");
    }
}
