package net.bancino.robotics.swerveio.log.csv;

import java.io.PrintStream;
import java.io.File;
import java.io.OutputStream;
import java.io.FileOutputStream;
import java.io.FileNotFoundException;

import java.util.Map;

import net.bancino.robotics.swerveio.SwerveDrive;
import net.bancino.robotics.swerveio.SwerveVector;
import net.bancino.robotics.swerveio.SwerveModule;
import net.bancino.robotics.swerveio.module.AbstractSwerveModule;

/**
 * A Swerve logging class that logs data in the CSV format to the specified
 * source. This can be helpful for graphing certain values, or quickly looking
 * for changes.
 * 
 * @author Jordan Bancino
 * @version 1.3.0
 * @since 1.2.3
 */
public class CSVSwerveLogger extends AbstractCSVSwerveLogger {

    /**
     * Create a new csv swerve logger.
     * 
     * @param csv The print stream to write output data to.
     */
    public CSVSwerveLogger(PrintStream csv) {
        super(csv,
                "time,joystickX,joystickY,joystickZ,gyroYaw,"
                        + "frontLeftPivotEncoder,frontLeftPivotSpeed,frontLeftDriveEncoder,frontLeftDriveSpeed,"
                        + "frontRightPivotEncoder,frontRightPivotSpeed,frontRightDriveEncoder,frontRightDriveSpeed,"
                        + "rearLeftPivotEncoder,rearLeftPivotSpeed,rearLeftDriveEncoder,rearLeftDriveSpeed,"
                        + "rearRightPivotEncoder,rearRightPivotSpeed,rearRightDriveEncoder,rearRightDriveSpeed");
    }

    /**
     * Create a new csv swerve logger.
     * 
     * @param csv The file to write output data to.
     * @throws FileNotFoundException if the specified file does not exist.
     */
    public CSVSwerveLogger(File csv) throws FileNotFoundException {
        this(new FileOutputStream(csv));
    }

    /**
     * Create a new csv swerve logger.
     * 
     * @param csv The output stream to write output data to.
     */
    public CSVSwerveLogger(OutputStream csv) {
        this(new PrintStream(csv));
    }

    @Override
    public void logState(SwerveDrive drive) {
        csv.print(System.currentTimeMillis() + ",");

        SwerveVector latestInput = drive.getLastSwerveVector();
        csv.print(latestInput.getStr() + "," + latestInput.getFwd() + "," + latestInput.getRcw() + ",");

        csv.print(drive.getLastGyroAngle() + ",");

        Map<SwerveModule, AbstractSwerveModule> moduleMap = drive.getModuleMap();
        printModules(moduleMap, SwerveModule.FRONT_LEFT, SwerveModule.FRONT_RIGHT, SwerveModule.REAR_LEFT,
                SwerveModule.REAR_RIGHT);

        csv.print("\n");
        csv.flush();
    }
}