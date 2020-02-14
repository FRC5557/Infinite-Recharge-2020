/*----------------------------------------------------------------------------*/
/* Copyright (c) 2019 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.commands;

import org.frcteam2910.common.math.Vector2;
import org.frcteam2910.common.robot.Utilities;

import edu.wpi.first.wpilibj.geometry.Translation2d;
import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.Robot;
import frc.robot.subsystems.swerve.DrivetrainSubsystem;

public class DriveCommand extends CommandBase {
  /**
   * Creates a new DriveCommand.
   */
  public DriveCommand() {
    // Use addRequirements() here to declare subsystem dependencies.
    addRequirements(DrivetrainSubsystem.getInstance());
  }

  // Called when the command is initially scheduled.
  @Override
  public void initialize() {

  }

  // Called every time the scheduler runs while the command is scheduled.
  @Override
  public void execute() {
    double forward = -Robot.getRobotContainer().getDriveForwardAxis().get();
    forward = Utilities.deadband(forward);
    // Square the forward stick
    forward = Math.copySign(Math.pow(forward, 2.0), forward);

    double strafe = Robot.getRobotContainer().getDriveStrafeAxis().get();
    strafe = Utilities.deadband(strafe);
    // Square the strafe stick
    strafe = Math.copySign(Math.pow(strafe, 2.0), strafe);

    double rotation = -Robot.getRobotContainer().getDriveRotationAxis().get();
    rotation = Utilities.deadband(rotation);
    // Square the rotation stick
    rotation = Math.copySign(Math.pow(rotation, 3.0), rotation);

    // DrivetrainSubsystem.getInstance().drive(new Translation2d(forward, strafe), rotation, true);
    DrivetrainSubsystem.getInstance().drive(
                new Vector2(
                        forward,
                        strafe
                ),
                rotation,
                true
        );
  }

  // Called once the command ends or is interrupted.
  @Override
  public void end(boolean interrupted) {
    // maybe? need to test
    DrivetrainSubsystem.getInstance().stop();
  }

  // Returns true when the command should end.
  @Override
  public boolean isFinished() {
    return false;
  }
}
