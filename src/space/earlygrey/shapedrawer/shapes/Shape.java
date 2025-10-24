package space.earlygrey.shapedrawer.shapes;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.FloatArray;

import space.earlygrey.shapedrawer.JoinType;
import space.earlygrey.shapedrawer.ShapeUtils.LineWidthFunction;

public interface Shape<T extends Shape> {

    void draw();

    T color(Color color);

    interface FilledCircle<T extends FilledCircle<T>> extends Shape<T> {

        T centre(float x, float y);

        T centre(Vector2 centre);

        T radius(float radius);

    }

    interface OutlinedCircle<T extends OutlinedCircle<T>> extends FilledCircle<OutlinedCircle<T>> {
        OutlinedCircle joinType(JoinType joinType);

        OutlinedCircle lineWidth(float width);

    }

    interface FilledEllipse<T extends FilledEllipse<T>> extends Shape<T> {

        T centre(float x, float y);

        T centre(Vector2 centre);

        T radiusX(float radiusX);

        T radiusY(float radiusY);

        T rotation(float rotation);

        T rotate(float rotation);

    }

    interface OutlinedEllipse<T extends OutlinedEllipse<T>> extends FilledEllipse<OutlinedEllipse<T>> {

        OutlinedEllipse joinType(JoinType joinType);

        OutlinedEllipse lineWidth(float width);

    }

    interface Line<T extends Line<T>> extends Shape<Line<T>> {

        Line from(float x, float y);

        Line to(float x, float y);

        Line from(Vector2 from);

        Line to(Vector2 to);

        Line joinType(JoinType joinType);

        Line lineWidth(float width);

        Line startColor(Color startColor);

        Line endColor(Color endColor);

        Line snap(boolean snap);

    }

    interface FilledTriangle<T extends FilledTriangle<T>> extends Shape<T> {

        T a(float x, float y);

        T b(float x, float y);

        T c(float x, float y);

        T a(Vector2 a);

        T b(Vector2 b);

        T c(Vector2 c);
    }

    interface OutlinedTriangle<T extends OutlinedTriangle<T>> extends FilledTriangle<OutlinedTriangle<T>> {
        OutlinedTriangle joinType(JoinType joinType);

        OutlinedTriangle lineWidth(float width);

    }

    interface FilledPolygon<T extends FilledPolygon<T>> extends Shape<T> {

        <V extends Vector2> T vertices(Iterable<V> points);

        T vertices(FloatArray points);

        T vertices(float[] points);

        T addVertex(float x, float y);

        T addVertex(Vector2 vertex);

        T offset(float x, float y);

        T scale(float x, float y);

    }

    interface OutlinedPolygon<T extends OutlinedPolygon<T>> extends FilledPolygon<T> {

        T joinType(JoinType joinType);

        T lineWidth(float width);

    }

    interface PolyLine<T extends PolyLine<T>> extends OutlinedPolygon<PolyLine<T>> {
        PolyLine lineWidth(LineWidthFunction width);
    }

    interface FilledRegularPolygon<T extends FilledRegularPolygon<T>> extends Shape<T> {

        T centre(Vector2 centre);

        T radiusX(float radiusX);

        T radiusY(float radiusY);

        T radius(float radius);

        T rotation(float rotation);

        T rotate(float rotation);

        T sides(int sides);
    }

    interface OutlinedRegularPolygon<T extends OutlinedRegularPolygon<T>> extends FilledRegularPolygon<OutlinedRegularPolygon<T>> {

        OutlinedRegularPolygon joinType(JoinType joinType);

        OutlinedRegularPolygon lineWidth(float width);
    }

    interface Sector<T extends Sector<T>> extends Shape<T> {

        T centre(float x, float y);

        T centre(Vector2 centre);

        T radius(float radius);

        T startAngle(float startAngle);

        T radians(float radians);

        T sides(int sides);

    }

    interface Arc<T extends Arc<T>> extends Sector<Arc<T>> {
        Arc joinType(JoinType joinType);

        Arc lineWidth(float width);
    }

    interface FilledRectangle<T extends FilledRectangle<T>> extends Shape<T> {

        T position(float x, float y);

        T position(Vector2 position);

        T size(float width, float height);

        T rotation(float rotation);

        T rotate(float rotation);

    }

    interface OutlinedRectangle<T extends OutlinedRectangle<T>> extends FilledRectangle<OutlinedRectangle<T>> {

        OutlinedRectangle joinType(JoinType joinType);

        OutlinedRectangle lineWidth(float width);
    }


}
