/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018-2019 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.subsystems.swerve;

import java.io.File;
import java.util.Map;
import java.util.HashMap;
import java.io.IOException;
import java.util.List;

import edu.wpi.first.wpilibj.SPI;
// import frc.robot.commands.DriveWithJoystick;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;
import net.bancino.robotics.swerveio.SwerveDrive;
import net.bancino.robotics.swerveio.SwerveMeta;
//import net.bancino.robotics.swerveio.encoder.SparkMaxEncoder;
import net.bancino.robotics.swerveio.encoder.AnalogEncoder;
import net.bancino.robotics.swerveio.encoder.AbstractEncoder;
import net.bancino.robotics.swerveio.module.AbstractSwerveModule;
import net.bancino.robotics.swerveio.pid.AbstractPIDController;
import net.bancino.robotics.swerveio.module.MK2SwerveModule;
import net.bancino.robotics.swerveio.SwerveModule;
import net.bancino.robotics.swerveio.exception.SwerveException;
import net.bancino.robotics.swerveio.exception.SwerveRuntimeException;
import net.bancino.robotics.swerveio.log.DashboardSwerveLogger;
import net.bancino.robotics.swerveio.log.csv.CSVPIDSwerveLogger;
import net.bancino.robotics.swerveio.si.Length;
import net.bancino.robotics.swerveio.si.Unit;
import net.bancino.robotics.swerveio.si.ChassisDimension;
import net.bancino.robotics.swerveio.si.SquareChassis;
import net.bancino.robotics.swerveio.gyro.AbstractGyro;
import net.bancino.robotics.swerveio.gyro.NavXGyro;
import net.bancino.robotics.swerveio.SwerveFlag;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * The drivetrain subsystem drives the robot! (wow!).
 *
 * This subsystem consists of the following components: - Swerve module (4x
 * drive + pivot motor)
 *
 * This subsystem should provide the following functions: - Run the drivetrain
 * with joystick - Run the drivetrain autonomously
 *
 * @author Jordan Bancino
 */
public class SwerveDrivetrain extends SwerveDrive {

    public static SwerveDrivetrain instance = null;

    public static SwerveDrivetrain getInstance() {
        if (instance == null) {
            try {
                instance = new SwerveDrivetrain(new NavXGyro(SPI.Port.kMXP));
            } catch (SwerveException e) {
                throw new SwerveRuntimeException(e);
            }
        }
        return instance;
    }

