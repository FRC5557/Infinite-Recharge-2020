/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018-2019 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import java.util.HashMap;

import com.revrobotics.ColorMatch;

import edu.wpi.first.wpilibj.util.Color;

/**
 * The Constants class provides a convenient place for teams to hold robot-wide
 * numerical or boolean constants. This class should not be used for any other
 * purpose. All constants should be declared globally (i.e. public static). Do
 * not put anything functional in this class.
 *
 * <p>
 * It is advised to statically import this class (or one of its inner classes)
 * wherever the constants are needed, to reduce verbosity.
 */
public final class Constants {

    static Constants instance = null;

    public static Constants getInstance() {
        if (instance == null) {
            instance = new Constants();
        }
        return instance;
    }

    /**
     * Swerve Drive Constants
     */

    public static final int DRIVETRAIN_FRONT_LEFT_ANGLE_MOTOR = 3; // CAN
    public static final int DRIVETRAIN_FRONT_LEFT_ANGLE_ENCODER = 1; // Analog
    public static final int DRIVETRAIN_FRONT_LEFT_DRIVE_MOTOR = 4; // CAN

    // public static final int DRIVETRAIN_FRONT_LEFT_ANGLE_MOTOR = 4; // CAN
    // public static final int DRIVETRAIN_FRONT_LEFT_ANGLE_ENCODER = 1; // Analog
    // public static final int DRIVETRAIN_FRONT_LEFT_DRIVE_MOTOR = 3; // CAN

    public static final int DRIVETRAIN_FRONT_RIGHT_ANGLE_MOTOR = 1; // CAN
    public static final int DRIVETRAIN_FRONT_RIGHT_ANGLE_ENCODER = 0; // Analog
    public static final int DRIVETRAIN_FRONT_RIGHT_DRIVE_MOTOR = 2; // CAN

    public static final int DRIVETRAIN_BACK_LEFT_ANGLE_MOTOR = 5; // CAN
    public static final int DRIVETRAIN_BACK_LEFT_ANGLE_ENCODER = 2; // Analog
    public static final int DRIVETRAIN_BACK_LEFT_DRIVE_MOTOR = 6; // CAN

    public static final int DRIVETRAIN_BACK_RIGHT_ANGLE_MOTOR = 7; // CAN
    public static final int DRIVETRAIN_BACK_RIGHT_ANGLE_ENCODER = 3; // Analog
    public static final int DRIVETRAIN_BACK_RIGHT_DRIVE_MOTOR = 8; // CAN

    public static final double SWERVE_DRIVE_MOTOR_PER_REVOLUTION = 42;

    public static final double FRONT_LEFT_DRIVE_PER_REVOLUTION = 8.285738945007324;
    public static final double FRONT_RIGHT_DRIVE_PER_REVOLUTION = 8.285738945007324;
    public static final double BACK_LEFT_DRIVE_PER_REVOLUTION = 8.261929512023926;
    public static final double BACK_RIGHT_DRIVE_PER_REVOLUTION = 8.214309692382812;

    public static final int PRIMARY_JOYSTICK_PORT = 0;
    public static final int SECONDARY_JOYSTICK_PORT = 0;

    public static final int LAUNCHER_MOTOR = 9;

    public static final int INTAKE_MOTOR = 10;

    public static final int FEEDER_MOTOR = 11;

    public static final int CLIMB_LEFT_MOTOR = 13;
    public static final int CLIMB_RIGHT_MOTOR = 14;

    /*
     * Spinner constants
     */

    public static final int SPINNER_MOTOR = 12;

    // this is the counter for the encoder value per 1 motor revolution
    public static final double SPINNER_MOTOR_COUNTER_PER_REVOLUTION = 1;

    // this is how many motor revolutions per one of the control system revolutions
    public static final double SPINNER_MOTOR_WHEEL_REVOLUTION_PER_SPINNER_REVOLUTION = 4.5;

    /**
     * Note: Any example colors should be calibrated as the user needs, these are
     * here as a basic example.
     */
    public static final Color kBlueTarget = ColorMatch.makeColor(0.143, 0.427, 0.429);
    public static final Color kGreenTarget = ColorMatch.makeColor(0.197, 0.561, 0.240);
    public static final Color kRedTarget = ColorMatch.makeColor(0.561, 0.232, 0.114);
    public static final Color kYellowTarget = ColorMatch.makeColor(0.361, 0.524, 0.113);

    // Rev Color threshold
    // blue 0.143, 0.427, 0.429
    // green 0.197, 0.561, 0.240
    // red 0.561, 0.232, 0.114
    // yellow 0.361, 0.524, 0.113

    // the point of this hashmap is to tell the robot that we need to have the
    // sensor pick up color X,
    // so in code, we tell it color X and it knows it needs to pick color Y so that
    // color X
    // is under the sensor.
    public static HashMap<SpinnerColor, SpinnerColor> colorMapping = new HashMap<SpinnerColor, SpinnerColor>();

    /**
     * Limelight constants
     */
    public static final double POWER_PORT_HEIGHT = 8.5;
    public static final double LIMELIGHT_TO_FLOOR = 1;
    public static final double LIMELIGHT_MOUNTNG_ANGLE = 45;

    public static final double OPTIMAL_DISTANCE_FROM_POWER_PORT = 3;

    public static final double SWERVE_MODULE_RAMP_RATE = 0;
    public static final double SWERVE_MODULE_P = 0.003;
    public static final double SWERVE_MODULE_I = 0.00000155;
    public static final double SWERVE_MODULE_D = 0;

    public Constants() {

        // verify that these are correct

    }

}
