package space.earlygrey.shapedrawer;

import com.badlogic.gdx.math.EarClippingTriangulator;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.ShortArray;

/**
 * <p>Contains functions for calculating the vertex data for filled polygons.
 * Separate subclasses that use either a PolygonBatch or jsut a Batch.</p>
 *
 * @author earlygrey
 */

abstract class FilledPolygonDrawer<T extends BatchManager> extends DrawerTemplate<T> {

    static final EarClippingTriangulator triangulator = new EarClippingTriangulator();

    FilledPolygonDrawer(T batchManager, AbstractShapeDrawer drawer) {
        super(batchManager, drawer);
    }

    abstract void polygon(float centreX, float centreY, int sides, float radiusX, float radiusY, float rotation, float startAngle, float radians, float innerColor, float outerColor);

    abstract void polygon(float[] vertices, short[] triangles, int trianglesCount, float offsetX, float offsetY, float scaleX, float scaleY);

    void polygon(float[] vertices, short[] triangles, int trianglesCount) {
        polygon(vertices, triangles, trianglesCount, 0, 0, 1, 1);
    }

    void polygon(float[] vertices) {
        polygon(vertices, 0, vertices.length);
    }

    void polygon(float[] vertices, int offset, int count) {
        ShortArray triangles = triangulator.computeTriangles(vertices, offset, count);
        polygon(vertices, triangles);
    }

    void polygon(float[] vertices, ShortArray triangles) {
        polygon(vertices, triangles.items, triangles.size);
    }

    void polygon(float[] vertices, short[] triangles) {
        polygon(vertices, triangles, triangles.length);
    }

    void polygon(float[] vertices, short[] triangles, float offsetX, float offsetY) {
        polygon(vertices, triangles, triangles.length, offsetX, offsetY, 1, 1);
    }

    void rectangle(float x, float y, float width, float height, float rotation) {
        float c = batchManager.floatBits;
        rectangle(x, y, width, height, rotation, c, c, c, c);
    }

    void rectangle(float x, float y, float width, float height, float rotation, float c1, float c2, float c3, float c4) {
        boolean caching = batchManager.isCachingDraws();
        batchManager.ensureSpaceForQuad();
        float cos = (float) Math.cos(rotation), sin = (float) Math.sin(rotation);
        float halfWidth = 0.5f * width, halfHeight = 0.5f * height;
        float centreX = x + halfWidth, centreY = y + halfHeight;
        x1(halfWidth * cos - halfHeight * sin + centreX);
        y1(halfWidth * sin + halfHeight * cos + centreY);
        x2(-halfWidth * cos - halfHeight * sin + centreX);
        y2(-halfWidth * sin + halfHeight * cos + centreY);
        x3(-halfWidth * cos - (-halfHeight * sin) + centreX);
        y3(-halfWidth * sin + (-halfHeight * cos) + centreY);
        x4(halfWidth * cos - (-halfHeight * sin) + centreX);
        y4(halfWidth * sin + (-halfHeight * cos) + centreY);
        color(c1,c2,c3,c4);
        batchManager.pushQuad();
        if (!caching) batchManager.pushToBatch();
    }

    public void triangle(float x1, float y1, float x2, float y2, float x3, float y3, float color1, float color2, float color3) {
        boolean caching = batchManager.isCachingDraws();
        vert1(x1, y1);
        vert2(x2, y2);
        vert3(x3, y3);
        color(color1, color2, color3);
        batchManager.pushTriangle();
        if (!caching) batchManager.pushToBatch();
    }

    static class BatchFilledPolygonDrawer extends FilledPolygonDrawer<BatchManager> {

        BatchFilledPolygonDrawer(BatchManager batchManager, AbstractShapeDrawer drawer) {
            super(batchManager, drawer);
        }

