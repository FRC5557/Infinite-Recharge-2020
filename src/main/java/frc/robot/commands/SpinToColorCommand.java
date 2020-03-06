/*----------------------------------------------------------------------------*/
/* Copyright (c) 2019 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.Robot;
import frc.robot.RobotContainer;
import frc.robot.SpinnerColor;
import frc.robot.subsystems.SpinnerSubsystem;
import frc.robot.Constants;

public class SpinToColorCommand extends CommandBase {

  SpinnerSubsystem spinnerSubsystem;
  SpinnerColor requiredColor;

  /**
   * Creates a new SpinToColorCommand.
   */
  public SpinToColorCommand() {
    spinnerSubsystem = SpinnerSubsystem.getInstance();
    // System.out.println(Constants.colorMapping.get(SpinnerColor.RED));
    // System.out.println(().toString());
    // System.out.println(Robot.getRobotContainer().getSpinnerColor());
    // this.requiredColor = SpinnerColor.GREEN;
    // Use addRequirements() here to declare subsystem dependencies.
    addRequirements(spinnerSubsystem);
    // addRequirements(LauncherSubsystem.getInstance());
  }

  // Called when the command is initially scheduled.
  @Override
  public void initialize() {
    this.requiredColor = Constants.colorMapping.get(RobotContainer.getInstance().getSpinnerColor());

  }

  // Called every time the scheduler runs while the command is scheduled.
  @Override
  public void execute() {
    // so what we wanna do is just keep spinning the motor at a slow rate
    // System.out.println(requiredColor);
    spinnerSubsystem.spinSpinner(-.1);
  }

  // Called once the command ends or is interrupted.
  @Override
  public void end(boolean interrupted) {
    spinnerSubsystem.spinSpinner(0.0);
  }

  // Returns true when the command should end.
  @Override
  public boolean isFinished() {
    System.out.println(spinnerSubsystem.getColor());

    // return when color sensor value = desired color
    if( Robot.getRobotContainer().getSpinnerColor() == SpinnerColor.UNKNOWN
        || spinnerSubsystem.getColor() == requiredColor) {
          System.out.println("FINISHED");
          return true;
        } 
        return false;
  }
}
