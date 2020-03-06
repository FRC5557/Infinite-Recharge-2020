package net.bancino.robotics.swerveio;

import edu.wpi.first.wpilibj.kinematics.ChassisSpeeds;
import edu.wpi.first.wpilibj.trajectory.Trajectory;
import net.bancino.robotics.swerveio.si.ChassisDimension;
import net.bancino.robotics.swerveio.si.Length;
import net.bancino.robotics.swerveio.si.Unit;

/**
 * SwerveIO Converter is a static class that provides simple utility methods for
 * commonly used conversions.
 * 
 * @author Jordan Bancino
 * @version 1.2.2
 * @since 1.2.2
 */
public final class SwerveIOUtils {
    /**
     * Convert an encoder reading to degrees in terms of 360. This is useful for
     * finding the current angle at which a module is pivoted.
     * 
     * @param currentEncoderCount The count you wish to find the angle for.
     * @param countsPerRevolution How many counts are in a revolution.
     * @return The angle (in degrees) that the current encoder count represents in
     *         correlation to the total counts per revolution.
     */
    public static double convertToDegrees(double currentEncoderCount, double countsPerRevolution) {
        return (360 / countsPerRevolution) * currentEncoderCount;
    }

    /**
     * Convert an angle measure into an encoder count. This useful for finding the
     * encoder position to go to for a given angle.
     * 
     * @param degreeMeasure       The angle measure in degrees to find the count
     *                            for.
     * @param countsPerRevolution How many counts are in a revolution.
     * @return The location on the encoder that will put the motor at that angle.
     */
    public static double convertToEncoderCount(double degreeMeasure, double countsPerRevolution) {
        return (countsPerRevolution / 360) * degreeMeasure;
    }

    /**
     * When manually driving the PID loop, we must modify our feedback to output the
     * correct the distance and orientation of PID output.
     * <p>
     * Normally, one would not modify the feedback, however this is necessary to
     * ensure the PID loop behaves properly, providing the correct outputs in the
     * correct direction, since we have to deal with crossing over 180 and of course
     * 0.
     *
     * @param currentPos The current position, that is, the feedback.
     * @param targetPos The setpoint, the desired position.
     * @param countsPerPivotRevolution The number of encoder counts in a single pivot revolution (360 degrees)
     * @return A corrected "feedback" value that should be put into the pivot pid loop.
     */
    public static double correctPivotFeedback(double currentPos, double targetPos, double countsPerPivotRevolution) {
        /* Set up some convenience variables for processing. */
        double quarterPivot = countsPerPivotRevolution / 4.0;
        double halfPivot = countsPerPivotRevolution / 2.0;

        /*
         * Normally, one would not modify the feedback, however this is necessary to
         * ensure the PID loop behaves properly, providing the correct outputs in the
         * correct direction, since we have to deal with crossing over 180 and of course
         * 0.
         */
        double feedbackMod = currentPos;
        /* Calculate the error between the setpoint and the feedback. */
        double positionDiff = targetPos - currentPos;

        /*
         * Modify the feedback to produce the correct PID output.
         */
        if (targetPos < currentPos) { /* If setpoint is less than feedback. */
            if (positionDiff > -halfPivot) { /* If error is greater than -180 degrees. */
                feedbackMod = currentPos; /* Do not modify the feedback. */
            } else { /* Setpoint is greater than feedback. */
                if (targetPos > quarterPivot
                        && positionDiff != -halfPivot) { /*
                                                          * If setpoint is greater than 90 degrees and the position
                                                          * difference is not equal to -180
                                                          */
                    feedbackMod = -1 * (countsPerPivotRevolution + positionDiff);
                } else {
                    feedbackMod = currentPos - countsPerPivotRevolution;
                }
            }
        }

        /* Setpoint is greater than feedback. */
        if (targetPos > currentPos) {
            if (positionDiff > halfPivot) { /* error is greater than 180 */
                feedbackMod = currentPos + countsPerPivotRevolution;
            } else {
                feedbackMod = currentPos;
            }
        }
        return feedbackMod;
    }

    /**
     * Flip an angle by 180 degrees.
     *
     * @param angle The angle, in degrees, to rotate by 180 degrees. This angle should be less than
     *              or equal to 360, but if it isn't, it will be converted and put into terms of 360.
     *
     * @return The lowest possible angle that represents the original angle rotated by 180 degrees.
     */
    public static double flip180(double angle) {
        double mAngle = angle % 360;
        if (mAngle >= 180) {
            return mAngle - 180;
        } else if (mAngle < 180) {
            return mAngle + 180;
        } else {
            throw new IllegalArgumentException("Invalid angle supplied: " + angle + " (angle % 360 = " + mAngle + ")");
        }
    }

    /**
     * Get the maximum chassis speed in meters per second.
     * 
     * @param gearRatio        The gear ratio of the drive motors.
     * @param maxInputDriveRPM The maximum rotations per minute that the drive motor can output. This is
     *                         input shaft rotations, not output.
     * @param wheelDiameter    The diameter of the wheel.
     * @return                 The maximum chassis speed that the given parameters can achieve, in meters per second.
     */
    public static double getMaxChassisSpeed(double gearRatio, double maxInputDriveRPM, Length wheelDiameter) {
        double maxInputRPS = maxInputDriveRPM / 60;
        double maxOutputRPS = maxInputRPS / gearRatio;
        double wheelCircumference = wheelDiameter.get(Unit.INCHES) * Math.PI;
        double maxOutputIPS = wheelCircumference * maxOutputRPS;
        //double maxOutputMPS = maxOutputIPS / 39.371;
        return new Length(maxOutputIPS, Unit.INCHES).get(Unit.METERS);
    }

