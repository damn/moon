package space.earlygrey.shapedrawer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.PolygonBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Matrix4;

import space.earlygrey.shapedrawer.FilledPolygonDrawer.BatchFilledPolygonDrawer;
import space.earlygrey.shapedrawer.FilledPolygonDrawer.PolygonBatchFilledPolygonDrawer;

/**
 * <p>Contains the mechanics for using the Batch and settings such as line width and pixel size.</p>
 *
 * @author earlygrey
 */

public abstract class AbstractShapeDrawer {

    //================================================================================
    // MEMBERS
    //================================================================================

    final BatchManager batchManager;
    float defaultLineWidth = 1;
    boolean defaultSnap = false;


    protected static final Matrix4 mat4 = new Matrix4();
    protected static final float[] trianglePathPoints = new float[6];

    protected final LineDrawer lineDrawer;
    protected final PathDrawer pathDrawer;
    protected final PolygonDrawer polygonDrawer;
    protected final FilledPolygonDrawer filledPolygonDrawer;


    private SideEstimator sideEstimator;


    //================================================================================
    // CONSTRUCTOR
    //================================================================================

    /**
     * <p>Creates a new ShapeDrawer with the given batch and region.</p>
     *
     * @param batch  the batch used for drawing. Cannot be changed.
     * @param region the texture region used for drawing. Can be changed later.
     */

    AbstractShapeDrawer(Batch batch, TextureRegion region, SideEstimator sideEstimator) {
        if (batch instanceof PolygonBatch) {
            PolygonBatchManager manager = new PolygonBatchManager((PolygonBatch) batch, region);
            filledPolygonDrawer = new PolygonBatchFilledPolygonDrawer(manager, this);
            batchManager = manager;
        } else {
            batchManager = new BatchManager(batch, region);
            filledPolygonDrawer = new BatchFilledPolygonDrawer(batchManager, this);
        }

        lineDrawer = new LineDrawer(batchManager, this);
        pathDrawer = new PathDrawer(batchManager, this);
        polygonDrawer = new PolygonDrawer(batchManager, this);

        this.sideEstimator = sideEstimator;
    }

    /**
     * Starts recording what is subsequently drawn, so it can be cached later in a {@link Drawing}.
     * Used in conjunction with {{@link AbstractShapeDrawer#stopRecording()}}.
     */
    public void startRecording() {
        batchManager.startRecording();
    }

    /**
     * Stops recording and returns a {@link Drawing}, which can be saved and drawn at any time
     * without having to perform shape calculations.
     */
    public Drawing stopRecording() {
        return batchManager.stopRecording();
    }


    //================================================================================
    // UPDATE METHODS
    //================================================================================

    /**
     * <p>Call this when the batch projection or transformation matrices are changed.
     * Calls {@link #update(boolean)} with {@code updatePixelSize} set to true.</p>
     * <p>NOTE: if you are not using an orthographic projection, you should call
     * {@link #update(boolean)} with {@code updatePixelSize} set to false.</p>
     */
    public void update() {
        update(true);
    }

    /**
     * <p>Call this when the batch projection or transformation matrices are changed.
     * Currently just calculates and updates the pixel size (see {@link #updatePixelSize()},
     * and only if {@code updatePixelSize} is set to true.</p>
     *
     * @param updatePixelSize whether to call {@link #updatePixelSize()}
     */
    public void update(boolean updatePixelSize) {
        if (updatePixelSize) updatePixelSize();
    }

    /**
     * <p>This uses the current projection and transformation matrices of the Batch to calculate the
     * size of a screen pixel along the x-axis in world units, and calls {@link #setPixelSize(float)} with
     * that value. You should use this if you have changed the batch projection or transformation matrices,
     * or more generally can just call {@link #update()}.</p>
     * <p>NOTE: this only works when the projection is orthographic!</p>
     *
     * @return the previous screen pixel size in world units
     */
    public float updatePixelSize() {
        Matrix4 trans = getBatch().getTransformMatrix(), proj = getBatch().getProjectionMatrix();
        mat4.set(proj).mul(trans);
        float scaleX = mat4.getScaleX();
        float worldWidth = 2f / scaleX;
        float newPixelSize = worldWidth / Gdx.graphics.getWidth();
        return setPixelSize(newPixelSize);
    }

    //================================================================================
    // HELPERS
    //================================================================================

    /**
     * <p>Makes a guess as to whether joins will be discernible on the screen on based on the thickness of the line.
     * This affects the default behaviour when a {@link JoinType} is unspecified.</p>
     * <p>You can override this if you want to change this behaviour.</p>
     *
     * @param lineWidth the width of the line in world units
     * @return whether drawing joins will likely be discernible
     */
    protected boolean isJoinNecessary(float lineWidth) {
        return lineWidth > 3 * getPixelSize();
    }

