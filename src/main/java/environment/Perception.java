package environment;


import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import gui.video.ItemDrawer.LinePoints;

/**
 *  A class for perceptions for agents. A Perception is a part of the World, perceived
 *  by an Agent. A Perception will be filled with Representations of the Items that
 *  Agent sees.
 */
public class Perception {

    /**
     * The height of this Perception.
     */
    private int height;

    /**
     * The width of this Perception.
     */
    private int width;


    /**
     * The x-coordinate of self within this Perception.
     */
    private int selfX;

    /**
     * The y-coordinate of self within this Perception.
     */
    private int selfY;

    /**
     * The absolute x-coordinate of the (0,0) position of this Perception.
     */
    private int offsetX;

    /**
     * The absolute y-coordinate of the (0,0) position of this Perception.
     */
    private int offsetY;

    private final CellPerception[][] cells;



    //--------------------------------------------------------------------------
    //		CONSTRUCTOR
    //--------------------------------------------------------------------------

    /**
     *  Initializes a new Perception object
     *
     * @param  width    width of the Perception
     * @param  height   height of the Perception
     * @param  offsetX  horizontal offset of the Perception
     * @param  offsetY  vertical offset of the Perception
     */
    public Perception(int width, int height, int offsetX, int offsetY) {
        cells = new CellPerception[width][height];
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                cells[i][j] = new CellPerception(offsetX + i, offsetY + j);
            }
        }
        setWidth(width);
        setHeight(height);
        setOffsetX(offsetX);
        setOffsetY(offsetY);
    }

    //--------------------------------------------------------------------------
    //		INSPECTORS
    //--------------------------------------------------------------------------


    /**
     * Gets a CellPerception from this Perception using absolute coordinates
     *  (the coordinates of the environment/worlds).
     *
     * @return The cell perception if x and y fall within this Perception's bounds, null otherwise.
     */
    @Nullable
    public CellPerception getCellPerceptionOnAbsPos(int x, int y) {
        int dx = x - getOffsetX();
        int dy = y - getOffsetY();
        return getCellAt(dx, dy);
    }

    /**
     * Gets a CellPerception from this Perception using coordinates
     * relative to the agent's position. x and y may be negative.
     *
     * @return The cell perception if x and y fall within this Perception's relative bounds, null otherwise.
     */
    @Nullable
    public CellPerception getCellPerceptionOnRelPos(int x, int y) {
        int dx = getSelfX() + x;
        int dy = getSelfY() + y;
        return getCellAt(dx, dy);
    }



    /**
     *  Returns all Representations that are situated right next to the agent that issued this
     *  Perception.
     *
     * @return    The Representations neighbouring the AgentRep of the agent that issued this
     *            Perception
     */
    public CellPerception[] getNeighbours() {
        CellPerception[] neighbours = new CellPerception[8]; // 8 squares surround the agentRep
        int next = 0;
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                if (i != 0 || j != 0) {
                    neighbours[next] = getCellAt(getSelfX() + i, getSelfY() + j);
                    next++;
                }
            }
        }
        return neighbours;
    }

    /**
     * Returns all Representations that are situated right next to the agent that issued this
     * Perception. The cells are in successive order in the returning array:
     *
     *        7 0 1
     *        6 A 2
     *        5 4 3
     *
     * @return    The Representations neighbouring the AgentRep of the agent that issued this
     *            Perception
     */
    public CellPerception[] getNeighboursInOrder() {
        CellPerception[] neighbours = new CellPerception[8]; // 8 squares surround the AgentRep
        int next = 0;
        neighbours[next++] = getCellPerceptionOnRelPos( 0, -1);
        neighbours[next++] = getCellPerceptionOnRelPos( 1, -1);
        neighbours[next++] = getCellPerceptionOnRelPos( 1, 0);
        neighbours[next++] = getCellPerceptionOnRelPos( 1, 1);
        neighbours[next++] = getCellPerceptionOnRelPos( 0, 1);
        neighbours[next++] = getCellPerceptionOnRelPos(-1, 1);
        neighbours[next++] = getCellPerceptionOnRelPos(-1, 0);
        neighbours[next] = getCellPerceptionOnRelPos(-1, -1);
        return neighbours;
    }




    /**
     *  Returns the distance between 2 coordinates
     *
     * @param  x1   First position's X
     * @param  y1   First position's Y
     * @param  x2   Second position's X
     * @param  y2   Second position's Y
     * @return  The distance between these coordinates
     */
    public static int distance(int x1, int y1, int x2, int y2) {
        // Bird's-eye view (number of steps an agent would need to cover the distance)
        return Math.max(Math.abs(x1 - x2), Math.abs(y1 - y2));
    }

    /**
     *  Returns the Manhattan distance between 2 coordinates
     *
     * @param  x1   First position's X
     * @param  y1   First position's Y
     * @param  x2   Second position's X
     * @param  y2   Second position's Y
     * @return  The Manhattan distance between these coordinates
     */
    public static int manhattanDistance(int x1, int y1, int x2, int y2) {
        //Manhattan distance
        return Math.abs(y2 - y1) + Math.abs(x2 - x1);
    }

    /**
     *  Returns the distance between 2 CellPerceptions
     *
     * @param ap1 the first CellPerception
     * @param ap2 the second CellPerception
     * @return    the distance between the two CellPerceptions
     */
    public static int distance(CellPerception ap1, CellPerception ap2) {
        return Math.max(Math.abs(ap2.getY() - ap1.getY()), Math.abs(ap2.getX() - ap1.getX()));
    }

    /**
     *  Returns the Manhattan distance between 2 CellPerceptions
     *
     * @param ap1 the first CellPerception
     * @param ap2 the second CellPerception
     * @return    the manhattan distance between the two CellPerceptions
     */
    public static int ManhattanDistance(CellPerception ap1, CellPerception ap2) {
        return Math.abs(ap2.getY() - ap1.getY()) + Math.abs(ap2.getX() - ap1.getX());
    }

    //--------------------------------------------------------------------------
    //		MUTATORS
    //--------------------------------------------------------------------------

    //--------------------------------------------------------------------------
    //		GETTERS & SETTERS
    //--------------------------------------------------------------------------


    /**
     *  Gets the X coordinate of the agent that issued this Perception
     *
     * @return    This perception's selfX
     */
    public int getSelfX() {
        return selfX;
    }

    /**
     *  Gets the Y coordinate of the agent that issued this Perception
     *
     * @return    This perception's selfY
     */
    public int getSelfY() {
        return selfY;
    }

    /**
     * Sets the x-coordinate of the owner of this perception
     *
     * @param  x  The x-coordinate of the self in this perception
     *            (not in the world)
     */
    public void setSelfX(int x) {
        selfX = x;
    }

    /**
     * Sets the y-coordinate of the owner of this perception
     *
     * @param  y  The y-coordinate of the self in this perception
     *            (not in the world)
     */
    public void setSelfY(int y) {
        selfY = y;
    }

    /**
     *  Sets a given Representation at the given coordinates in this perception
     *
     * @param  x     The X coordinate
     * @param  y     The Y coordinate
     * @param  cell  The Representation that is to be set here
     */
    public void setCellPerceptionAt(int x, int y, CellPerception cell) {
        cells[x][y] = cell;
    }

    /**
     *  Sets the width of this Perception
     *
     * @param  w  The new width value
     */
    private void setWidth(int w) {
        this.width = w;
    }

    /**
     *  Sets the height of this Perception
     *
     * @param  h  The new height value
     */
    private void setHeight(int h) {
        this.height = h;
    }

    /**
     *  Returns the CellPerception that is positioned on the given coordinates in this Perception.
     */
    @Nullable
    public CellPerception getCellAt(int x, int y) {
        try {
            return cells[x][y];
        } catch (ArrayIndexOutOfBoundsException exc) {
            return null;
        }
    }

    /**
     *  Gets the width of this Perception
     *
     * @return    This Perception's width
     */
    public int getWidth() {
        return width;
    }

    /**
     *  Gets the height of this Perception
     *
     * @return    This Perception's height
     */
    public int getHeight() {
        return height;
    }

    /**
     * Gets the horizontal offset of this Perception with respect to the
     * coordinates of the environment/worlds.
     *
     * @return  The horizontal offset
     */
    public int getOffsetX() {
        return this.offsetX;
    }

    /**
     * Gets the vertical offset of this Perception with respect to the
     * coordinates of the environment/worlds.
     *
     * @return  The vertical offset
     */
    public int getOffsetY() {
        return this.offsetY;
    }

    /**
     * Sets the horizontal offset of this Perception.
     *
     * @param offsetX  The new horizontal offset
     */
    private void setOffsetX(int offsetX) {
        this.offsetX = offsetX;
    }

    /**
     * Sets the vertical offset of this Perception.
     *
     * @param offsetY  The new vertical offset
     */
    private void setOffsetY(int offsetY) {
        this.offsetY = offsetY;
    }



    /**
     * Determine the shape of the view that should be drawn in the environment.
     * @return A list of LinePoints that contains all the lines that should be drawn.
     */
    public List<LinePoints> getShape() {
        List<LinePoints> shape = new ArrayList<>();

        final int maxX = cells.length;
        final int maxY = cells[0].length;

        for (int i = 0; i < maxX; i++) {
            for (int j = 0; j < maxY; j++) {
                if (getCellAt(i, j) != null) {
                    if (i + 1 >= maxX || getCellAt(i + 1, j) == null) {
                        shape.add(new LinePoints(new Coordinate(offsetX + i + 1, offsetY + j), 
                            new Coordinate(offsetX + i + 1, offsetY + j + 1)));
                    }
                    if (i - 1 < 0 || getCellAt(i - 1, j) == null) {
                        shape.add(new LinePoints(new Coordinate(offsetX + i, offsetY + j), 
                            new Coordinate(offsetX + i, offsetY + j + 1)));
                    }
                    if (j + 1 >= maxY || getCellAt(i, j + 1) == null) {
                        shape.add(new LinePoints(new Coordinate(offsetX + i, offsetY + j + 1), 
                            new Coordinate(offsetX + i + 1, offsetY + j + 1)));
                    }
                    if (j - 1 < 0 || getCellAt(i, j - 1) == null) {
                        shape.add(new LinePoints(new Coordinate(offsetX + i, offsetY + j), 
                            new Coordinate(offsetX + i + 1, offsetY + j)));
                    }
                }
            }
        }
        return shape;
    }



    public void addRep(int i, int j, Representation rep) {
        cells[i][j].addRep(rep);
    }


    public void nullifyCellAt(int i, int j) {
        try {
            cells[i][j] = null;
        } catch (ArrayIndexOutOfBoundsException ignored) {}
    }
}
