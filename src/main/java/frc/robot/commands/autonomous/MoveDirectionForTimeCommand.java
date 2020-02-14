/*----------------------------------------------------------------------------*/
/* Copyright (c) 2019 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.commands.autonomous;

import org.frcteam2910.common.math.Vector2;

import edu.wpi.first.wpilibj.geometry.Translation2d;
import edu.wpi.first.wpilibj2.command.CommandBase;
import edu.wpi.first.wpilibj2.command.WaitCommand;
import frc.robot.Direction;
import frc.robot.subsystems.swerve.DrivetrainSubsystem;

public class MoveDirectionForTimeCommand extends WaitCommand {
  DrivetrainSubsystem drive;

  Direction direction;
  double strafe;
  double forward;
  double angle;

  /**
   * Creates a new MoveBackForTimeCommand.
   */
  public MoveDirectionForTimeCommand(double time, Direction direction) {
    super(time);
    this.direction = direction;
    drive = DrivetrainSubsystem.getInstance();
    addRequirements(drive);
    switch(direction) {
      case FORWARD:
        strafe = 0;
        forward = .5;
        break;

      case BACKWARD:
        strafe = 0;
        forward = -.5;
        break;

      case RIGHT:
        strafe = -.5;
        forward = 0;
        break;
    
      case LEFT:
        strafe = .5;
        forward = 0;
        break;
      
      default:
        strafe = 0;
        forward = 0;
        break;
    }
  }

  // Called when the command is initially scheduled.
  // @Override
  // public void initialize() {
      
  // }

  // Called every time the scheduler runs while the command is scheduled.
  @Override
  public void execute() {
    // System.out.println(forward + "   " + strafe);
    // for now, no rotation but we'll switch something up later
    
      drive.drive(new Vector2(forward, strafe), 0, true);
    
  }

  // Called once the command ends or is interrupted.
  @Override
  public void end(boolean interrupted) {
    drive.stop();
  }

}
