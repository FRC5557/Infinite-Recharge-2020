package net.bancino.robotics.swerveio.exception;

/**
 * A generic runtime exception that is the superclass for all exceptions
 * specific to this library. See the documentation for <code>Exception</code>.
 * 
 * @author Jordan Bancino
 */
public class SwerveRuntimeException extends RuntimeException {
    private static final long serialVersionUID = 35272989249717212L;

    public SwerveRuntimeException() {
        super();
    }

    public SwerveRuntimeException(String message) {
        super(message);
    }

    protected SwerveRuntimeException(String message, Throwable cause, boolean enableSuppression,
            boolean writeStackTrace) {
        super(message, cause, enableSuppression, writeStackTrace);
    }

    public SwerveRuntimeException(Throwable cause) {
        super(cause);
    }
}