    public SwerveDrivetrain(AbstractGyro gyro) throws SwerveException {
        super(new SwerveMeta() {
            // private final AbstractEncoder frontRightEncoder = new
            // SparkMaxEncoder(SparkMaxEncoder.EncoderMode.ANALOG,
            // FRONT_RIGHT_ENCODER_OFFSET);
            // private final AbstractEncoder frontLeftEncoder = new
            // SparkMaxEncoder(SparkMaxEncoder.EncoderMode.ANALOG,
            // FRONT_LEFT_ENCODER_OFFSET);
            // private final AbstractEncoder rearLeftEncoder = new
            // SparkMaxEncoder(SparkMaxEncoder.EncoderMode.ANALOG,
            // REAR_LEFT_ENCODER_OFFSET);
            // private final AbstractEncoder rearRightEncoder = new
            // SparkMaxEncoder(SparkMaxEncoder.EncoderMode.ANALOG,
            // REAR_RIGHT_ENCODER_OFFSET);

            /* The encoder offsets tell us where zero is for each motor. */
            private final double FRONT_RIGHT_ENCODER_OFFSET = 75.905 + 180;
            private final double FRONT_LEFT_ENCODER_OFFSET = 163.376835;
            private final double REAR_LEFT_ENCODER_OFFSET = 340.331861;
            private final double REAR_RIGHT_ENCODER_OFFSET = 317.177098;

            private final AbstractEncoder frontRightEncoder = new AnalogEncoder(
                    Constants.DRIVETRAIN_FRONT_RIGHT_ANGLE_ENCODER, FRONT_RIGHT_ENCODER_OFFSET);
            private final AbstractEncoder frontLeftEncoder = new AnalogEncoder(
                    Constants.DRIVETRAIN_FRONT_LEFT_ANGLE_ENCODER, FRONT_LEFT_ENCODER_OFFSET);
            private final AbstractEncoder rearLeftEncoder = new AnalogEncoder(
                    Constants.DRIVETRAIN_BACK_LEFT_ANGLE_ENCODER, REAR_LEFT_ENCODER_OFFSET);
            private final AbstractEncoder rearRightEncoder = new AnalogEncoder(
                    Constants.DRIVETRAIN_BACK_RIGHT_ANGLE_ENCODER, REAR_RIGHT_ENCODER_OFFSET);

            @Override
            public String name() {
                return "5557 SwerveIO Drivetrain";
            }

            @Override
            public ChassisDimension chassisDimensions() {
                return new SquareChassis(new Length(30, Unit.INCHES));
            }

            @Override
            public double countsPerPivotRevolution() {
                return 360;
            }

            @Override
            public Map<SwerveModule, AbstractSwerveModule> moduleMap() {
                var modules = new HashMap<SwerveModule, AbstractSwerveModule>();
                modules.put(SwerveModule.FRONT_RIGHT, new MK2SwerveModule(Constants.DRIVETRAIN_FRONT_RIGHT_DRIVE_MOTOR,
                        Constants.DRIVETRAIN_FRONT_RIGHT_ANGLE_MOTOR, frontRightEncoder));
                modules.put(SwerveModule.FRONT_LEFT, new MK2SwerveModule(Constants.DRIVETRAIN_FRONT_LEFT_DRIVE_MOTOR,
                        Constants.DRIVETRAIN_FRONT_LEFT_ANGLE_MOTOR, frontLeftEncoder));
                modules.put(SwerveModule.REAR_LEFT, new MK2SwerveModule(Constants.DRIVETRAIN_BACK_LEFT_DRIVE_MOTOR,
                        Constants.DRIVETRAIN_BACK_LEFT_ANGLE_MOTOR, rearLeftEncoder));
                modules.put(SwerveModule.REAR_RIGHT, new MK2SwerveModule(Constants.DRIVETRAIN_BACK_RIGHT_DRIVE_MOTOR,
                        Constants.DRIVETRAIN_BACK_RIGHT_ANGLE_MOTOR, rearRightEncoder));
                return modules; /* Return the module map for the constructor's use. */
            }

            @Override
            public AbstractGyro gyro() {
                return gyro;
            }

            @Override
            public void modifyModule(AbstractSwerveModule module) {
                AbstractPIDController modulePid = module.getPivotPIDController();
                modulePid.setOutputRampRate(Constants.SWERVE_MODULE_RAMP_RATE);
                modulePid.setP(Constants.SWERVE_MODULE_P);
                modulePid.setI(Constants.SWERVE_MODULE_I);
                modulePid.setD(Constants.SWERVE_MODULE_D);
            }

            @Override
            public List<SwerveFlag> applyFlags() {
                return List.of(SwerveFlag.ENABLE_PIVOT_OPTIMIZE);
            }

            @Override
            public void initialize(SwerveDrive swerve) {
                swerve.zeroDriveEncoders();
                swerve.setFieldCentric(true);

                // swerve.setIdleAngle(0, false);

                // swerve.startLogging(new DashboardSwerveLogger());

                // File logFile = new File("/home/lvuser/pid.csv");
                // try {
                // logFile.createNewFile();
                // swerve.startLogging(100, new CSVPIDSwerveLogger(logFile,
                // SwerveModule.FRONT_LEFT));
                // } catch (IOException e) {
                // System.out.println("Error Creating Robot CSV: " + e);
                // e.printStackTrace();
                // }
            }

        });
    }
}