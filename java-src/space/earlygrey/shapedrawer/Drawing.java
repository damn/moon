package space.earlygrey.shapedrawer;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;

/**
 * Holds a cache of shapes drawn with a {@link ShapeDrawer}, which can be drawn at any time without having to perform the shape calculations again.
 * @author earlygrey
 */

public class Drawing {

    BatchManager batchManager;
    Array<float[]> vertexBatches;

    private float[] tmpVertices;

    private float offsetX, offsetY, scaleX = 1, scaleY = 1;

    Drawing(BatchManager batchManager) {
        this.batchManager = batchManager;
        vertexBatches = new Array<>();
    }

    public void draw() {
        draw(offsetX, offsetY);
    }

    public void draw(float x, float y) {
        draw(x, y, scaleX, scaleY);
    }

    /**
     * Draw the cached shapes to the screen. Must be used in between {@link Batch#begin()} and {@link Batch#end()}.
     */
    public void draw(float x, float y, float scaleX, float scaleY) {
        if (batchManager.r == null) throw new IllegalStateException("The texture region is null. Please set a texture region first (e.g. in the constructor or by calling setTextureRegion(TextureRegion region))");
        for (int i = 0; i < vertexBatches.size; i++) {
            float[] vertices = vertexBatches.get(i);
            getBatchManager().getBatch().draw(batchManager.r.getTexture(), applyTransformation(vertices, x, y, scaleX, scaleY), 0, vertices.length);
        }
    }

    void pushVertices(float[] vertices, int count) {
        float[] copy = new float[count];
        System.arraycopy(vertices, 0, copy, 0, count);
        vertexBatches.add(copy);
    }

    void finalise() {
        vertexBatches.setSize(vertexBatches.size);
        int max = 0;
        for (int i = 0; i < vertexBatches.size; i++) {
            float[] vertices = vertexBatches.get(i);
            if (vertices.length > max) {
                max = vertices.length;
            }
        }
        tmpVertices = new float[max];
    }

    float[] applyTransformation(float[] vertices, float x, float y, float scaleX, float scaleY) {
        if (!needsTransforming(x, y, scaleX, scaleY)) {
            return vertices;
        }
        for (int i = 0; i < vertices.length; i+= BatchManager.VERTEX_SIZE) {
            tmpVertices[i] = x + scaleX * vertices[i];
            tmpVertices[i+1] = y + scaleY * vertices[i+1];
            tmpVertices[i+2] = vertices[i+2];
            tmpVertices[i+3] = vertices[i+3];
            tmpVertices[i+4] = vertices[i+4];
        }
        return tmpVertices;
    }

    boolean needsTransforming(float x, float y, float scaleX, float scaleY) {
        return x != 0 || y != 0 || scaleX != 1 ||scaleY != 1;
    }

    BatchManager getBatchManager() {
        return batchManager;
    }

    public float getOffsetX() {
        return offsetX;
    }

    public void setOffset(float offsetX, float offsetY) {
        this.offsetX = offsetX;
        this.offsetY = offsetY;
    }

    public float getOffsetY() {
        return offsetY;
    }

    public float getScaleX() {
        return scaleX;
    }

    public void setScaleX(float scaleX, float scaleY) {
        this.scaleX = scaleX;
        this.scaleY = scaleY;
    }

    public float getScaleY() {
        return scaleY;
    }

    static class PolygonDrawing extends Drawing {

        Array<short[]> triangleBatches;

        PolygonDrawing(PolygonBatchManager batchManager) {
            super(batchManager);
            triangleBatches = new Array<>();
        }

        @Override
        public void draw(float x, float y, float scaleX, float scaleY) {
            if (batchManager.r == null) throw new IllegalStateException("The texture region is null. Please set a texture region first (e.g. in the constructor or by calling setTextureRegion(TextureRegion region))");
            for (int i = 0; i < vertexBatches.size; i++) {
                float[] vertices = vertexBatches.get(i);
                short[] triangles = triangleBatches.get(i);
                getBatchManager().getBatch().draw(batchManager.r.getTexture(), applyTransformation(vertices, x, y, scaleX, scaleY), 0, vertices.length, triangles, 0, triangles.length);
            }
        }

        @Override
        @SuppressWarnings("unchecked")
        PolygonBatchManager getBatchManager() {
            return (PolygonBatchManager) super.getBatchManager();
        }

        @Override
        void pushVertices(float[] vertices, int count) {
            super.pushVertices(vertices, count);
            short[] triangles = new short[getBatchManager().getTrianglesArrayOffset()];
            System.arraycopy(getBatchManager().triangles, 0, triangles, 0, triangles.length);
            triangleBatches.add(triangles);
        }

        @Override
        void finalise() {
            super.finalise();
            triangleBatches.setSize(triangleBatches.size);
        }
    }

}
