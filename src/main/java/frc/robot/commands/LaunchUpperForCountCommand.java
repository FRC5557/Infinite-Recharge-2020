/*----------------------------------------------------------------------------*/
/* Copyright (c) 2019 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.commands;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj2.command.CommandBase;
import edu.wpi.first.wpilibj2.command.WaitCommand;
import frc.robot.subsystems.IntakeSubsystem;
import frc.robot.subsystems.LauncherSubsystem;

public class LaunchUpperForCountCommand extends CommandBase {
    int ballCount;
    boolean didBreak = false;
    int currentBallCount = 0;
    DigitalInput lineBreakSensor;
    LauncherSubsystem launcher;
    IntakeSubsystem intake;

    /**
     * Creates a new LanchUpperForTimeCommand.
     */
    public LaunchUpperForCountCommand(int ballCount) {
        this.ballCount = ballCount;
        intake = IntakeSubsystem.getInstance();
        launcher = LauncherSubsystem.getInstance();
        lineBreakSensor = new DigitalInput(0);
        addRequirements(launcher, intake);
    }

    // Called when the command is initially scheduled.
    @Override
    public void initialize() {
    }

    // Called every time the scheduler runs while the command is scheduled.
    @Override
    public void execute() {
        if (lineBreakSensor.get()) {
            if (didBreak) {

            }
        } else {

        }

        intake.intakeIn();
        launcher.launchUpper();
    }

    // Called once the command ends or is interrupted.
    @Override
    public void end(boolean interrupted) {
        intake.stop();
        launcher.stop();
    }

    @Override
    public boolean isFinished() {
        return this.ballCount == currentBallCount;
    }

}
