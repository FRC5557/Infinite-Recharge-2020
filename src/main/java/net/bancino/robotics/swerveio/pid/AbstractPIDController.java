package net.bancino.robotics.swerveio.pid;

/**
 * An abstract representation of a PID controller, which can be used for
 * controlling the pivot and drive motors of a swerve module.
 * 
 * @author Jordan Bancino
 * @version 1.2.3
 * @since 1.2.3
 */
public interface AbstractPIDController {

    /**
     * Set the proportional gain of the PID loop coefficient. (The motor will
     * correct itself proportional to the offset of the measure compared to its
     * targeted measure.) It's only weakness will be when it treats positive and
     * negative offset equally, otherwise it narrows error to 0.
     * 
     * @param p Proportional gain value. Must be positive.
     */
    public void setP(double p);

    /**
     * Set the integral gain of the PID loop coefficient. (The motor will correct
     * itself based on past errors and integrates the history of offset to narrow
     * error down to 0.) The downside of using a purely integral system is that it's
     * slow to start, as it takes time to accumulate enough information to
     * accurately form it's coefficient.
     * 
     * @param i Integral gain value. Must be positive.
     */
    public void setI(double i);

    /**
     * Set the derivative gain of the PID loop coefficient. (The motor will correct
     * error based on it's rate of change, not seeking to bring the error to 0, but
     * seeking to keep the error that the system is stable.) A derivative loop will
     * never bring your error to 0, but will simply keep your error from growing
     * larger by catching any change and correcting it.
     * 
     * @param d Derivative gain value. Must be positive.
     */
    public void setD(double d);

    /**
     * Sets the priority held in feed-forward augment. In a closed loop system, the
     * feed-forward predicts the outcome of the next output from the motor
     * controllers for better error correcting accuracy.
     * 
     * @param f Feed-forward gain value. Must be positive.
     */
    public void setF(double f);

    /**
     * A convenience function for setting PID values all at once. See {@link #setP},
     * {@link #setI}, and {@link #setD}
     * 
     * @param p The value to feed into {@link #setP}.
     * @param i The value to feed into {@link #setI}.
     * @param d The value to feed into {@link #setD}.
     */
    public default void setPID(double p, double i, double d) {
        setP(p);
        setI(i);
        setD(d);
    }

    /**
     * A convenience function for setting PID values all at once. See {@link #setP},
     * {@link #setI}, {@link #setD}, and {@link #setF}
     * 
     * @param p The value to feed into {@link #setP}.
     * @param i The value to feed into {@link #setI}.
     * @param d The value to feed into {@link #setD}.
     * @param f The value to feed into {@link #setF}.
     */
    public default void setPID(double p, double i, double d, double f) {
        setPID(p, i, d);
        setF(f);
    }

    /**
     * Set the maximum output value contributed by the I component of the system
     * This can be used to prevent large windup issues and make tuning simpler
     * 
     * @param max Units are the same as the expected output value
     */
    public void setMaxIOutput(double max);

    /**
     * Set the output limits of the pivot PID controller.
     * 
     * @param min The minimum value the PID should output.
     * @param max The maximum value the PID should output.
     */
    public void setOutputLimits(double min, double max);

    /**
     * Set the output limits of the pivot PID controller.
     * 
     * @param output Will produce an output limit of -output to +output.
     */
    public default void setOutputLimits(double output) {
        setOutputLimits(-output, output);
    }

    /**
     * A simple toggle for reversing the PID loop's output.
     * 
     * @param reversed Whether or not to flip the PID output.
     */
    public void setDirection(boolean reversed);

    /**
     * This is the main function of the PID loop. It performs all the calculations
     * to generate PID output.
     * 
     * @param feedback The output feedback; your current reference; where you are
     *                 currently.
     * @param setpoint The destination location. This is where you want to be.
     * @return The output of the PID calculation given the input.
     */
    public double getOutput(double feedback, double setpoint);

    /**
     * Get the previous output from the PID loop. This should store whatever the previous call to
     * the output function generates.
     * 
     * @return The previous output of the PID calculation.
     */
    public double getPreviousOutput();

    /**
     * Reset the controller, erasing the previous output, feedback, and setpoints,
     * if stored.
     */
    public void reset();

    /**
     * Sets the ramp rate for the output.
     * 
     * @param rate Time in seconds to go from 0 to 100%.
     */
    public void setOutputRampRate(double rate);

    /**
     * Set the output range of this PID controller. This effectively scales the
     * controller so it knows the bounds of what it will be fed.
     * 
     * @param range The setpoint input range. In relation to swerve drive, this will
     *              be the counts per revolution of your pivot encoder, and will
     *              have no limit on the drive motor.
     */
    public void setSetpointRange(double range);

    /**
     * Set a filter on the output to reduce sharp oscillations. <br>
     * 0.1 is likely a sane starting value. Larger values use historical data more
     * heavily, with low values weigh newer data. 0 will disable, filtering, and use
     * only the most recent value. <br>
     * Increasing the filter strength will P and D oscillations, but force larger I
     * values and increase I term overshoot.<br>
     * Uses an exponential wieghted rolling sum filter, according to a simple <br>
     * 
     * <pre>
     * output*(1-strength)*sum(0..n){output*strength^n}
     * </pre>
     * 
     * algorithm.
     * 
     * @param strength valid between [0..1), meaning [current output only..
     *                 historical output only)
     */
    public void setOutputFilter(double strength);

}