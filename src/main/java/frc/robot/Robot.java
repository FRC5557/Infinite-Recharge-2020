/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2019 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;

import org.frcteam2910.common.math.Rotation2;
import org.frcteam2910.common.math.Vector2;
import org.frcteam2910.common.robot.UpdateManager;
import org.frcteam2910.common.control.MaxAccelerationConstraint;
import org.frcteam2910.common.control.MaxVelocityConstraint;
import org.frcteam2910.common.control.Path;
import org.frcteam2910.common.control.SplinePathBuilder;
import org.frcteam2910.common.control.Trajectory;
import org.frcteam2910.common.control.TrajectoryConstraint;
// import org.frcteam2910.common.robot.subsystems.SubsystemManager;
// import org.frc
// import org
import org.frcteam2910.common.io.PathReader;

import edu.wpi.first.cameraserver.CameraServer;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Filesystem;
import edu.wpi.first.wpilibj.Notifier;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInWidgets;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.CommandScheduler;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.ParallelRaceGroup;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import frc.robot.commands.DriveCommand;
import frc.robot.commands.FeedInCommand;
import frc.robot.commands.FeedOutForTimeCommand;
import frc.robot.commands.IntakeInCommand;
import frc.robot.commands.LaunchUpperForTimeCommand;
import frc.robot.commands.RotateAndAimCommandGroup;
import frc.robot.commands.RotateToAngleCommand;
import frc.robot.commands.RotateToTargetCommand;
import frc.robot.commands.autonomous.*;
import frc.robot.subsystems.Limelight;
import frc.robot.subsystems.SpinnerSubsystem;
import frc.robot.subsystems.swerve.DrivetrainSubsystem;
import frc.robot.subsystems.swerve.SwerveDrivetrain;
import io.github.oblarg.oblog.Logger;
// import net.bancino.robotics.swerveio.commands.PathweaverSwerveDrive;
import net.bancino.robotics.swerveio.command.PathweaverSwerveDrive;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the TimedRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the build.gradle file in the
 * project.
 */
public class Robot extends TimedRobot {
  private Command m_autonomousCommand;

  private static RobotContainer robotContainer;

  private static final double UPDATE_DT = 5.0e-3;

  private double lastTimestamp = 0.0;
  Trajectory autonTrajectory;

  // private final UpdateManager updateManager = new
  // UpdateManager(DrivetrainSubsystem.getInstance());

  SendableChooser<Command> autonomousModes;
  Command autonomousCommand;
  NetworkTableEntry timeBack;

  /**
   * This function is run when the robot is first started up and should be used
   * for any initialization code.
   */
  @Override
  public void robotInit() {
    // Instantiate our RobotContainer. This will perform all our button bindings,
    // and put our
    // autonomous chooser on the dashboard.
    robotContainer = new RobotContainer();

    this.setupAutonomousOptions();

    // SpinnerSubsystem.getInstance();

    // DrivetrainSubsystem.getInstance().drive(Vector2.ZERO, 0, true);
    Limelight.getInstance().disableLEDs();
    Limelight.getInstance().enableDriverMode();
    // CameraServer.getInstance().startAutomaticCapture();
  }

  /**
   * This function is called every robot packet, no matter the mode. Use this for
   * items like diagnostics that you want ran during disabled, autonomous,
   * teleoperated and test.
   *
   * <p>
   * This runs after the mode specific periodic functions, but before LiveWindow
   * and SmartDashboard integrated updating.
   */
  @Override
  public void robotPeriodic() {
    // Runs the Scheduler. This is responsible for polling buttons, adding
    // newly-scheduled
    // commands, running already-scheduled commands, removing finished or
    // interrupted commands,
    // and running subsystem periodic() methods. This must be called from the
    // robot's periodic
    // block in order for anything in the Command-based framework to work.
    CommandScheduler.getInstance().run();
    // Logger.updateEntries();

  }

  /**
   * This function is called once each time the robot enters Disabled mode.
   */
  @Override
  public void disabledInit() {
  }

  @Override
  public void disabledPeriodic() {
  }

  /**
   * This autonomous runs the autonomous command selected by your
   * {@link RobotContainer} class.
   */
  @Override
  public void autonomousInit() {
    DrivetrainSubsystem.getInstance().resetGyroAngle(Rotation2.ZERO);

    autonomousCommand = autonomousModes.getSelected();
    autonomousCommand.schedule();

    

    // try {
    //   new PathweaverSwerveDrive(SwerveDrivetrain.getInstance(), "StraightLine.wpilib.json").schedule();
    // } catch (IOException e) {
    //   // TODO Auto-generated catch block
    //   e.printStackTrace();
    // }

  }

