package net.bancino.robotics.swerveio.log;

import java.util.Map;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import net.bancino.robotics.swerveio.SwerveDrive;
import net.bancino.robotics.swerveio.SwerveModule;
import net.bancino.robotics.swerveio.SwerveVector;
import net.bancino.robotics.swerveio.module.AbstractSwerveModule;

/**
 * A SmartDashboard logging class designed for cleanly logging swerve drive
 * variables to the dashboard. This can be used for viewing variables in real
 * time.
 * 
 * @author Jordan Bancino
 * @version 1.2.2
 * @since 1.2.2
 */
public class DashboardSwerveLogger implements SwerveLogger {

    private String tableName;

    /**
     * Construct a dashboard logger using the default table name.
     */
    public DashboardSwerveLogger() {
        this("SwerveIO");
    }

    /**
     * Construct a dashboard logger using the given table name.
     * 
     * @param tableName The name of the table to log all swerve data to.
     */
    public DashboardSwerveLogger(String tableName) {
        this.tableName = tableName;
    }

    @Override
    public void logState(SwerveDrive drive) {
        SwerveVector latestInput = drive.getLastSwerveVector();
        String joystick = tableName + "/Joystick/";
        SmartDashboard.putNumber(joystick + "Fwd (Y)", latestInput.getFwd());
        SmartDashboard.putNumber(joystick + "Str (X)", latestInput.getStr());
        SmartDashboard.putNumber(joystick + "Rcw (Z)", latestInput.getRcw());

        SmartDashboard.putNumber(tableName + "/Gyro/Yaw", drive.getLastGyroAngle());

        Map<SwerveModule, AbstractSwerveModule> moduleMap = drive.getModuleMap();
        for (SwerveModule key : moduleMap.keySet()) {
            AbstractSwerveModule module = moduleMap.get(key);
            String entry = tableName + "/Drivetrain/" + key + "/";
            SmartDashboard.putNumber(entry + "Pivot/Speed", module.getPivotMotorSpeed());
            double pivotEncoder = module.getPivotMotorEncoder();
            SmartDashboard.putNumber(entry + "Pivot/Encoder", pivotEncoder);
            double calculatedAngle = drive.getCalculator().getWheelAngle(key, latestInput.getFwd(),
                    latestInput.getStr(), latestInput.getRcw(), drive.getLastGyroAngle());
            SmartDashboard.putNumber(entry + "Pivot/Calculated Angle (CW)", calculatedAngle);
            SmartDashboard.putNumber(entry + "Pivot/Difference", calculatedAngle - pivotEncoder);

            SmartDashboard.putNumber(entry + "Drive/Speed", module.getDriveMotorSpeed());
            SmartDashboard.putNumber(entry + "Drive/Encoder", module.getDriveMotorEncoder());

        }
    }

    @Override
    public boolean requiresEnabledDriverStation() {
        return false;
    }
}