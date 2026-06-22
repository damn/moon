package space.earlygrey.shapedrawer;

import com.badlogic.gdx.graphics.g2d.PolygonBatch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import space.earlygrey.shapedrawer.Drawing.PolygonDrawing;

class PolygonBatchManager extends BatchManager {

    protected short[] triangles;
    protected int triangleCount = 0;

    PolygonBatchManager(PolygonBatch batch, TextureRegion region) {
        super(batch, region);
        //need at least (3 * vxs) triangles
        //n quads arranged in a loop, each sharing 2 vxs with the next requires 2n vertices
        // and 2n triangles (so 2n*3 indices)
        int trianglesLength = (int) Math.ceil(((double) verts.length / (double) VERTEX_SIZE) * 3);
        triangles = new short[trianglesLength];
    }

    //Note that the constructor ensures that the Batch is a PolygonBatch.
    @Override
    @SuppressWarnings("unchecked")
    public PolygonBatch getBatch() {
        return (PolygonBatch) super.getBatch();
    }

    @Override
    Drawing createDrawing() {
        return new PolygonDrawing(this);
    }

    @Override
    void pushQuad() {
        pushTriangleIndices((short) vertexCount, (short) (vertexCount+1), (short) (vertexCount+2));
        pushTriangleIndices((short) vertexCount, (short) (vertexCount+2), (short) (vertexCount+3));
        super.pushQuad();
    }


    @Override
    void pushTriangle() {
        pushTriangleIndices((short) vertexCount, (short) (vertexCount+1), (short) (vertexCount+2));
        vertexCount += 3;
    }

    void pushTriangleIndices(short t1, short t2, short t3) {
        int t = getTrianglesArrayOffset();
        triangles[t] = t1;
        triangles[t+1] = t2;
        triangles[t+2] = t3;
        triangleCount++;
    }

    protected void pushVertexData(float[] vertices, short[] triangles, int trianglesArrayCount, float color) {
        pushVertexData(vertices, triangles, trianglesArrayCount, color, 0f, 0f, 1, 1);
    }

    protected void pushVertexData(float[] vertices, short[] triangles, int trianglesArrayCount,
                                  float color, float offsetX, float offsetY, float scaleX, float scaleY) {

        int v = getVerticesArrayIndex();

        int t = getTrianglesArrayOffset();
        for (int j = 0, n = trianglesArrayCount; j < n; j++) {
            this.triangles[t+j] = (short) (vertexCount + triangles[j]);
        }
        triangleCount += trianglesArrayCount / 3;

        for (int j = 0; j < vertices.length; j+=2) {
            float x = scaleX * vertices[j] + offsetX, y = scaleY * vertices[j + 1] + offsetY;
            verts[v + SpriteBatch.X1] = x;
            verts[v + SpriteBatch.Y1] = y;
            verts[v + SpriteBatch.C1] = color;
            v += VERTEX_SIZE;
        }
        vertexCount += vertices.length / 2;
    }

    @Override
    void ensureSpaceForTriangle() {
        ensureSpace(3);
    }

    /**
     * <p>Calls {@link PolygonBatch#draw(com.badlogic.gdx.graphics.Texture, float[], int, int, short[], int, int)}
     * using the currently cached vertex and triangle information.</p>
     */
    @Override
    void pushToBatch() {
        if (vertexCount == 0) return;
        if (isRecording()) {
            drawing.pushVertices(verts, getVerticesArrayIndex());
        } else {
            getBatch().draw(r.getTexture(), verts, 0, getVerticesArrayIndex(), triangles, 0, getTrianglesArrayOffset());
        }

        vertexCount = 0;
        triangleCount = 0;
    }

    @Override
    void increaseCacheSize(int minSize) {
        super.increaseCacheSize(minSize);
        int trianglesLength = (int) Math.ceil(((double) verts.length / (double) VERTEX_SIZE) * 3);
        triangles = new short[trianglesLength];
    }

    int getTrianglesArrayOffset() {
        return 3* triangleCount;
    }

}
