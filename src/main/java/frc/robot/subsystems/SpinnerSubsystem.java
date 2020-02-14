/*----------------------------------------------------------------------------*/
/* Copyright (c) 2019 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.subsystems;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.StatusFrame;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.revrobotics.CANPIDController;
import com.revrobotics.CANSparkMax;
import com.revrobotics.ColorMatch;
import com.revrobotics.ColorMatchResult;
import com.revrobotics.ColorSensorV3;
import com.revrobotics.CANSparkMax.IdleMode;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.wpilibj.I2C;
/* import edu.wpi.first.wpilibj.Talon; */
import edu.wpi.first.wpilibj.controller.PIDController;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.util.Color;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;
import frc.robot.SpinnerColor;
import frc.robot.commands.SpinToColorCommand;

public class SpinnerSubsystem extends SubsystemBase {

  /**
   * The code for the motor + controller assumes using a talon with an RS7 encoder
   * This is just for prototyping and may not represent the final decision.
   */

  public static SpinnerSubsystem instance = null;

  public static SpinnerSubsystem getInstance() {
    if (instance == null) {
      instance = new SpinnerSubsystem();
    }
    return instance;
  }

  private CANSparkMax spinnerSparkMax;
  //private TalonSRX spinnerTalon;

  // Most of the color sensor code is taken straight from
  // https://github.com/FRCTeam225/Ri3D2020/blob/master/src/main/java/frc/robot/ColorMatcher.java

  /**
   * Change the I2C port below to match the connection of your color sensor
   */
  private I2C.Port i2cPort;

  private ColorSensorV3 colorSensor;

  /**
   * A Rev Color Match object is used to register and detect known colors. This
   * can be calibrated ahead of time or during operation.
   * 
   * This object uses a simple euclidian distance to estimate the closest match
   * with given confidence range.
   */
  private final ColorMatch colorMatcher;

  NetworkTableEntry redEntry, greenEntry, blueEntry, confidenceEntry, detectedColorEntry;

  /**
   * Creates a new SpinnerSubsystem.
   */
  public SpinnerSubsystem() {
    this.setupSparkMax();
    i2cPort = I2C.Port.kOnboard;
    colorSensor = new ColorSensorV3(i2cPort);
    colorMatcher = new ColorMatch();

    colorMatcher.addColorMatch(Constants.kBlueTarget);
    colorMatcher.addColorMatch(Constants.kGreenTarget);
    colorMatcher.addColorMatch(Constants.kRedTarget);
    colorMatcher.addColorMatch(Constants.kYellowTarget);

    colorMatcher.setConfidenceThreshold(0.80);

    redEntry = Shuffleboard.getTab("competition").add("Red", 0).getEntry();
    greenEntry = Shuffleboard.getTab("competition").add("Green", 0).getEntry();
    blueEntry = Shuffleboard.getTab("competition").add("Blue", 0).getEntry();
    confidenceEntry = Shuffleboard.getTab("competition").add("Confidence", 0).getEntry();
    detectedColorEntry = Shuffleboard.getTab("competition").add("Detected Color", "none").getEntry();
  }

  private void setupSparkMax() {
    spinnerSparkMax = new CANSparkMax(Constants.SPINNER_MOTOR, MotorType.kBrushless);

    spinnerSparkMax.set(0);
    //spinnerTalon.configFactoryDefault();
    spinnerSparkMax.restoreFactoryDefaults(); //I believe this does the same thing as the talon method, but not sure
    //spinnerSparkMax.setNeutralMode(NeutralMode.Brake); 
    spinnerSparkMax.setIdleMode(IdleMode.kBrake);

  }

  protected void outputColorData() {
    /**
     * The method GetColor() returns a normalized color value from the sensor and
     * can be useful if outputting the color to an RGB LED or similar. To read the
     * raw color, use GetRawColor().
     * 
     * The color sensor works best when within a few inches from an object in well
     * lit conditions (the built in LED is a big help here!). The farther an object
     * is the more light from the surroundings will bleed into the measurements and
     * make it difficult to accurately determine its color.
     */
    Color detectedColor = colorSensor.getColor();

    /**
     * Run the color match algorithm on our detected color
     */
    String colorString;
    ColorMatchResult match = colorMatcher.matchClosestColor(detectedColor);

    if (match.color == Constants.kBlueTarget) {
      colorString = "Blue";
    } else if (match.color == Constants.kRedTarget) {
      colorString = "Red";
    } else if (match.color == Constants.kGreenTarget) {
      colorString = "Green";
    } else if (match.color == Constants.kYellowTarget) {
      colorString = "Yellow";
    } else {
      colorString = "Unknown";
    }

    /**
     * Open Smart Dashboard or Shuffleboard to see the color detected by the sensor.
     */

    redEntry.setValue(detectedColor.red);
    greenEntry.setValue(detectedColor.green);
    blueEntry.setValue(detectedColor.blue);
    confidenceEntry.setValue(match.confidence);
    detectedColorEntry.setValue(colorString);

    // Shuffleboard.getTab("competition").add("Red", detectedColor.red);
    // Shuffleboard.getTab("competition").add("Green", detectedColor.green);
    // Shuffleboard.getTab("competition").add("Blue", detectedColor.blue);
    // Shuffleboard.getTab("competition").add("Confidence", match.confidence);
    // Shuffleboard.getTab("competition").add("Detected Color", colorString);
  }

  public void spinToColor(SpinnerColor desiredColor) {
    // run a command here like spinMotor until color hit
    new SpinToColorCommand().schedule();
  }

  @Override
  public void periodic() {
    // This method will be called once per scheduler run
    outputColorData();
  }

  public ColorSensorV3 getColorSensor() {
    return this.colorSensor;
  }

  public SpinnerColor getColor() {

    // TODO: put following code in its own method cause its used twice in this file

    /**
     * The method GetColor() returns a normalized color value from the sensor and
     * can be useful if outputting the color to an RGB LED or similar. To read the
     * raw color, use GetRawColor().
     * 
     * The color sensor works best when within a few inches from an object in well
     * lit conditions (the built in LED is a big help here!). The farther an object
     * is the more light from the surroundings will bleed into the measurements and
     * make it difficult to accurately determine its color.
     */
    Color detectedColor = colorSensor.getColor();

    /**
     * Run the color match algorithm on our detected color
     */
    SpinnerColor spinnerColor;
    ColorMatchResult match = colorMatcher.matchClosestColor(detectedColor);

    // i will convert this match to a SpinnerColor object for ease of use
    if (match.color == Constants.kBlueTarget) {
      spinnerColor = SpinnerColor.BLUE;
    } else if (match.color == Constants.kRedTarget) {
      spinnerColor = SpinnerColor.RED;
    } else if (match.color == Constants.kGreenTarget) {
      spinnerColor = SpinnerColor.GREEN;
    } else if (match.color == Constants.kYellowTarget) {
      spinnerColor = SpinnerColor.YELLOW;
    } else {
      spinnerColor = SpinnerColor.UNKNOWN;
    }

    return spinnerColor;

  }

  public void spinSpinner(double speed) {
    spinnerSparkMax.set(speed);
  }

  public void zeroEncoder() {
    spinnerSparkMax.getEncoder().setPosition(0);
  }

  public double getEncoderPosition() {
    return spinnerSparkMax.getEncoder().getPosition();
  }

  public CANSparkMax getSpinnerSparkMax() {
    return this.spinnerSparkMax;
  }

  public CANPIDController getPIDController() {
    return this.spinnerSparkMax.getPIDController();
  }

  

}
