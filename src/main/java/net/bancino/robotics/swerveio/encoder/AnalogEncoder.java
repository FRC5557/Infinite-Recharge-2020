package net.bancino.robotics.swerveio.encoder;

import edu.wpi.first.wpilibj.AnalogInput;
import edu.wpi.first.wpilibj.RobotController;
import net.bancino.robotics.swerveio.exception.SwerveImplementationException;

/**
 * A representation of the encoders that the MK2 module uses. These are plugged
 * into the roboRIO directly, via the analog inputs.
 * 
 * @author Jordan Bancino
 * @version 1.3.0
 * @since 1.0.0
 */
public class AnalogEncoder implements AbstractEncoder {

    private AnalogInput encoder;
    /* AnalogInputs can't be zero'd by default, an offset must be used. */
    private double encoderOffset = 0;

    /**
     * Create an MK2 Encoder.
     * 
     * @param port The analog input port on the roboRIO that this encoder is
     *             connected to.
     */
    public AnalogEncoder(int port) {
        encoder = new AnalogInput(port);
    }

    /**
     * Create an MK2 Encoder with an initial offset. This is useful for creating an
     * encoder for a module that, in the straight position, isn't at zero on the
     * encoder. This is therefore a software fix for encoders that aren't placed
     * "straight". One use case for this would be hard-coding offsets in the robot
     * map for each module and passing them in to the encoders.
     * 
     * @param port          The analog port on the roboRIO that this encoder is
     *                      connected to.
     * @param initialOffset The starting offset to add to the raw encoder value.
     */
    public AnalogEncoder(int port, double initialOffset) {
        this(port);
        encoderOffset = initialOffset;
    }

    /**
     * These encoders are read by voltage. The voltage scales from 0 to 5 volts,
     * then resets upon the next revolution. This reading is scaled to degrees in
     * terms of 0 to 360.
     * 
     * @return An angle measure, in degrees, that the encoder shaft is rotated at.
     */
    @Override
    public double get() {
        double raw = (360 / RobotController.getVoltage5V()) * encoder.getVoltage();
        if (raw - encoderOffset < 0) {
            return raw + (360 - encoderOffset);
        } else {
            return raw - encoderOffset;
        }
    }

    /**
     * This encoder does not support setting the position, but it is accomplished by
     * this class by maintaining an offset from the current value. Therefore, if the
     * current value is 45 degrees and the zero function is called, the offset will
     * be 45, and the get() function will read 0 because the offset is subtracted
     * from the current value.
     * 
     * @param val The new value to set the encoder to.
     */
    @Override
    public void set(double val) throws SwerveImplementationException {
        encoderOffset = get() - val;
    }

    @Override
    public double countsPerRevolution() {
        return 360;
    }

    /**
     * Get the offset from the real value that this encoder is reading. You find the
     * encoder's real value by performing a get(), then adding this offset. This
     * defaults to zero, and only changes if the set() function is called.
     * 
     * @return The offset that this encoder is applying to it's actual raw value.
     */
    public double getOffset() {
        return encoderOffset;
    }
}