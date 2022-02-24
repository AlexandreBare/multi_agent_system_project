package util;

import java.io.File;

public class Variables {

    public static final String BASE_PATH = System.getProperty("user.dir");

    public static final String CONFIG_PATH = BASE_PATH + File.separator + "configfiles" + File.separator;
    public static final String OUTPUT_PATH = BASE_PATH + File.separator + "output" + File.separator;

    public static final String IMPLEMENTATIONS_PATH = Variables.CONFIG_PATH + "implementations" + File.separator;
    public static final String ENVIRONMENTS_PATH = Variables.CONFIG_PATH + "environments" + File.separator;

    public static final String WORLD_PROPERTIES_FILE = Variables.CONFIG_PATH + "worlds.properties";
    public static final String LAWS_PROPERTIES_FILE = Variables.CONFIG_PATH + "lawsoftheuniverse.properties";
    public static final String PERCEPTION_LAWS_PROPERTIES_FILE = Variables.CONFIG_PATH + "perceptionlaws.properties";
}
