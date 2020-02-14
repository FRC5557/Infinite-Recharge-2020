/*----------------------------------------------------------------------------*/
/* Copyright (c) 2019 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.commands.autonomous;

import com.revrobotics.CANEncoder;
import com.revrobotics.CANPIDController;
import com.revrobotics.ControlType;

import org.frcteam2910.common.math.Vector2;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.Constants;
import frc.robot.Direction;
import frc.robot.subsystems.swerve.DrivetrainSubsystem;

public class MoveDirectionForDistanceCommand extends CommandBase {
  double rotations;
  DrivetrainSubsystem drive = DrivetrainSubsystem.getInstance();
  private CANPIDController frontLeftPIDController, frontRightPIDController, backLeftPIDController,
      backRightPIDController;
  private CANPIDController[] controllers;
  // private CANEncoder frontLeftCANEncoder =
  // drive.getSwerveModules()[0].getDriveMotor().getEncoder(),
  // frontRightCANEncoder =
  // drive.getSwerveModules()[1].getDriveMotor().getEncoder(),
  // backLeftCANEncoder =
  // drive.getSwerveModules()[2].getDriveMotor().getEncoder(),
  // backRightCANEncoder =
  // drive.getSwerveModules()[3].getDriveMotor().getEncoder();
  // private CANEncoder[] encoders = { frontLeftCANEncoder, frontRightCANEncoder,
  // backLeftCANEncoder,
  // backRightCANEncoder };
  public double kP, kI, kD, kIz, kFF, kMaxOutput, kMinOutput;
  Direction direction;

  /**
   * Creates a new MoveBackForDistanceCommand.
   */
  public MoveDirectionForDistanceCommand(double distance, Direction direction) {

    // do some calculation here to convert distance (feet?) to rotations
    this.rotations = distance;
    this.direction = direction;

    // frontLeftPIDController =
    // drive.getSwerveModules()[0].getDriveMotor().getPIDController();
    // frontRightPIDController =
    // drive.getSwerveModules()[1].getDriveMotor().getPIDController();
    // backLeftPIDController =
    // drive.getSwerveModules()[2].getDriveMotor().getPIDController();
    // backRightPIDController =
    // drive.getSwerveModules()[3].getDriveMotor().getPIDController();

    CANPIDController[] controllersLocal = { frontLeftPIDController, frontRightPIDController, backLeftPIDController,
        backRightPIDController };
    this.controllers = controllersLocal;

    addRequirements(drive);
  }

  // Called when the command is initially scheduled.
  @Override
  public void initialize() {

    // BEVEL GEARS ON LEFT

    // NEED TO TEST THIS!!
    // drive.moveWheelsToDirection(direction);

    // DrivetrainSubsystem.getInstance().getSwerveModules()[0].getDriveMotor().getEncoder().setPosition(0);
    // DrivetrainSubsystem.getInstance().getSwerveModules()[1].getDriveMotor().getEncoder().setPosition(0);
    // DrivetrainSubsystem.getInstance().getSwerveModules()[2].getDriveMotor().getEncoder().setPosition(0);
    // DrivetrainSubsystem.getInstance().getSwerveModules()[3].getDriveMotor().getEncoder().setPosition(0);

    kP = 0.1;
    kI = 1e-4;
    kD = 1;
    kIz = 0;
    kFF = 0;
    kMaxOutput = 0.5;
    kMinOutput = -0.5;

    for (CANPIDController controller : controllers) {
      controller.setP(kP);
      controller.setI(kI);
      controller.setD(kD);
      controller.setIZone(kIz);
      controller.setFF(kFF);
      controller.setOutputRange(kMinOutput, kMaxOutput);
    }

  }

  // Called every time the scheduler runs while the command is scheduled.
  @Override
  public void execute() {

    controllers[0].setReference(Constants.FRONT_LEFT_DRIVE_PER_REVOLUTION * rotations, ControlType.kPosition, 0, 4);
    controllers[1].setReference(Constants.FRONT_RIGHT_DRIVE_PER_REVOLUTION * rotations, ControlType.kPosition, 0, 4);
    controllers[2].setReference(Constants.BACK_LEFT_DRIVE_PER_REVOLUTION * rotations, ControlType.kPosition, 0, 4);
    controllers[3].setReference(Constants.BACK_RIGHT_DRIVE_PER_REVOLUTION * rotations, ControlType.kPosition, 0, 4);

    // drive.getSwerveModules()[0].setTargetVelocity(0, 0);
    // DrivetrainSubsystem.getInstance().moveRight(0.2);
  }

  // Called once the command ends or is interrupted.
  @Override
  public void end(boolean interrupted) {
    // drive.holonomicDrive(Vector2.ZERO, 0);
  }

  private boolean encoderHitPosition(CANEncoder encoder, double revCount) {
    return encoder.getPosition() + .5 >= revCount;
  }

  private boolean encodersFinished() {
    // return encoderHitPosition(encoders[0],
    // Constants.FRONT_LEFT_DRIVE_PER_REVOLUTION * rotations)
    // && encoderHitPosition(encoders[1], Constants.FRONT_RIGHT_DRIVE_PER_REVOLUTION
    // * rotations)
    // && encoderHitPosition(encoders[2], Constants.BACK_LEFT_DRIVE_PER_REVOLUTION *
    // rotations)
    // && encoderHitPosition(encoders[3], Constants.BACK_RIGHT_DRIVE_PER_REVOLUTION
    // * rotations);
    return true;
  }

  // Returns true when the command should end.
  @Override
  public boolean isFinished() {
    return encodersFinished();
  }
}