        @Override
        void polygon(float centreX, float centreY, int sides, float radiusX, float radiusY, float rotation, float startAngle, float radians, float innerColor, float outerColor) {
            if (radians==0) return;
            radians = Math.min(radians, ShapeUtils.PI2);

            boolean wasCaching = batchManager.startCaching();

            float angleInterval = MathUtils.PI2 / sides;
            float endAngle = startAngle + radians;

            float cos = (float) Math.cos(angleInterval), sin = (float) Math.sin(angleInterval);
            float cosRot = (float) Math.cos(rotation), sinRot = (float) Math.sin(rotation);

            int start = (int) Math.ceil(sides * (startAngle / ShapeUtils.PI2));
            int end = (int) Math.floor(sides * (endAngle / ShapeUtils.PI2)) + 1;

            if (ShapeUtils.epsilonEquals(start * angleInterval, startAngle)) start++;

            B.set(1, 0).rotateRad(startAngle).scl(radiusX, radiusY);


            int n = end-start+1;
            if (n<2) {
                // there are no "regular" segments, will never enter loop,
                // so just push the one triangle from start angle to end angle
                batchManager.ensureSpaceForTriangle();
                A.set(1, 0).rotateRad(startAngle).scl(radiusX, radiusY);
                B.set(1, 0).rotateRad(endAngle).scl(radiusX, radiusY);
                vert1(centreX, centreY);
                x2(A.x*cosRot-A.y*sinRot  + centreX);
                y2(A.x*sinRot+A.y*cosRot + centreY);
                x3(B.x*cosRot-B.y*sinRot  + centreX);
                y3(B.x*sinRot+B.y*cosRot + centreY);
                color(innerColor,outerColor,outerColor);
                batchManager.pushTriangle();
            } else {
                //prepare for regular segments
                dir.set(1, 0).rotateRad(Math.min(start * angleInterval, endAngle));
                C.set(dir).scl(radiusX, radiusY);
            }


            for (int i = 0; i < n-1; i++) {

                A.set(B);
                B.set(C);
                if (i<n-2) {
                    dir.set(dir.x * cos - dir.y * sin, dir.x * sin + dir.y * cos);
                    C.set(dir).scl(radiusX, radiusY);
                } else {
                    C.set(1, 0).rotateRad(endAngle).scl(radiusX, radiusY);
                }

                if (i%2==0) {
                    //skip every second triangle so that we can draw it as a quad with the next triangle
                    batchManager.ensureSpaceForQuad();
                    vert1(centreX, centreY);
                    x2(A.x*cosRot-A.y*sinRot  + centreX);
                    y2(A.x*sinRot+A.y*cosRot + centreY);
                    x3(B.x*cosRot-B.y*sinRot  + centreX);
                    y3(B.x*sinRot+B.y*cosRot + centreY);
                    x4(C.x*cosRot-C.y*sinRot  + centreX);
                    y4(C.x*sinRot+C.y*cosRot + centreY);
                    color(innerColor, outerColor, outerColor, outerColor);
                    batchManager.pushQuad();
                } else if (i==n-2) {
                    //draw final triangle
                    batchManager.ensureSpaceForTriangle();
                    C.set(1, 0).rotateRad(endAngle).scl(radiusX, radiusY);
                    vert1(centreX, centreY);
                    x2(B.x*cosRot-B.y*sinRot  + centreX);
                    y2(B.x*sinRot+B.y*cosRot + centreY);
                    x3(C.x*cosRot-C.y*sinRot  + centreX);
                    y3(C.x*sinRot+C.y*cosRot + centreY);
                    color(innerColor, outerColor, outerColor);
                    batchManager.pushTriangle();
                }
            }

            if (!wasCaching) batchManager.endCaching();
        }

