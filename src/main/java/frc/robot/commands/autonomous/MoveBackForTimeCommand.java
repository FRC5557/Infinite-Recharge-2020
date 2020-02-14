/*----------------------------------------------------------------------------*/
/* Copyright (c) 2019 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.commands.autonomous;

import edu.wpi.first.wpilibj2.command.CommandBase;
import edu.wpi.first.wpilibj2.command.WaitCommand;
import frc.robot.Direction;
import frc.robot.subsystems.swerve.DrivetrainSubsystem;

public class MoveBackForTimeCommand extends WaitCommand {
  DrivetrainSubsystem drive;

  /**
   * Creates a new MoveBackForTimeCommand.
   */
  public MoveBackForTimeCommand(double time, Direction direction) {
    super(time);
    drive = DrivetrainSubsystem.getInstance();
    addRequirements(drive);
    
  }

  // Called when the command is initially scheduled.
  // @Override
  // public void initialize() {

  // }

  // Called every time the scheduler runs while the command is scheduled.
  @Override
  public void execute() {
    
  }

  // Called once the command ends or is interrupted.
  @Override
  public void end(boolean interrupted) {
    drive.stop();
  }

}
