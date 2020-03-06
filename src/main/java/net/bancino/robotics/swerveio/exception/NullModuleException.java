package net.bancino.robotics.swerveio.exception;

import net.bancino.robotics.swerveio.SwerveModule;

/**
 * An exception that indicates a null parameter was passed for a swerve drive
 * module.
 * 
 * @author Jordan Bancino
 */
public class NullModuleException extends SwerveException {
    private static final long serialVersionUID = 7343634906936151028L;

    /**
     * Create a null module exception.
     * 
     * @param module The module that is null.
     */
    public NullModuleException(SwerveModule module) {
        super(module + " swerve module is null.");
    }
}