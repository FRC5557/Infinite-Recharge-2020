/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.subsystems;

import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;

import com.revrobotics.CANPIDController;
import com.revrobotics.CANSparkMax;
import com.revrobotics.ControlType;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

/**
 * Add your docs here.
 */
public class IntakeSubsystem extends SubsystemBase {

  static IntakeSubsystem instance;
  private CANSparkMax intakeMotor;
  private CANPIDController intakePIDController;
  double kP, kI, kD, kIz, kFF, kMaxOutput, kMinOutput;

  public static IntakeSubsystem getInstance() {
    if (instance == null) {
      instance = new IntakeSubsystem();
    }
    return instance;
  }

  public IntakeSubsystem() {
    intakeMotor = new CANSparkMax(Constants.INTAKE_MOTOR, MotorType.kBrushless);
    intakePIDController = intakeMotor.getPIDController();

    kP = 5e-5;
    kI = 1e-6;
    kD = 0;
    kIz = 0;
    kFF = 0;
    kMaxOutput = 0.5;
    kMinOutput = -0.5;

    intakePIDController.setP(kP);
    intakePIDController.setI(kI);
    intakePIDController.setD(kD);
    intakePIDController.setIZone(kIz);
    intakePIDController.setFF(kFF);
    intakePIDController.setOutputRange(kMinOutput, kMaxOutput);
  }

  public void intakeIn() {
    // intakePIDController.setReference(0.5, ControlType.kVelocity);
    intakeMotor.set(-0.6);

  }

  public void intakeSlow() {
    intakeMotor.set(-0.4);
  }

  public void intakeOut() {
    // intakePIDController.setReference(-0.5, ControlType.kVelocity);
    intakeMotor.set(0.4);
  }

  public void stop() {
    intakeMotor.set(0);
  }

}
