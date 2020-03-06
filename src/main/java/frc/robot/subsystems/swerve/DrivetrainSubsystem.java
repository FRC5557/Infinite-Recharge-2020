/*----------------------------------------------------------------------------*/
/* Copyright (c) 2019 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.subsystems.swerve;

import frc.robot.Constants;
import frc.robot.Direction;

import javax.annotation.concurrent.GuardedBy;

import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel;

import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.wpilibj.AnalogInput;
import edu.wpi.first.wpilibj.SPI;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.drive.Vector2d;
import edu.wpi.first.wpilibj.geometry.Rotation2d;
import edu.wpi.first.wpilibj.geometry.Translation2d;
import edu.wpi.first.wpilibj.kinematics.ChassisSpeeds;
import edu.wpi.first.wpilibj.kinematics.SwerveDriveKinematics;
import edu.wpi.first.wpilibj.kinematics.SwerveModuleState;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInLayouts;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardLayout;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

import org.frcteam2910.common.control.CentripetalAccelerationConstraint;
import org.frcteam2910.common.control.HolonomicMotionProfiledTrajectoryFollower;
import org.frcteam2910.common.control.MaxAccelerationConstraint;
import org.frcteam2910.common.control.MaxVelocityConstraint;
import org.frcteam2910.common.control.PidConstants;
import org.frcteam2910.common.control.PidController;
import org.frcteam2910.common.drivers.Gyroscope;
import org.frcteam2910.common.math.RigidTransform2;
import org.frcteam2910.common.math.Rotation2;
import org.frcteam2910.common.math.Vector2;
import org.frcteam2910.common.robot.UpdateManager;
import org.frcteam2910.common.robot.drivers.Mk2SwerveModuleBuilder;
// import org.frcteam2910.common.robot.drivers.Mk2SwerveModule;
import org.frcteam2910.common.robot.drivers.NavX;
import org.frcteam2910.common.util.DrivetrainFeedforwardConstants;
import org.frcteam2910.common.util.HolonomicDriveSignal;
import org.frcteam2910.common.util.HolonomicFeedforward;
import org.frcteam2910.common.drivers.SwerveModule;
import org.frcteam2910.common.kinematics.ChassisVelocity;
import org.frcteam2910.common.kinematics.SwerveKinematics;
import org.frcteam2910.common.kinematics.SwerveOdometry;
import org.frcteam2910.common.control.TrajectoryConstraint;

public class DrivetrainSubsystem extends SubsystemBase implements UpdateManager.Updatable {
  /**
   * Creates a new DrivetrainSubsystem.
   */
  private static final double TRACKWIDTH = 30;
  private static final double WHEELBASE = 30;

  /**
   * These are the individual module offsets that represent their "zero" positions
   */
  private static final double FRONT_LEFT_ANGLE_OFFSET = -Math.toRadians(30.179290);
  private static final double FRONT_RIGHT_ANGLE_OFFSET = -Math.toRadians(156.568652);
  private static final double BACK_LEFT_ANGLE_OFFSET = -Math.toRadians(166.602586 + 180);
  private static final double BACK_RIGHT_ANGLE_OFFSET = -Math.toRadians(317.177098);

  /**
   * INSTANCE_LOCK is used for thread safety instance is the general static
   * instance object
   */
  private static final Object INSTANCE_LOCK = new Object();
  private static DrivetrainSubsystem instance;

  /**
   * This constructs each swerve module with the given parameters
   */
  private final SwerveModule frontLeftModule = new Mk2SwerveModuleBuilder(
      new Vector2(TRACKWIDTH / 2.0, WHEELBASE / 2.0))
          .angleEncoder(new AnalogInput(Constants.DRIVETRAIN_FRONT_LEFT_ANGLE_ENCODER), FRONT_LEFT_ANGLE_OFFSET)
          .angleMotor(
              new CANSparkMax(Constants.DRIVETRAIN_FRONT_LEFT_ANGLE_MOTOR, CANSparkMaxLowLevel.MotorType.kBrushless),
              Mk2SwerveModuleBuilder.MotorType.NEO)
          .driveMotor(
              new CANSparkMax(Constants.DRIVETRAIN_FRONT_LEFT_DRIVE_MOTOR, CANSparkMaxLowLevel.MotorType.kBrushless),
              Mk2SwerveModuleBuilder.MotorType.NEO)
          .build();
  private final SwerveModule frontRightModule = new Mk2SwerveModuleBuilder(
      new Vector2(TRACKWIDTH / 2.0, -WHEELBASE / 2.0))
          .angleEncoder(new AnalogInput(Constants.DRIVETRAIN_FRONT_RIGHT_ANGLE_ENCODER), FRONT_RIGHT_ANGLE_OFFSET)
          .angleMotor(
              new CANSparkMax(Constants.DRIVETRAIN_FRONT_RIGHT_ANGLE_MOTOR, CANSparkMaxLowLevel.MotorType.kBrushless),
              Mk2SwerveModuleBuilder.MotorType.NEO)
          .driveMotor(
              new CANSparkMax(Constants.DRIVETRAIN_FRONT_RIGHT_DRIVE_MOTOR, CANSparkMaxLowLevel.MotorType.kBrushless),
              Mk2SwerveModuleBuilder.MotorType.NEO)
          .build();
  private final SwerveModule backLeftModule = new Mk2SwerveModuleBuilder(
      new Vector2(-TRACKWIDTH / 2.0, WHEELBASE / 2.0))
          .angleEncoder(new AnalogInput(Constants.DRIVETRAIN_BACK_LEFT_ANGLE_ENCODER), BACK_LEFT_ANGLE_OFFSET)
          .angleMotor(
              new CANSparkMax(Constants.DRIVETRAIN_BACK_LEFT_ANGLE_MOTOR, CANSparkMaxLowLevel.MotorType.kBrushless),
              Mk2SwerveModuleBuilder.MotorType.NEO)
          .driveMotor(
              new CANSparkMax(Constants.DRIVETRAIN_BACK_LEFT_DRIVE_MOTOR, CANSparkMaxLowLevel.MotorType.kBrushless),
              Mk2SwerveModuleBuilder.MotorType.NEO)
          .build();
  private final SwerveModule backRightModule = new Mk2SwerveModuleBuilder(
      new Vector2(-TRACKWIDTH / 2.0, -WHEELBASE / 2.0))
          .angleEncoder(new AnalogInput(Constants.DRIVETRAIN_BACK_RIGHT_ANGLE_ENCODER), BACK_RIGHT_ANGLE_OFFSET)
          .angleMotor(
              new CANSparkMax(Constants.DRIVETRAIN_BACK_RIGHT_ANGLE_MOTOR, CANSparkMaxLowLevel.MotorType.kBrushless),
              Mk2SwerveModuleBuilder.MotorType.NEO)
          .driveMotor(
              new CANSparkMax(Constants.DRIVETRAIN_BACK_RIGHT_DRIVE_MOTOR, CANSparkMaxLowLevel.MotorType.kBrushless),
              Mk2SwerveModuleBuilder.MotorType.NEO)
          .build();

  private final SwerveKinematics kinematics = new SwerveKinematics(new Vector2(TRACKWIDTH / 2.0, WHEELBASE / 2.0), // Front
                                                                                                                   // Left
      new Vector2(TRACKWIDTH / 2.0, -WHEELBASE / 2.0), // Front Right
      new Vector2(-TRACKWIDTH / 2.0, WHEELBASE / 2.0), // Back Left
      new Vector2(-TRACKWIDTH / 2.0, -WHEELBASE / 2.0) // Back Right
  );

  private final SwerveModule[] modules = { frontLeftModule, frontRightModule, backLeftModule, backRightModule };

  /**
   * Below are variables or objects used for calculations crucial to swerve drive
   * each with their respective "locks" for thread safety
   */
  private final Object sensorLock = new Object();
  @GuardedBy("sensorLock")
  private final Gyroscope gyroscope = new NavX(SPI.Port.kMXP);

  private final SwerveOdometry odometry = new SwerveOdometry(kinematics, RigidTransform2.ZERO);

  private final Object kinematicsLock = new Object();
  @GuardedBy("kinematicsLock")
  private RigidTransform2 pose = RigidTransform2.ZERO;

  private NetworkTableEntry[] moduleAngleEntries = new NetworkTableEntry[modules.length];

  private NetworkTableEntry poseXEntry;
  private NetworkTableEntry poseYEntry;
  private NetworkTableEntry poseAngleEntry;

  private final Object stateLock = new Object();
  @GuardedBy("stateLock")
  private HolonomicDriveSignal driveSignal = null;

  public DrivetrainSubsystem() {

    synchronized (sensorLock) {
      /**
       * We found that inverting the gyroscope gives us the proper behavior
       */
      gyroscope.setInverted(true);
    }

    /**
     * This displays some key information about swerve to the shuffleboard
     */
    ShuffleboardTab tab = Shuffleboard.getTab("Drivetrain");
    poseXEntry = tab.add("Pose X", 0.0).withPosition(0, 0).withSize(1, 1).getEntry();
    poseYEntry = tab.add("Pose Y", 0.0).withPosition(0, 1).withSize(1, 1).getEntry();
    poseAngleEntry = tab.add("Pose Angle", 0.0).withPosition(0, 2).withSize(1, 1).getEntry();

    ShuffleboardLayout frontLeftModuleContainer = tab.getLayout("Front Left Module", BuiltInLayouts.kList)
        .withPosition(1, 0).withSize(2, 3);
    moduleAngleEntries[0] = frontLeftModuleContainer.add("Angle", 0.0).getEntry();

    ShuffleboardLayout frontRightModuleContainer = tab.getLayout("Front Right Module", BuiltInLayouts.kList)
        .withPosition(3, 0).withSize(2, 3);
    moduleAngleEntries[1] = frontRightModuleContainer.add("Angle", 0.0).getEntry();

    ShuffleboardLayout backLeftModuleContainer = tab.getLayout("Back Left Module", BuiltInLayouts.kList)
        .withPosition(5, 0).withSize(2, 3);
    moduleAngleEntries[2] = backLeftModuleContainer.add("Angle", 0.0).getEntry();

    ShuffleboardLayout backRightModuleContainer = tab.getLayout("Back Right Module", BuiltInLayouts.kList)
        .withPosition(7, 0).withSize(2, 3);
    moduleAngleEntries[3] = backRightModuleContainer.add("Angle", 0.0).getEntry();
  }

  public static DrivetrainSubsystem getInstance() {
    synchronized (INSTANCE_LOCK) {
      if (instance == null) {
        instance = new DrivetrainSubsystem();
      }

      return instance;
    }
  }

  public RigidTransform2 getPose() {
    synchronized (kinematicsLock) {
      return pose;
    }
  }

  public void drive(Vector2 translationalVelocity, double rotationalVelocity, boolean fieldOriented) {
    synchronized (stateLock) {
      driveSignal = new HolonomicDriveSignal(translationalVelocity, rotationalVelocity, fieldOriented);
    }
  }

  public void drive(HolonomicDriveSignal signal) {
    synchronized (stateLock) {
      driveSignal = signal;
    }
  }

  public void resetGyroAngle(Rotation2 angle) {
    synchronized (sensorLock) {
      gyroscope.setAdjustmentAngle(gyroscope.getUnadjustedAngle().rotateBy(angle.inverse()));
    }
  }

  public void stop() {
    // drive(new Translation2d(0, 0), 0, true);
    drive(Vector2.ZERO, 0, true);
  }

  @Override
  public void update(double timestamp, double dt) {
    updateOdometry(dt);

    HolonomicDriveSignal driveSignal;
    synchronized (stateLock) {
      driveSignal = this.driveSignal;
    }

    updateModules(driveSignal, dt);
  }

  private void updateOdometry(double dt) {
    Vector2[] moduleVelocities = new Vector2[modules.length];
    for (int i = 0; i < modules.length; i++) {
      var module = modules[i];
      module.updateSensors();

      moduleVelocities[i] = Vector2.fromAngle(Rotation2.fromRadians(module.getCurrentAngle()))
          .scale(module.getCurrentVelocity());
    }

    Rotation2 angle;
    synchronized (sensorLock) {
      angle = gyroscope.getAngle();
    }

    RigidTransform2 pose = odometry.update(angle, dt, moduleVelocities);

    synchronized (kinematicsLock) {
      this.pose = pose;
    }
  }

  private void updateModules(HolonomicDriveSignal signal, double dt) {
    ChassisVelocity velocity;
    if (signal == null) {
      velocity = new ChassisVelocity(Vector2.ZERO, 0.0);
    } else if (signal.isFieldOriented()) {
      velocity = new ChassisVelocity(signal.getTranslation().rotateBy(getPose().rotation.inverse()),
          signal.getRotation());
    } else {
      velocity = new ChassisVelocity(signal.getTranslation(), signal.getRotation());
    }

    Vector2[] moduleOutputs = kinematics.toModuleVelocities(velocity);
    SwerveKinematics.normalizeModuleVelocities(moduleOutputs, 1.0);

    for (int i = 0; i < modules.length; i++) {
      var module = modules[i];
      module.setTargetVelocity(moduleOutputs[i]);
      module.updateState(dt);
    }
  }

  @Override
  public void periodic() {
    var pose = getPose();
    poseXEntry.setDouble(pose.translation.x);
    poseYEntry.setDouble(pose.translation.y);
    poseAngleEntry.setDouble(pose.rotation.toDegrees());

    for (int i = 0; i < modules.length; i++) {
      var module = modules[i];
      moduleAngleEntries[i].setDouble(Math.toDegrees(module.getCurrentAngle()));
    }
  }

  public Gyroscope getGyroscope() {
    return this.gyroscope;
  }
}
