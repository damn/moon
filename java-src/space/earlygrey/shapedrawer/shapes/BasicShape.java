package space.earlygrey.shapedrawer.shapes;

import com.badlogic.gdx.graphics.Color;

import space.earlygrey.shapedrawer.JoinType;
import space.earlygrey.shapedrawer.ShapeDrawer;
import space.earlygrey.shapedrawer.ShapeUtils.ConstantLineWidth;
import space.earlygrey.shapedrawer.ShapeUtils.LineWidthFunction;

public abstract class BasicShape<T extends BasicShape> {

    final ShapeDrawer drawer;

    final ConstantLineWidth CONSTANT_LINE_WIDTH = new ConstantLineWidth();
    LineWidthFunction lineWidth;
    JoinType joinType;

    float color;

    boolean filled;

    BasicShape(ShapeDrawer drawer) {
        this.drawer = drawer;
    }

    void reset(boolean filled) {
        this.filled = filled;
        lineWidth = CONSTANT_LINE_WIDTH.width(drawer.getDefaultLineWidth());
        joinType = JoinType.POINTY;
        color = drawer.getPackedColor();
    }

    void draw(Runnable drawCall) {
        float c = drawer.setColor(color);
        drawCall.run();
        drawer.setColor(c);
    }

    void setLineWidth(float width) {
        lineWidth = CONSTANT_LINE_WIDTH.width(width);
    }

    abstract T joinType(JoinType joinType);

    abstract T lineWidth(float width);

    abstract T color(Color color);

}
