package net.bancino.robotics.swerveio.encoder;

import net.bancino.robotics.swerveio.exception.SwerveImplementationException;

/**
 * A representation of an encoder. This can be used in swerve module
 * implementations. This interface is generally very easy to implement and
 * implementations often require very little code, it is simply a wrapper for
 * the position functions on the encoder.
 * 
 * @author Jordan Bancino
 */
public interface AbstractEncoder {

    /**
     * Get the encoder's current position.
     * 
     * @return The encoder's current position. Note that this may not always be
     *         double precision, it could be an int or whatever. This should be a
     *         raw value straight from the hardware. If adjustments need to occour,
     *         they can do so within reason here. Module-specific modifications
     *         should occour in the swerve module's encoder-related functions.
     */
    public double get();

    /**
     * Set the encoder's position.
     * 
     * @param val The value to set the encoder to.
     * @throws SwerveImplementationException If the underlying encoder does not
     *                                       support setting the position.
     */
    public void set(double val) throws SwerveImplementationException;

    /**
     * Zero the encoder. The default implementation is the equal to calling set(0).
     * 
     * @throws SwerveImplementationException If the underlying encoder does not
     *                                       support setting the position.
     */
    public default void zero() throws SwerveImplementationException {
        set(0);
    }

    /**
     * Get the number of counts it takes this encoder to do one full revolution.
     * 
     * @return An arbitrary number of units it takes to go one full revolution of
     *         the encoder shaft, or -1 if unknown.
     */
    public double countsPerRevolution();
}