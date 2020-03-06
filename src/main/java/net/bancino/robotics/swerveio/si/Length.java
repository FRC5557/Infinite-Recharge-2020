package net.bancino.robotics.swerveio.si;

/**
 * A length object that holds an abstract value which can be converted
 * to any desired unit.
 * 
 * @author Jordan Bancino
 * @version 1.3.0
 * @since 1.3.0
 */
public class Length {

    private final Unit baseUnit;
    private final double baseValue;
    
    /**
     * Create an immutable length object.
     *
     * @param value The numerical value of this length.
     * @param unit  The unit that the value is in.
     */
    public Length(double value, Unit unit) {
        if (value > 0) {
            this.baseValue = value;
            this.baseUnit = unit;
        } else {
            throw new IllegalArgumentException("Lengths cannot be negative.");
        }
    }

    /**
     * Get this length in the specifed units.
     * @param unit The unit to retrieve the length in.
     * @return     The value of the length in the specified units.
     */
    public double get(Unit unit) {
        return (baseValue * baseUnit.conversionFactor()) / unit.conversionFactor();
    }

    /**
     * Add two lengths.
     * 
     * @param length The length to add.
     * @return The resulting length from the operation.
     */
    public Length add(Length length) {
        return new Length(baseValue + length.get(baseUnit), baseUnit);
    }

    /**
     * Subtract two lengths.
     * 
     * @param length The length to subract.
     * @return The resulting length from the operation.
     */
    public Length subtract(Length length) {
        return new Length(baseValue - length.get(baseUnit), baseUnit);
    }

    /**
     * Multiply two lengths.
     * 
     * @param length The length to multiply.
     * @return The resulting length from the operation.
     */
    public Length multiply(Length length) {
        return new Length(baseValue * length.get(baseUnit), baseUnit);
    }

    /**
     * Divide two lengths.
     * 
     * @param length The length to divide.
     * @return The resulting length from the operation.
     */
    public Length divide(Length length) {
        return new Length(baseValue / length.get(baseUnit), baseUnit);
    }
}