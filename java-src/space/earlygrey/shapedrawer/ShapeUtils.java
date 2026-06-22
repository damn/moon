package space.earlygrey.shapedrawer;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

public class ShapeUtils {

    public static final float EPSILON = 0.001f;
    public static final float PI = MathUtils.PI;
    public static final float PI2 = (float) (2.*Math.PI);
    public static final float PI_2 = (float) (Math.PI / 2.);
    public static final float PI_4 = (float) (Math.PI / 4.);
    public static final float SQRT2 = (float) Math.sqrt(2.);
    public static final float SQRT3 = (float) Math.sqrt(3.);

    public interface LineWidthFunction {
        float getWidth(int i, float t);
    }

    public static class ConstantLineWidth implements LineWidthFunction {

        float width;

        @Override
        public float getWidth(int i, float t) {
            return width;
        }

        public ConstantLineWidth width(float width) {
            this.width = width;
            return this;
        }

    }


    public static float snap(float a, float pixelSize, float halfPixelSize) {
        return  (Math.round(a / pixelSize) * pixelSize) + halfPixelSize;
    }

    public static boolean epsilonEquals(float a, float b) {
        return Math.abs(a-b) < EPSILON;
    }

    public static float normaliseAngleToPositive(float angle) {
        angle =  angle % PI2;
        angle = (angle + PI2) % PI2;
        return angle;
    }

    public static float floor(float x, float interval) {
        return (float) (Math.floor(x / interval) * interval);
    }

    public static float ceil(float x, float interval) {
        return (float) (Math.ceil(x / interval) * interval);
    }

    public static float angleRad (Vector2 v, Vector2 reference) {
        return (float) Math.atan2(reference.x * v.y - reference.y * v.x, v.x * reference.x + v.y * reference.y);
    }

    static float pathLength(float[] path) {
        if (path.length < 4) return 0;
        float l = 0;
        for (int i = 0; i < path.length-4; i+=2) {
            l += Vector2.dst(path[i], path[i+1], path[i+2], path[i+3]);
        }
        return l;
    }

}
