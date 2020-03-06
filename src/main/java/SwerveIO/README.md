# SwerveIO

Pronounced "Swerve - ee - oh" (rhymes with oreo, we decided to keep this mis-pronunciation by one of our members because we thought it was funny and different), **SwerveIO** is another swerve drive library written in Java by Team 6090 and other contributers, just to see if we could. SwerveIO aims to be as simple and easy to use as possible, handing all the hard programming. SwerveIO intends to be simple, yet also powerful, so that rookies and veterans alike can get the most out of their swerve drive as possible. Everything in this library can be extended and built upon to fit the widest variety of needs.

**Notice**: This library is under _heavy_ developement. Often, there are lots of breaking API changes with new versions. Additionally, this library is not published to any repositories other than this one, so you'll need to include it as a source dependency in gradle, or download the JAR from the packages page.

## Table Of Contents
- [SwerveIO](#swerveio)
  - [Table Of Contents](#table-of-contents)
  - [Features](#features)
  - [Knowns](#knowns)
    - [Working](#working)
    - [Not Working](#not-working)
  - [Basic Usage](#basic-usage)
  - [Advanced Usage](#advanced-usage)

## Features

- **Expandable**: A collection of interfaces allow the use of any motor controllers and encoders, with the option to use a combination of motor controllers and encoders.
- **Sensible Defaults**: SwerveIO provides built-in swerve module implementations for popular configurations, including REVRobotics Spark Max, and CTRE Talon motor controllers. If Team 6090 has experience with it, we will have an implementation for you. You're also more than welcome to add your own module implementations to our library via Github pull requests! Please make sure that you don't add custom modules, but if it's a kit, by all means, we want it!
- **Java**: Written in Java by Java developers, SwerveIO takes advantage of the Java language and follows all the conventions of Java libraries. This makes for seamless integration with your Java robotics projects.
- **Open**: All the classes used in this library are open and can be used independently if desired, such as `SwerveDriveCalculator` or the PID loop calculator (`MiniPID`).
- **Simple**: All the hard work is done beneath the abstraction layer of this library, all you need to do is pass encoders and motors in the form of modules to the library.

- **Automatic Control**: Just input your swerve drive specs. SwerveIO performs all the calculations needed for driving, and runs it's own PID loop.
- **Advanced Logging API**: SwerveIO uses the `SwerveLogger` interface to log the complete state of the swerve drive as it moves. Implementing this interface allows you to output data to either a file, the NetworkTables, a database, a server, or more! Default implementations include:
  - **SmarktDashboard Logger**: Log all swerve data to the dashboard so that they can be viewed live as you're running.
  - **CSV Logger**: Log swerve data to a csv file that can then be graphed if desired.
  - **Build your own**: Take a look at the default implementations to give you some inspiration, then write your own logger, and if you'd like, submit it via a pull request!

## Support

At the moment, the currently officially supported swerve drive modules are listed here:

- Swerve Drive Specialties' MK2 Module

The currently supported encoders are listed here:

- REVRobotics Spark Max (Both internal and alternate)
- Analog input encoders that come with the MK2 Swerve Modules.

Basic unofficial support can be achieved by implementing `AbstractEncoder` (or using one of our current encoder implementations) and passing two motor controllers and their encoders into a `GenericSwerveModule`.

## Basic Usage

To include SwerveIO in your robot project, you can either:

1. Use the WPILib VSCode extension to install the vendor libraries. Go to your WPILib command palette, select "Manage Vendor Libraries" and install a new library using the "online" install method. Input the URL below.
2. Download the vendor library JSON file located at the URL below, then drop it into your `vendordeps` folder.

Vendor dependency URL : `https://team6090.bancino.net/Team6090.json`

If you are using the Gradle Wrapper and your IDE does not download the dependencies automatically, you may need to run the command `./gradlew build` inside the project directory. Otherwise, run `gradle build`. You may need to refresh your IDE or reload the build/classpath configuration.

---

This library provides a `SwerveDrive` class that extends WPILib's `Subsystem` class. You'll want your drivetrain subsystem to extend `SwerveDrive` which will then automatically inherit `Subsystem`. For our code, we generally follow the below format. Note that this *is not* a copy-paste solution, but rather
just a reference. The idea is that you'll want to extend `SwerveDrive`, and pass in an implementation of
`SwerveMeta` to the superclass constructor. You can put the `SwerveMeta` implementation in it's own
class if you want, but we just use an annonymous class, to keep everything in the same place.

[Example SwerveIO Project using SwerveDriveSpecialties MK2 Kit](https://github.com/Team6090/SwerveIOTestBase)

The above example example creates all the modules and passes them to the superclass, which has a default implementation of the `drive()` function responsible for handing everything. To drive this swerve drive, just pass the joysticks Y, X and Z values into a `SwerveVector` for `drive()`. This is of course very bare-bones, but this will get the job done. A gyro is not required for operation, but is highly recommended. If you pass in a gyro, field-centric drive will automatically be enabled. You can disable it using `setFieldCentric(false)`.

If your swerve module does not have a default implementation, just instantiate the `GenericSwerveModule` class with speed controllers and encoders. This will be the most basic way to do it, but advanced programmers may want to extend `GenericSwerveModule`, or even directly implement `AbstractSwerveModule`. See the `MK2SwerveModule` class for inspiration. 

You can simply instantiate `GenericSwerveModule` with any speed controller of your choice, as long as you set up the proper encoders that go with them. For this, you may need to implemement the `AbstractEncoder` interface, which would probably be easier than implementing all of `AbstractSwerveModule` or even extending `GenericSwerveModule`.

As you can see, to create a fully functioning swerve drive subsystem, you just need to extend the `SwerveDrive` class, and know these values:

- The base width
- The base length
- How many counts on the encoder it takes to go one full pivot revolution. This will usually be done by manually twisting a module and watching the counts. Or, if you are using a 1:1 encoder, which is highly recommended, knowing how many counts a full revolution of the encoder shaft is is all you need.

If you're writing your own module implementation, you'll also need to know:

- The diameter of the wheel
- The maximum output in RPMs of your drive motor
- The drive gear ratio

These are all required for calculating the velocity of the swerve drive.

To drive the example drivetrain above, advanced users will probably want to write your own joystick command, but if you'd like,
you can use our competition-ready, minimal joystick command, just call this in your `RobotContainer`:

```java
swerveDrive.setDefaultCommand(new SwerveDriveTeleop(swerveDrive, xbox0, XboxController.Axis.kLeftY, XboxController.Axis.kLeftX, XboxController.Axis.kRightX));
```

Where `swerveDrive` is your drivetrain object, and `xbox0` is your XBox joystick. The extra parameters are the forward, strafe, and rotational axes of the joystick to use (respectively). You can set these axes to any axis on the controller, but this configuration is recommended.

## Advanced Usage

The examples shown here only scratch the surface of what you can do with SwerveIO. Additional resources include:

- Our SwerveIO Test Base: [SwerveIO Test Base](https://github.com.Team6090/SwerveIOTestBase)
- Our 2020 code, which will use SwerveIO: [Infinite Recharge](https://github.com/Team6090/InfiniteRecharge)
- The SwerveIO JavaDoc documentation. You can build it yourself using `gradle javadoc`. You can find the documentation in the build folder (`build/docs/javadoc/index.html`). Or, check the releases page to download the documentation for the latest public release.
