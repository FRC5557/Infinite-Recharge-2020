package net.bancino.robotics.swerveio.command;

import edu.wpi.first.wpilibj2.command.CommandBase;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

/**
 * A WPILib command that is a wrapper for Java's Runnable interface.
 * This allows the execution of any arbitrary code as a command. This is incredibly
 * useful for mapping API functions or other random blocks of code to a button
 * without having to explicitly create a command. This also allows the creation of
 * commands with lambda expressons, something super useful in the RobotContainer class.
 * 
 * @author Jordan Bancino
 */
public class RunnableCommand extends CommandBase {
    private final Runnable run;
    private boolean isFinished = false;
    public RunnableCommand(Runnable run, SubsystemBase... requiredSubsystems) {
        addRequirements(requiredSubsystems);
        this.run = run;
    }

    @Override
    public void initialize() {
        isFinished = false;
    }

    @Override
    public void execute() {
        run.run();
        isFinished = true;
    }

    @Override
    public boolean isFinished() {
        return isFinished;
    }
}