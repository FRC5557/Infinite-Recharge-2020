package frc.robot.commands;

import edu.wpi.first.wpilibj.GenericHID;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.geometry.Translation2d;
import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.Robot;
import frc.robot.subsystems.swerve.DrivetrainSubsystem;

import org.frcteam2910.common.control.Trajectory;
import org.frcteam2910.common.math.Vector2;
import org.frcteam2910.common.util.HolonomicDriveSignal;

import java.util.Optional;
import java.util.function.Supplier;

public class FollowTrajectoryCommand extends CommandBase {
    private final Supplier<Trajectory> trajectorySupplier;

    private Trajectory trajectory;

    DrivetrainSubsystem drive;

    public FollowTrajectoryCommand(Trajectory trajectory) {
        
        this(() -> trajectory);
    }

    public FollowTrajectoryCommand(Supplier<Trajectory> trajectorySupplier) {
        this.trajectorySupplier = trajectorySupplier;
        drive = DrivetrainSubsystem.getInstance();
        addRequirements(drive);
        // this.setRunWhenDisabled(true);
    }

    @Override
    public void initialize() {
        System.out.println("Starting follow trajetory");
        trajectory = trajectorySupplier.get();
        // DrivetrainSubsystem.getInstance().resetKinematics(new Translation2d(0, 0),
        // Timer.getFPGATimestamp());
        DrivetrainSubsystem.getInstance().getFollower().follow(trajectory);
        
    }

    @Override
    public void execute() {
        // ok so now here we gonna have to get all the proper variables to pass into the follower update method
        // DrivetrainSubsystem.getInstance().getFollower().
        // missing: velocity and rotational velocity
        
        System.out.println(drive.getDriveSignal().getTranslation());
        HolonomicDriveSignal signal = DrivetrainSubsystem.getInstance().getFollower().update(drive.getPose(), drive.getDriveSignal().getTranslation() , drive.getDriveSignal().getRotation(), Timer.getFPGATimestamp(), 5.0e-3).orElse(new HolonomicDriveSignal(Vector2.ZERO, 0, true));
        System.out.println(signal.getTranslation());
        DrivetrainSubsystem.getInstance().drive(signal);
    }


    @Override
    public void end(boolean interrupted) {
        System.out.println("Finished following");
        if (interrupted) {
            DrivetrainSubsystem.getInstance().getFollower().cancel();
        }
        DrivetrainSubsystem.getInstance().stop();
        // DrivetrainSubsystem.getInstance()
        // .setSnapRotation(trajectory.calculate(trajectory.getDuration()).rotation.toRadians());
        // DrivetrainSubsystem.getInstance().setsnap
        DrivetrainSubsystem.getInstance().setSnapRotation(
                trajectory.calculate(trajectory.getDuration()).getPathState().getRotation().toRadians());
        new Thread(() -> {
            Robot.getRobotContainer().getPrimaryController().setRumble(GenericHID.RumbleType.kLeftRumble, 1.0);
            Robot.getRobotContainer().getPrimaryController().setRumble(GenericHID.RumbleType.kRightRumble, 1.0);
            Timer.delay(0.5);
            Robot.getRobotContainer().getPrimaryController().setRumble(GenericHID.RumbleType.kLeftRumble, 0.0);
            Robot.getRobotContainer().getPrimaryController().setRumble(GenericHID.RumbleType.kRightRumble, 0.0);
        }).start();
    }

    @Override
    public boolean isFinished() {
        // Only finish when the trajectory is completed
        return DrivetrainSubsystem.getInstance().getFollower().getCurrentTrajectory().isEmpty();
    }
}