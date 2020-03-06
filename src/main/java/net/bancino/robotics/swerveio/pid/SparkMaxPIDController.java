package net.bancino.robotics.swerveio.pid;

import com.revrobotics.CANPIDController;
import com.revrobotics.CANSparkMax;

public class SparkMaxPIDController implements AbstractPIDController {

    CANSparkMax motor;
    CANPIDController pid;

    public SparkMaxPIDController(CANSparkMax sparkMax) {
        if (sparkMax != null) {
            this.motor = sparkMax;
            if (sparkMax.getPIDController() != null) {
                this.pid = sparkMax.getPIDController();
            } else {
                throw new IllegalArgumentException("This Spark Max's PID controller is null. That should not happen.");
            }
        } else {
            throw new IllegalArgumentException("null motor controller passed into this PID controller.");
        }
    }

    @Override
    public void setP(double p) {
        pid.setP(p);
    }

    @Override
    public void setI(double i) {
        pid.setI(i);
    }

    @Override
    public void setD(double d) {
        pid.setD(d);
    }

    @Override
    public void setF(double f) {
        pid.setFF(f);

    }

    @Override
    public void setMaxIOutput(double max) {
        pid.setIZone(max);
    }

    @Override
    public void setOutputLimits(double min, double max) {
        pid.setOutputRange(min, max);
    }

    @Override
    public void setDirection(boolean reversed) {
        motor.setInverted(reversed);

    }

    @Override
    public double getOutput(double feedback, double setpoint) {
        throw new UnsupportedOperationException(
                "Extracting raw output from a Spark Max PID controller is not implemented.");
    }

    @Override
    public void reset() {
        throw new UnsupportedOperationException("Resetting Spark Max PID controllers not allowed");
    }

    @Override
    public void setOutputRampRate(double rate) {
        motor.setClosedLoopRampRate(rate);
    }

    @Override
    public void setSetpointRange(double range) {
        throw new UnsupportedOperationException("There is no setpoint range for a Spark Max pid controller.");
    }

    @Override
    public void setOutputFilter(double strength) {
        throw new UnsupportedOperationException("Output filter not implemented in Spark Max PID controller.");
    }

    @Override
    public double getPreviousOutput() {
        throw new UnsupportedOperationException(
                "Extracting raw output from a Spark Max PID controller is not implemented.");
    }
}