    /**
     * Get the maximum chassis angular velocity in radians per second.
     *
     * @param maxChassisSpeed The maximum chassis forward velocity in meters per second.
     * @param chassisDimension   The length of one side of the chassis. It shouldn't matter which one,
     *                        but we really don't know. 
     * @return                The maximum angular velocity in radians per second.
     */
    public static double getMaxChassisOmega(double maxChassisSpeed, ChassisDimension chassisDimension) {
        double maxChassisRevsPerSecond = (chassisDimension.getRadius().get(Unit.METERS) * Math.PI) / maxChassisSpeed;
        double maxChassisRadiansPerSecond = maxChassisRevsPerSecond * (2 * Math.PI);
        return maxChassisRadiansPerSecond;
    }

    /**
     * Convert a swerve joystick vector into a chassis speed vector.
     * @param v                The swerve vector to convert.
     * @param gearRatio        The gear ratio of the drive motors.
     * @param maxInputDriveRPM The maximum rotations per minute that the drive motor can output. This is
     *                         input shaft rotations, not output.
     * @param wheelDiameter    The diameter of the wheel.
     * @param chassisDimension    The length of one side of the chassis. It shouldn't matter which one,
     *                         but we really don't know. 
     * @return                 A WPILib chassis speed object that represents the swerve vector provided.
     */
    public static ChassisSpeeds convertToChassisSpeeds(SwerveVector v, double gearRatio, double maxInputDriveRPM, Length wheelDiameter, ChassisDimension chassisDimension) {
        double maxOutputMPS = getMaxChassisSpeed(gearRatio, maxInputDriveRPM, wheelDiameter);
        double maxChassisRadiansPerSecond = getMaxChassisOmega(maxOutputMPS, chassisDimension);
        return new ChassisSpeeds(v.getFwd() * maxOutputMPS, v.getStr() * maxOutputMPS, v.getStr() * maxChassisRadiansPerSecond);
    }

    /**
     * Convert a Pathweaver state object into a ChassisSpeeds vector that can be passed into a swerve drive.
     * 
     * @param pathweaverState The trajectory state to convert to the chassis speed object.
     * @param angularVelocity The angular velocity to apply to the resultant vector.
     * @return A ChassisSpeeds object that has the velocity components of the current state object,
     *         and the constant angular velocity component.
     */
    public static ChassisSpeeds convertToChassisSpeeds(Trajectory.State pathweaverState, double angularVelocity) {
        double angle = pathweaverState.poseMeters.getRotation().getRadians();
        double velocity = pathweaverState.velocityMetersPerSecond;
        double velocityX = velocity * Math.cos(angle);
        double velocityY = velocity * Math.sin(angle);

        return new ChassisSpeeds(velocityX, velocityY, angularVelocity);
    }

    /**
     * Given a trajectory, calculate the constant angular velocity required to get
     * from the starting angle to the final angle.
     *
     * @param trajectory The trajectory to calculate the constant angular velocity for.
     * @return The angular velocity in radians per second.
     */
    public static double getAngularVelocity(Trajectory trajectory) {
        double trajectoryDuration = trajectory.getTotalTimeSeconds();
        Trajectory.State initialState = trajectory.sample(0);
        Trajectory.State finalState = trajectory.sample(trajectoryDuration);
        double initialAngle = initialState.poseMeters.getRotation().getRadians();
        double finalAngle = finalState.poseMeters.getRotation().getRadians();
        return (finalAngle - initialAngle) / trajectoryDuration;
    }

    /**
     * Convert a chassis speed vector into a swerve vector.
     * @param v                The chassis speed vector to convert.
     * @param gearRatio        The gear ratio of the drive motors.
     * @param maxInputDriveRPM The maximum rotations per minute that the drive motor can output. This is
     *                         input shaft rotations, not output.
     * @param wheelDiameter    The diameter of the wheel.
     * @param chassisDimension    The length of one side of the chassis. It shouldn't matter which one,
     *                         but we really don't know. 
     * @return                 A swerve vector object that represents the chassis speed provided.
     *                         This can be passed into a swerve drive 
     */
    public static SwerveVector convertToSwerveVector(ChassisSpeeds v, double gearRatio, double maxInputDriveRPM, Length wheelDiameter, ChassisDimension chassisDimension) {
        double maxOutputMPS = getMaxChassisSpeed(gearRatio, maxInputDriveRPM, wheelDiameter);
        double maxChassisRadiansPerSecond = getMaxChassisOmega(maxOutputMPS, chassisDimension);
        return new SwerveVector(v.vxMetersPerSecond / maxOutputMPS, v.vyMetersPerSecond / maxOutputMPS, v.omegaRadiansPerSecond / maxChassisRadiansPerSecond);
    }
}