/*----------------------------------------------------------------------------*/
/* Copyright (c) 2019 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.CommandBase;
import edu.wpi.first.wpilibj2.command.WaitCommand;
import frc.robot.subsystems.LauncherSubsystem;

public class LaunchUpperForTimeCommand extends WaitCommand {
  LauncherSubsystem launcher;

  /**
   * Creates a new LanchUpperForTimeCommand.
   */
  public LaunchUpperForTimeCommand(double time) {
    super(time);
    launcher = LauncherSubsystem.getInstance();
    addRequirements(launcher);
  }

  // Called when the command is initially scheduled.
  @Override
  public void initialize() {
  }

  // Called every time the scheduler runs while the command is scheduled.
  @Override
  public void execute() {
    launcher.feedIn();
    launcher.launchUpper();
  }

  // Called once the command ends or is interrupted.
  @Override
  public void end(boolean interrupted) {
    launcher.stop();
    launcher.stopFeed();
  }

}
