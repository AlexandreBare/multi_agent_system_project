package environment;

import java.util.Objects;
import java.util.function.Predicate;

import util.Pair;

/**
 * A class to represent a 2-dimensional coordinate.
 */
public class Coordinate extends Pair<Integer, Integer> {

    public Coordinate(int x, int y) {
        super(x, y);
    }

    public int getX() {
        return this.first;
    }

    public int getY() {
        return this.second;
    }

    public String toString() {
        return String.format("(%d,%d)", this.getX(), this.getY());
    }

    public Coordinate diff(Coordinate other) {
        return new Coordinate(first - other.first, second - other.second);
    }

    public Coordinate add(Coordinate other) {
        return new Coordinate(first + other.first, second + other.second);
    }

    public boolean any(Predicate<Integer> pred) {
        return pred.test(first) || pred.test(second);
    }

    public boolean all(Predicate<Integer> pred) {
        return pred.test(first) && pred.test(second);
    }

    /**
     * Returns a new Coordinate containing the sign of this (-1, 0 or 1)
     */
    public Coordinate sign() {
        int newFst = 0, newSnd = 0;

        if (first != 0)
            newFst = first < 0 ? -1 : 1;
        if (second != 0)
            newSnd = second < 0 ? -1 : 1;

        return new Coordinate(newFst, newSnd);
    }


    @Override
    public boolean equals(Object other) {
        if (! (other instanceof Coordinate casted))
            return false;

        if (casted.getX() != getX())
            return false;

        return casted.getY() == getY();
    }

    @Override
    public int hashCode() {
        return Objects.hash(first, second);
    }

}
