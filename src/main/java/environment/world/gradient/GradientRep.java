package environment.world.gradient;

import environment.Representation;

public class GradientRep extends Representation {

    private final int value;
    

    protected GradientRep(int x, int y, int value) {
        super(x, y);
        this.value = value;
    }


    public int getValue() {
        return this.value;
    }

    @Override
    public char getTypeChar() {
        return 'g';
    }

    @Override
    public boolean isWalkable() {
        return true;
    }
}
