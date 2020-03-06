package net.bancino.robotics.swerveio.log;

import net.bancino.robotics.swerveio.SwerveDrive;

/**
 * An interface that allows support for logging the state of a swerve
 * drive to an output source, either a log file, the NetworkTables, or something
 * else. This is run iteratively via the scheduler when activated, so when
 * implementing this method, make sure that it is set up for periodic handling.
 * 
 * @author Jordan Bancino
 * @version 1.3.0
 * @since 1.2.2
 */
public interface SwerveLogger {

    /**
     * Log the state of the swerve drive. This is run once per scheduler cycle.
     * 
     * @param drive The swerve drive to log the state of.
     */
    public void logState(SwerveDrive drive);

    public boolean requiresEnabledDriverStation();
}