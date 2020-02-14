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

  private CANSparkMax launcherMotor;
  private CANPIDController launcherPIDController;
  public double kP, kI, kD, kIz, kFF, kMaxOutput, kMinOutput, maxRPM;

  // private CANSparkMax feederMotor;

  /**
   * Creates a new LauncherSubsystem.
   */
  public LauncherSubsystem() {
    this.launcherMotor = new CANSparkMax(Constants.LAUNCHER_MOTOR, MotorType.kBrushless);
    this.launcherPIDController = launcherMotor.getPIDController();

    // this.feederMotor = new CANSparkMax(Constants.FEEDER_MOTOR, MotorType.kBrushless);

    kP = 5e-5;
    kI = 1e-6;
    kD = 0;
    kIz = 0;
    kFF = 0;
    kMaxOutput = 1;
    kMinOutput = -1;
    maxRPM = 5700;

    launcherPIDController.setP(kP);
    launcherPIDController.setI(kI);
    launcherPIDController.setD(kD);
    launcherPIDController.setIZone(kIz);
    launcherPIDController.setFF(kFF);
    launcherPIDController.setOutputRange(kMinOutput, kMaxOutput);
  }

  public void launchUpper() {
    launcherPIDController.setReference(0.9, ControlType.kVelocity);
  }

  public void launchLower() {
    launcherPIDController.setReference(0.5, ControlType.kVelocity);
  }

  public CANSparkMax getLauncherMotor() {
    return this.launcherMotor;
  }

  public void stop() {
    launcherMotor.set(0);
  }

  public void feedIn() {
    // feederMotor.set(.5);
  }

  public void stopFeed() {
    // feederMotor.set(0);
  }

  public void launch(double speed) {
    launcherMotor.set(speed);
  }

  @Override
  public void periodic() {
    // This method will be called once per scheduler run
  }
}
