package space.earlygrey.shapedrawer;

import com.badlogic.gdx.math.Vector2;

import static space.earlygrey.shapedrawer.ShapeUtils.epsilonEquals;
import static space.earlygrey.shapedrawer.ShapeUtils.snap;

/**
 * <p>Contains functions for calculating vertex data for drawing individual lines.</p>
 *
 * @author earlygrey
 */

class LineDrawer extends DrawerTemplate<BatchManager> {

    private final Vector2 l = new Vector2(), startOffset = new Vector2(), endOffset = new Vector2();

    LineDrawer(BatchManager batchManager, AbstractShapeDrawer drawer) {
        super(batchManager, drawer);
    }

    void line(float x1, float y1, float x2, float y2, float lineWidth, boolean snap, float c1, float c2) {
        pushLine(x1, y1, x2, y2, lineWidth, snap, c1, c2);
        batchManager.pushToBatch();
    }

    void line(float x1, float y1, float x2, float y2, float startLineWidth, float endLineWidth, boolean snap) {
        pushLine(x1, y1, x2, y2, startLineWidth, endLineWidth, snap, batchManager.floatBits, batchManager.floatBits);
        batchManager.pushToBatch();
    }

    void pushLine(float x1, float y1, float x2, float y2, float lineWidth, boolean snap) {
        pushLine(x1, y1, x2, y2, lineWidth, snap, batchManager.floatBits, batchManager.floatBits);
    }

    void pushLine(float x1, float y1, float x2, float y2, float startLineWidth, float endLineWidth, boolean snap) {
        pushLine(x1, y1, x2, y2, startLineWidth, endLineWidth, snap, batchManager.floatBits, batchManager.floatBits);
    }

    void pushLine(float x1, float y1, float x2, float y2, float lineWidth, boolean snap, float c1, float c2) {
        pushLine(x1, y1, x2, y2, lineWidth, lineWidth, snap, c1, c2);
    }

    void pushLine(float x1, float y1, float x2, float y2, float startLineWidth, float endLineWidth, boolean snap, float c1, float c2) {

        batchManager.ensureSpaceForQuad();

        l.set(x2 - x1, y2 - y1);

        if (snap) {
            /*
            First "snap" the given coords so they're right in the centre of the pixel,
            then extend the line very slightly in each direction, so it covers the centre of the start and end pixels.
            This is because nearest neighbour just takes the colour from right in the centre.
            */
            float offset = batchManager.offset;
            float pixelSize = batchManager.pixelSize, halfPixelSize = batchManager.halfPixelSize;
            x1 = snap(x1, pixelSize, halfPixelSize) - Math.signum(l.x) * offset;
            y1 = snap(y1, pixelSize, halfPixelSize) - Math.signum(l.y) * offset;
            x2 = snap(x2, pixelSize, halfPixelSize) + Math.signum(l.x) * offset;
            y2 = snap(y2, pixelSize, halfPixelSize) + Math.signum(l.y) * offset;
        }

        if (epsilonEquals(x1, x2)) {
            startOffset.set(startLineWidth / 2, 0);
            endOffset.set(endLineWidth / 2, 0);
        } else if (epsilonEquals(y1, y2)) {
            startOffset.set(0, startLineWidth / 2);
            endOffset.set(0, endLineWidth / 2);
        } else {

            startOffset.set(l).setLength(startLineWidth / 2);
            startOffset.set(-startOffset.y, startOffset.x); // rotate -pi/2

            endOffset.set(l).setLength(endLineWidth / 2);
            endOffset.set(-endOffset.y, endOffset.x); // rotate -pi/2
        }

        x1(x1 + startOffset.x);
        y1(y1 + startOffset.y);
        x2(x1 - startOffset.x);
        y2(y1 - startOffset.y);

        x3(x2 - endOffset.x);
        y3(y2 - endOffset.y);
        x4(x2 + endOffset.x);
        y4(y2 + endOffset.y);

        color1(c1);
        color2(c1);
        color3(c2);
        color4(c2);

        batchManager.pushQuad();
        if (!batchManager.isCachingDraws()) batchManager.pushToBatch();
    }

}
