package space.earlygrey.shapedrawer;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;

/**
 * <p>Template for the classes that take care of drawing individual shape types.
 * Contains utility methods for pushing vertex information to the drawer.</p>
 *
 * @author earlygrey
 */

abstract class DrawerTemplate<T extends BatchManager> {

    static final Vector2 A = new Vector2(), B = new Vector2(), C = new Vector2(), D = new Vector2(), E = new Vector2(), dir = new Vector2();
    static final Vector2 vec1 = new Vector2();

    final AbstractShapeDrawer drawer;
    final T batchManager;

    DrawerTemplate(T batchManager, AbstractShapeDrawer drawer) {
        this.drawer = drawer;
        this.batchManager = batchManager;
    }

    /**
     * <p>Draws a trangle that fills the gap at joints when {@link JoinType#SMOOTH} join type is used.</p>
     * @param A the point before the join
     * @param B the point at the join
     * @param C the point after the join
     * @param D
     * @param E
     */
    void drawSmoothJoinFill(Vector2 A, Vector2 B, Vector2 C, Vector2 D, Vector2 E, float halfLineWidth) {
        batchManager.ensureSpaceForTriangle();
        boolean bendsLeft = Joiner.prepareSmoothJoin(A, B, C, D, E, halfLineWidth, false);
        vert1(bendsLeft?E:D);
        vert2(bendsLeft?D:E);
        bendsLeft = Joiner.prepareSmoothJoin(A, B, C, D, E, halfLineWidth, true);
        vert3(bendsLeft?E:D);
        float c = batchManager.floatBits;
        color(c,c,c);
        batchManager.pushTriangle();
    }

    void drawSmoothJoinFill(Vector2 A, Vector2 B, Vector2 C, Vector2 D, Vector2 E, Vector2 offset, float cos, float sin, float halfLineWidth) {
        batchManager.ensureSpaceForTriangle();
        boolean bendsLeft = Joiner.prepareSmoothJoin(A, B, C, D, E, halfLineWidth, false);
        Vector2 V1 = bendsLeft?E:D, V2 = bendsLeft?D:E;
        vert1(V1.x*cos-V1.y*sin  + offset.x, V1.x*sin+V1.y*cos + offset.y);
        vert2(V2.x*cos-V2.y*sin  + offset.x, V2.x*sin+V2.y*cos + offset.y);
        bendsLeft = Joiner.prepareSmoothJoin(A, B, C, D, E, halfLineWidth, true);
        Vector2 V3 = bendsLeft?E:D;
        float x = V3.x*cos-V3.y*sin  + offset.x, y = V3.x*sin+V3.y*cos + offset.y;
        vert3(x, y);
        float c = batchManager.floatBits;
        color(c,c,c);
        batchManager.pushTriangle();
    }


    //VERTEX SETTING UTILITY FUNCTIONS
    void x1(float x1){batchManager.x1(x1);}
    void y1(float y1){batchManager.y1(y1);}
    void x2(float x2){batchManager.x2(x2);}
    void y2(float y2){batchManager.y2(y2);}
    void x3(float x3){batchManager.x3(x3);}
    void y3(float y3){batchManager.y3(y3);}
    void x4(float x4){batchManager.x4(x4);}
    void y4(float y4){batchManager.y4(y4);}
    void vert1(float x, float y) {x1(x);y1(y);}
    void vert2(float x, float y) {x2(x);y2(y);}
    void vert3(float x, float y) {x3(x);y3(y);}
    void vert4(float x, float y) {x4(x);y4(y);}
    void vert1(Vector2 V) {vert1(V.x, V.y);}
    void vert2(Vector2 V) {vert2(V.x, V.y);}
    void vert3(Vector2 V) {vert3(V.x, V.y);}
    void vert4(Vector2 V) {vert4(V.x, V.y);}
    void vert1(Vector2 V, Vector2 offset) {vert1(V.x+offset.x, V.y+offset.y);}
    void vert2(Vector2 V, Vector2 offset) {vert2(V.x+offset.x, V.y+offset.y);}
    void vert3(Vector2 V, Vector2 offset) {vert3(V.x+offset.x, V.y+offset.y);}
    void vert4(Vector2 V, Vector2 offset) {vert4(V.x+offset.x, V.y+offset.y);}
    void color1(float c) {batchManager.color1(c);}
    void color2(float c) {batchManager.color2(c);}
    void color3(float c) {batchManager.color3(c);}
    void color4(float c) {batchManager.color4(c);}
    void color(float c1, float c2, float c3) {
        color1(c1); color2(c2); color3(c3);}
    void color(float c1, float c2, float c3, float c4) {
        color1(c1); color2(c2); color3(c3); color4(c4);}
    float x1() {return batchManager.x1();}
    float y1() {return batchManager.y1();}
    float x2() {return batchManager.x2();}
    float y2() {return batchManager.y2();}
    float x3() {return batchManager.x3();}
    float y3() {return batchManager.y3();}
    float x4() {return batchManager.x4();}
    float y4() {return batchManager.y4();}




    // DEBUGGING
    void print1234() {
        System.out.println(
                "("+x1()+","+y1()+")  "  +
                "("+x2()+","+y2()+")  " +
                "("+x3()+","+y3()+")  " +
                "("+x4()+","+y4()+")");

    }
    void printABC() {
        System.out.println("A: "+A+", B: "+B+", C: "+C);
    }

    void drawPoint(Vector2 point, Color color) {
        drawPoint(point.x, point.y, color);
    }
    void drawPoint(Vector2 point, Color color, float r) {
        drawPoint(point, color.toFloatBits(), r);
    }
    void drawPoint(Vector2 point, float color, float r) {
        drawPoint(point.x, point.y, color, r);
    }
    void drawPoint(float x, float y, Color color) {
        drawPoint(x, y, color.toFloatBits(), 3);
    }
    void drawPoint(float x, float y, float color, float r) {
        Color c = batchManager.getBatch().getColor();
        batchManager.getBatch().setPackedColor(color);
        batchManager.getBatch().draw(batchManager.getRegion(), x-r, y-r, 2*r, 2*r);
        batchManager.getBatch().setColor(c);
    }

    void draw1234() {
        drawPoint(x1(), y1(), Color.GREEN);
        drawPoint(x2(), y2(), Color.ORANGE);
        drawPoint(x3(), y3(), Color.YELLOW);
        drawPoint(x4(), y4(), Color.PURPLE);
    }

    void drawABC() {
        drawABC(Vector2.Zero);
    }

    void drawABC(Vector2 offset) {
        drawPoint(A, Color.GREEN);
        drawPoint(B, Color.ORANGE);
        drawPoint(C, Color.YELLOW);

    }
    void drawDE(boolean scheme1) {
        drawPoint(D, scheme1?Color.YELLOW:Color.CHARTREUSE);
        drawPoint(E, scheme1?Color.PINK:Color.TAN);
    }
    void drawDE() {
        drawDE(true);
    }

}
