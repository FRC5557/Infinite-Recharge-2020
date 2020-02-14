/*----------------------------------------------------------------------------*/
/* Copyright (c) 2019 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.commands;

import com.revrobotics.CANPIDController;
import com.revrobotics.ControlType;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.Constants;
import frc.robot.subsystems.SpinnerSubsystem;

public class SpinForRevolutionsCommand extends CommandBase {
  SpinnerSubsystem spinnerSubsystem;
  CANPIDController controller;

  // this is how many revolutions of the control system, not of the motor
  double revolutions;

  double totalRevolutionsNeeded = 0;
  public double kP, kI, kD, kIz, kFF, kMaxOutput, kMinOutput;


  /**
   * Creates a new SpinForRevolutionsCommand.
   */
  public SpinForRevolutionsCommand(double revolutions) {
    spinnerSubsystem = SpinnerSubsystem.getInstance();
    this.revolutions = revolutions;
    this.controller = SpinnerSubsystem.getInstance().getPIDController();
    // Use addRequirements() here to declare subsystem dependencies.
    addRequirements(spinnerSubsystem);
  }

  // Called when the command is initially scheduled.
  @Override
  public void initialize() {

    kP = 0.05;
    kI = 1e-4;
    kD = 1;
    kIz = 0;
    kFF = 0;
    kMaxOutput = 1;
    kMinOutput = -1;

    // we will need to zero the encoder here
    spinnerSubsystem.zeroEncoder();
    // how many revs for one wheel turn * how many wheel turns per one revolution of
    // the control system
    // * how many revolutions of the control system are desired
    // i have to double check that this math is right
    // totalRevolutionsNeeded = Constants.SPINNER_MOTOR_COUNTER_PER_REVOLUTION * Constants.SPINNER_MOTOR_WHEEL_REVOLUTION_PER_SPINNER_REVOLUTION * revolutions;
    totalRevolutionsNeeded = this.revolutions * Constants.SPINNER_MOTOR_COUNTER_PER_REVOLUTION;
    controller.setP(kP);
    controller.setI(kI);
    controller.setD(kD);
    controller.setIZone(kIz);
    controller.setFF(kFF);
    controller.setOutputRange(kMinOutput, kMaxOutput);
  }

  // Called every time the scheduler runs while the command is scheduled.
  @Override
  public void execute() {
    // spin the motor slowly until it reaches the desired revolution count
    controller.setReference(this.totalRevolutionsNeeded, ControlType.kPosition);
    // System.out.println(spinnerSubsystem.getEncoderPosition());
  }

  // Called once the command ends or is interrupted.
  @Override
  public void end(boolean interrupted) {
    spinnerSubsystem.spinSpinner(0.0);
  }

  // Returns true when the command should end.
  @Override
  public boolean isFinished() {
    return false;
  }
}
