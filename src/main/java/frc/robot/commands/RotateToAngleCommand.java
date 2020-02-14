/*----------------------------------------------------------------------------*/
/* Copyright (c) 2019 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.commands;

import org.frcteam2910.common.control.PidConstants;
import org.frcteam2910.common.control.PidController;
import org.frcteam2910.common.math.Rotation2;
import org.frcteam2910.common.math.Vector2;

import edu.wpi.first.wpilibj.controller.PIDController;
import edu.wpi.first.wpilibj.geometry.Translation2d;
import edu.wpi.first.wpilibj2.command.CommandBase;
import edu.wpi.first.wpiutil.math.MathUtil;
import frc.robot.subsystems.swerve.DrivetrainSubsystem;

public class RotateToAngleCommand extends CommandBase {
  double kP = 0.1;
  double kI = 1e-4;
  double kD = 1;

  /**
   * Creates a new RotateToAngleCommand.
   */
  public RotateToAngleCommand(double angle) {
    // Use addRequirements() here to declare subsystem dependencies.
    addRequirements(DrivetrainSubsystem.getInstance());
  }

  // Called when the command is initially scheduled.
  @Override
  public void initialize() {
    // here we have to figure out which direction to rotate; CCW or CW
    // we also may or may not need to setup some PID stuff here too
    System.out.println("Startig rotate to angle");
  }

  // Called every time the scheduler runs while the command is scheduled.
  @Override
  public void execute() {
    Rotation2 currentGyroAngleRotation = DrivetrainSubsystem.getInstance().getGyroscope().getAngle();
    double currentGyroAngle = currentGyroAngleRotation.toDegrees();
    if (currentGyroAngle > 180) {
      currentGyroAngle = (360 - currentGyroAngle) * 0.01;
    } else {
      currentGyroAngle = (0 - currentGyroAngle) * 0.01;
    }

    DrivetrainSubsystem.getInstance().drive(new Vector2(0, 0), currentGyroAngle, true);
  }

  // Called once the command ends or is interrupted.
  @Override
  public void end(boolean interrupted) {
    // DrivetrainSubsystem.getInstance().holonomicDrive(Vector2.ZERO, 0);
  }

  // Returns true when the command should end.
  @Override
  public boolean isFinished() {
    return DrivetrainSubsystem.getInstance().getGyroscope().getAngle().toDegrees() <= 1
        || DrivetrainSubsystem.getInstance().getGyroscope().getAngle().toDegrees() >= 357;
  }
}
