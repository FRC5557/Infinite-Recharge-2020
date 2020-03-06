package net.bancino.robotics.swerveio.encoder;

import com.revrobotics.CANEncoder;
import com.revrobotics.CANAnalog;
import com.revrobotics.CANSparkMax;

import net.bancino.robotics.swerveio.exception.SwerveImplementationException;

/**
 * An encoder implementation that maps to a Spark Max motor controller's
 * internal encoder, if one is available.
 * 
 * @author Jordan Bancino
 */
public class SparkMaxEncoder implements AbstractEncoder {

    /**
     * The encoder mode tells the encoder object where to read from.
     * The spark max API has three modes: Internal, which reads from the 
     * encoder data port, Alternate, which reads from the data port on top,
     * and Analog. I have no idea where that reads from, but this will expose
     * the CANANalog's getVoltage() method.
     */
    public static enum EncoderMode {
        INTERNAL, ALTERNATE, ANALOG
    }

    private Object encoder;
    private boolean ranSetController = false;

    private EncoderMode encoderMode = EncoderMode.INTERNAL;
    @SuppressWarnings("unused")
    private double encoderOffset = 0;

    /**
     * Create a new spark max encoder with a motor controller. This is the
     * recommended way of creating this encoder.
     * 
     * @param sparkMax The motor controller to use.
     */
    public SparkMaxEncoder(CANSparkMax sparkMax) {
        setController(sparkMax);
    }

    /**
     * Create a new spark max encoder with a motor controller and some
     * other settings.
     *
     * @param sparkMax The motor controller to use.
     * @param encoderMode Where to read the encoder from.
     * @param encoderOffset The encoder offset for absolute encoders. Set to zero to disable.
     */
    public SparkMaxEncoder(CANSparkMax sparkMax, EncoderMode encoderMode, double encoderOffset) {
        this(sparkMax);
        this.encoderMode = encoderMode;
        this.encoderOffset = encoderOffset;
    }

    /**
     * Use this constructor only if the motor controller this encoder will be using
     * is not yet available. Pass it to the setController() function as soon as it
     * is possible.
     */
    public SparkMaxEncoder() {

    }

    public SparkMaxEncoder(EncoderMode encoderMode, double encoderOffset) {
        this.encoderMode = encoderMode;
        this.encoderOffset = encoderOffset;
    }

    /**
     * This enables the motor controller to be set after instantiation. Note that
     * this function can only be run once for the life of an encoder because
     * encoders cannot change their sources. So, for instance, if this encoder is
     * instantiated with a motor controller, then this function will not work. If
     * the empty constructor is used, then you can call this function only once for
     * this object.
     * 
     * @param sparkMax The motor controller to use.
     */
    public void setController(CANSparkMax sparkMax) {
        if (!ranSetController) {
            if (sparkMax == null) {
                throw new IllegalArgumentException("Spark Max cannot be null.");
            }
            switch (encoderMode) {
                case INTERNAL:
                    encoder = sparkMax.getEncoder();
                    break;
                case ALTERNATE:
                    encoder = sparkMax.getAlternateEncoder();
                    break;
                case ANALOG:
                    encoder = sparkMax.getAnalog(CANAnalog.AnalogMode.kAbsolute);
                    break;
            }
            if (encoder == null) {
                throw new UnsupportedOperationException("No encoder attached to Spark Max.");
            }
            ranSetController = true;
        } else {
            throw new UnsupportedOperationException(
                    "Cannot call setController() more than one time per encoder object!");
        }
    }

    @Override
    public double get() {
        if (encoder != null) {
            if (encoder instanceof CANEncoder) {
                return ((CANEncoder) encoder).getPosition();
            } else if (encoder instanceof CANAnalog) {
                return ((CANAnalog) encoder).getPosition();
            } else {
                throw new IllegalArgumentException("Something happened and I don't know what.");
            }
            //double raw = encoder.getPosition();
            //if (useAlternateEncoder) {
            //    if (raw - encoderOffset < 0) {
            //        return raw + (countsPerRevolution() - encoderOffset);
            //    } else {
            //        return raw - encoderOffset;
            //    }
            //} else {
            //    return raw;
            //}
            //return raw;
        } else {
            throw new UnsupportedOperationException("This encoder is not monitoring a motor controller.");
        }
    }

    @Override
    public void set(double val) throws SwerveImplementationException {
        if (encoder != null) {
            if (encoder instanceof CANEncoder) {
                ((CANEncoder) encoder).setPosition(val);
                encoderOffset = 0;
            } else if (encoder instanceof CANAnalog) {
                throw new UnsupportedOperationException("SparkMax CANAnalog does not support setting position.");
            } else {
                throw new IllegalArgumentException("Something happened and I don't know what.");
            }
        } else {
            throw new UnsupportedOperationException("This encoder is not monitoring a motor controller.");
        }
    }

    @Override
    public double countsPerRevolution() {
        return 360;
    }
}