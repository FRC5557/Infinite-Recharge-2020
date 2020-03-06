/*----------------------------------------------------------------------------*/
/* Copyright (c) 2019 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/
 
package frc.robot.commands;
 
import org.frcteam2910.common.math.Rotation2;
import org.frcteam2910.common.math.Vector2;

import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.wpilibj.GenericHID.RumbleType;
import edu.wpi.first.wpilibj.controller.PIDController;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;
import edu.wpi.first.wpilibj2.command.CommandBase;
import edu.wpi.first.wpiutil.math.MathUtil;
import frc.robot.Robot;
import frc.robot.subsystems.Limelight;
import frc.robot.subsystems.LauncherSubsystem;
import frc.robot.commands.RotateToTargetCommand;
 
 
/**
 * This command is made with the hope that the driver can press a button and limelight
 * will be able to determine the distance that the robot is from the powerport and adjust 
 * the angular velocity of the launcher accordingly. This assumes that the launcher has a constant 
 * angle and that the limelight is at a fixed position at an angle that does not vary. Best of luck to me. 
 * 
 * Important - the experimental data used was gotten testing the launcher on a stool on the auto line
 *             the final version should have updated values using the finished product.
 */
 
public class LaunchFromDistanceCommand extends CommandBase {
    
    private LauncherSubsystem launcher;
    private Limelight limelight;

    double k;

    NetworkTableEntry a2Tab, distanceTab;
 
