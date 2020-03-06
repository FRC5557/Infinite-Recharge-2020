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
public class StorageSubsystem extends SubsystemBase {
 
  static StorageSubsystem instance;
  private CANSparkMax StorageMotor;
  private CANPIDController StoragePIDController;
  double kP, kI, kD, kIz, kFF, kMaxOutput, kMinOutput;
 
  public static StorageSubsystem getInstance(){
    if(instance == null){
      instance = new StorageSubsystem();
    }
    return instance;
  }
 
  public StorageSubsystem(){
    this.StorageMotor = new CANSparkMax(Constants.FEEDER_MOTOR, MotorType.kBrushless);
    this.StoragePIDController = StorageMotor.getPIDController();
 
    kP = 5e-5;
    kI = 1e-6;
    kD = 0;
    kIz = 0;
    kFF = 0;
    kMaxOutput = 0.5;
    kMinOutput = -0.5;
 
    StoragePIDController.setP(kP);
    StoragePIDController.setI(kI);
    StoragePIDController.setD(kD);
    StoragePIDController.setIZone(kIz);
    StoragePIDController.setFF(kFF);
    StoragePIDController.setOutputRange(kMinOutput, kMaxOutput);
  }
 
  public void feedLauncher(){
    // StoragePIDController.setReference(0.2, ControlType.kVelocity);
    StorageMotor.set(-0.2);
  }
  public void holdLauncher(){
    // StoragePIDController.setReference(-.2, ControlType.kVelocity);
    StorageMotor.set(.2);
  }
 
  public void stop() {
    StorageMotor.set(0);
  } 
}
 

