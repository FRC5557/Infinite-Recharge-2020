/*----------------------------------------------------------------------------*/
/* Copyright (c) 2019 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.commands.autonomous;

import com.revrobotics.CANEncoder;

import org.frcteam2910.common.math.Vector2;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.subsystems.swerve.DrivetrainSubsystem;

public class MoveForwardForRotationsCommand extends CommandBase {
  double rotations;
  DrivetrainSubsystem drive = DrivetrainSubsystem.getInstance();
  // private CANEncoder frontLeftCANEncoder =
  // drive.getSwerveModules()[0].getDriveMotor().getEncoder(),
  // frontRightCANEncoder =
  // drive.getSwerveModules()[1].getDriveMotor().getEncoder(), backLeftCANEncoder
  // = drive.getSwerveModules()[2].getDriveMotor().getEncoder(),
  // backRightCANEncoder =
  // drive.getSwerveModules()[3].getDriveMotor().getEncoder();

  /**
   * Creates a new MoveForwardForRotationsCommand.
   */
  public MoveForwardForRotationsCommand(double rotations) {
    // Use addRequirements() here to declare subsystem dependencies.
    this.rotations = rotations * 2;
    addRequirements(drive);
  }

  // Called when the command is initially scheduled.
  @Override
  public void initialize() {
    // DrivetrainSubsystem.getInstance().getSwerveModules()[0].getDriveMotor().getEncoder().setPosition(0);
    // DrivetrainSubsystem.getInstance().getSwerveModules()[1].getDriveMotor().getEncoder().setPosition(0);
    // DrivetrainSubsystem.getInstance().getSwerveModules()[2].getDriveMotor().getEncoder().setPosition(0);
    // DrivetrainSubsystem.getInstance().getSwerveModules()[3].getDriveMotor().getEncoder().setPosition(0);

  }

  // Called every time the scheduler runs while the command is scheduled.
  @Override
  public void execute() {
    // drive.holonomicDrive(new Vector2(-0.5, 0), 0, false);
  }

  // Called once the command ends or is interrupted.
  @Override
  public void end(boolean interrupted) {
    // drive.holonomicDrive(Vector2.ZERO, 0, false);
    drive.stop();
  }

  // Returns true when the command should end.
  @Override
  public boolean isFinished() {
    // System.out.println(frontLeftCANEncoder.getPosition());
    // return frontLeftCANEncoder.getPosition() >= rotations;
    return true;
  }
}
