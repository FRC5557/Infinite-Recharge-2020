package net.bancino.robotics.swerveio;

import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.List;

import edu.wpi.first.wpilibj2.command.SubsystemBase;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.kinematics.ChassisSpeeds;
import net.bancino.robotics.swerveio.exception.NullModuleException;
import net.bancino.robotics.swerveio.exception.SwerveException;
import net.bancino.robotics.swerveio.exception.SwerveImplementationException;
import net.bancino.robotics.swerveio.gyro.AbstractGyro;
import net.bancino.robotics.swerveio.log.SwerveLogger;
import net.bancino.robotics.swerveio.module.AbstractSwerveModule;
import net.bancino.robotics.swerveio.SwerveFlag;

/**
 * A class designed to be extended extended and used as a WPILib subsystem. This
 * takes care of consolidating swerve modules so they can be driven as a system
 * easily.
 * 
 * @author Jordan Bancino
 * @version 2.0.0
 * @since 1.0.0
 */
public abstract class SwerveDrive extends SubsystemBase {

    /**
     * A module map that contains all the swerve modules in this swerve drive. For
     * internal use only, this can be used to pull specific modules, and also
     * iterate over all the modules.
     */
    protected final Map<SwerveModule, AbstractSwerveModule> moduleMap;

    /**
     * A calculator is provided by default so that calculations can be easily
     * retrieved. If invalid base dimensions are provide, the default of a 1:1 base
     * is used.
     */
    protected final SwerveDriveCalculator calc;

    /**
     * How many counts it takes to perform a single revolution of a swerve module.
     * That is, a full 360 degrees.
     * 
     * You'll probably want to use a 1:1 encoder reading.
     */
    protected final double countsPerPivotRevolution;

    protected boolean fieldCentric = true;
    protected final AbstractGyro gyro;

    protected final List<SwerveFlag> flags;

    /**
     * When not moving, the idle angle is the default position. This is set
     * initially to 135, to point the modules in towards the center of the bot.
     */
    protected double idleAngle = 135;

    /**
     * Diagonal flip will alternate the idle angle on the modules, causing them to
     * point opposite on diagonal corners. For instance, if you have an idle angle
     * of 135, setting this to true will set two diagonal modules to 45, because it
     * will flip them all so they point toward the center.
     */
    protected boolean diagonalFlipIdle = true;

    protected SwerveVector lastSwerveVector = new SwerveVector(0, 0, 0);
    protected double lastGyroAngle;
    protected final HashMap<SwerveModule, Double> lastPivotAngle = new HashMap<>();

    /**
     * Construct a new swerve drive with the given swerve meta.
     *
     * @param swerveMeta The swerve meta interface implementation to construct this
     *                   swerve drive with.
     * @throws SwerveException If there is an error configuring this swerve drive
     *                         given the meta implementation.
     */
    public SwerveDrive(SwerveMeta swerveMeta) throws SwerveException {
        if (swerveMeta.moduleMap() != null) {
            if (swerveMeta.moduleMap().size() == SwerveModule.length) {
                moduleMap = swerveMeta.moduleMap();
                for (var moduleName : moduleMap.keySet()) {
                    var module = moduleMap.get(moduleName);
                    if (module != null) {
                        swerveMeta.modifyModule(module);
                    } else {
                        throw new NullModuleException(moduleName);
                    }
                }
            } else {
                throw new SwerveException(
                        "Expected map of size " + SwerveModule.length + ". Got size " + swerveMeta.moduleMap().size());
            }
        } else {
            throw new SwerveException("Expected a module map, but " + swerveMeta.name() + " didn't provide one.");
        }
        if (swerveMeta.chassisDimensions() != null) {
            this.calc = new SwerveDriveCalculator(swerveMeta.chassisDimensions());
        } else {
            throw new SwerveException("Expected chassis dimensions, but " + swerveMeta.name() + " didn't provide any.");
        }
        if (swerveMeta.countsPerPivotRevolution() > 0) {
            this.countsPerPivotRevolution = swerveMeta.countsPerPivotRevolution();
        } else {
            throw new SwerveException("Expected a valid counts per pivot revolution, but " + swerveMeta.name()
                    + " provided " + swerveMeta.countsPerPivotRevolution());
        }
        gyro = swerveMeta.gyro();
        fieldCentric = (gyro != null);
        if (swerveMeta.applyFlags() != null) {
            flags = swerveMeta.applyFlags();
        } else {
            flags = List.of();
        }
        swerveMeta.initialize(this);
    }