    /**
     * See {@link AbstractShapeDrawer#isJoinNecessary(float)}.
     */
    protected boolean isJoinNecessary() {
        return isJoinNecessary(getDefaultLineWidth());
    }

    protected int estimateSidesRequired(float radiusX, float radiusY) {
        return sideEstimator.estimateSidesRequired(getPixelSize(), radiusX, radiusY);
    }

    //================================================================================
    // GETTERS AND SETTERS
    //================================================================================


    /**
     * @return the current {@link SideEstimator}
     */
    public final SideEstimator getSideEstimator() {
        return sideEstimator;
    }

    /**
     * <p>Sets a new {@link SideEstimator} and returns the old {@link SideEstimator}.</p>
     *
     * @param sideEstimator
     * @return the old {@link SideEstimator}
     */
    public SideEstimator setSideEstimator(SideEstimator sideEstimator) {
        final SideEstimator oldSideEstimator = this.sideEstimator;
        this.sideEstimator = sideEstimator;
        return oldSideEstimator;
    }

    /**
     * <p>This is used internally to make estimates about how things will appear on screen. It affects
     * line endpoint snapping (see {@link ShapeDrawer#line(float, float, float, float, float, boolean)}) and
     * estimating the number of sides required to draw an ellipse
     * (see {@link ShapeDrawer#ellipse(float, float, float, float, float, float)}).</p>
     *
     * @param pixelSize the size of a screen pixel in world units
     * @return the previous screen pixel size in world units
     */
    public float setPixelSize(float pixelSize) {
        return batchManager.setPixelSize(pixelSize);
    }

    /**
     * @return the current setting for the pixel size in world units
     */
    public float getPixelSize() {
        return batchManager.getPixelSize();
    }

    /**
     * @return the batch this ShapeDrawer was initialised with
     */
    public Batch getBatch() {
        return batchManager.getBatch();
    }

    /**
     * @return the current TextureRegion used for drawing
     */
    public TextureRegion getRegion() {
        return batchManager.getRegion();
    }

    /**
     * @return the default line width used if one is not specified in the method signature
     */
    public float getDefaultLineWidth() {
        return defaultLineWidth;
    }

    /**
     * <p>Sets the default line width used if one is not specified in the method signature.</p>
     *
     * @param defaultLineWidth the line width to be used as a default
     * @return the previous default line width
     */
    public float setDefaultLineWidth(float defaultLineWidth) {
        float oldWidth = this.defaultLineWidth;
        this.defaultLineWidth = defaultLineWidth;
        return oldWidth;
    }

    /**
     * @return whether lines are snapped to the centre of pixels if it is not specified in the method signature
     */
    public boolean isDefaultSnap() {
        return defaultSnap;
    }

    /**
     * <p>Sets whether line endpoints are snapped to the centre of pixels if it is not specified in the method signature.</p>
     *
     * @param defaultSnap whether to snap
     * @return the previous default snap setting
     */
    public boolean setDefaultSnap(boolean defaultSnap) {
        boolean oldSnap = this.defaultSnap;
        this.defaultSnap = defaultSnap;
        return oldSnap;
    }

    /**
     * <p>Sets the TextureRegion used to draw.</p>
     *
     * @param region the region to use
     * @return the previous texture region
     */
    public TextureRegion setTextureRegion(TextureRegion region) {
        return batchManager.setTextureRegion(region);
    }

    /**
     * <p>Sets the colour of the ShapeDrawer. This works like {@link Batch#setColor(Color)} though drawing is not affected by
     * the colour of the Batch. Internally only the packed float value is stored, see {@link Color#toFloatBits()}.</p>
     *
     * @param color the colour to use
     * @return the previous packed float value of the ShapeDrawer's colour
     */
    public float setColor(Color color) {
        return setColor(color.toFloatBits());
    }

    /**
     * <p>Sets the colour of the ShapeDrawer. This works just like {@link Batch#setColor(Color)} though drawing is not affected by
     * the colour of the Batch.</p>
     *
     * @param floatBits the packed float value of the colour, see {@link Color#toFloatBits()}.
     * @return the previous packed float value of the ShapeDrawer's colour
     */
    public float setColor(float floatBits) {
        return batchManager.setColor(floatBits);
    }

    /**
     * @return the packed colour of this ShapeDrawer
     */
    public float getPackedColor() {
        return batchManager.getPackedColor();
    }

    /**
     * <p>Sets the packed colour using {@link Color#toFloatBits(float, float, float, float)}.</p>
     *
     * @param r
     * @param g
     * @param b
     * @param a
     * @return the packed colour of this ShapeDrawer
     */
    public float setColor(float r, float g, float b, float a) {
        return setColor(Color.toFloatBits(r, g, b, a));
    }

}
