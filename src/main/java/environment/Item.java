package environment;

import gui.video.Drawer;

/**
 * A class for objects that can be put on certain positions in a 'World'.
 */
abstract public class Item<T extends Representation> implements Representable<T>, Cloneable {

    /**
     * This item's x-coordinate
     */
    private int x;

    /**
     * This item's y-coordinate
     */
    private int y;

    /**
     * Initializes a new Item instance
     *
     * @param x  X-coordinate of the Item
     * @param y  Y-coordinate of the Item
     */
    protected Item(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Set this item's coordinates to the given values.
     *
     * @param x  X-coordinate
     * @param y  Y-coordinate
     */
    public void moveTo(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Gets this item's X coordinate
     */
    public int getX() {
        return x;
    }

    /**
     * Gets this item's Y coordinate
     */
    public int getY() {
        return y;
    }

    /**
     * Sets the X coordinate of this item
     * @param nx The new 'x' value
     */
    protected void setX(int nx) {
        this.x = nx;
    }

    /**
     * Sets the Y coordinate of this item
     * @param ny The new 'y' value
     */
    protected void setY(int ny) {
        this.y = ny;
    }


    /**
     * Gets a representation for this item
     */
    @Override
    abstract public T getRepresentation();

    /**
     * Draw this item.
     * @param drawer The drawing visitor
     */
    abstract public void draw(Drawer drawer);

}
