package net.bancino.robotics.swerveio.exception;

/**
 * If a swerve module is not fully implemented, it should throw this exception
 * when unimplemented calls are made. This is also used by the driver class when
 * a module does not behave as expected.
 * 
 * @author Jordan Bancino
 */
public class SwerveImplementationException extends SwerveRuntimeException {
    private static final long serialVersionUID = 6781810448194629607L;

    public SwerveImplementationException(String msg) {
        super(msg);
    }
}