  /**
   * This function is called periodically during autonomous.
   */
  @Override
  public void autonomousPeriodic() {
    // System.out.println(DrivetrainSubsystem.getInstance().getSwerveModules()[0].getDriveMotor().getEncoder().getCountsPerRevolution());
    // System.out.println(DrivetrainSubsystem.getInstance().getSwerveModules()[2].getDriveMotor().getEncoder().getPosition());
  }

  @Override
  public void teleopInit() {

    if (autonomousCommand != null) {
      autonomousCommand.cancel();
    }
    
    Limelight.getInstance().enableDriverMode();
    Limelight.getInstance().disableLEDs();

    CommandScheduler.getInstance().setDefaultCommand(DrivetrainSubsystem.getInstance(), new DriveCommand());

  }

  /**
   * This function is called periodically during operator control.
   */
  @Override
  public void teleopPeriodic() {
    handleGameData();
  }

  private void handleGameData() {
    String gameData;
    gameData = DriverStation.getInstance().getGameSpecificMessage();
    if (gameData.length() > 0) {
      switch (gameData.charAt(0)) {
      case 'B':
        // Blue case code
        robotContainer.setSpinnerColor(SpinnerColor.BLUE);
        break;
      case 'G':
        // Green case code
        robotContainer.setSpinnerColor(SpinnerColor.GREEN);
        break;
      case 'R':
        // Red case code
        robotContainer.setSpinnerColor(SpinnerColor.RED);
        break;
      case 'Y':
        // Yellow case code
        robotContainer.setSpinnerColor(SpinnerColor.YELLOW);
        break;
      default:
        // This is corrupt data
        robotContainer.setSpinnerColor(SpinnerColor.UNKNOWN);
        break;
      }
    } else {
      // Code for no data received yet
    }
  }

  @Override
  public void testInit() {
    // Cancels all running commands at the start of test mode.
    CommandScheduler.getInstance().cancelAll();
  }

  /**
   * This function is called periodically during test mode.
   */
  @Override
  public void testPeriodic() {
  }

  public static RobotContainer getRobotContainer() {
    return robotContainer;
  }

  private void setupAutonomousOptions() {
    ShuffleboardTab tab = Shuffleboard.getTab("competition");
    autonomousModes = new SendableChooser<Command>();
    autonomousModes.setDefaultOption("5 Ball Auto",
        new SequentialCommandGroup(
            new ParallelRaceGroup(new MoveDirectionForTimeCommand(1.2, Direction.FORWARD), new IntakeInCommand()),
            new RotateToTargetCommand(Direction.RIGHT), new MoveDirectionForTimeCommand(0.3, Direction.BACKWARD),
            new RotateToTargetCommand(Direction.RIGHT), new LaunchUpperForTimeCommand(5)));

    autonomousModes.addOption("Start From Right",
        new SequentialCommandGroup(new MoveDirectionForTimeCommand(.5, Direction.BACKWARD),
            new RotateToTargetCommand(Direction.RIGHT), new FeedOutForTimeCommand(0.5), new ParallelRaceGroup(new FeedOutForTimeCommand(1), new LaunchUpperForTimeCommand(1)), new ParallelRaceGroup(new FeedInCommand(), new LaunchUpperForTimeCommand(5))));
    autonomousModes.addOption("Start From Left",
        new SequentialCommandGroup(new MoveDirectionForTimeCommand(.5, Direction.BACKWARD),
            new RotateToTargetCommand(Direction.LEFT), new FeedOutForTimeCommand(0.5), new ParallelRaceGroup(new FeedOutForTimeCommand(1), new LaunchUpperForTimeCommand(1)), new ParallelRaceGroup(new FeedInCommand(), new LaunchUpperForTimeCommand(5))));
    autonomousModes.addOption("Start From Middle",
        new SequentialCommandGroup(new MoveDirectionForTimeCommand(.5, Direction.FORWARD),
            new RotateToTargetCommand(Direction.LEFT), new LaunchUpperForTimeCommand(5)));
    autonomousModes.addOption("Start From Right, Move Forward",
    new SequentialCommandGroup(new MoveDirectionForTimeCommand(.5, Direction.FORWARD),
        new RotateToTargetCommand(Direction.RIGHT), new FeedOutForTimeCommand(0.5), new ParallelRaceGroup(new FeedOutForTimeCommand(1), new LaunchUpperForTimeCommand(1)), new ParallelRaceGroup(new FeedInCommand(), new LaunchUpperForTimeCommand(5))));
    tab.add("Autonomous Mode", autonomousModes).withWidget(BuiltInWidgets.kComboBoxChooser);
    timeBack = tab.add("Time to move back", 5).withWidget(BuiltInWidgets.kNumberSlider)
        .withProperties(Map.of("min", .1, "max", 2)).getEntry();
  }
}
