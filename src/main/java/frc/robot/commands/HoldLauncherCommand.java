/*----------------------------------------------------------------------------*/
/* Copyright (c) 2019 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/
 
package frc.robot.commands;
 
import edu.wpi.first.wpilibj2.command.CommandBase;
import edu.wpi.first.wpilibj2.command.CommandGroupBase;
import frc.robot.Robot;
import frc.robot.RobotContainer;
import frc.robot.subsystems.StorageSubsystem;
import frc.robot.Constants;
 
//creates a new FeedLauncherCommand class that is a subclass of CommandBase
public class HoldLauncherCommand extends CommandBase{
 
    StorageSubsystem storage;
     /*
    creates a new FeedLauncherCommand command that 
    is a method of the FeedLauncherCommand class
    */
    public HoldLauncherCommand(){
        storage = StorageSubsystem.getInstance();
        addRequirements(storage);
    }
 
    /*called when command is first called, provides any 
    necessary data which in this case is nothing
    */
    @Override
    public void initialize(){
 
    }   
 
    //this is what runs when the command is called
    //current output is .5
    @Override
    public void execute(){
 
        StorageSubsystem.getInstance().holdLauncher(); //this is the method in the StorageSubsystem class that gets the feed motor rotating at a certain speed.
 
    }
 
    /*
    I don't know if this will be used, I think the use of toggle in RobotContainer is enough but I will test tomorrow.
    This is because this command doesn't rely on anything except for user input (i.e. no sensor feedback) so toggle should be enough
    */
    @Override 
    public void end(boolean interrupted){
 
        StorageSubsystem.getInstance().stop();
 
    }

    @Override
    public boolean isFinished() {
        return false;
    }
    
   
 
}
    
 

