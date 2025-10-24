package space.earlygrey.shapedrawer;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.FloatArray;

import space.earlygrey.shapedrawer.ShapeUtils.ConstantLineWidth;
import space.earlygrey.shapedrawer.ShapeUtils.LineWidthFunction;

/**
 * <p>Contains functions for calculating the vertex data for lines in a path with various joins.</p>
 *
 * @author earlygrey
 */

class PathDrawer extends DrawerTemplate<BatchManager> {

    private FloatArray path = new FloatArray();
    private FloatArray lineWidths = new FloatArray();
    private FloatArray tempPath = new FloatArray();

    private final Vector2 D0 = new Vector2(), E0 = new Vector2();

    private static final ConstantLineWidth CONSTANT_LINE_WIDTH = new ConstantLineWidth();

    PathDrawer(BatchManager batchManager, AbstractShapeDrawer drawer) {
        super(batchManager, drawer);
    }

    <T extends Vector2> void path(Iterable<T> userPath, float lineWidth, JoinType joinType) {
        path(userPath, lineWidth, joinType, true);
    }

    <T extends Vector2> void path(Iterable<T> userPath, float lineWidth, JoinType joinType, boolean open) {
        for (Vector2 v : userPath) {
            tempPath.add(v.x, v.y);
        }

        path(tempPath.items, 0, tempPath.size, lineWidth, joinType, open);
        tempPath.clear();
    }

    <T extends Vector2> void path(Iterable<T> userPath, JoinType joinType, boolean open, LineWidthFunction lineWidth) {
        for (Vector2 v : userPath) {
            tempPath.add(v.x, v.y);
        }

        path(tempPath.items, 0, tempPath.size, lineWidth, joinType, open);
        tempPath.clear();
    }

    void path(FloatArray userPath, LineWidthFunction lineWidth, JoinType joinType, boolean open) {
        path(userPath.items, 0, userPath.size, lineWidth, joinType, open);
    }

    void path (float[] userPath, float lineWidth, JoinType joinType) {
        path (userPath, 0, userPath.length, lineWidth, joinType);
    }

    void path (float[] userPath, float lineWidth, JoinType joinType, boolean open) {
        path (userPath, 0, userPath.length, lineWidth, joinType, open);
    }

    void path (float[] userPath, int start, int end, float lineWidth, JoinType joinType) {
        path(userPath, start, end, lineWidth, joinType, true);
    }

    void path (float[] userPath, int start, int end, final float lineWidth, JoinType joinType, boolean open) {
        path(userPath, start, end, CONSTANT_LINE_WIDTH.width(lineWidth), joinType, open);
    }

    void path (float[] userPath, int start, int end, LineWidthFunction lineWidth, JoinType joinType, boolean open) {
        path(userPath, start, end, lineWidth, joinType, open, 0, 0, 1, 1);
    }

    void path (float[] userPath, int start, int end, LineWidthFunction lineWidth, JoinType joinType, boolean open, float offsetX, float offsetY, float scaleX, float scaleY) {

        if (userPath.length < 4) return;

        //construct new path consisting of unique consecutive points
        path.add(userPath[start]);
        path.add(userPath[start+1]);
        for(int i = start+2; i < end; i+=2) {
            if (!ShapeUtils.epsilonEquals(userPath[i-2], userPath[i]) || !ShapeUtils.epsilonEquals(userPath[i-1], userPath[i+1])) {
                path.add(offsetX + scaleX * userPath[i], offsetY + scaleY * userPath[i+1]);
            }
        }
        if (path.size < 4) {
            path.clear();
            return;
        }
        if (path.size == 4) {
            drawer.lineDrawer.line(path.items[0], path.items[1], path.items[2], path.items[3], lineWidth.getWidth(0, 0), lineWidth.getWidth(1, 1), false);
            path.clear();
            return;
        }
        setLineWidths(path.items, path.size / 2, lineWidth);
        boolean wasCaching = batchManager.startCaching();
        if (joinType==JoinType.NONE) {
            drawPathNoJoin(open);
        } else {
            drawPathWithJoin(open, joinType == JoinType.POINTY);
        }
        if (!wasCaching) batchManager.endCaching();
        path.clear();
        lineWidths.clear();
    }

    private void drawPathNoJoin(boolean open) {
        for (int i = 0; i < path.size - 2; i+=2) {
            drawer.lineDrawer.pushLine(path.get(i), path.get(i+1), path.get(i+2), path.get(i+3), lineWidths.get(i / 2), lineWidths.get((i / 2) + 1), false);
        }
        if (!open) {
            drawer.lineDrawer.pushLine(path.get(path.size - 2), path.get(path.size - 1), path.get(0), path.get(1), lineWidths.get(lineWidths.size - 1), lineWidths.get(0), false);
        }
    }