    /**
     * Set the field centric mode of the swerve drive. This requires a gyro
     * implementation.
     * 
     * @param fieldCentric Whether or not field-centric drive is enabled or
     *                     disabled.
     */
    public void setFieldCentric(boolean fieldCentric) {
        if (fieldCentric && gyro == null) {
            throw new UnsupportedOperationException("Cannot enable field-centric mode without a gyro.");
        } else {
            this.fieldCentric = fieldCentric;
        }
    }

    /**
     * Whether or not this swerve drive is field centric.
     * 
     * @return Whether or not the gyro is being used to offset the kinematic
     *         calculations to provide field-centric navigation.
     */
    public boolean isFieldCentric() {
        return fieldCentric;
    }

    /**
     * Set the angle at which all swerve modules will idle at.
     * 
     * @param angle        The angle (in degrees) at which to idle the modules at
     *                     when no motion is occuring. This can be any arbitrary
     *                     value, as it will automatically be converted to the
     *                     correct angle.
     * @param diagonalFlip Whether or not to flip two diagonal angles to the
     *                     opposite of the provided angle. This is useful for
     *                     "locking" the swerve drive, ensuring it doesn't move. For
     *                     example, providing an angle of 45 and setting this
     *                     parameter to true, you can lock the swerve in place to
     *                     prevent it from being pushed around.
     */
    public void setIdleAngle(double angle, boolean diagonalFlip) {
        if (angle < 0) {
            this.idleAngle = (angle <= -360) ? 360 - Math.abs(angle) % 360 : 360 - Math.abs(angle);
        } else {
            this.idleAngle = (angle >= 360) ? angle % 360 : angle;
        }
        this.diagonalFlipIdle = diagonalFlip;
    }

    /**
     * Drive the entire chassis with a WPILib ChassisSpeeds object. This converts
     * the chassis speed object into a raw swerve vector based on individual module
     * values. <b>Note:</b> If you are mis-matching modules with different gear
     * ratios and max RPMS, THIS WILL NOT WORK.
     *
     * @param v A WPILib ChassisSpeeds vector that represents the velocity to apply
     *          to this swerve base.
     */
    public void drive(ChassisSpeeds v) {
        for (SwerveModule key : moduleMap.keySet()) {
            AbstractSwerveModule module = moduleMap.get(key);
            SwerveVector rawVector = SwerveIOUtils.convertToSwerveVector(v, module.getGearRatio(),
                    module.getDriveMaxRPM(), module.getWheelDiameter(), calc.getChassisDimension());
            drive(rawVector, key);
        }
    }

    /**
     * A drive function that should be implemented to drive the robot with the
     * joystick. This will drive all modules.
     * 
     * @param v The vector (X, Y, Z)
     * @throws SwerveImplementationException If there is an error with the
     *                                       implementation of any swerve module.
     */
    public void drive(SwerveVector v) {
        for (SwerveModule module : moduleMap.keySet()) {
            drive(v, module);
        }
    }

    /**
     * A drive function that should be implemented to drive the robot with the
     * joystick. This will drive the specified modules.
     * 
     * @param v       The vector (X, Y, Z)
     * @param modules The modules to drive.
     * @throws SwerveImplementationException If there is an error with the
     *                                       implementation of any swerve module.
     */
    public void drive(SwerveVector v, SwerveModule... modules) {
        for (SwerveModule module : modules) {
            drive(v, module);
        }
    }

