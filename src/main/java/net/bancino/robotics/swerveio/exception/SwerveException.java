package net.bancino.robotics.swerveio.exception;

/**
 * A generic exception that is the superclass for all exceptions specific to
 * this library. See the documentation for <code>Exception</code>.
 * 
 * @author Jordan Bancino
 */
public class SwerveException extends Exception {
    private static final long serialVersionUID = 1L;

    public SwerveException() {
        super();
    }

    public SwerveException(String message) {
        super(message);
    }

    protected SwerveException(String message, Throwable cause, boolean enableSuppression, boolean writeStackTrace) {
        super(message, cause, enableSuppression, writeStackTrace);
    }

    public SwerveException(Throwable cause) {
        super(cause);
    }

}