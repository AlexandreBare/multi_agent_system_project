package util;

public interface Generator {
    boolean shouldGenerate();

    String generateEnvironmentString();
}