    public LaunchFromDistanceCommand(){
    
        launcher = LauncherSubsystem.getInstance();
        limelight = Limelight.getInstance();
        addRequirements(launcher);

        a2Tab = Shuffleboard.getTab("launcher").add("A2", 0).getEntry();
        distanceTab = Shuffleboard.getTab("launcher").add("distance", 0).getEntry();

}
 
 
@Override
public void initialize(){
 
    limelight.disableDriverMode();
    limelight.enableLEDs();
    
    double limelightHeight = 8.85 ;                        //height of the center of the limelight camera lens
    final double powerportHeight = 83.25;               //height of center of reflective tape on powerport
    double dh = powerportHeight - limelightHeight;
/* 

    double a1 = 50.00;                                  //angle at which the limelight is mounted
    double a2 = Limelight.getInstance().getAngleY();    //Vertical Offset From Crosshair To Target
    System.out.println("A2: " + a2);
    double dx = (dh/Math.tan(a1+a2));                  //this is the distance that the limelight is from the face of the powerport
    System.out.println(dx);                             //prints out the distance 
 


    
    /**
     * Now, using this value found for dx, distance of the limelight from the powerport,
     * we can apply kinematics to find the constant we need to vary intial velocity by.
     * 
     * Solving for maximum intial velocity using experimental data yields:
     * 
     * dx = (k) * Vmax * cos(theta) * t
     * 
     * dx = distance from the launcher to the powerport face
     * k  = the constant applied to the output of the motor to limit initial velocity
     * Vmax = maximum velocity the launcher can output a powercell with
     * 
     * now rearranging for Vmax: 
     * 
     * Vmax = (dx)/((k)* cos(theta)*t)
     * 
     * Plug in in knowns once we've done experitments for Vmax
     * 
     * 
     * 
     * 
     * Solving algebraically for k in this equation is tough, so I need to do it later.
     *
    final double theta = Math.toRadians(55); // the angle of the launcher, this is a placeholder for the current value ==b
    final double v = 347.0;//not final value, place holder for max V determined experimentally.
    final double a = -386.04; //this is the acceleration due to gravity in inches per second per second
    k = Math.sqrt((a * Math.pow(dx, 2))/ 2 * Math.pow(v, 2) * Math.pow(Math.cos(theta), 2) * (dh -(dx * Math.tan(theta))));
    // second try k = (Math.sqrt(a) * dx * Math.sqrt((1/Math.cos(theta)))) / (v * (Math.sqrt(2 * dx * Math.sin(theta) - (2 * dh * Math.cos(theta)))));
    // first try k = Math.sqrt(Math.sqrt(Math.pow(a, 2)* Math.pow(v, 4) *  Math.pow(Math.cos(theta), 2) * (2 * a * dx * dh * Math.sin(theta) * Math.cos(theta) + Math.pow(dx, 2) * Math.pow(Math.sin(theta), 2)   + Math.pow(dh, 2) * Math.pow(Math.cos(theta),2) ))/(Math.pow(a,2)*  Math.pow(v, 4) * Math.pow(Math.sin(theta), 2) * Math.pow(Math.cos(theta), 2)  - Math.pow(v, 4) * Math.pow(Math.sin(theta), 2) *Math.pow(Math.cos(theta), 2)) - (Math.pow(a,2)* dx * Math.pow(v, 2) * Math.sin(theta) * Math.cos(theta))/(Math.pow(a,2)*  Math.pow(v, 4) * Math.pow(Math.sin(theta), 2) * Math.pow(Math.cos(theta), 2)  - Math.pow(v, 4) * Math.pow(Math.sin(theta), 2) *Math.pow(Math.cos(theta), 2)) - (a * dh* Math.pow(v, 2) * Math.pow(Math.cos(theta), 2))/(Math.pow(a,2)*  Math.pow(v, 4) * Math.pow(Math.sin(theta), 2) * Math.pow(Math.cos(theta), 2)  - Math.pow(v, 4) * Math.pow(Math.sin(theta), 2) *Math.pow(Math.cos(theta), 2)));//this is not the real value of k at the moment, k will be in terms of other variables.
   
    System.out.println("K: " + k);
    System.out.println("dx: " + dx);
    System.out.println("dh: " + dh);
    System.out.println("theta: " + theta);
    System.out.println("a: " + a);
    System.out.println("v: " + v);
*/

}
 
 
/**
 * This is what will run when this is called. Using kinematics and trig, I found equations that
 * will allow me to relate the vertical distance (dh) between the limelight, the center of the 
 * reflective tape on the powerport and the horizontal distance (dx) between the limelight and 
 * the face of the powerport. Using this, dx, can be found and used in kinematic equations to 
 * determine the output needed on the launcher motors.
 * 
 * Equations used are: 
 * dx = (dh/tan(a1+a2))
 * 
 * dx = horizontal distance
 * dh = vertical distance
 * a1 = angle of the limelight
 * a2 = offset of angle of the power port and a1
 * 
 * http://docs.limelightvision.io/en/latest/cs_estimating_distance.html
 * 
 * 
 * TODO: Get experimental values using the final assembly and make dx account for the distance between 
 * the limelight and the launcher.
*/
@Override
public void execute(){

    
    double limelightHeight = 44.00 ;                        //height of the center of the limelight camera lens
    final double powerportHeight = 90.25;               //height of center of reflective tape on powerport
    double dh = powerportHeight - limelightHeight;
 
    
    
    double a1 = 12.5;                                  //angle at which the limelight is mounted
    double a2 = Limelight.getInstance().getAngleY();    //Vertical Offset From Crosshair To Target
    double dx = (dh/(Math.tan(Math.toRadians(a1+a2))));                  //this is the distance that the limelight is from the face of the powerport
    a2Tab.setDouble(a2);
    distanceTab.setDouble(dx);    
    // LauncherSubsystem.getInstance().launch(k);
 
    
}
 
 /*
    I don't know if this will be used, I think the use of toggle in RobotContainer is enough but I will test tomorrow.
    This is because this command doesn't rely on anything except for user input (i.e. no sensor feedback) so toggle should be enough
    */
    @Override 
    public void end(boolean interrupted){
        Limelight.getInstance().enableDriverMode();
        Limelight.getInstance().disableLEDs();

        LauncherSubsystem.getInstance().stop();
 
    }

    @Override
    public boolean isFinished() {
        return false;
    }
}
 

