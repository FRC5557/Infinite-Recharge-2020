package net.bancino.robotics.swerveio.gyro;

/**
 * This interface represents a gyro that can be used in a swerve drivetrain.
 * If we don't have support for your gyro, simply implement this interface.
 * 
 * @author Jordan Bancino
 * @since 2.0.0
 * @version 2.0.0
 */
public interface AbstractGyro {

    /**
     * Get the angle of this gyro.
     * 
     * @return The yaw angle, in degrees. This should go from 0 to 360. If the sensor does not return
     *         the angle in terms of 0 to 360, then this function should convert it. For instance, if
     *         the sensor returns 0 to -180 and 0 to 180, you'll have to scale it to 0 to 360 here.
     */
    public double getAngle();

    /**
     * Zero the gyro, by setting the current angle to zero. If the
     * gyro hardware does not support this, a software offset should
     * be performed in this method.
     */
    public void zero();
}