package space.earlygrey.shapedrawer.shapes;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;

import space.earlygrey.shapedrawer.JoinType;
import space.earlygrey.shapedrawer.ShapeDrawer;
import space.earlygrey.shapedrawer.shapes.Shape.OutlinedRegularPolygon;

class BasicRegularPolygon extends BasicShape<BasicRegularPolygon> implements OutlinedRegularPolygon {

    Vector2 centre;

    float scaleX, scaleY, rotation;

    int sides;

    protected BasicRegularPolygon(ShapeDrawer drawer) {
        super(drawer);
        centre = new Vector2();
    }

    @Override
    void reset(boolean filled) {
        super.reset(filled);
        centre.setZero();
        scaleX = 1;
        scaleY = 1;
        rotation = 0;
        sides = 6;
    }

    @Override
    public BasicRegularPolygon joinType(JoinType joinType) {
        this.joinType = joinType;
        return this;
    }

    @Override
    public BasicRegularPolygon lineWidth(float width) {
        setLineWidth(width);
        return this;
    }

    @Override
    public BasicRegularPolygon color(Color color) {
        this.color = color.toFloatBits();
        return this;
    }

    @Override
    public BasicRegularPolygon centre(Vector2 centre) {
        this.centre.set(centre);
        return this;
    }

    @Override
    public BasicRegularPolygon radiusX(float radiusX) {
        this.scaleX = radiusX;
        return this;
    }

    @Override
    public BasicRegularPolygon radiusY(float radiusY) {
        this.scaleY = radiusY;
        return this;
    }

    @Override
    public BasicRegularPolygon radius(float radius) {
        this.scaleX = radius;
        this.scaleY = radius;
        return this;
    }

    @Override
    public BasicRegularPolygon rotation(float rotation) {
        this.rotation = rotation;
        return this;
    }

    @Override
    public BasicRegularPolygon rotate(float rotation) {
        this.rotation += rotation;
        return this;
    }

    @Override
    public BasicRegularPolygon sides(int sides) {
        this.sides = sides;
        return this;
    }

    @Override
    public void draw() {
        float c = drawer.setColor(color);
        if (filled) {
            drawer.filledPolygon(centre.x, centre.y, sides, scaleX, scaleY, rotation);
        } else {
            drawer.polygon(centre.x, centre.y, sides, scaleX, scaleY, rotation, lineWidth.getWidth(0, 0), joinType);
        }
        drawer.setColor(c);
    }
}
