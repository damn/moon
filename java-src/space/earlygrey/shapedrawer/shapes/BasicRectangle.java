package space.earlygrey.shapedrawer.shapes;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;

import space.earlygrey.shapedrawer.JoinType;
import space.earlygrey.shapedrawer.ShapeDrawer;
import space.earlygrey.shapedrawer.shapes.Shape.OutlinedRectangle;

class BasicRectangle extends BasicShape<BasicRectangle> implements OutlinedRectangle {

    float x, y, width, height, rotation;


    BasicRectangle(ShapeDrawer drawer) {
        super(drawer);
    }

    @Override
    void reset(boolean filled) {
        super.reset(filled);
        x = 0;
        y = 0;
        width = 1;
        height = 1;
        rotation = 0;
    }

    @Override
    public BasicRectangle color(Color color) {
        this.color = color.toFloatBits();
        return this;
    }

    @Override
    public BasicRectangle joinType(JoinType joinType) {
        this.joinType = joinType;
        return this;
    }

    @Override
    public BasicRectangle lineWidth(float width) {
        setLineWidth(width);
        return this;
    }

    @Override
    public OutlinedRectangle position(float x, float y) {
        this.x = x;
        this.y = y;
        return this;
    }

    @Override
    public OutlinedRectangle position(Vector2 position) {
        return position(position.x, position.y);
    }

    @Override
    public OutlinedRectangle size(float width, float height) {
        this.width = width;
        this.height = height;
        return this;
    }

    @Override
    public OutlinedRectangle rotation(float rotation) {
        this.rotation = rotation;
        return this;
    }

    @Override
    public OutlinedRectangle rotate(float rotation) {
        this.rotation += rotation;
        return this;
    }

    @Override
    public void draw() {
        if (filled) {
            draw(() -> drawer.filledRectangle(x, y, width, height, rotation));
        } else {
            draw(() -> drawer.rectangle(x, y, width, height, lineWidth.getWidth(0, 0), rotation, joinType));
        }
    }
}
