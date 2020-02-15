/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018-2019 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.CommandBase;
import edu.wpi.first.wpilibj2.command.CommandGroupBase;
import frc.robot.Robot;
import frc.robot.RobotContainer;
import frc.robot.subsystems.LauncherSubsystem;
import frc.robot.Constants;

public class LaunchLowerCommand extends CommandBase {
    LauncherSubsystem launcher;

    public LaunchLowerCommand() {
        launcher = LauncherSubsystem.getInstance();
        addRequirements(launcher);
    }

    @Override
    public void initialize() {

    }

    @Override
    public void execute() {
        launcher.feedIn();
        launcher.launchLower();
    }

    @Override
    public void end(boolean interrupted) {
        launcher.stop();
        launcher.stopFeed();
    }

    @Override
    public boolean isFinished() {
        return false;
    }

}