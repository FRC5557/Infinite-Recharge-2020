package net.bancino.robotics.swerveio;

import java.io.PrintStream;
import java.io.Serializable;
import java.util.ArrayList;

/**
 * A basic "point" collector that collects the state of the values being passed
 * into the swerve drive every iteration. This can be used to "play back" a run.
 * That is, you can record inserted points and then play them back on the swerve
 * drive, effectively creating an autonomous that performs a pre-recorded
 * action. Traditionally, a button press will start collecting points, another
 * button press will stop collecting points, and a third button press will play
 * back the collected points.
 * 
 * @author Jordan Bancino
 */
public class VectorCollector implements Serializable {

    private static final long serialVersionUID = 4031466732994117907L;

    private ArrayList<SwerveVector> points = new ArrayList<>();
    /* The current location in the points list. */
    private int cursor = 0;

    /**
     * Collect a point and seek it, moving the cursor forward. This should be run
     * iteratively, so that this object can construct a list of operations to run.
     * 
     * @param v The vector value to collect.
     * @return A point representing the current values.
     */
    public SwerveVector collect(SwerveVector v) {
        points.add(cursor, v);
        return seek();
    }

    /**
     * Reset the cursor, this should be run after all points are collected, and
     * before they are played back.
     */
    public void rewind() {
        cursor = 0;
    }

    /**
     * Retrieve the current point and set the cursor forward to the next one.
     * 
     * @return The point located at the cursor.
     */
    public SwerveVector seek() {
        SwerveVector p = peek();
        cursor++;
        return p;
    }

    /**
     * Retrieve the current point without setting the cursor forward.
     * 
     * @return The point located at the cursor.
     */
    public SwerveVector peek() {
        return peekAt(cursor);
    }

    /**
     * Get the point at a specific time. This is useful for manual seeking instead
     * seeking sequentially using seek().
     * 
     * @param index The index of the point you wish to retrieve.
     * @return The point located at the given index.
     */
    public SwerveVector peekAt(int index) {
        return points.get(index);
    }

    /**
     * Pretty-print every single point in this collector, sequentially.
     * 
     * @param out The output stream to print to.
     */
    public void prettyPrint(PrintStream out) {
        prettyPrint(out, 0, points.size());
    }

    /**
     * Pretty-print the point at the specified index.
     * 
     * @param out   The output stream to print to.
     * @param point The index of the point to print.
     */
    public void prettyPrint(PrintStream out, int point) {
        SwerveVector p = peekAt(point);
        out.printf("[SwerveVector: %4d] %s\n", point, p.toString());
    }

    /**
     * Pretty-print a section of points.
     * 
     * @param out   The outputstream to print to.
     * @param start The starting point to print (inclusive).
     * @param end   The point to end at (exclusive).
     */
    public void prettyPrint(PrintStream out, int start, int end) {
        for (int i = start; i < end; i++) {
            prettyPrint(out, i);
        }
    }
}