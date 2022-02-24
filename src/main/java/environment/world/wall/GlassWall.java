package environment.world.wall;

import java.util.logging.Logger;

import gui.video.Drawer;

public class GlassWall extends Wall {

    private final Logger logger = Logger.getLogger(GlassWall.class.getName());

    public GlassWall(int x, int y) {
        super(x, y);
        this.logger.fine(String.format("Glass wall created at %d %d", x, y));
    }

    @Override
    public GlassWallRep getRepresentation() {
        return new GlassWallRep(getX(), getY());
    }

    @Override
    public void draw(Drawer drawer) {
        drawer.drawGlassWall(this);
    }


    public String generateEnvironmentString() {
        return String.format("%s\n", this.getClass().getName()) +
                String.format("nbArgs %d\n", 2) +
                String.format("Integer %d\n", this.getX()) +
                String.format("Integer %d\n", this.getY());
    }
}
