package space.earlygrey.shapedrawer.shapes;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;

import space.earlygrey.shapedrawer.JoinType;
import space.earlygrey.shapedrawer.ShapeDrawer;
import space.earlygrey.shapedrawer.shapes.Shape.OutlinedCircle;

public class BasicCircle implements OutlinedCircle {

    private BasicEllipse ellipse;

    protected BasicCircle(ShapeDrawer drawer) {
        ellipse = new BasicEllipse(drawer);
    }

    void reset(boolean filled) {
        ellipse.reset(filled);
    }

    @Override
    public BasicCircle color(Color color) {
        ellipse.color(color);
        return this;
    }

    @Override
    public BasicCircle centre(float x, float y) {
        ellipse.centre(x, y);
        return this;
    }

    @Override
    public BasicCircle centre(Vector2 centre) {
        ellipse.centre(centre);
        return this;
    }

    @Override
    public BasicCircle radius(float radius) {
        ellipse.radiusX(radius);
        ellipse.radiusY(radius);
        return this;
    }

    @Override
    public BasicCircle joinType(JoinType joinType) {
        ellipse.joinType(joinType);
        return this;
    }

    @Override
    public BasicCircle lineWidth(float width) {
        ellipse.setLineWidth(width);
        return this;
    }

    @Override
    public void draw() {
        ellipse.draw();
    }
}