    /**
     * A drive function that should be implemented to drive the robot with the
     * joystick. This is used to drive only individual modules.
     * 
     * @param v      The vector to drive the swerve drive with.
     * @param module The individual module to drive.
     * @throws SwerveImplementationException If there is an error with the
     *                                       implementation of any swerve module.
     */
    public void drive(SwerveVector v, SwerveModule module) {
        double fwd = 0;
        if (!flags.contains(SwerveFlag.DISABLE_FWD)) {
            fwd = v.getFwd();
        }
        double str = 0;
        if (!flags.contains(SwerveFlag.DISABLE_STR)) {
            str = v.getStr();
        }
        double rcw = 0;
        if (!flags.contains(SwerveFlag.DISABLE_RCW)) {
            rcw = v.getRcw();
        }
        /* Use the swerve drive calculator to calculate target speeds and angles. */
        double speed = calc.getWheelSpeed(module, fwd, str, rcw);
        /* Get the target angle from the calculator, scaled from 0 to 360 */
        boolean useGyro = (gyro != null) && fieldCentric;
        double gyroAngle = (useGyro) ? gyro.getAngle() : 0;
        double targetAngle = calc.getWheelAngle(module, fwd, str, rcw, gyroAngle);

        /* Get a reference to the module to get feedback from it. */
        AbstractSwerveModule swerveModule = moduleMap.get(module);

        double currentAngle = SwerveIOUtils.convertToDegrees(swerveModule.getPivotMotorEncoder(),
                countsPerPivotRevolution);

        if (flags.contains(SwerveFlag.ENABLE_PIVOT_LAST_ANGLE)) {
            if (speed < swerveModule.getOutputThreshhold()) {
                if (lastPivotAngle.containsKey(module)) {
                    targetAngle = lastPivotAngle.get(module);
                }
            } else {
                lastPivotAngle.put(module, currentAngle);
            }
        } else { /* Idle-Angle mode */
            /*
             * If the calculated drive speed for this module is less than that module's
             * minimum output threshhold then snap to the idle angle, not the calculated
             * angle. Otherwise, drive.
             */
            if (speed < swerveModule.getOutputThreshhold()) {
                switch (module) {
                case FRONT_LEFT:
                case REAR_RIGHT:
                    targetAngle = idleAngle;
                    break;
                case FRONT_RIGHT:
                case REAR_LEFT:
                    targetAngle = diagonalFlipIdle ? 180 - idleAngle : idleAngle;
                    break;
                default:
                    targetAngle = 0;
                    break;
                }
            }
        }

        if (flags.contains(SwerveFlag.ENABLE_PIVOT_OPTIMIZE)) {
            double flippedAngle = SwerveIOUtils.flip180(targetAngle);
            double diff = Math.abs(targetAngle - currentAngle);
            double flipDiff = Math.abs(flippedAngle - currentAngle);
            if (flipDiff < 90 && diff > 90) {
                targetAngle = flippedAngle;
                speed *= -1;
            }
        }

        /* Convert the target angle into a target position on the pivot encoder. */
        double targetPos = SwerveIOUtils.convertToEncoderCount(targetAngle, countsPerPivotRevolution);
        if (!flags.contains(SwerveFlag.DISABLE_PIVOT)) {
            swerveModule.setPivotReference(targetPos);
        }
        if (!flags.contains(SwerveFlag.DISABLE_DRIVE)) {
            swerveModule.setDriveMotorSpeed(speed);
        }
        lastSwerveVector = v;
        lastGyroAngle = gyroAngle;
    }

    /**
     * Set the chassis angle based on a gyro reading. This doesn't necessarily
     * require field-centric drive, but it does require that a working gyro is
     * provided.
     * 
     * @param gyroAngle The angle in terms of 0-360 to set the pivot to.
     */
    public void setAngle(double gyroAngle) {
        if (gyro != null) {
            throw new UnsupportedOperationException("The chassis angle control loop is not implemented yet.");
        } else {
            throw new UnsupportedOperationException(
                    "Cannot run the chassis angle control loop without a feedback device (gyro not provided).");
        }
    }

    /**
     * Stop all the modules, stopping this swerve drive.
     */
    public void stop() {
        for (AbstractSwerveModule module : moduleMap.values()) {
            module.stop();
        }
    }

    /**
     * Zero all the drive encoders in this swerve drive.
     */
    public void zeroDriveEncoders() {
        for (AbstractSwerveModule module : moduleMap.values()) {
            module.zeroDriveEncoder();
        }
    }

    /**
     * Zero all the pivot and drive encoders in this swerve drive.
     */
    public void zero() {
        for (AbstractSwerveModule module : moduleMap.values()) {
            module.zero();
        }
    }

    /**
     * Calls the reset function on all the modules, stopping each module and
     * resetting all the encoders
     */
    public void reset() {
        for (AbstractSwerveModule module : moduleMap.values()) {
            module.reset();
        }
    }

    /**
     * The logging API, for logging the state of the swerve drive.
     * 
     * This boolean controls whether or not to log. Defaults to false because no
     * logger is provided by default.
     */
    private boolean doLog = false;
    private long logWait = 1000;
    private long lastTime = 0;

    /**
     * The logger that is responsible for logging the state of this swerve drive.
     */
    private ArrayList<SwerveLogger> loggers = new ArrayList<>();

    /**
     * Start logging using the current logger.
     * 
     * @param wait The wait time, in milliseconds, to wait in-between snapshots.
     *             Note that this is a minimum, because it isn't always sure when
     *             the periodic method will run. Every 20ms is assumed, so the wait
     *             could be anywhere between wait and wait + 20ms.
     * @throws IllegalArgumentException If no logger is registered.
     */
    public final void startLogging(long wait) {
        startLogging(wait, loggers.toArray(new SwerveLogger[loggers.size()]));
    }

