package environment;

import environment.ActiveItemID.ActionPriority;
import synchronizer.Synchronization;

/**
 * A class for active items. Active items are Items with an ID and a view.
 */
abstract public class ActiveItem<T extends Representation> extends Item<T> {

    private final ActiveItemID id;
    private int view;

    

    /**
     * Constructor.
     *
     * @param x    the x-coordinate for this ActiveItem
     * @param y    the y-coordinate for this ActiveItem
     * @param view the view range for this ActiveItem
     * @param id   the ID for this ActiveItem
     */
    protected ActiveItem(int x, int y, int view, ActiveItemID id) {
        super(x, y);
        this.id = id;
        this.view = view;
    }

    /**
     * Gets the ID of this ActiveItem.
     * @return  the ID value
     */
    public ActiveItemID getID() {
        return id;
    }


    public ActionPriority getPriority() {
        return this.id.getActionPriority();
    }

    /**
     * Gets the view range of this ActiveItem
     * @return  the view range
     */
    public int getView() {
        return view;
    }

    /**
     * Sets the view range for this ActiveItem
     * @param  view  the new view range
     */
    public void setView(int view) {
        this.view = view;
    }


    public abstract ActiveImp generateImplementation(Environment env, Synchronization synchronizer);

}
