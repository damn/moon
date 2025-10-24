package space.earlygrey.shapedrawer.shapes;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;

import space.earlygrey.shapedrawer.JoinType;
import space.earlygrey.shapedrawer.ShapeDrawer;
import space.earlygrey.shapedrawer.shapes.Shape.OutlinedTriangle;

class BasicTriangle  extends BasicShape<BasicTriangle> implements OutlinedTriangle {

   Vector2 a, b, c;


   BasicTriangle(ShapeDrawer drawer) {
      super(drawer);
      a = new Vector2();
      b = new Vector2();
      c = new Vector2();
   }

   @Override
   void reset(boolean filled) {
      super.reset(filled);
      a.set(0, 0);
      b.set(0.5f, 1);
      c.set(1, 0);
   }

   @Override
   public BasicTriangle color(Color color) {
      this.color = color.toFloatBits();
      return this;
   }

   @Override
   public BasicTriangle joinType(JoinType joinType) {
      this.joinType = joinType;
      return this;
   }

   @Override
   public BasicTriangle lineWidth(float width) {
      setLineWidth(width);
      return this;
   }

   @Override
   public OutlinedTriangle a(float x, float y) {
      a.set(x, y);
      return this;
   }

   @Override
   public OutlinedTriangle b(float x, float y) {
      b.set(x, y);
      return this;
   }

   @Override
   public OutlinedTriangle c(float x, float y) {
      c.set(x, y);
      return this;
   }

   @Override
   public OutlinedTriangle a(Vector2 a) {
      this.a.set(a);
      return this;
   }

   @Override
   public OutlinedTriangle b(Vector2 b) {
      this.b.set(b);
      return this;
   }

   @Override
   public OutlinedTriangle c(Vector2 c) {
      this.c.set(c);
      return this;
   }

   @Override
   public void draw() {
      if (filled) {
         draw(() -> drawer.filledTriangle(a, b, c));
      } else {
         draw(() -> drawer.triangle(a, b, c, lineWidth.getWidth(0, 0), joinType, color));
      }
   }
   
}
