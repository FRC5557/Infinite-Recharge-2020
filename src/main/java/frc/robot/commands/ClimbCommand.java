/*----------------------------------------------------------------------------*/
/* Copyright (c) 2019 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.Direction;
import frc.robot.subsystems.ClimbSubsystem;

public class ClimbCommand extends CommandBase {
  Direction direction;
  ClimbSubsystem climb;

  /**
   * Creates a new ClimbCommand.
   */
  public ClimbCommand(Direction direction) {
    this.direction = direction;
    this.climb = ClimbSubsystem.getInstance();
    addRequirements(climb);
  }

  // Called when the command is initially scheduled.
  @Override
  public void initialize() {
    // nothing here for now
  }

  // Called every time the scheduler runs while the command is scheduled.
  @Override
  public void execute() {
    switch (direction) {
    case UP:
      climb.climbUp();
      break;
    case DOWN:
      climb.climbDown();
      break;
    case LEFT:
      climb.climbLeftUp();
      break;
    case RIGHT:
      climb.climbRightUp();
      break;
    default:
      break;
    }
  }

  // Called once the command ends or is interrupted.
  @Override
  public void end(boolean interrupted) {
    climb.stopClimb();
  }

  // Returns true when the command should end.
  @Override
  public boolean isFinished() {
    return false;
  }
}
