/*----------------------------------------------------------------------------*/
/* Copyright (c) 2019 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.commands;

import org.frcteam2910.common.math.Vector2;

import edu.wpi.first.wpilibj.geometry.Translation2d;
import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.Direction;
import frc.robot.subsystems.Limelight;
import frc.robot.subsystems.swerve.DrivetrainSubsystem;

public class RotateToTargetCommand extends CommandBase {

  Limelight limelight;
  DrivetrainSubsystem drive;
  Direction direction;

  double kP = 0.001;

  /**
   * Creates a new RotateToTargetCommand.
   */
  public RotateToTargetCommand(Direction direction) {
    drive = DrivetrainSubsystem.getInstance();
    this.direction = direction;
    limelight = Limelight.getInstance();
    addRequirements(drive);
  }

  // Called when the command is initially scheduled.
  @Override
  public void initialize() {
    limelight.disableDriverMode();
    limelight.enableLEDs();
  }

  // Called every time the scheduler runs while the command is scheduled.
  @Override
  public void execute() {
    limelight.disableDriverMode();
    limelight.enableLEDs();

    if (limelight.hasTarget()) {

      double desiredAngle = limelight.getAngleX();
      System.out.println(desiredAngle);
      double rotation = desiredAngle * kP;
      drive.drive(new Vector2(0, 0), rotation, true);
    } else {
      System.out.println("NO TARGET");
      if (direction == Direction.LEFT) {
        drive.drive(new Vector2(0, 0), -0.01, true);
      } else {
        drive.drive(new Vector2(0, 0), 0.01, true);
      }
    }

  }

  // Called once the command ends or is interrupted.
  @Override
  public void end(boolean interrupted) {
    drive.stop();
    limelight.enableDriverMode();
    limelight.disableLEDs();
  }

  // Returns true when the command should end.
  @Override
  public boolean isFinished() {
    // return false;
    /**
     * Need to find the right threshold here 
     */
    if (limelight.getAngleX() > -0.01 && limelight.getAngleX() < 0.01 && limelight.hasTarget()
        && limelight.getAngleX() != 0) {
      System.out.println("Finished aiming");
      return true;
    } else {
      return false;
    }
  }
}
