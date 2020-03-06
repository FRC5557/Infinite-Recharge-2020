package net.bancino.robotics.swerveio;

import static java.lang.Math.atan2;
import static java.lang.Math.cos;
import static java.lang.Math.pow;
import static java.lang.Math.sin;
import static java.lang.Math.sqrt;
import static java.lang.Math.toDegrees;
import static java.lang.Math.toRadians;

import net.bancino.robotics.swerveio.si.Length;
import net.bancino.robotics.swerveio.si.Unit;
import net.bancino.robotics.swerveio.si.ChassisDimension;

/**
 * A swerve drive calculator that performs all the calculations required to
 * obtain wheel angles and speeds. The proofs behind why this works is not
 * documented here, but there is plenty online. It is just a little
 * trigonometry.
 * 
 * @author Jordan Bancino
 */
public class SwerveDriveCalculator {
    private Length baseWidth;
    private Length baseLength;

    /**
     * Instantiate a calculator with the given base dimensions. Note that these
     * don't have to be exact measurements, they just need to be proportionate to
     * each other, because they're used in ratios. Because of this, it doesn't
     * matter whether imperical or metric measurements are taken.
     * 
     * @param baseWidth  The width of the drive base.
     * @param baseLength The length of the drive base.
     */
    public SwerveDriveCalculator(Length baseWidth, Length baseLength) {
        setBase(baseWidth, baseLength);
    }

    public SwerveDriveCalculator(ChassisDimension chassisDimension) {
        setBase(chassisDimension.getWidth(), chassisDimension.getLength());
    }


    /**
     * Create a calculator with the default base ratio, which is 1.
     */
    public SwerveDriveCalculator() {
        this(new Length(0.5, Unit.METERS), new Length(0.5, Unit.METERS));
    }

    /**
     * If your base magically decides to dynamically change widths at runtime, you
     * can set new widths here.
     * 
     * @param baseWidth The new base width to be used in future calculations.
     */
    public void setBaseWidth(Length baseWidth) {
        if (baseWidth.get(Unit.METERS) == 0) {
            throw new IllegalArgumentException("Base width cannot be zero.");
        }
        this.baseWidth = baseWidth;
    }

    /**
     * Get the width of the drive base this calculator is using to perform its
     * calculations.
     * 
     * @return The base width.
     */
    public Length getBaseWidth() {
        return baseWidth;
    }

    /**
     * If your base magically decides to dynamically change lengths at runtime, you
     * can set new lengths here.
     * 
     * @param baseLength The new base length to be used in future calculations.
     */
    public void setBaseLength(Length baseLength) {
        if (baseLength.get(Unit.METERS) == 0) {
            throw new IllegalArgumentException("Base length cannot be zero.");
        }
        this.baseLength = baseLength;
    }

    /**
     * Get the length of the drive base this calculator is using to perform its
     * calculations.
     * 
     * @return The base length.
     */
    public Length getBaseLength() {
        return baseLength;
    }

    /**
     * Set the base dimensions. This is a convenience method for setBaseWidth() and
     * setBaseLength().
     * 
     * @param baseWidth  The width to set.
     * @param baseLength The length to set.
     */
    public void setBase(Length baseWidth, Length baseLength) {
        setBaseWidth(baseWidth);
        setBaseLength(baseLength);
    }

    /**
     * Get the chassis dimensions being used in this calculator.
     * @return A ChassisDimension object that represents the dimensions being used in this calculator.
     */
    public ChassisDimension getChassisDimension() {
        return new ChassisDimension(baseWidth, baseLength);
    }

    /**
     * Get the wheel angle for the given degrees.
     * 
     * @param module The wheel to get the angle of.
     * @param fwd    The Y degree (from an input device)
     * @param str    The X degree (from an input device)
     * @param rcw    The Z degree (from an input device)
     * @return The angle (in degrees) that the given wheel should be set to.
     */
    public double getWheelAngle(SwerveModule module, double fwd, double str, double rcw) {
        double[] tmp = getWheelConstants(module, fwd, str, rcw);
        return toDegrees(atan2(tmp[0], tmp[1]));
    }

    /**
     * Get the wheel angle for the given degrees. This is the field-centric method
     * that recalculates the values based on the gyro angle.
     * 
     * @param module    The wheel to get the angle of.
     * @param fwd       The Y degree (from an input device)
     * @param str       The X degree (from an input device)
     * @param rcw       The Z degree (from an input device)
     * @param gyroAngle The gyro angle (in degrees) measured from the zero position
     *                  (sraight down field)
     * @return The angle (in degrees) that the given wheel should be set to.
     */
    public double getWheelAngle(SwerveModule module, double fwd, double str, double rcw, double gyroAngle) {
        double cosAngle = cos(toRadians(gyroAngle));
        double sinAngle = sin(toRadians(gyroAngle));
        double modFwd = (fwd * cosAngle) + (str * sinAngle);
        double modStr = (-fwd * sinAngle) + (str * cosAngle);
        return getWheelAngle(module, modFwd, modStr, rcw);
    }

    /**
     * Get the wheel speed for the given degrees.
     * 
     * @param module The wheel to get the speed of.
     * @param fwd    The Y degree (from an input device)
     * @param str    The X degree (from an input device)
     * @param rcw    The Z degree (from an input device)
     * @return The speed (scaled 0 to 1) that the wheel given wheel should be set
     *         to.
     */
    public double getWheelSpeed(SwerveModule module, double fwd, double str, double rcw) {
        double[] tmp = getWheelConstants(module, fwd, str, rcw);
        double wheelSpeed = sqrt(pow(tmp[0], 2) + pow(tmp[1], 2));
        return (wheelSpeed > 1f) ? 1f : wheelSpeed;
    }

    /**
     * Wheel constants are generated by using the degrees provided, these aren't
     * really "constants" exactly since they're caculated depending on the provided
     * degree values, but there's really no other name for them
     * 
     * @param module The wheel to fetch constants for.
     * @param fwd    The Y degree
     * @param str    The X degree
     * @param rcw    the Z degree
     * @return An array of floats containing the wheel constants. This array will
     *         always be of size 2.
     */
    private double[] getWheelConstants(SwerveModule module, double fwd, double str, double rcw) {
        /* Calculate the ratio constant */
        double baseLength = this.baseLength.get(Unit.METERS);
        double baseWidth = this.baseWidth.get(Unit.METERS);
        final double R = sqrt(pow(baseLength, 2) + pow(baseWidth, 2));
        /* Allocate the array */
        double sides[] = new double[2];
        /*
         * The exact way these are calculated may seem arbitrary, but reading up on any
         * swerve guide, or looking at any diagram will show you exactly how these are
         * calcuated, it's just a little basic trig.
         * 
         * This basically involves computing the sides of a triangle that will be used
         * to construct a vector.
         */
        switch (module) {
        case FRONT_RIGHT:
            sides[0] = str + rcw * (baseLength / R);
            sides[1] = fwd - rcw * (baseWidth / R);
            break;
        case FRONT_LEFT:
            sides[0] = str + rcw * (baseLength / R);
            sides[1] = fwd + rcw * (baseWidth / R);
            break;
        case REAR_LEFT:
            sides[0] = str - rcw * (baseLength / R);
            sides[1] = fwd + rcw * (baseWidth / R);
            break;
        case REAR_RIGHT:
            sides[0] = str - rcw * (baseLength / R);
            sides[1] = fwd - rcw * (baseWidth / R);
            break;
        }
        return sides;
    }
}
