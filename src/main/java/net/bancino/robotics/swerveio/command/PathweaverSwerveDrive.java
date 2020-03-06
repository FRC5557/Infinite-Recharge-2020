package net.bancino.robotics.swerveio.command;

import edu.wpi.first.wpilibj2.command.CommandBase;
import edu.wpi.first.wpilibj.Filesystem;
import edu.wpi.first.wpilibj.trajectory.Trajectory;
import edu.wpi.first.wpilibj.trajectory.TrajectoryUtil;
import edu.wpi.first.wpilibj.kinematics.ChassisSpeeds;
import java.io.IOException;
import java.nio.file.Path;
import net.bancino.robotics.swerveio.SwerveDrive;
import net.bancino.robotics.swerveio.SwerveIOUtils;

/**
 * A command that, while running, will execute a PathWeaver trajectory
 * on a Swerve drive.
 *
 * @author Jordan Bancino
 */
public class PathweaverSwerveDrive extends CommandBase {

    private final Trajectory trajectory;
    private final SwerveDrive swerve;
    private final double angularVelocity;

    private Trajectory.State currentState;
    private long startTime, currentTime;

    /**
     * Create a Pathweaver swerve drive.
     *
     * @param swerve The swerve drive object to drive.
     * @param pathweaverJson The pathweaver file to load, relative to the deploy directory.
     * @throws IOException If there is an IO error when reading the pathweaver json file.
     */
    public PathweaverSwerveDrive(SwerveDrive swerve, String pathweaverJson) throws IOException {
        if (pathweaverJson != null) {
            Path jsonPath = Filesystem.getDeployDirectory().toPath().resolve(pathweaverJson);
            trajectory = TrajectoryUtil.fromPathweaverJson(jsonPath);
            angularVelocity = SwerveIOUtils.getAngularVelocity(trajectory);
        } else {
            throw new IllegalArgumentException("Pathweaver JSON file name cannot be null.");
        }
        if (swerve != null) {
            this.swerve = swerve;
            addRequirements(swerve);
        } else {
            throw new IllegalArgumentException("Swerve drive cannot be null.");
        }
    }

    @Override
    public void initialize() {
        startTime = System.currentTimeMillis();
    }

    private double getTimeSeconds() {
        currentTime = System.currentTimeMillis();
        return (currentTime - startTime) / 1000;
    }

    @Override
    public void execute() {
        currentState = trajectory.sample(getTimeSeconds());
        ChassisSpeeds swerveVector = SwerveIOUtils.convertToChassisSpeeds(currentState, angularVelocity);
        swerve.drive(swerveVector);
        System.out.println("[SwerveIO Pathweaver] (" + getTimeSeconds() + " s) Running vector: " + swerveVector);
    }

    @Override
    public boolean isFinished() {
        return currentState == null || trajectory.getTotalTimeSeconds() <= getTimeSeconds();
    }
}