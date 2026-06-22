package space.earlygrey.shapedrawer.shapes;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

import space.earlygrey.shapedrawer.JoinType;
import space.earlygrey.shapedrawer.ShapeDrawer;
import space.earlygrey.shapedrawer.shapes.Shape.Arc;

public class BasicArc extends BasicShape<BasicArc> implements Arc {

    Vector2 centre;

    float radius, startAngle, radians;
    int sides;

    protected BasicArc(ShapeDrawer drawer) {
        super(drawer);
        centre = new Vector2();
    }

    @Override
    void reset(boolean filled) {
        super.reset(filled);
        centre.setZero();
        radians = MathUtils.PI;
        startAngle = 0;
        radius = 1;
        sides = 6;
    }

    @Override
    public BasicArc color(Color color) {
        this.color = color.toFloatBits();
        return this;
    }

    @Override
    public BasicArc joinType(JoinType joinType) {
        this.joinType = joinType;
        return this;
    }

    @Override
    public BasicArc lineWidth(float width) {
        setLineWidth(width);
        return this;
    }

    @Override
    public BasicArc centre(float x, float y) {
        this.centre.set(x, y);
        return this;
    }

    @Override
    public BasicArc centre(Vector2 centre) {
        this.centre.set(centre);
        return this;
    }

    @Override
    public BasicArc radius(float radius) {
        this.radius = radius;
        return this;
    }

    @Override
    public BasicArc startAngle(float startAngle) {
        this.startAngle = startAngle;
        return this;
    }

    @Override
    public BasicArc radians(float radians) {
        this.radians = radians;
        return this;
    }

    @Override
    public BasicArc sides(int sides) {
        this.sides = sides;
        return this;
    }

    @Override
    public void draw() {
        if (filled) {
            draw(() -> drawer.sector(centre.x, centre.y, radius, startAngle, radians, sides));
        } else {
            draw(() -> drawer.arc(centre.x, centre.y, radius, startAngle, radians, lineWidth.getWidth(0, 0), joinType, sides));
        }
    }

}
