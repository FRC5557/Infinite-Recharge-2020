/*----------------------------------------------------------------------------*/
/* Copyright (c) 2019 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.subsystems;

import org.frcteam2910.common.math.Rotation2;
import org.frcteam2910.common.math.Vector2;
import org.frcteam2910.common.util.HolonomicDriveSignal;

import edu.wpi.first.wpilibj.controller.PIDController;
import edu.wpi.first.wpilibj2.command.PIDSubsystem;
import frc.robot.subsystems.swerve.DrivetrainSubsystem;

public class RotateToTargetSubsystem extends PIDSubsystem {
  /**
   * Creates a new RotateToTargetSubsystem.
   */
  public RotateToTargetSubsystem() {
    super(
        // The PIDController used by the subsystem
        new PIDController(0.1, 0, 0));

    setSetpoint(0); // Sets where the PID controller should move the system

  }

  @Override
  public void useOutput(double output, double setpoint) {
    // Use the output here
    DrivetrainSubsystem.getInstance().drive(Vector2.ZERO, output, true);
  }

  @Override
  public double getMeasurement() {
    // Return the process variable measurement here
    return Limelight.getInstance().getAngleX();
  }
}
