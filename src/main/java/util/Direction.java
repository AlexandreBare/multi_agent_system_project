package util;

import java.util.Arrays;

/**
 * Helper class to convert directions from int to an own String
 * representation and vice versa.
 */
public enum Direction {

    NORTH(0, -1, Direction.NORTH_ID), 
    EAST(1, 0, Direction.EAST_ID),
    SOUTH(0, 1, Direction.SOUTH_ID),
    WEST(-1, 0, Direction.WEST_ID);



    public static final int NORTH_ID = 0;
    public static final int EAST_ID = 1;
    public static final int SOUTH_ID = 2;
    public static final int WEST_ID = 3;
    

    private final int xChange;
    private final int yChange;
    private final int directionId;

    

    Direction(int xChange, int yChange, int id) {
        this.xChange = xChange;
        this.yChange = yChange;
        this.directionId = id;
    }

    public int getXChange() {
        return xChange;
    }

    public int getYChange() {
        return yChange;
    }

    public int getId() {
        return directionId;
    }


    public static Direction valueOfByName(String name) {
        return Direction.valueOf(name.toUpperCase());
    }

    public static Direction valueOfById(int id) {
        return Arrays.stream(Direction.values())
            .filter(d -> d.getId() == id)
            .findFirst().orElseThrow();
    }
}
