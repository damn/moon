package space.earlygrey.shapedrawer.shapes;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;

import space.earlygrey.shapedrawer.JoinType;
import space.earlygrey.shapedrawer.ShapeDrawer;
import space.earlygrey.shapedrawer.shapes.Shape.OutlinedEllipse;

public class BasicEllipse extends BasicShape implements OutlinedEllipse {

    Vector2 centre;

    float radiusX, radiusY, rotation;

    protected BasicEllipse(ShapeDrawer drawer) {
        super(drawer);
        centre = new Vector2();
    }

    @Override
    void reset(boolean filled) {
        super.reset(filled);
        centre.setZero();
        radiusX = 1;
        radiusY = 1;
        rotation = 0;
    }

    @Override
    public BasicEllipse color(Color color) {
        this.color = color.toFloatBits();
        return this;
    }

    @Override
    public BasicEllipse centre(float x, float y) {
        this.centre.set(x, y);
        return this;
    }

    @Override
    public BasicEllipse centre(Vector2 centre) {
        this.centre.set(centre);
        return this;
    }

    @Override
    public BasicEllipse radiusX(float radiusX) {
        this.radiusX = radiusX;
        return this;
    }

    @Override
    public BasicEllipse radiusY(float radiusY) {
        this.radiusY = radiusY;
        return this;
    }

    @Override
    public BasicEllipse rotation(float rotation) {
        this.rotation = rotation;
        return this;
    }

    @Override
    public BasicEllipse rotate(float rotation) {
        this.rotation += rotation;
        return this;
    }

    @Override
    public BasicEllipse joinType(JoinType joinType) {
        this.joinType = joinType;
        return this;
    }

    @Override
    public BasicEllipse lineWidth(float width) {
        setLineWidth(width);
        return this;
    }

    @Override
    public void draw() {

        float c = drawer.setColor(color);
        if (filled) {
            drawer.filledEllipse(centre.x, centre.y, radiusX, radiusY, rotation);
        } else {
            drawer.ellipse(centre.x, centre.y, radiusX, radiusY, rotation, lineWidth.getWidth(0, 0), joinType);
        }
        drawer.setColor(c);

    }
}