    private void setLineWidths(float[] path, int size, LineWidthFunction lineWidth) {
        if (lineWidth == CONSTANT_LINE_WIDTH) {
            float w = lineWidth.getWidth(0, 0);
            for (int i = 0; i < size; i++) {
                lineWidths.add(w);
            }
            return;
        }

        float totalPathLength = ShapeUtils.pathLength(path);
        float lengthDrawn = 0;
        for (int i = 0; i < size - 1; i++) {
            float t = lengthDrawn / totalPathLength;
            lineWidths.add(lineWidth.getWidth(i, t));
            lengthDrawn += Vector2.dst(path[2*i], path[2*i+1], path[2*i+2], path[2*i+3]);
        }
        lineWidths.add(lineWidth.getWidth(size - 1, 1));
    }


    private void drawPathWithJoin(boolean open, boolean pointyJoin) {

        float c = batchManager.floatBits;

        batchManager.ensureSpaceForQuad();

        for (int i = 2; i < path.size - 2; i+=2) {
            int vertexIndex = i / 2;

            float halfWidthA = lineWidths.get(vertexIndex-1) / 2, halfWidthB = lineWidths.get(vertexIndex) / 2;

            A.set(path.get(i-2), path.get(i-1));
            B.set(path.get(i), path.get(i+1));
            C.set(path.get(i+2), path.get(i+3));

            if (pointyJoin) {
                Joiner.preparePointyJoin(A, B, C, D, E, halfWidthB);
            } else {
                Joiner.prepareSmoothJoin(A, B, C, D, E, halfWidthB, false);
            }
            vert3(D);
            vert4(E);

            if (i == 2) {
                if (open) {
                    Joiner.prepareFlatEndpoint(path.get(2), path.get(3), path.get(0), path.get(1), D, E, halfWidthA);
                    vert1(E);
                    vert2(D);
                } else {
                    vec1.set(path.get(path.size  -2), path.get(path.size - 1));
                    if (pointyJoin) {
                        Joiner.preparePointyJoin(vec1, A, B, D0, E0, halfWidthA);
                    } else {
                        Joiner.prepareSmoothJoin(vec1, A, B, D0, E0, halfWidthA, true);
                    }
                    vert1(E0);
                    vert2(D0);
                }
            }

            float x3, y3, x4, y4;
            if (pointyJoin) {
                x3 = x3();
                y3 = y3();
                x4 = x4();
                y4 = y4();
            } else {
                Joiner.prepareSmoothJoin(A, B, C, D, E, halfWidthB, true);
                x3 = D.x;
                y3 = D.y;
                x4 = E.x;
                y4 = E.y;
            }
            color(c, c, c, c);
            batchManager.pushQuad();
            if (!pointyJoin) drawSmoothJoinFill(A, B, C, D, E, halfWidthB);
            batchManager.ensureSpaceForQuad();
            vert1(x4, y4);
            vert2(x3, y3);
        }

        float halfWidthEnd =  lineWidths.get(lineWidths.size - 1) / 2;

        if (open) {
            //draw last link on path
            Joiner.prepareFlatEndpoint(B, C, D, E, halfWidthEnd);
            vert3(E);
            vert4(D);
            color(c, c, c, c);
            batchManager.pushQuad();
        } else {
            float halfWidthStart =  lineWidths.get(0) / 2;
            if (pointyJoin) {
                //draw last link on path
                A.set(path.get(0), path.get(1));
                Joiner.preparePointyJoin(B, C, A, D, E, halfWidthEnd);
                vert3(D);
                vert4(E);
                color(c, c, c, c);
                batchManager.pushQuad();

                //draw connection back to first vertex
                batchManager.ensureSpaceForQuad();
                vert1(D);
                vert2(E);
                vert3(E0);
                vert4(D0);
                color(c, c, c, c);
                batchManager.pushQuad();
            } else {
                //draw last link on path
                A.set(B);
                B.set(C);
                C.set(path.get(0), path.get(1));
                Joiner.prepareSmoothJoin(A, B, C, D, E, halfWidthEnd, false);
                vert3(D);
                vert4(E);
                color(c, c, c, c);
                batchManager.pushQuad();
                drawSmoothJoinFill(A, B, C, D, E, halfWidthEnd);

                //draw connection back to first vertex
                batchManager.ensureSpaceForQuad();
                Joiner.prepareSmoothJoin(A, B, C, D, E, halfWidthEnd, true);
                vert3(E);
                vert4(D);
                A.set(path.get(2), path.get(3));
                Joiner.prepareSmoothJoin(B, C, A, D, E, halfWidthStart, false);
                vert1(D);
                vert2(E);
                color(c, c, c, c);
                batchManager.pushQuad();
                drawSmoothJoinFill(B, C, A, D, E, halfWidthStart);
            }
        }
    }

}
