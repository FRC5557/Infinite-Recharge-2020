package net.bancino.robotics.swerveio.gyro;

import edu.wpi.first.wpilibj.SPI;
import com.kauailabs.navx.frc.AHRS;

/**
 * A Kauai Labs FRC NavX Gyro that implements the abstract 
 * gyro interface for use with the swerve drive.
 * 
 * @author Jordan Bancino
 * @version 2.0.0
 * @since 2.0.0
 */
public class NavXGyro implements AbstractGyro {

    private final AHRS gyro;

    /**
     * Create a gyro object on the given port.
     * 
     * @param port The SPI port that the gyro is on.
     */
    public NavXGyro(SPI.Port port) {
        if (port != null) {
            gyro = new AHRS(port);
        } else {
            throw new IllegalArgumentException("NavX Port cannot be null.");
        }
    }

    @Override
    public double getAngle() {
        double yaw = gyro.getYaw();
        if (yaw < 0) {
            return yaw + 360;
        } else {
            return yaw;
        }
    }
    
    /**
     * The NavX gyro has an option for getting the continuous angle
     * of the yaw. This doesn't reset at 0 or 360, but rather, continues
     * counting up or down, depending on the direction. This therefore may
     * return numbers greater than 360, or less than 0.
     * 
     * @return The continuous angle of the NavX gyro.
     */
    public double getContinuousAngle() {
        return gyro.getAngle();
    }

    @Override
    public void zero() {
        gyro.zeroYaw();
    }

    /**
     * Whether or not the gyro is conncted. This is useful for debugging, or waiting
     * for calibration/zeroing.
     * 
     * @return Whether or not the gyro is connected to the respective port.
     */
    public boolean isConnected() {
        return gyro.isConnected();
    }

}