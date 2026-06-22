package space.earlygrey.shapedrawer;

/**
 * The type of mitre joint used for connecting
 */
public enum JoinType {
    /**
     * No mitering is performed. This defaults to {@link ShapeDrawer#line(float, float, float, float, float, boolean)}
     * and is the fastest option.
     */
    NONE,

    /**
     * A standard mitre joint.
     */
    POINTY,

    /**
     * A truncated mitre joint.
     */
    SMOOTH
}
