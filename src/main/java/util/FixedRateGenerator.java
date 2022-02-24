package util;

public class FixedRateGenerator implements Generator {
    
    private int index;
    private final int frequency;

    public FixedRateGenerator(int frequency) {
        this.frequency = frequency;
    }

    @Override
    public boolean shouldGenerate() {
        return this.index++ % this.frequency == 0;
    }


    @Override
    public String generateEnvironmentString() {
        return String.format("Integer %d", this.frequency);
    }
}
