package space.earlygrey.shapedrawer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

class PolygonDrawer extends DrawerTemplate<BatchManager> {

    static final Vector2 centre = new Vector2(), radius = new Vector2();

    PolygonDrawer(BatchManager batchManager, AbstractShapeDrawer drawer) {
        super(batchManager, drawer);
    }

    void polygon(float centreX, float centreY, int sides, float radiusX, float radiusY, float rotation, float lineWidth, JoinType joinType, float startAngle, float radians) {
        if (radians==0) return;
        radians = Math.min(radians, ShapeUtils.PI2);

        float halfLineWidth = 0.5f*lineWidth;

        centre.set(centreX, centreY);
        radius.set(radiusX, radiusY);

        boolean wasCaching = batchManager.startCaching();
        if (joinType==JoinType.NONE) {
            drawPolygonNoJoin(centre, sides, lineWidth, rotation, radius, startAngle, radians);
        } else {
            drawPolygonWithJoin(centre, sides, halfLineWidth, rotation, radius, startAngle, radians, joinType==JoinType.SMOOTH);
        }
        if (!wasCaching) batchManager.endCaching();
    }

    void drawPolygonNoJoin(Vector2 centre, int sides, float lineWidth, float rotation, Vector2 radius, float startAngle, float radians) {
        float angleInterval = MathUtils.PI2 / sides;
        float endAngle = startAngle + radians;

        float cos = (float) Math.cos(angleInterval), sin = (float) Math.sin(angleInterval);
        float cosRot = (float) Math.cos(rotation), sinRot = (float) Math.sin(rotation);

        int start = (int) Math.ceil(sides * (startAngle / ShapeUtils.PI2));
        int end = (int) Math.floor(sides * (endAngle / ShapeUtils.PI2)) + 1;

        dir.set(1, 0).rotateRad(Math.min(start * angleInterval, endAngle));
        A.set(1, 0).rotateRad(startAngle).scl(radius);
        B.set(dir).scl(radius);

        for (int i = start; i <= end; i++) {
            float x1 = A.x*cosRot-A.y*sinRot  + centre.x, y1 = A.x*sinRot+A.y*cosRot + centre.y;
            float x2 = B.x*cosRot-B.y*sinRot  + centre.x, y2 = B.x*sinRot+B.y*cosRot + centre.y;
            drawer.lineDrawer.pushLine(x1, y1, x2, y2, lineWidth, false);
            if (i<end-1) {
                A.set(B);
                dir.set(dir.x * cos - dir.y * sin, dir.x * sin + dir.y * cos);
                B.set(dir).scl(radius);
            } else if (i==end-1) {
                A.set(B);
                B.set(1, 0).rotateRad(endAngle).scl(radius);
            }
        }
    }

    void drawPolygonWithJoin(Vector2 centre, int sides, float halfLineWidth, float rotation, Vector2 radius, float startAngle, float radians, boolean smooth) {

        float c = batchManager.floatBits;

        boolean full = ShapeUtils.epsilonEquals(radians, ShapeUtils.PI2);

        float angleInterval = MathUtils.PI2 / sides;
        float endAngle = startAngle + radians;

        float cos = (float) Math.cos(angleInterval), sin = (float) Math.sin(angleInterval);
        float cosRot = (float) Math.cos(rotation), sinRot = (float) Math.sin(rotation);

        int start, end;

        if (full) {
            start = 1;
            end = sides;
            dir.set(1, 0).rotateRad(start * angleInterval);
            A.set(1, 0).rotateRad((start-2) * angleInterval).scl(radius);
            C.set(dir).scl(radius);
            B.set(1, 0).rotateRad((start-1) * angleInterval).scl(radius);
        } else {
            start = (int) Math.ceil(sides * (startAngle / ShapeUtils.PI2));
            if (ShapeUtils.epsilonEquals(start * angleInterval, startAngle)) start++;
            end = (int) Math.floor(sides * (endAngle / ShapeUtils.PI2)) + 1;
            end = Math.min(end, start + sides);
            dir.set(1, 0).rotateRad(Math.min(start * angleInterval, endAngle));
            A.set(1, 0).rotateRad((start-1) * angleInterval).scl(radius);
            B.set(1, 0).rotateRad(startAngle).scl(radius);
            C.set(dir).scl(radius);
        }
        for (int i = start; i <= end; i++) {

            batchManager.ensureSpaceForQuad();

            if (!full && i==start) {
                Joiner.prepareRadialEndpoint(B, D, E, halfLineWidth);
            } else {
                if (smooth) {
                    Joiner.prepareSmoothJoin(A, B, C, D, E, halfLineWidth, true);
                } else {
                    Joiner.preparePointyJoin(A, B, C, D, E, halfLineWidth);
                }
            }
            vert1(E.x*cosRot-E.y*sinRot  + centre.x, E.x*sinRot+E.y*cosRot + centre.y);
            vert2(D.x*cosRot-D.y*sinRot  + centre.x, D.x*sinRot+D.y*cosRot + centre.y);

            if (full || i<end) {
                A.set(B);
                B.set(C);
                dir.set(dir.x * cos - dir.y * sin, dir.x * sin + dir.y * cos);
                C.set(dir).scl(radius);
            } else {
                B.set(1, 0).rotateRad(endAngle).scl(radius);
            }

            if (full || i<end) {
                if (smooth) {
                    Joiner.prepareSmoothJoin(A, B, C, D, E, halfLineWidth, false);
                } else {
                    Joiner.preparePointyJoin(A, B, C, D, E, halfLineWidth);
                }
            } else {
                Joiner.prepareRadialEndpoint(B, D, E, halfLineWidth);
            }

            vert3(D.x*cosRot-D.y*sinRot  + centre.x, D.x*sinRot+D.y*cosRot + centre.y);
            vert4(E.x*cosRot-E.y*sinRot  + centre.x, E.x*sinRot+E.y*cosRot + centre.y);

            color(c,c,c,c);
            batchManager.pushQuad(); //push current AB

            if (smooth && (full || i<end)) drawSmoothJoinFill(A, B, C, D, E, centre, cosRot, sinRot, halfLineWidth);
        }
    }


}
