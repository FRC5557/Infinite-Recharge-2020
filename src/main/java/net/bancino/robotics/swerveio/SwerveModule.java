package net.bancino.robotics.swerveio;

/**
 * Each module is driven independently, they all have their own speeds and
 * angles. Use this enumeration to specify modules.
 * 
 * @author Jordan Bancino
 */
public enum SwerveModule {
    FRONT_LEFT, FRONT_RIGHT, REAR_LEFT, REAR_RIGHT;

    /**
     * The length of this enum. Used for checking a module map for the proper
     * length. Instead of calling this code all the time, we set this static
     * variable that we can reference. This will reduce overhead, if only by
     * slightly
     */
    public static final int length = SwerveModule.values().length;
}