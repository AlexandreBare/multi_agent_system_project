package synchronizer;

import java.io.IOException;
import java.util.logging.Logger;

import util.AsciiReader;

/**
 * A class responsible for the creation of instances of all classes of this package needed at startup time when distributed
 * synchronization is used. The creation method used by this class requires a text file from which the number of
 * PersonalSynchronizer instances are read.
 */
public class FileDistributedSynchronization extends DistributedSynchronization {

    /**
     * The file containing the number of Personal Synchronizers needed.
     */
    private final String configFile;
    
    private final Logger logger = Logger.getLogger(FileDistributedSynchronization.class.getName());

    /**
     * Initialize a new File-DistributedSynchronization instance, relying on the file <file>.
     * @param file The configFile from which the number of needed synchronizers is read.
     * @pre   file <> null
     * @post  new.configFile==file.
     */
    public FileDistributedSynchronization(String file) {
        configFile = file;
    }

    /**
     * Create the number of Personal Synchronizers mentioned in configFile.
     */
    public void createSynchroPackage() {
        int nbSynchros = 0;
        try {
            AsciiReader reader = new AsciiReader(configFile);
            reader.check("nbSynchros");
            nbSynchros = reader.readInt();
        } catch (IOException e) {
            this.logger.severe(String.format("Error when opening file: %s", configFile));
        } catch (Exception e) {
            this.logger.severe(e.getMessage());
        }
        for (int i = 0; i < nbSynchros; i++) {
            synchros[i] = new PersonalSynchronizer(i);
        }
    }
}
