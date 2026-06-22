package space.earlygrey.shapedrawer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Matrix4;

/**
 * <p>Managers the vertex data and when it it sent to the batch.</p>
 *
 * @author earlygrey
 */

class BatchManager {

    //================================================================================
    // MEMBERS
    //================================================================================

    protected final Batch batch;
    protected TextureRegion r;
    protected float floatBits;
    protected float[] verts;
    protected int vertexCount;

    protected float pixelSize = 1, halfPixelSize = 0.5f * pixelSize;
    protected float offset = ShapeUtils.EPSILON * pixelSize;
    protected boolean cacheDraws = false;

    protected static final Matrix4 mat4 = new Matrix4();

    Drawing drawing = null;

    // These are named just for clarity
    static final int DEFAULT_VERTEX_CACHE_SIZE = 2000;
    static final int VERTEX_SIZE = 5, QUAD_PUSH_SIZE = 4 * VERTEX_SIZE;

    BatchManager (Batch batch, TextureRegion region) {
        this.batch = batch;
        verts = new float[DEFAULT_VERTEX_CACHE_SIZE];
        setTextureRegion(region);
        setColor(Color.WHITE);
    }


    public void update() {
        update(true);
    }

    public void update(boolean updatePixelSize) {
        if (updatePixelSize) updatePixelSize();
    }

    public float updatePixelSize() {
        Matrix4 trans = getBatch().getTransformMatrix(), proj = getBatch().getProjectionMatrix();
        mat4.set(proj).mul(trans);
        float scaleX = mat4.getScaleX();
        float worldWidth = 2f / scaleX;
        float newPixelSize = worldWidth / Gdx.graphics.getWidth();
        return setPixelSize(newPixelSize);
    }


    public float setPixelSize(float pixelSize) {
        float oldPixelSize = this.pixelSize;
        this.pixelSize = pixelSize;
        halfPixelSize = 0.5f * pixelSize;
        offset = ShapeUtils.EPSILON * pixelSize;
        return oldPixelSize;
    }

    public TextureRegion setTextureRegion(TextureRegion region) {
        TextureRegion oldRegion = this.r;
        this.r = region;
        setTextureRegionUV();
        return oldRegion;
    }

    private void setTextureRegionUV() {
        if (r != null) {
            float u = 0.5f * (r.getU() + r.getU2());
            float v = 0.5f * (r.getV() + r.getV2());
            for (int i = 0; i < verts.length; i += VERTEX_SIZE) {
                verts[i + SpriteBatch.U1] = u;
                verts[i + SpriteBatch.V1] = v;
            }
        }
    }

    public float setColor(Color color) {
        return setColor(color.toFloatBits());
    }

    public float setColor(float floatBits) {
        float oldColor = getPackedColor();
        this.floatBits = floatBits;
        return oldColor;
    }

    public float getPackedColor() {
        return floatBits;
    }

    /**
     *
     * @return whether drawing is currently being cached
     */
    boolean isCachingDraws() {
        return cacheDraws;
    }

    /**
     * <p>Begin caching draw calls by storing vertex information in a float[] until it all gets set to the
     * Batch with one call to {@link Batch#draw(Texture, float[], int, int)}.</p>
     * @return whether drawing was being cached before this method was called
     */
    boolean startCaching() {
        boolean wasCaching = isCachingDraws();
        this.cacheDraws = true;
        return wasCaching;
    }

    /**
     * <p>Stops caching and calls {@link Batch#draw(Texture, float[], int, int)} if anything is cached.</p>
     */
    void endCaching() {
        this.cacheDraws = false;
        if (vertexCount>0) pushToBatch();
    }




    public float getPixelSize() {
        return pixelSize;
    }


    public Batch getBatch() {
        return batch;
    }

    public TextureRegion getRegion() {
        return r;
    }


    //================================================================================
    // RECORDING
    //================================================================================

    void startRecording() {
        drawing = createDrawing();
    }

    Drawing createDrawing() {
        return new Drawing(this);
    }

    Drawing stopRecording() {
        pushToBatch();
        drawing.finalise();
        Drawing returnVal = drawing;
        drawing = null;
        return returnVal;
    }

    boolean isRecording() {
        return drawing != null;
    }

    //================================================================================
    // DRAWING
    //================================================================================

