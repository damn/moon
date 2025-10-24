package space.earlygrey.shapedrawer.shapes;

import space.earlygrey.shapedrawer.ShapeDrawer;
import space.earlygrey.shapedrawer.shapes.Shape.Arc;
import space.earlygrey.shapedrawer.shapes.Shape.Line;
import space.earlygrey.shapedrawer.shapes.Shape.OutlinedCircle;
import space.earlygrey.shapedrawer.shapes.Shape.OutlinedEllipse;
import space.earlygrey.shapedrawer.shapes.Shape.OutlinedPolygon;
import space.earlygrey.shapedrawer.shapes.Shape.OutlinedRectangle;
import space.earlygrey.shapedrawer.shapes.Shape.OutlinedRegularPolygon;
import space.earlygrey.shapedrawer.shapes.Shape.OutlinedTriangle;
import space.earlygrey.shapedrawer.shapes.Shape.PolyLine;

public class Pen extends Utensil {

    final BasicLine LINE;

    public Pen(ShapeDrawer drawer) {
        super(drawer);
        LINE = new BasicLine(drawer);
    }

    @Override
    boolean filled() {
        return false;
    }

    public Line<?> line() {
        LINE.reset(false);
        return LINE;
    }

    public PolyLine<?> polyLine() {
        POLYLINE.reset(false);
        POLYLINE.setOpen(true);
        return POLYLINE;
    }

    @Override
    public OutlinedPolygon<?> polygon() {
        POLYLINE.reset(filled());
        return POLYLINE;
    }

    @Override
    public OutlinedRegularPolygon<?> regularPolygon() {
        REGULAR_POLYGON.reset(false);
        return REGULAR_POLYGON;
    }

    @Override
    public OutlinedCircle<?> circle() {
        CIRCLE.reset(false);
        return CIRCLE;
    }

    @Override
    public OutlinedEllipse<?> ellipse() {
        ELLIPSE.reset(false);
        return ELLIPSE;
    }

    @Override
    public OutlinedRectangle<?> rectangle() {
        RECTANGLE.reset(false);
        return RECTANGLE;
    }

    public Arc<?> arc() {
        ARC.reset(false);
        return ARC;
    }

    @Override
    public OutlinedTriangle<?> triangle() {
        TRIANGLE.reset(false);
        return TRIANGLE;
    }

}
