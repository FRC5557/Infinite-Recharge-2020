package net.bancino.robotics.swerveio;

/**
 * SwerveFlags are flags that can be applied to a swerve drive.
 * These flags are not necessarily considered features, but they
 * may or may not be helpful in debugging or trying experimental
 * features and whatnot. Features that are experimental and therefore
 * not enabled by default will be prefaced with ENABLE_ while features
 * that are enabled by default will be prefaced with DISABLE_. Note
 * that these flags are not intended for competition-ready robot code,
 * but rather for testing. Flags may or may not work well for each other,
 * and some flags may depend on other flags in indirect ways.
 *
 * @author Jordan Bancino
 * @version 2.0.2
 * @since 2.0.2
 */
public enum SwerveFlag {
    /**
     * The pivot optimize flag will enable an experimental feature that
     * will optimize the pivot motors in a way that won't rotate them
     * more than 90 degrees. Because this feature is unstable and still a
     * work in progress, it isn't enabled by default.
     */
    ENABLE_PIVOT_OPTIMIZE,

    /**
     * This flag replaces the idle-angle functionality. It is an experimental
     * feature that will remember the last pivot angle and stay there when the
     * joystick input is negligable, instead of snapping to the calculated angle
     * of zero.
     */
    ENABLE_PIVOT_LAST_ANGLE,

    /**
     * Disable the forward parameter of the swerve vector. This will
     * drive everything like normal, except it will pass in 0 for the
     * forward parameter when performing calculations. This can be helpful
     * in cases when you want to test something with a joystick or tune a loop
     * where you don't want joystick interference, but don't want to modify
     * your joystick command.
     */
    DISABLE_FWD,

    /**
     * Disable the strafe parameter of the swerve vector. This will
     * drive everything like normal, except it will pass in 0 for the
     * strafe parameter when performing calculations. This can be helpful
     * in cases when you want to test something with a joystick or tune a loop
     * where you don't want joystick interference, but don't want to modify
     * your joystick command.
     */
    DISABLE_STR,

    /**
     * Disable the rotational parameter of the swerve vector. This will
     * drive everything like normal, except it will pass in 0 for the
     * rotational parameter when performing calculations. This can be helpful
     * in cases when you want to test something with a joystick or tune a loop
     * where you don't want joystick interference, but don't want to modify
     * your joystick command.
     */
    DISABLE_RCW,

    /**
     * Disable the pivot reference setting. If this flag is enabled, the calculations
     * will still be performed, but the PID output will not be passed into the modules.
     */
    DISABLE_PIVOT,

    /**
     * Disable the drive reference setting. If this flag is enabled, the calculations
     * will still be performed, but the output will not be passed into the modules.
     */
    DISABLE_DRIVE
}