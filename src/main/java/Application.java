import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import javax.swing.UIManager;

import gui.setup.MainMenu;

/**
 * Main class for the packet world application.
 */
public class Application {
    
    public static void main(String[] args) {

        final Level LOG_LEVEL = Level.SEVERE;

        // For now this sufficies (simplifies things a lot)
        Logger rootLogger = LogManager.getLogManager().getLogger("");
        rootLogger.setLevel(LOG_LEVEL);
        Arrays.stream(rootLogger.getHandlers()).forEach(h -> h.setLevel(LOG_LEVEL));


        // String path = ApplicationRunner.class
        //         .getClassLoader()
        //         .getResource("logging.properties")
        //         .getFile();
        // System.setProperty("java.util.logging.config.file", path);


        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception ignored) {}

        MainMenu mm = new MainMenu();
        mm.setVisible(true);
    }
}
