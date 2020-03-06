package net.bancino.robotics.swerveio.si;

/**
 * A shortcut for creating a square chassis dimension.
 *
 * @author Jordan Bancino
 * @version 2.0.0
 * @since 2.0.0
 */
public class SquareChassis extends ChassisDimension {
    public SquareChassis(Length sideLength) {
        super(sideLength, sideLength);
    }
}