        @Override
        void polygon(float[] vertices, short[] triangles, int trianglesCount, float x, float y, float scaleX, float scaleY) {
            float c = batchManager.floatBits;
            for (int i = 0; i < trianglesCount; i += 3) {
                batchManager.ensureSpaceForTriangle();
                vert1(scaleX * vertices[2 * triangles[i]] + x, scaleY * vertices[2 * triangles[i] + 1] + y);
                vert2(scaleX * vertices[2 * triangles[i + 1]] + x, scaleY * vertices[2 * triangles[i + 1] + 1] + y);
                vert3(scaleX * vertices[2 * triangles[i + 2]] + x, scaleY * vertices[2 * triangles[i + 2] + 1] + y);
                color(c, c, c);
                batchManager.pushTriangle();
            }
            batchManager.pushToBatch();
        }
    }

    static class PolygonBatchFilledPolygonDrawer extends FilledPolygonDrawer<PolygonBatchManager> {

        PolygonBatchFilledPolygonDrawer(PolygonBatchManager batchManager, AbstractShapeDrawer drawer) {
            super(batchManager, drawer);
        }

        @Override
        void polygon(float centreX, float centreY, int sides, float radiusX, float radiusY, float rotation, float startAngle, float radians, float innerColor, float outerColor) {
            if (radians==0) return;
            radians = Math.min(radians, ShapeUtils.PI2);

            boolean wasCaching = batchManager.startCaching();

            float angleInterval = MathUtils.PI2 / sides;
            float endAngle = startAngle + radians;

            float cos = (float) Math.cos(angleInterval), sin = (float) Math.sin(angleInterval);
            float cosRot = (float) Math.cos(rotation), sinRot = (float) Math.sin(rotation);

            int start = (int) Math.ceil(sides * (startAngle / ShapeUtils.PI2));
            int end = (int) Math.floor(sides * (endAngle / ShapeUtils.PI2)) + 1;

            if (ShapeUtils.epsilonEquals(start * angleInterval, startAngle)) start++;

            int n = end-start+1;
            batchManager.ensureSpace(n + 2);
            int vertexOffset = batchManager.getVerticesArrayIndex();

            //centre point - triangle index 0
            vert1(centreX, centreY);
            color1(innerColor);
            batchManager.pushVertex();

            //first perimeter vertex (at start angle) - triangle index 1
            A.set(1, 0).rotateRad(startAngle).scl(radiusX, radiusY);
            x1(A.x*cosRot-A.y*sinRot  + centreX);
            y1(A.x*sinRot+A.y*cosRot + centreY);
            color1(outerColor);
            batchManager.pushVertex();
            batchManager.pushTriangleIndices((short) vertexOffset, (short) (vertexOffset+1), (short) (vertexOffset+2));

            //loop through evenly spaced perimeter vertices
            dir.set(1, 0).rotateRad(Math.min(start * angleInterval, endAngle));
            A.set(dir).scl(radiusX, radiusY);
            for (int i = 0; i < n-1; i++) {
                x1(A.x*cosRot-A.y*sinRot  + centreX);
                y1(A.x*sinRot+A.y*cosRot + centreY);
                color1(outerColor);
                batchManager.pushVertex();
                dir.set(dir.x * cos - dir.y * sin, dir.x * sin + dir.y * cos);
                A.set(dir).scl(radiusX, radiusY);

                batchManager.pushTriangleIndices((short) vertexOffset, (short) (vertexOffset+i+2), (short) (vertexOffset+i+3));
            }

            //last perimeter vertex (at end angle) - triangle index n+1 (already included in loop)
            A.set(1, 0).rotateRad(endAngle).scl(radiusX, radiusY);
            x1(A.x*cosRot-A.y*sinRot  + centreX);
            y1(A.x*sinRot+A.y*cosRot + centreY);
            color1(outerColor);
            batchManager.pushVertex();

            if (!wasCaching) batchManager.endCaching();
        }

        @Override
        void polygon(float[] vertices, short[] triangles, int trianglesCount, float offsetX, float offsetY, float scaleX, float scaleY) {
            int n = vertices.length / 2;
            batchManager.ensureSpace(n);
            batchManager.pushVertexData(vertices, triangles, trianglesCount, batchManager.floatBits,
                    offsetX, offsetY, scaleX, scaleY);
            batchManager.pushToBatch();
        }

    }


}
