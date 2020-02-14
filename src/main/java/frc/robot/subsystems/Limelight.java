package frc.robot.subsystems;

import edu.wpi.first.networktables.*;

public class Limelight {

    private static Limelight instance = null;

    public static Limelight getInstance() {
        if (instance == null) {
            instance = new Limelight();
        }
        return instance;
    }

    private NetworkTable table;

    public Limelight() {
        table = NetworkTableInstance.getDefault().getTable("limelight");
    }

    public double getAngleX() {
        return table.getEntry("tx").getDouble(0);
    }

    public double getAngleY() {
        return table.getEntry("ty").getDouble(0);
    }

    public double getArea() {
        return table.getEntry("ta").getDouble(0);
    }

    public double getSkew() {
        return table.getEntry("ts").getDouble(0);
    }

    public boolean hasTarget() {
        return table.getEntry("tv").getDouble(0) == 0 ? false : true;
    }

    public void enableLEDs() {
        table.getEntry("ledMode").setDouble(3);
    }

    public void disableLEDs() {
        table.getEntry("ledMode").setDouble(1);
    }

    public void enableDriverMode() {
        table.getEntry("camMode").setDouble(1);
    }

    public void disableDriverMode() {
        table.getEntry("camMode").setDouble(0);
    }
}