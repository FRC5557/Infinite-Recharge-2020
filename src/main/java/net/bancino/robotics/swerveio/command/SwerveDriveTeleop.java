package net.bancino.robotics.swerveio.command;

import edu.wpi.first.wpilibj2.command.CommandBase;
import edu.wpi.first.wpilibj.XboxController;

import net.bancino.robotics.swerveio.SwerveDrive;
import net.bancino.robotics.swerveio.SwerveVector;

/**
 * A simple command for driving a swerve drive. 
 *
 * @author Jordan Bancino
 * @version 2.0.0
 * @since 2.0.0
 */
public class SwerveDriveTeleop extends CommandBase {

    private SwerveDrive swerve;
    private XboxController joystick;
    private XboxController.Axis fwdAxis, strAxis, rcwAxis;

    private double deadband, throttle;

    /**
     * Create a new swerve drive joystick command.
     *
     * @param swerve The swerve drive to drive.
     * @param joystick The joystick to read from.
     * @param fwdAxis The axis to read from that will provide values for the Y movement of the swerve drive.
     * @param strAxis The axis to read from that will provide values for the X movement of the swerve drive.
     * @param rcwAxis The axis to read from that will provide values for the angular movement of the swerve drive.
     */
    public SwerveDriveTeleop(SwerveDrive swerve, XboxController joystick, XboxController.Axis fwdAxis, XboxController.Axis strAxis, XboxController.Axis rcwAxis) {
        if (swerve != null) {
            this.swerve = swerve;
        } else {
            throw new IllegalArgumentException("Swerve Drive cannot be null.");
        }
        if (joystick != null) {
            this.joystick = joystick;
            this.fwdAxis = fwdAxis;
            this.strAxis = strAxis;
            this.rcwAxis = rcwAxis;
        } else {
            throw new IllegalArgumentException("Xbox controller cannot be null.");
        }

        addRequirements(swerve);
        setDeadband(0.2);
        setThrottle(0.5);
    }

    public void setDeadband(double deadband) {
      this.deadband = deadband;
    }

    public void setThrottle(double throttle) {
      this.throttle = throttle;
    }

    @Override
    public void execute() {
        double fwd = - throttle(deadband(joystick.getRawAxis(fwdAxis.value)));
        double str = throttle(deadband(joystick.getRawAxis(strAxis.value)));
        double rcw = throttle(deadband(joystick.getRawAxis(rcwAxis.value)));
        SwerveVector joystickVector = new SwerveVector(fwd, str, rcw);
        swerve.drive(joystickVector);
    }

    @Override
    public boolean isFinished() {
        return false;
    }

  private double throttle(double raw) {
    return raw * throttle;
  }

  /**
   * Calculate a deadband
   * 
   * @param raw The input on the joystick to mod
   * @return The result of the mod.
   */
  private double deadband(double raw) {
    /* This will be our result */
    double mod;
    /* Compute the deadband mod */
    if (raw < 0.0d) {
      if (raw <= -deadband) {
        mod = (raw + deadband) / (1 - deadband);
      } else {
        mod = 0.0d;
      }
    } else {
      if (raw >= deadband) {
        mod = (raw - deadband) / (1 - deadband);
      } else {
        mod = 0.0d;
      }
    }
    /* Return the result. */
    return mod;
  }
}