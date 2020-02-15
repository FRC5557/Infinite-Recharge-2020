/*----------------------------------------------------------------------------*/
/* Copyright (c) 2019 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.commands;

import org.frcteam2910.common.math.Rotation2;
import org.frcteam2910.common.math.Vector2;

import edu.wpi.first.wpilibj.GenericHID.RumbleType;
import edu.wpi.first.wpilibj.controller.PIDController;
import edu.wpi.first.wpilibj.geometry.Translation2d;
import edu.wpi.first.wpilibj2.command.CommandBase;
import edu.wpi.first.wpiutil.math.MathUtil;
import frc.robot.Robot;
import frc.robot.subsystems.Limelight;
import frc.robot.subsystems.swerve.DrivetrainSubsystem;

/**
 * My hope for this command is to have the driver hold a button and it will move
 * to an optimal distance calculated before hand and will auto aim at the inner
 * target and allow us to constantly and efficiently get to the optimal place
 * and angle all the time to ensure we make every shot.
 */
public class GetInRangeAndAimCommand extends CommandBase {

  private DrivetrainSubsystem drive;
  private Limelight limelight;
  private boolean isAligned = false;

  PIDController xController, yController;

  /**
   * Creates a new GetInRangeAndAimCommand.
   */
  public GetInRangeAndAimCommand() {
    xController = new PIDController(0.1, 1e-4, 1);
    yController = new PIDController(-0.1, 1e-4, 1);
    xController.setSetpoint(0);
    yController.setSetpoint(0);
    // xController.
    drive = DrivetrainSubsystem.getInstance();
    limelight = Limelight.getInstance();
    // Use addRequirements() here to declare subsystem dependencies.
    addRequirements(drive);

  }

  // Called when the command is initially scheduled.
  @Override
  public void initialize() {
    limelight.disableDriverMode();
    limelight.enableLEDs();
  }

  // Called every time the scheduler runs while the command is scheduled.

  /**
   * What we will have here is the actual logic behind aiming and getting in
   * range. The logic behind this will have to be custom made due to us using
   * swerve instead of a standard drive.
   * http://docs.limelightvision.io/en/latest/cs_aiming.html
   * http://docs.limelightvision.io/en/latest/cs_autorange.html
   * http://docs.limelightvision.io/en/latest/cs_aimandrange.html
   */
  @Override
  public void execute() {
    if (limelight.hasTarget()) {
      Robot.getRobotContainer().getSwerveController().setRumble(RumbleType.kLeftRumble, 0.0);

      /**
       * First things first is to calculate distance from target and move towards it
       * second step would be to go ahead and calculate our offset from the target
       * rotation wise so we can go ahead and adjust accordingly. This whole process
       * should happen very quickly and in a closed loop to ensure that it works every
       * time and very accurately. Something we may or may not have to be weary of is
       * bumping into one of the field elements while moving. Potentially we can have
       * an ultrasonic or somehting to prevent that from happening? Not sure if its a
       * big enough issue to worry about at the moment.
       */

      // TODO: we will need different PID constants for the different portions, but we
      // can do that later
      double KpDistance = 0.1;
      // "Instead of actually calculating the distance, you can use the limelight
      // cross-hair. Just position your robot at your ideal distance from the target
      // and calibrate the y-position of the cross-hair. Now your y-angle will report
      // as 0.0 when your robot is at the corect distance. Using this trick, you don’t
      // ever have to actually calculate the actual distance and your code can look
      // something like this"
      double y_adjust = -KpDistance * limelight.getAngleY();
      // double y_adjust = yController.calculate(limelight.getAngleY());

      // we can now set our robot to move forward / back towards this new Y value
      // in swerve terms, this most likely means the "forward"

      // now we can get the x value we have to adjust by and pass that in for strafe
      double x_adjust = KpDistance * limelight.getAngleX();
      // double x_adjust = xController.calculate(limelight.getAngleX());
      // for now, lets assume that we are already looking straight on at the target so
      // we can ignore any sort of rotation. However, this may be important later!
      // for rotation, we can always just get the current gyro value and
      // hypothetically just turn the robot till it gets to -180 b, but thats
      // something
      // for later lol

      // System.out.println("X ADJUST: " + x_adjust);
      // System.out.println("Y ADJUST: " + y_adjust);

      Rotation2 currentGyroAngleRotation = DrivetrainSubsystem.getInstance().getGyroscope().getAngle();
      // DrivetrainSubsystem.getInstance().getG
      // double newOutput = MathUtil.clamp(
      // pidController.calculate(currentGyroAngle.toDegrees(), 0), -0.25, .25);
      // double newOutput = pid.calculate(currentGyroAngle.toDegrees(), 0);
      double currentGyroAngle = currentGyroAngleRotation.toDegrees();
      if (currentGyroAngle > 180) {
        currentGyroAngle = (360 - currentGyroAngle) * 0.01;
      } else {
        currentGyroAngle = (0 - currentGyroAngle) * 0.01;
      }

      drive.drive(new Vector2(Math.copySign(Math.pow(y_adjust, 2.0), y_adjust),
          Math.copySign(Math.pow(x_adjust, 2.0), -x_adjust)), currentGyroAngle, false);

      // once aligned, we set this boolean to true which will finish the command
      // we also later might need to set this to a certain range and not just 0, but
      // again we can do that later once we can get more testing in
      // if (y_adjust == 0 && x_adjust == 0) {
      // this.isAligned = true;
      // }
    } else {
      Robot.getRobotContainer().getSwerveController().setRumble(RumbleType.kLeftRumble, 0.5);
    }

  }

  // Called once the command ends or is interrupted.
  @Override
  public void end(boolean interrupted) {
    // stop the motors
    drive.stop();
    limelight.enableDriverMode();
    limelight.disableLEDs();
    Robot.getRobotContainer().getSwerveController().setRumble(RumbleType.kLeftRumble, 0.0);

  }

  // Returns true when the command should end.
  @Override
  public boolean isFinished() {
    return this.isAligned;
  }
}
