package net.bancino.robotics.swerveio.si;

/**
 * A unit with conversion factors based on centimeters, the smalled unit
 * you'd realistically need.
 * 
 * @author Jordan Bancino
 * @version 1.3.0
 * @since 1.3.0
 */
public enum Unit {
    INCHES(2.54), FEET(30.48), CENTIMETERS(1), METERS(100);

    public static final Unit BASE_UNIT = CENTIMETERS;

    private double conversionFactor;

    /**
     * Construct a new unit with the given conversion factor
     * @param conversionFactor How many centimeters are in this unit.
     */
    Unit(double conversionFactor) {
        this.conversionFactor = conversionFactor;
    }

    /**
     * Get the conversion factor of this unit. This is how many centimeters
     * are equivalent to this unit.
     *
     * @return The number of centimeters in this unit.
     */
    public double conversionFactor() {
        return conversionFactor;
    }
}