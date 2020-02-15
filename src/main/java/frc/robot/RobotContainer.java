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
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.InstantCommand;
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

  private Controller controller = new XboxController(Constants.PRIMARY_JOYSTICK_PORT);
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

    updateManager.startLoop(5.0e-3);

    // Configure the button bindings
    controller.getLeftXAxis().setInverted(false);
    controller.getRightXAxis().setInverted(true);
    controller.getLeftYAxis().setInverted(true);
    controller.getRightXAxis().setScale(0.45);

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
    getResetGyroButton()
        .whenPressed(new InstantCommand(() -> DrivetrainSubsystem.getInstance().resetGyroAngle(Rotation2.ZERO)));

    this.controller.getAButton().whileHeld(new RotateAndAimCommandGroup());

    // this.controller.getAButton().whileHeld(new GetInRangeAndAimCommand());

    // this.controller.getBButton().whileHeld(new RotateToAngleCommand(0));
    this.controller.getBButton().whileHeld(new RotateToTargetCommand(Direction.RIGHT));

    this.controller.getStartButton().toggleWhenPressed(new SequentialCommandGroup(new LaunchFromDistanceCommand()));

    // this.controller.getYButton().toggleWhenActive(new SpinToColorCommand());

    // this.controller.getXButton().toggleWhenPressed(new
    // SpinForRevolutionsCommand(1));
  }

  /**
   * Use this to pass the autonomous command to the main {@link Robot} class.
   *
   * @return the command to run in autonomous
   */
  public Command getAutonomousCommand() {
    // An ExampleCommand will run in autonomous
    return null;
  }

  public Controller getPrimaryController() {
    return controller;
  }

  public SpinnerColor getSpinnerColor() {
    // System.out.println("Called spinner color");
    // System.out.println(this.spinnerColor);
    return this.spinnerColor;
  }

  public void setSpinnerColor(SpinnerColor color) {
    this.spinnerColor = color;
  }

  public Axis getDriveForwardAxis() {
    return controller.getLeftYAxis();
  }

  public Axis getDriveStrafeAxis() {
    return controller.getLeftXAxis();
  }

  public Axis getDriveRotationAxis() {
    return controller.getRightXAxis();
  }

  public Button getResetGyroButton() {
    return controller.getBackButton();
  }

}
