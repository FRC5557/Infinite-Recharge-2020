package net.bancino.robotics.swerveio;

/**
 * An immutable container for a swerve drive vector, which holds the forward,
 * strafe, and rotation values for a single iteration.
 * 
 * @author Jordan Bancino
 * @version 1.2.2
 * @since 1.0.0
 */
public class SwerveVector {
    private final double fwd, str, rcw;

    /**
     * Construct a point.
     * 
     * @param fwd The forward (Y) value for this point.
     * @param str The strafe (X) value for this point.
     * @param rcw The rotation (Z) value for this point.
     */
    public SwerveVector(double fwd, double str, double rcw) {
        this.fwd = fwd;
        this.str = str;
        this.rcw = rcw;
    }

    /**
     * @return The fwd value for this point.
     */
    public double getFwd() {
        return fwd;
    }

    /**
     * @return The str value for this point.
     */
    public double getStr() {
        return str;
    }

    /**
     * @return The rcw value for this point.
     */
    public double getRcw() {
        return rcw;
    }

    /**
     * Add a swerve vector to this one.
     * @param v The vector to add to this one.
     * @return  The resulting vector from adding all the components together.
     */
    public SwerveVector add(SwerveVector v) {
        return new SwerveVector(fwd + v.getFwd(), str + v.getStr(), rcw + v.getRcw());
    }

    /**
     * @return A string representation of this point, showing all the values it
     *         holds.
     */
    @Override
    public String toString() {
        return String.format("FWD: %6f \t STR: %6f \t RCW: %6f", getFwd(), getStr(), getRcw());
    }
}
