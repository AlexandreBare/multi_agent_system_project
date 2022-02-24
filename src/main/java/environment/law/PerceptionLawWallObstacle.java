package environment.law;

import environment.Perception;
import environment.world.wall.WallRep;

/**
 * A PerceptionLaw for not allowing agents to perceive Items that are
 * situated behind a Wall Item from their point of view. They will only
 * receive general information about these Items, such as their
 * coordinates.
 */
public class PerceptionLawWallObstacle implements PerceptionLaw {

    /**
     * Initializes a new PerceptionLawWallObstacle instance
     */
    public PerceptionLawWallObstacle() {}

    /**
     * Enforces this PerceptionLaw on a given Perception 'perception'.
     * All Representations that are situated behind a WallRep relative to the
     * perceiving agent should be generalised as 'Representations', thus removing
     * any specific information about them.
     *     
     * Only works for the WallWorld show in the presentation of this thesis.
     * This algorithm is purely made for this demonstration.
     * To get a realistic effect a much (much) more complicated algorithm
     * is needed.
     *
     * @param perception The perception on which we will enforce this perception law
     * @return     A perception containing only unspecified 'Representations'
     *             for those representations that are situated behind a wall
     *             relative to the perceiving agent.
     */
    public Perception enforce(Perception perception) {
        Perception newPerception = new Perception(perception.getWidth(), perception.getHeight(),
                                            perception.getOffsetX(), perception.getOffsetY());
        int aX = perception.getSelfX();
        int aY = perception.getSelfY();
        for (int tX = 0; tX < perception.getWidth(); tX++) {
            for (int tY = 0; tY < perception.getHeight(); tY++) {
                if (obstructed(perception, tX, tY)) {
                    newPerception.nullifyCellAt(tX, tY);
                } else {
                    newPerception.setCellPerceptionAt(tX, tY, perception.getCellAt(tX, tY));
                }
            }
        }
        //newPerception.setSelf(perception.getSelf(), aX, aY);
        newPerception.setSelfX(aX);
        newPerception.setSelfY(aY);
        return newPerception;
    }

    public static boolean obstructed(Perception perception, int x1, int y1) {
        int x0 = perception.getSelfX();
        int y0 = perception.getSelfY();
        int dx =  Math.abs(x1 - x0);
        int sx = x0 < x1 ? 1 : -1;
        int dy = -Math.abs(y1 - y0);
        int sy = y0 < y1 ? 1 : -1;
        int err = dx + dy;  /* error value e_xy */
        int e2;
        while (true) {  /* loop */
            if (x0 == x1 && y0 == y1) break;
            e2 = 2 * err;
            if (e2 >= dy) { /* e_xy+e_x > 0 */
                if (x0 == x1) break;
                err += dy;
                x0 += sx;
            }
            if (e2 <= dx) { /* e_xy+e_y < 0 */
                if (y0 == y1) break;
                err += dx;
                y0 += sy;
            }
            if (0 <= x0 && x0 < perception.getWidth() 
                    && 0 <= y0 && y0 < perception.getHeight() 
                    && perception.getCellAt(x0, y0).getRepOfType(WallRep.class) != null 
                    && !perception.getCellAt(x0, y0).getRepOfType(WallRep.class).isSeeThrough()) {
                return true;
            }
        }
        return false;
    }

}
