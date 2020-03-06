package net.bancino.robotics.swerveio.si;

/**
 * A Chassis Dimension is an immutable object that stores two lengths.
 *
 * @author Jordan Bancino
 * @version 2.0.0
 * @since 2.0.0
 */
public class ChassisDimension {

    private final Length trackWidth, trackLength;

    /**
     * Construct a chassis dimension.
     *
     * @param trackWidth The track width of the chassis, measured from left to right.
     * @param trackLength The track length of the chassis, measured from front to back.
     */
    public ChassisDimension(Length trackWidth, Length trackLength) {
        if (trackWidth != null) {
            this.trackWidth = trackWidth;
        } else {
            throw new IllegalArgumentException("A chassis must have a track width.");
        }
        if (trackLength != null) {
            this.trackLength = trackLength;
        } else {
            throw new IllegalArgumentException("A chassis must have a track length.");
        }
    }

    /**
     * Get the track width of this chassis.
     * 
     * @return The width, measured from left to right.
     */
    public Length getWidth() {
        return trackWidth;
    }

    /**
     * Get the track length of this chassis.
     *
     * @return the lenght, measured from back to front.
     */
    public Length getLength() {
        return trackLength;
    }

    /**
     * Get the radius (used in angular velocity calculations) that this base will
     * produce when travelling in a circle.
     *
     * @return The radius, that is, the length from the center of the chassis to one of the corners.
     *         This will work even if the base isn't a square as well.
     */
    public Length getRadius() {
        double halfLength = trackLength.get(Unit.BASE_UNIT) / 2;
        double halfWidth = trackWidth.get(Unit.BASE_UNIT) / 2;
        double hypotenuse = Math.sqrt(Math.pow(halfLength, 2) + Math.pow(halfWidth, 2));
        return new Length(hypotenuse, Unit.BASE_UNIT);
    }
}