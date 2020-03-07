/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018-2019 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import frc.robot.input.*;

import org.frcteam2910.common.math.Rotation2;
import org.frcteam2910.common.robot.UpdateManager;

import edu.wpi.first.wpilibj.GenericHID;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.geometry.Rotation2d;
import frc.robot.commands.*;
import frc.robot.subsystems.swerve.DrivetrainSubsystem;
import frc.robot.subsystems.swerve.SwerveDrivetrain;
import io.github.oblarg.oblog.Logger;
import net.bancino.robotics.swerveio.command.SwerveDriveTeleop;
import net.bancino.robotics.swerveio.log.DashboardSwerveLogger;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.ParallelRaceGroup;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import edu.wpi.first.wpilibj2.command.button.Button;
import edu.wpi.first.wpilibj2.command.button.JoystickButton;

/**
 * This class is where the bulk of the robot should be declared. Since
 * Command-based is a "declarative" paradigm, very little robot logic should
 * actually be handled in the {@link Robot} periodic methods (other than the
 * scheduler calls). Instead, the structure of the robot (including subsystems,
 * commands, and button mappings) should be declared here.
 */
public class RobotContainer {
  // The robot's subsystems and commands are defined here...

  private Controller swerveController = new XboxController(Constants.PRIMARY_JOYSTICK_PORT);
  private Controller manipulatorController = new XboxController(Constants.SECONDARY_JOYSTICK_PORT);

  private SpinnerColor spinnerColor;

  DrivetrainSubsystem drivetrain = DrivetrainSubsystem.getInstance();

  private final UpdateManager updateManager = new UpdateManager(drivetrain);

  public static RobotContainer instance = null;

  public static RobotContainer getInstance() {
    if (instance == null) {
      instance = new RobotContainer();
    }
    return instance;
  }

  /**
   * The container for the robot. Contains subsystems, OI devices, and commands.
   */
  public RobotContainer() {
    if (instance == null) {
      instance = this;
    }

    // Logger.configureLoggingAndConfig(this, false);

    // SwerveDrivetrain.getInstance().startLogging(new DashboardSwerveLogger());

    // this starts the update thread for swerve drive
    updateManager.startLoop(5.0e-3);

    // SwerveDrivetrain.getInstance().setDefaultCommand(new
    // SwerveDriveTeleop(SwerveDrivetrain.getInstance(),
    // new edu.wpi.first.wpilibj.XboxController(Constants.PRIMARY_JOYSTICK_PORT)));

    // edu.wpi.first.wpilibj.XboxController controller = new
    // edu.wpi.first.wpilibj.XboxController(Constants.PRIMARY_JOYSTICK_PORT);
    // new JoystickButton(controller, 7).whenPressed(new InstantCommand(() ->
    // {System.out.println("Reset angle");
    // SwerveDrivetrain.getInstance().setIdleAngle(0, false);}));
    // SwerveDriveCommand swerveDriveTeleop = new
    // SwerveDriveCommand(SwerveDrivetrain.getInstance(), controller,
    // edu.wpi.first.wpilibj.XboxController.Axis.kLeftX,
    // edu.wpi.first.wpilibj.XboxController.Axis.kLeftY,
    // edu.wpi.first.wpilibj.XboxController.Axis.kRightX);
    // // swerveDriveTeleop.setThrottle(0.4);
    // SwerveDrivetrain.getInstance().setDefaultCommand(swerveDriveTeleop);

    // Configure the controls for swerve drive
    swerveController.getLeftXAxis().setInverted(false);
    swerveController.getRightXAxis().setInverted(true);
    swerveController.getLeftYAxis().setInverted(true);
    swerveController.getRightXAxis().setScale(0.45);

    // the following sets up our color system code
    spinnerColor = SpinnerColor.UNKNOWN;
    Constants.colorMapping.put(SpinnerColor.BLUE, SpinnerColor.RED);
    Constants.colorMapping.put(SpinnerColor.GREEN, SpinnerColor.YELLOW);
    Constants.colorMapping.put(SpinnerColor.YELLOW, SpinnerColor.GREEN);
    Constants.colorMapping.put(SpinnerColor.RED, SpinnerColor.BLUE);
    Constants.colorMapping.put(SpinnerColor.UNKNOWN, SpinnerColor.UNKNOWN);

    configureButtonBindings();

  }

  /**
   * Use this method to define your button->command mappings. Buttons can be
   * created by instantiating a {@link GenericHID} or one of its subclasses
   * ({@link edu.wpi.first.wpilibj.Joystick} or {@link XboxController}), and then
   * passing it to a {@link edu.wpi.first.wpilibj2.command.button.JoystickButton}.
   */
  private void configureButtonBindings() {

    /**
     * This year we are opting for 2 drivers, (tentatively) one for drivetrain
     * movement such as positioning and rotation and the other for everthing else
     * (such as intake, launching, etc)
     */

    /**
     * Swerve driver
     */
    getResetGyroButton()
        .whenPressed(new InstantCommand(() -> DrivetrainSubsystem.getInstance().resetGyroAngle(Rotation2.ZERO)));
    // this.swerveController.getAButton().whileHeld(new RotateAndAimCommandGroup());

    /**
     * Maniuplator(s) driver: They are responsible for launcher, intake, spin system
     * and climb
     */
    manipulatorController.getAButton().whileHeld(new LaunchFromDistanceCommand());
    // manipulatorController.getXButton().toggleWhenActive(new
    // SequentialCommandGroup(new ParallelRaceGroup(new FeedOutForTimeCommand(1),
    // new LaunchUpperCommand()), new LaunchUpperCommand()));
    manipulatorController.getBButton().toggleWhenActive(new LaunchLowerCommand());
    manipulatorController.getXButton().whileHeld(new HoldLauncherCommand());
    manipulatorController.getYButton().whileHeld(new FeedLauncherCommand());
    manipulatorController.getLeftTriggerAxis().whileHeld(new IntakeOutCommand());
    manipulatorController.getRightTriggerAxis().whileHeld(new IntakeInCommand());
    manipulatorController.getRightBumperButton().whileHeld(new ClimbCommand(Direction.UP));
    manipulatorController.getLeftBumperButton().whileHeld(new ClimbCommand(Direction.DOWN));
    manipulatorController.getLeftJoystickButton().whileHeld(new ClimbCommand(Direction.LEFT));
    manipulatorController.getRightJoystickButton().whileHeld(new ClimbCommand(Direction.RIGHT));
    manipulatorController.getDPadButton(DPadButton.Direction.UP).toggleWhenActive(new LaunchFromDistanceCommand());
    // swerveController.getXButton().toggleWhenPressed(new SpinToColorCommand());
    // manipulatorController.getYButton().toggleWhenPressed(new
    // SpinForRevolutionsCommand(4));
  }

  public Controller getSwerveController() {
    return swerveController;
  }

  public SpinnerColor getSpinnerColor() {
    return this.spinnerColor;
  }

  public void setSpinnerColor(SpinnerColor color) {
    this.spinnerColor = color;
  }

  public Axis getDriveForwardAxis() {
    return swerveController.getLeftYAxis();
  }

  public Axis getDriveStrafeAxis() {
    return swerveController.getLeftXAxis();
  }

  public Axis getDriveRotationAxis() {
    return swerveController.getRightXAxis();
  }

  public Button getResetGyroButton() {
    return swerveController.getBackButton();
  }

}
