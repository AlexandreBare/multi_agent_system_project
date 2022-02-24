package environment.world.wall;

public class GlassWallRep extends WallRep {

    protected GlassWallRep(int x, int y) {
        super(x, y);
    }

    @Override
    public char getTypeChar() {
        return 'G';
    }

    @Override
    public boolean isWalkable() {
        return false;
    }
    
    @Override
    public boolean isSeeThrough() {
        return true;
    }
}
