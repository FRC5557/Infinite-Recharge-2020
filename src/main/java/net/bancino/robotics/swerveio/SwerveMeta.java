package net.bancino.robotics.swerveio;

import java.util.Map;
import java.util.List;

import net.bancino.robotics.swerveio.si.ChassisDimension;
import net.bancino.robotics.swerveio.gyro.AbstractGyro;
import net.bancino.robotics.swerveio.module.AbstractSwerveModule;

/**
 * A swerve-meta interface that holds things required for
 * the creation of a Swerve Drive. The SwerveDrive object
 * takes an implementation of this interface.
 * 
 * @author Jordan Bancino
 * @version 2.0.0
 * @since 2.0.0
 */
public interface SwerveMeta {

    /**
     * Name your swerve drive, if you want. This really doesn't
     * do much, but is helpful in identifying your SwerveMeta
     * instances if you've got more than one.
     *
     * @return The name of the swerve drive.
     */
    public String name();

    /**
     * Get the dimensions of the chassis.
     *
     * @return a ChassisDimension object representing the dimensions
     *         of the chassis of this swerve drive.
     */
    public ChassisDimension chassisDimensions();

    /**
     * Get the number of encoder counts it takes to go one 
     * pivot revolution.
     *
     * @return The number of encoder counts it takes to rotate the
     *         wheel one full revolution.
     */
    public double countsPerPivotRevolution();

    /**
     * Create a module map.
     * 
     * @return A map containing a swerve module for each of the values in the
     *         enumeration. This can be any type of map.
     */
    public Map<SwerveModule, AbstractSwerveModule> moduleMap();

    /**
     * Get a gyro to be used for field centric navigation.
     * 
     * @return A gyro implementation for the field centric
     *         navigation, or null if you don't want to use
     *         field centric drive.
     */
    public AbstractGyro gyro();

    /**
     * Apply some modifications to a swerve module. This is called on each
     * module in the module map.
     * 
     * @param module The module to modify. In Java, modules are passed by reference,
     *               so nothing needs to be returned.
     */
    public void modifyModule(AbstractSwerveModule module);

    /**
     * Though it isn't strictly required, it can sometimes be nice to have
     * a set block for initialization settings. This function can be used
     * for such settings. For instance, you can set the idle angle, or add
     * loggers, or perform any other functionality on the swerve drive object
     * that is not a part of the constructor or the SwerveMeta interface.
     *
     * @param swerve The swerve drive that is being constructed. When you pass
     *               the SwerveMeta implementation into the constructor of
     *               SwerveDrive, it will call this method on itself. So, in a sense,
     *               this function is really a callback that is called after all
     *               other construction is complete.
     */
    public void initialize(SwerveDrive swerve);

    
    public default List<SwerveFlag> applyFlags() {
        return null;
    }
}