package net.bancino.robotics.swerveio.log.csv;

import java.io.PrintStream;
import java.io.File;
import java.io.OutputStream;
import java.io.FileOutputStream;
import java.io.FileNotFoundException;

import java.util.Map;

import net.bancino.robotics.swerveio.SwerveModule;
import net.bancino.robotics.swerveio.log.SwerveLogger;
import net.bancino.robotics.swerveio.module.AbstractSwerveModule;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * A Swerve logging class that logs data in the CSV format to the specified
 * source. This can be helpful for graphing certain values, or quickly looking
 * for changes.
 * 
 * @author Jordan Bancino
 * @version 1.3.0
 * @since 1.2.3
 */
public abstract class AbstractCSVSwerveLogger implements SwerveLogger {

    protected PrintStream csv;

    /**
     * Create a new csv swerve logger.
     * 
     * @param csv The print stream to write output data to.
     * @param header The header string (comma separated) to print to the top of the csv.
     */
    public AbstractCSVSwerveLogger(PrintStream csv, String header) {
        if (csv != null) {
            this.csv = csv;
            csv.println(header);
        } else {
            throw new IllegalArgumentException("No Print Stream Specified.");
        }
    }

    /**
     * Create a new csv swerve logger.
     * 
     * @param csv The file to write output data to.
     * @param header The header string (comma separated) to print to the top of the csv. 
     * @throws FileNotFoundException if the specified file does not exist.
     */
    public AbstractCSVSwerveLogger(File csv, String header) throws FileNotFoundException {
        this(new FileOutputStream(csv), header);
        SmartDashboard.putString(this.toString(), csv.getAbsolutePath());
    }

    /**
     * Create a new csv swerve logger.
     * 
     * @param csv The output stream to write output data to.
     * @param header The header string (comma separated) to print to the top of the csv.
     */
    public AbstractCSVSwerveLogger(OutputStream csv, String header) {
        this(new PrintStream(csv), header);
    }

    /**
     * This function is used to print the modules in a special order. THis will
     * print them in the order they're specified, instead of just iterating over the
     * module map, because the module map does not ensure any order.
     * 
     * @param moduleMap The module map to pull modules from.
     * @param modules   The swerve modules to read from the map and log as a csv
     *                  row.
     */
    protected void printModules(Map<SwerveModule, AbstractSwerveModule> moduleMap,
            SwerveModule... modules) {
        for (int i = 0; i < modules.length; i++) {
            AbstractSwerveModule m = moduleMap.get(modules[i]);
            csv.print(m.getPivotMotorEncoder() + ",");
            csv.print(m.getPivotMotorSpeed() + ",");
            csv.print(m.getDriveMotorEncoder() + ",");
            csv.print(m.getDriveMotorSpeed());
            if (i < modules.length - 1) {
                csv.print(",");
            }
        }
    }

    /**
     * Print a comma-separated row to the csv stream. A new line is written at the end.
     * 
     * @param values The objects to print, comma separated.
     */
    protected void printRow(Object... values) {
        for (int i = 0; i < values.length; i++) {
            csv.print(values[i]);
            if (i < values.length - 1) {
                csv.print(",");
            } else {
                csv.print("\n");
            }
        }
    }

    @Override
    public boolean requiresEnabledDriverStation() {
        return true;
    }
}