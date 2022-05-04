package environment;

import java.util.*;
import java.util.function.Predicate;

import java.util.regex.Pattern;
import java.util.regex.Matcher;

import util.Pair;

/**
 * A class to represent a 2-dimensional coordinate.
 */
public class Coordinate extends Pair<Integer, Integer> {

    public Coordinate(int x, int y) {
        super(x, y);
    }

    /**
     * Converts a string of the format "(%d, %d)...(%d, %d)" to a list of coordinates
     *
     * @param data  The data string (format: "(%d, %d)...(%d, %d)")
     *
     * @return      A list of coordinates
     */
    public static List<Coordinate> string2Coordinates(String data){
        Pattern p = Pattern.compile("\\d+"); // A pattern to search for int's...
        Matcher m = p.matcher(data); // ... in the given string: data
        List<Coordinate> coordinates = new ArrayList<>();
        while (m.find()) { // As long as a int can be found in this string
            int x = Integer.parseInt(m.group()); // Save the x-coordinate
            m.find(); // Find the next int
            int y = Integer.parseInt(m.group()); // Save the y-coordinate
            coordinates.add(new Coordinate(x, y)); // Store the coordinates in a list
        }
        return coordinates;
    }

    //    public static Set<Coordinate> string2Coordinates(String data){
//        Pattern p = Pattern.compile("\\d+"); // A pattern to search for int's...
//        Matcher m = p.matcher(data); // ... in the given string: data
//        Set<Coordinate> coordinates = new HashSet<>();
//        while (m.find()) { // As long as a int can be found in this string
//            int x = Integer.parseInt(m.group()); // Save the x-coordinate
//            m.find(); // Find the next int
//            int y = Integer.parseInt(m.group()); // Save the y-coordinate
//            coordinates.add(new Coordinate(x, y)); // Store the coordinates in a list
//        }
//        return coordinates;
//    }

    /**
     * Converts a list of coordinates to a string of "(%d, %d)...(%d, %d)" format
     *
     * @param coordinatesList   The list of coordinates
     *
     * @return      The list of coordinates converted to the aforementioned string format
     */
    public static String coordinates2String(List<Coordinate> coordinatesList){
        String data = "";
        for(Coordinate coordinate: coordinatesList){
            data += coordinate.toString();
        }
        return data;
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

    public Coordinate divideBy(int divider) {
        return new Coordinate((int) first/divider, (int) second/divider);
    }

    /**
     * Computes the "maximum coordinate distance" (= the highest coordinate difference in absolute value)
     * between 2 pair of coordinates.
     *
     * @param other     The other pair of coordinates
     *
     * @return          The distance between the 2 pair of coordinates
     */
    public int distanceFrom(Coordinate other){
        return distanceFrom(other, "MaxCoordinateDistance");
    }

    /**
     * Computes the distance between 2 pair of coordinates.
     *
     * @param other     The other pair of coordinates
     * @param method    "MaxCoordinateDistance": the highest coordinate difference in absolute value
     *                  "ManhattanDistance": the sum of the coordinates difference in absolute values
     *
     * @return          The distance between the 2 pair of coordinates
     */
    public int distanceFrom(Coordinate other, String method){
        Coordinate diff = this.diff(other);

        if (method.equals("ManhattanDistance")){
            return Math.abs(diff.getX()) + Math.abs(diff.getY());
        }


        // As the agent can move diagonally, distances should be measured by the maximum of the
        // coordinates difference in absolute values.
        // if (method.equals("MaxCoordinateDistance"))
        return Math.max(Math.abs(diff.getX()), Math.abs(diff.getY()));
    }

    /**
     * Finds the closest (in terms of "maximum coordinate distance") pair of coordinates
     * from a list of coordinates pairs to the given coordinates pair
     *
     * @param coordinatesList    A list of coordinates pairs
     *
     * @return                   The closest pair of coordinates
     */
    public Coordinate closestCoordinatesTo(List<Coordinate> coordinatesList) {
        if (coordinatesList.isEmpty())
            return null;

        List<Integer> distances = coordinatesList.stream().map(c -> c.distanceFrom(this)).toList();
        int minDistanceIndex = distances.indexOf(Collections.min(distances));
        Coordinate destinationCoordinates = coordinatesList.get(minDistanceIndex);
        return destinationCoordinates;
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
