package net.bancino.robotics.swerveio.module;

import net.bancino.robotics.swerveio.pid.AbstractPIDController;
import net.bancino.robotics.swerveio.si.Length;

/**
 * A swerve module definition that swerve drive implementations should use to
 * drive a module. These methods should be standardized and implemented
 * similarly across variations in motor controllers and motors. This class is
 * the sole abstraction layer between the hardware-specific API and the SwerveIO
 * API.
 * 
 * @author Jordan Bancino
 * @author Ethan Snyder
 */
public interface AbstractSwerveModule {

    /**
     * Set the speed of the pivot motor. A negative value is the reverse direction.
     * 
     * @param speed The speed to set the motor to.
     */
    public void setPivotMotorSpeed(double speed);

    /**
     * Set the speed of the drive motor. A negative value is the reverse direction.
     * 
     * @param speed The speed to set the motor to.
     */
    public void setDriveMotorSpeed(double speed);

    /**
     * Get the currently set speed of the pivot motor.
     * 
     * @return The speed of the motor.
     */
    public double getPivotMotorSpeed();

    /**
     * Get the currently set speed of the drive motor.
     * 
     * @return The speed of the motor.
     */
    public double getDriveMotorSpeed();

    /**
     * Get an encoder reading from the pivot motor.
     * 
     * @return A raw encoder reading.
     */
    public double getPivotMotorEncoder();

    /**
     * Get an encoder reading from the drive motor.
     * 
     * @return A raw encoder reading.
     */
    public double getDriveMotorEncoder();

    /**
     * Zero the pivot encoder.
     */
    public void zeroPivotEncoder();

    /**
     * Zero the drive encoder.
     */
    public void zeroDriveEncoder();

    /**
     * Stop the pivot motor.
     */
    public void stopPivotMotor();

    /**
     * Stop the drive motor.
     */
    public void stopDriveMotor();

    /**
     * Set the drive motor to the given reference. This should act as the interface
     * for a closed-loop position control. PID constants should be placed in the
     * actual implementation of the module.
     * 
     * @param ref The reference to set for closed loop control. This is the actual
     *            desired setpoint.
     */
    public void setDriveReference(double ref);

    /**
     * Set the pivot motor to the given reference. This should act as the interface
     * for a closed-loop position control. PID constants should be placed in the
     * actual implementation of the module.
     * 
     * @param ref The reference to set for closed loop control. This is the actual
     *            desired setpoint.
     */
    public void setPivotReference(double ref);

    /**
     * Get the PID controller driving the drive motor.
     * 
     * @return The PID controller driving the drive motor.
     */
    public AbstractPIDController getDrivePIDController();

    /**
     * Get the PID controller driving the pivot motor.
     * 
     * @return The PID controller driving the pivot motor.
     */
    public AbstractPIDController getPivotPIDController();

    /**
     * Output threshold is the absolute minimum current percentage that a motor
     * controller can run on that produces motion.
     * 
     * @return The absolute minimum current percentage. This value is usually
     *         discovered by experimentation. The default is 2% current, as this
     *         seems to be a safe average for FRC motor controllers and motors.
     */
    public default double getOutputThreshhold() {
        return 0.02;
    }

    /**
     * The gear ratio of the *driving* part of this module.
     *
     * @return A ratio in terms of Input:Output, or, Input/Outout.
     */
    public double getGearRatio();

    /**
     * Get the maximum realistic output in rotations per minute that this
     * module can produce.
     *
     * @return RPMs. This should be the top speed of the slowest motor.
     */
    public double getDriveMaxRPM();

    /**
     * If the actual maximum speed in rotations per minute is different
     * from the theoretical, set it here.
     *
     * @param rpm The value (in RPMs) to set as the top speed. This should
     *            be the top speed of the slowest motor output, not the 
     *            actual wheel output.
     */
    public void setDriveMaxRPM(double rpm);

    /**
     * Get the diameter of the wheel attached to this module.
     *
     * @return The diameter of the wheel IN INCHES.
     */
    public Length getWheelDiameter();

    /**
     * Stop the entire module, this just calls the stop function for each motor.
     */
    public default void stop() {
        stopPivotMotor();
        stopDriveMotor();
    }

    /**
     * Zero all the encoders by calling the zero functions for both the drive and
     * pivot motors.
     */
    public default void zero() {
        zeroPivotEncoder();
        zeroDriveEncoder();
    }

    /**
     * Reset this module by stopping all motors and resetting all the encoders.
     */
    public default void reset() {
        stop();
        zero();
    }
}
