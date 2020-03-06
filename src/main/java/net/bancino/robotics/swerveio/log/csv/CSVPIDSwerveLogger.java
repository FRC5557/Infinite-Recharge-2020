package net.bancino.robotics.swerveio.log.csv;

import java.io.PrintStream;
import java.io.File;
import java.io.OutputStream;
import java.io.FileOutputStream;
import java.io.FileNotFoundException;

import net.bancino.robotics.swerveio.SwerveDrive;
import net.bancino.robotics.swerveio.SwerveIOUtils;
import net.bancino.robotics.swerveio.SwerveVector;
import net.bancino.robotics.swerveio.SwerveModule;
import net.bancino.robotics.swerveio.module.AbstractSwerveModule;

/**
 * A Swerve logging class that logs pid data from the specified module in the
 * CSV format to the specified source. This can be helpful for graphing certain
 * values, or quickly looking for changes.
 * 
 * @author Jordan Bancino
 * @version 1.3.0
 * @since 1.3.0
 */
public class CSVPIDSwerveLogger extends AbstractCSVSwerveLogger {

    private SwerveModule module;

    /**
     * Create a new csv swerve logger.
     * 
     * @param csv The print stream to write output data to.
     * @param module The swerve module to log PID values for.
     */
    public CSVPIDSwerveLogger(PrintStream csv, SwerveModule module) {
        super(csv, "time,fwd,str,rcw,setpoint,feedback,feedbackMod,difference,output");
        this.module = module;
    }

    /**
     * Create a new csv swerve logger.
     * 
     * @param csv The file to write output data to.
     * @param module The swerve module to log PID values for.
     * @throws FileNotFoundException if the specified file does not exist.
     */
    public CSVPIDSwerveLogger(File csv, SwerveModule module) throws FileNotFoundException {
        this(new FileOutputStream(csv), module);
    }

    /**
     * Create a new csv swerve logger.
     * 
     * @param csv The output stream to write output data to.
      *@param module The swerve module to log PID values for.
     */
    public CSVPIDSwerveLogger(OutputStream csv, SwerveModule module) {
        this(new PrintStream(csv), module);
    }

    @Override
    public void logState(SwerveDrive drive) {
        csv.print(System.currentTimeMillis() + ",");

        SwerveVector latestInput = drive.getLastSwerveVector();
        double fwd = latestInput.getFwd();
        double str = latestInput.getStr();
        double rcw = latestInput.getRcw();
        csv.print(fwd + "," + str + "," + rcw + ",");

        AbstractSwerveModule swerveModule = drive.getModuleMap().get(module);

        double setpoint = drive.getCalculator().getWheelAngle(module, fwd, str, rcw, drive.getLastGyroAngle());
        double feedback = swerveModule.getPivotMotorEncoder();
        double feedbackMod = SwerveIOUtils.correctPivotFeedback(feedback, setpoint, drive.getCountsPerPivotRevolution());
        double difference = setpoint - feedback;
        double previousOutput = swerveModule.getPivotPIDController().getPreviousOutput();

        csv.print(setpoint + "," + feedback + "," + feedbackMod + "," + difference + "," + previousOutput);

        csv.print("\n");
        csv.flush();
    }
}