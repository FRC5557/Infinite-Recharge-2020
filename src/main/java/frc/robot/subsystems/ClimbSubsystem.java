/*----------------------------------------------------------------------------*/
/* Copyright (c) 2019 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.subsystems;

import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;

public class ClimbSubsystem extends SubsystemBase {

  CANSparkMax leftClimbMotor, rightClimbMotor;

  public static ClimbSubsystem instance = null;

  public static ClimbSubsystem getInstance() {
    if (instance == null) {
      instance = new ClimbSubsystem();
    }
    return instance;
  }

  /**
   * Creates a new ClimbSubsystem.
   */
  public ClimbSubsystem() {
    leftClimbMotor = new CANSparkMax(Constants.CLIMB_LEFT_MOTOR, MotorType.kBrushless);
    rightClimbMotor = new CANSparkMax(Constants.CLIMB_RIGHT_MOTOR, MotorType.kBrushless);
  }

  public void climbLeftUp() {
    leftClimbMotor.set(0.5);
  }

  public void climbLeftDown() {
    leftClimbMotor.set(-0.5);
  }

  public void climbRightUp() {
    rightClimbMotor.set(-0.5);
  }

  public void climbRightDown() {
    rightClimbMotor.set(0.5);
  }

  public void climbUp() {
    climbLeftUp();
    climbRightUp();
  }

  public void climbDown() {
    climbLeftDown();
    climbRightDown();
  }

  public void stopClimb() {
    leftClimbMotor.set(0);
    rightClimbMotor.set(0);
  }

  /**
   * The aim of this command is to use the gyro's pitch and roll values to
   * determine whether the robot is offset somehow and use that to drive the climb
   * motors
   */
  public void gyroAdjustedClimb() {
    // will write this command later when I can run some physical tests
  }

  @Override
  public void periodic() {
    // This method will be called once per scheduler run
  }
}
