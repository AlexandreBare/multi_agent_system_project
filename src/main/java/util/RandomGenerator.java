package util;

import java.util.Locale;

public record RandomGenerator(double chance) implements Generator {

    @Override
    public boolean shouldGenerate() {
        return Math.random() <= this.chance;
    }

    @Override
    public String generateEnvironmentString() {
        return String.format(Locale.ENGLISH, "Double %.4f", this.chance);
    }

}