    void pushVertex() {
        vertexCount++;
    }

    /**
     * <p>Adds the colour and texture coordinates of four vertices to the cache and progresses the index. If drawing is
     * not currently being cached, immediately calls {@link #pushToBatch()}.</p>
     * @return whether the vertex data was pushed to the Batch
     */
    void pushQuad() {
        vertexCount += 4;
    }

    /**
     <p>Adds the colour and texture coordinates of three vertices to the cache and progresses the index. If drawing is
     * not currently being cached, immediately calls {@link #pushToBatch()}.</p>
     * @return whether the vertex data was pushed to the Batch
     */
    void pushTriangle() {
        x4(x3());
        y4(y3());
        pushQuad();
    }

    void ensureSpaceForTriangle() {
        ensureSpace(4);
    }
    void ensureSpaceForQuad() {
        ensureSpace(4);
    }
    void ensureSpace(int vertices) {
        if (vertices * VERTEX_SIZE > verts.length) {
            increaseCacheSize(vertices * VERTEX_SIZE);
        } else if (verticesRemaining() < vertices) {
            pushToBatch();
        }

    }

    void increaseCacheSize(int minSize) {
        pushToBatch();
        int newSize = verts.length;
        while (minSize > newSize) {
            newSize *= 2;
        }
        verts = new float[newSize];
        setTextureRegionUV();
    }

    int verticesRemaining() {
        return (verts.length - QUAD_PUSH_SIZE * vertexCount) / VERTEX_SIZE;
    }

    /**
     * <p>Calls {@link Batch#draw(Texture, float[], int, int)} using the currently cached vertex information.</p>
     */
    void pushToBatch() {
        if (vertexCount == 0) return;
        if (isRecording()) {
            drawing.pushVertices(verts, getVerticesArrayIndex());
        } else {
            if (r == null) throw new IllegalStateException("The texture region is null. Please set a texture region first (e.g. in the constructor or by calling setTextureRegion(TextureRegion region))");
            batch.draw(r.getTexture(), verts, 0, getVerticesArrayIndex());
        }
        vertexCount = 0;
    }

    int getVerticesArrayIndex() {
        return VERTEX_SIZE * vertexCount;
    }

    protected void x1(float x1){verts[getVerticesArrayIndex() + SpriteBatch.X1] = x1;}
    protected void y1(float y1){verts[getVerticesArrayIndex() + SpriteBatch.Y1] = y1;}
    protected void x2(float x2){verts[getVerticesArrayIndex() + SpriteBatch.X2] = x2;}
    protected void y2(float y2){verts[getVerticesArrayIndex() + SpriteBatch.Y2] = y2;}
    protected void x3(float x3){verts[getVerticesArrayIndex() + SpriteBatch.X3] = x3;}
    protected void y3(float y3){verts[getVerticesArrayIndex() + SpriteBatch.Y3] = y3;}
    protected void x4(float x4){verts[getVerticesArrayIndex() + SpriteBatch.X4] = x4;}
    protected void y4(float y4){verts[getVerticesArrayIndex() + SpriteBatch.Y4] = y4;}
    protected float x1() {return verts[getVerticesArrayIndex() + SpriteBatch.X1];}
    protected float y1() {return verts[getVerticesArrayIndex() + SpriteBatch.Y1];}
    protected float x2() {return verts[getVerticesArrayIndex() + SpriteBatch.X2];}
    protected float y2() {return verts[getVerticesArrayIndex() + SpriteBatch.Y2];}
    protected float x3() {return verts[getVerticesArrayIndex() + SpriteBatch.X3];}
    protected float y3() {return verts[getVerticesArrayIndex() + SpriteBatch.Y3];}
    protected float x4() {return verts[getVerticesArrayIndex() + SpriteBatch.X4];}
    protected float y4() {return verts[getVerticesArrayIndex() + SpriteBatch.Y4];}
    void color1(float c) {verts[getVerticesArrayIndex() + SpriteBatch.C1] = c;}
    void color2(float c) {verts[getVerticesArrayIndex() + SpriteBatch.C2] = c;}
    void color3(float c) {verts[getVerticesArrayIndex() + SpriteBatch.C3] = c;}
    void color4(float c) {verts[getVerticesArrayIndex() + SpriteBatch.C4] = c;}
}
