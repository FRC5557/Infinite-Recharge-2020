package net.bancino.robotics.swerveio.module;

import edu.wpi.first.wpilibj.SpeedController;
import net.bancino.robotics.swerveio.SwerveIOUtils;
import net.bancino.robotics.swerveio.encoder.AbstractEncoder;
import net.bancino.robotics.swerveio.pid.AbstractPIDController;
import net.bancino.robotics.swerveio.pid.MiniPID;
import net.bancino.robotics.swerveio.si.Length;

/**
 * A generic swerve module class that provides every single function that can
 * possibly be implemented using only two speed controllers and a pivot encoder.
 * If you wish to implement your own module, it is often easier to extend this
 * class instead of implementing AbstractSwerveModule directly.
 * 
 * @author Jordan Bancino
 */
public class GenericSwerveModule implements AbstractSwerveModule {

    private SpeedController driveMotor, pivotMotor;
    private AbstractEncoder pivotEncoder, driveEncoder;
    private double driveGearRatio, driveMaxRPM;
    private Length wheelDiameter;
    protected AbstractPIDController pivotPid = new MiniPID(0, 0, 0), drivePid = new MiniPID(0, 0, 0);

    /**
     * Construct a generic swerve module.
     * 
     * @param driveMotor     The drive motor controller.
     * @param pivotMotor     The pivot motor controller.
     * @param driveEncoder   The drive encoder.
     * @param pivotEncoder   The pivot encoder. It is assumed that the encoder is 1:1:
     *                       That is, the counts per revolution of the encoder shaft
     *                       is equal to the counts per revolution of the pivot
     *                       module.
     * @param driveGearRatio The gear ratio to assume. This will be passed directly
     *                       into the getGearRatio() function.
     * @param driveMaxRPM    The maximum rotations per minute that this module can achieve.
     * @param wheelDiameter  The diameter of the drive wheel.
     */
    public GenericSwerveModule(SpeedController driveMotor, SpeedController pivotMotor, AbstractEncoder driveEncoder,
            AbstractEncoder pivotEncoder, double driveGearRatio, double driveMaxRPM, Length wheelDiameter) {
        if (driveMotor == null) {
            throw new IllegalArgumentException("Drive motor must not be null.");
        } else if (driveEncoder == null) {
            throw new IllegalArgumentException("Drive encoder must not be null.");
        } else if (pivotMotor == null) {
            throw new IllegalArgumentException("Pivot motor must not be null.");
        } else if (pivotEncoder == null) {
            throw new IllegalArgumentException("Pivot encoder must not be null.");
        } else if (driveGearRatio <= 0) {
            throw new IllegalArgumentException("Drive gear ratio must be greater than 0.");
        } else if (wheelDiameter == null) {
            throw new IllegalArgumentException("Wheel diameter must not be null.");
        } else {
            this.driveMotor = driveMotor;
            this.pivotMotor = pivotMotor;
            this.pivotEncoder = pivotEncoder;
            this.driveEncoder = driveEncoder;
            this.driveGearRatio = driveGearRatio;
            this.wheelDiameter = wheelDiameter;
            setDriveMaxRPM(driveMaxRPM);
        }

        /*
         * Don't let the PID loop output anything greater than the limits of the motor.
         */
        pivotPid.setOutputLimits(-1, 1);
        drivePid.setOutputLimits(-1, 1);

        /* Tell the PID loop what the maximum range of the encoder is. */
        pivotPid.setSetpointRange(pivotEncoder.countsPerRevolution());
    }

    /**
     * @return The drive motor controller that this class was instantiated with.
     */
    protected SpeedController getDriveMotor() {
        return driveMotor;
    }

    /**
     * @return The pivot motor controller that this class was instantiated with.
     */
    protected SpeedController getPivotMotor() {
        return pivotMotor;
    }

    /**
     * @return The pivot encoder that this class was instantiated with.
     */
    protected AbstractEncoder getPivotEncoder() {
        return pivotEncoder;
    }

    /**
     * @return the drive encoder that this class was instantiated with.
     */
    protected AbstractEncoder getDriveEncoder() {
        return driveEncoder;
    }

    @Override
    public void setPivotMotorSpeed(double speed) {
        pivotMotor.set(speed);

    }

    @Override
    public void setDriveMotorSpeed(double speed) {
        driveMotor.set(speed);

    }

    @Override
    public double getPivotMotorSpeed() {
        return pivotMotor.get();
    }

    @Override
    public double getDriveMotorSpeed() {
        return driveMotor.get();
    }

    @Override
    public double getDriveMotorEncoder() {
        return driveEncoder.get();
    }

    @Override
    public void zeroDriveEncoder() {
        driveEncoder.zero();
    }

    @Override
    public double getPivotMotorEncoder() {
        return pivotEncoder.get();
    }

    @Override
    public void zeroPivotEncoder() {
        pivotEncoder.zero();
    }

    @Override
    public void stopPivotMotor() {
        pivotMotor.stopMotor();
    }

    @Override
    public void stopDriveMotor() {
        driveMotor.stopMotor();
    }

    @Override
    public void setPivotReference(double ref) {
        double feedbackMod = SwerveIOUtils.correctPivotFeedback(pivotEncoder.get(), ref, pivotEncoder.countsPerRevolution());
        setPivotMotorSpeed(-1 * pivotPid.getOutput(feedbackMod, ref));
    }

    @Override
    public void setDriveReference(double ref) {
        setDriveMotorSpeed(drivePid.getOutput(driveEncoder.get(), ref));
    }

    @Override
    public AbstractPIDController getDrivePIDController() {
        return drivePid;
    }

    @Override
    public AbstractPIDController getPivotPIDController() {
        return pivotPid;
    }

    @Override
    public double getGearRatio() {
        return driveGearRatio;
    }

    @Override
    public double getDriveMaxRPM() {
        return driveMaxRPM;
    }

    @Override
    public void setDriveMaxRPM(double rpm) {
        if (rpm > 0) {
            this.driveMaxRPM = rpm;
        } else {
            throw new IllegalArgumentException("Maximum Drive RPMs is surely greater than " + rpm + ", right?");
        }
    }

    @Override
    public Length getWheelDiameter() {
        return wheelDiameter;
    }
}