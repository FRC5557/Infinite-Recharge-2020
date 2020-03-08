/*----------------------------------------------------------------------------*/
/* Copyright (c) 2019 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.subsystems;

import com.revrobotics.CANPIDController;
import com.revrobotics.CANSparkMax;
import com.revrobotics.ControlType;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;

public class LauncherSubsystem extends SubsystemBase {

  public static LauncherSubsystem instance = null;

  public static LauncherSubsystem getInstance() {
    if (instance == null) {
      instance = new LauncherSubsystem();
    }
    return instance;
  }

  private CANSparkMax launcherMotor, secondLauncherMotor;

  // private CANSparkMax feederMotor;

  /**
   * Creates a new LauncherSubsystem.
   */
  public LauncherSubsystem() {
    this.launcherMotor = new CANSparkMax(Constants.LAUNCHER_MOTOR, MotorType.kBrushless);
    this.secondLauncherMotor = new CANSparkMax(Constants.SECOND_LAUNCHER_MOTOR, MotorType.kBrushless);
    // this.feederMotor = new CANSparkMax(Constants.FEEDER_MOTOR,
    // MotorType.kBrushless);

  }

  public void launchUpper() {
    // launcherPIDController.setReference(0.9, ControlType.kVelocity);
    // launcherMotor.set(-0.80);
    // launcherMotor.set(-.78);
    // launcherMotor.set(-0.82);
    launcherMotor.set(-0.9);
    secondLauncherMotor.set(0.9);
  }

  public void launchLower() {
    // launcherPIDController.setReference(0.5, ControlType.kVelocity);
    launcherMotor.set(-0.2);
    secondLauncherMotor.set(0.2);
  }

  public void launchForSpeed(double speed) {
    launcherMotor.set(speed);
    secondLauncherMotor.set(-speed);
  }

  public CANSparkMax getLauncherMotor() {
    return this.launcherMotor;
  }

  public void stop() {
    launcherMotor.set(0);
    secondLauncherMotor.set(0);
  }

  /*
   * public void feedIn() { feederMotor.set(-.20); }
   * 
   * public void feedOut() { feederMotor.set(.20); }
   * 
   * 
   * public void stopFeed() { feederMotor.set(0); }
   */
  public void launch(double speed) {
    launcherMotor.set(speed);
  }

  @Override
  public void periodic() {
    // This method will be called once per scheduler run
  }
}
