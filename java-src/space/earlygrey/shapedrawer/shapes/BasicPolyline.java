package space.earlygrey.shapedrawer.shapes;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.EarClippingTriangulator;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.FloatArray;
import com.badlogic.gdx.utils.ShortArray;

import space.earlygrey.shapedrawer.JoinType;
import space.earlygrey.shapedrawer.ShapeDrawer;
import space.earlygrey.shapedrawer.ShapeUtils.LineWidthFunction;
import space.earlygrey.shapedrawer.shapes.Shape.PolyLine;

public class BasicPolyline extends BasicShape<BasicPolyline> implements PolyLine<BasicPolyline> {

    FloatArray vertices;

    float offsetX, offsetY, scaleX, scaleY;

    boolean open;

    EarClippingTriangulator triangulator = new EarClippingTriangulator();

    BasicPolyline(ShapeDrawer drawer) {
        super(drawer);
        vertices = new FloatArray();
    }

    @Override
    void reset(boolean filled) {
        super.reset(filled);
        vertices.clear();
        open = false;
        offsetX = 0;
        offsetY = 0;
        scaleX = 1;
        scaleY = 1;
    }

    public BasicPolyline setOpen(boolean open) {
        this.open = open;
        return this;
    }

    @Override
    public BasicPolyline color(Color color) {
        this.color = color.toFloatBits();
        return this;
    }

    @Override
    public BasicPolyline joinType(JoinType joinType) {
        this.joinType = joinType;
        return this;
    }

    @Override
    public BasicPolyline lineWidth(float width) {
        setLineWidth(width);
        return this;
    }

    @Override
    public BasicPolyline lineWidth(LineWidthFunction width) {
        this.lineWidth = width;
        return this;
    }

    @Override
    public <T extends Vector2> BasicPolyline vertices(Iterable<T> points) {
        this.vertices.clear();
        points.forEach(p -> this.vertices.add(p.x, p.y));
        return this;
    }

    @Override
    public BasicPolyline vertices(FloatArray points) {
        this.vertices.clear();
        this.vertices.addAll(points);
        return this;
    }

    @Override
    public BasicPolyline vertices(float[] points) {
        this.vertices.clear();
        this.vertices.addAll(points, 0, points.length);
        return this;
    }

    @Override
    public BasicPolyline addVertex(float x, float y) {
        this.vertices.add(x, y);
        return this;
    }

    @Override
    public BasicPolyline addVertex(Vector2 vertex) {
        return addVertex(vertex.x, vertex.y);
    }

    @Override
    public BasicPolyline offset(float x, float y) {
        offsetX = x;
        offsetY = y;
        return this;
    }

    @Override
    public BasicPolyline scale(float x, float y) {
        scaleX = x;
        scaleY = y;
        return this;
    }

    @Override
    public void draw() {
        if (filled) {
            ShortArray triangles = triangulator.computeTriangles(vertices.items, 0, vertices.size);
            draw(() -> drawer.filledPolygon(vertices.items, triangles.items, triangles.size, offsetX, offsetY, scaleX, scaleY));
        } else {
            draw(() -> drawer.path(vertices.items, 0, vertices.size, lineWidth, joinType, open, offsetX, offsetY, scaleX, scaleY));
        }
    }

}