    /**
     * Start logging using the current loggers and the default log time.
     * 
     * @throws IllegalArgumentException If no logger is registered.
     */
    public final void startLogging() {
        startLogging(logWait);
    }

    /**
     * Start logging with the provided loggers
     * 
     * @param wait    The wait time, in milliseconds, to wait in-between snapshots.
     *                Note that this is a minimum, because it isn't always sure when
     *                the periodic method will run. Every 20ms is assumed, so the
     *                wait could be anywhere between wait and wait + 20ms.
     * @param loggers The loggers to use.
     * @throws IllegalArgumentException If any logger is null.
     */
    public final void startLogging(long wait, SwerveLogger... loggers) {
        for (SwerveLogger logger : loggers) {
            if (logger != null) {
                this.loggers.add(logger);
                doLog = true;
            } else {
                throw new IllegalArgumentException("No valid swerve logger provided.");
            }
        }
        if (wait > 0) {
            logWait = wait;
        } else {
            throw new IllegalArgumentException("Wait must be greater than 0.");
        }
    }

    /**
     * Start logging with the provided loggers
     * 
     * @param loggers The loggers to use.
     * @throws IllegalArgumentException If any logger is null.
     */
    public final void startLogging(SwerveLogger... loggers) {
        startLogging(logWait, loggers);
    }

    /**
     * Stop the logger from executing under this class. Note that if a logger is
     * running elsewhere, such as an overridden periodic method, this will not
     * affect it.
     */
    public final void stopLogging() {
        doLog = false;
    }

    /**
     * Return the loggers registered to this class, if any.
     * 
     * @return The current loggers, or null. Note that the list may just have a size
     *         of 1.
     */
    public final ArrayList<SwerveLogger> getLoggers() {
        if (loggers.size() == 0) {
            return null;
        } else {
            return loggers;
        }
    }

    /**
     * The SubsystemBase periodic method is run once every scheduler routine.
     * <b>Important:</b> This function is used for the logging functionality. If you
     * are using the logging features, you must call "super.periodic()" somewhere in
     * your periodic routine, if you choose to override this function.
     */
    @Override
    public void periodic() {
        long time = System.currentTimeMillis();
        if (doLog && time - lastTime >= logWait) {
            for (SwerveLogger logger : loggers) {
                if (logger != null) {
                    if (logger.requiresEnabledDriverStation()) {
                        if (DriverStation.getInstance().isEnabled()) {
                            logger.logState(this);
                        }
                    } else {
                        logger.logState(this);
                    }

                }
            }
            lastTime = time;

        }
    }

    /* Exposed internal variables, intended to be used for logging only. */

    /**
     * Get the last point that was fed into this swerve drive. Contained in the
     * point are the forward, strafe, and rotation variables of this vector.
     * 
     * @return A Swerve vector.
     */
    public SwerveVector getLastSwerveVector() {
        return lastSwerveVector;
    }

    /**
     * Get the last gyro angle that was fed into this swerve drive.
     * 
     * @return A Gyro angle in degrees.
     */
    public double getLastGyroAngle() {
        return lastGyroAngle;
    }

    /**
     * Get the gyro object that this swerve drive was created with, if any.
     * 
     * @return The gyro object that this swerve drive is reading from. Null if none.
     */
    public AbstractGyro getGyro() {
        return gyro;
    }

    /**
     * Get the calculator that this swerve drive is using to compute the values it
     * needs.
     * 
     * @return A swerve drive calculator that is actively being used by the swerve
     *         drive.
     */
    public SwerveDriveCalculator getCalculator() {
        return calc;
    }

    /**
     * Get the abstract module map, containing all the swerve modules. This map
     * contains references to all the modules that this swerve drive was
     * instantiated with. Regardless of their type, the module map exposes the
     * abstract swerve module API.
     * 
     * @return An internal module map that is used to drive this swerve drive. Do
     *         <b>NOT</b> modify this map in any way lest you break the swerve drive
     *         entirely, this method is only exposed for the purpose of iterating
     *         the map to access the underlying modules. Do not modify the
     *         underlying modules either, they are intended to be read-only when
     *         gathered from this function. However, I suppose if you're
     *         <i>really</i> sure what you're doing, nothing is stopping you from
     *         passing arbitrary references or PID values to the modules.
     */
    public Map<SwerveModule, AbstractSwerveModule> getModuleMap() {
        return moduleMap;
    }

    /**
     * Get the number of encoder counts per pivot revolution.
     * 
     * @return The encoder counts it takes to go one full revolution of the pivot
     *         motor.
     */
    public double getCountsPerPivotRevolution() {
        return countsPerPivotRevolution;
    }
}
