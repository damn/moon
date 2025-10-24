package space.earlygrey.shapedrawer.shapes;

import space.earlygrey.shapedrawer.ShapeDrawer;
import space.earlygrey.shapedrawer.shapes.Shape.FilledCircle;
import space.earlygrey.shapedrawer.shapes.Shape.FilledEllipse;
import space.earlygrey.shapedrawer.shapes.Shape.FilledPolygon;
import space.earlygrey.shapedrawer.shapes.Shape.FilledRectangle;
import space.earlygrey.shapedrawer.shapes.Shape.FilledRegularPolygon;
import space.earlygrey.shapedrawer.shapes.Shape.FilledTriangle;

abstract class Utensil {

   ShapeDrawer drawer;
   final BasicPolyline POLYLINE;

   final BasicRegularPolygon REGULAR_POLYGON;

   final BasicCircle CIRCLE;

   final BasicEllipse ELLIPSE;

   final BasicRectangle RECTANGLE;

   final BasicArc ARC;

   final BasicTriangle TRIANGLE;

   Utensil(ShapeDrawer drawer) {
      this.drawer = drawer;
      POLYLINE = new BasicPolyline(drawer);
      REGULAR_POLYGON = new BasicRegularPolygon(drawer);
      CIRCLE = new BasicCircle(drawer);
      ELLIPSE = new BasicEllipse(drawer);
      RECTANGLE = new BasicRectangle(drawer);
      ARC = new BasicArc(drawer);
      TRIANGLE = new BasicTriangle(drawer);
   }

   abstract boolean filled();

   public FilledPolygon<?> polygon() {
      POLYLINE.reset(filled());
      return POLYLINE;
   }

   public FilledRegularPolygon<?> regularPolygon() {
      REGULAR_POLYGON.reset(filled());
      return REGULAR_POLYGON;
   }

   public FilledCircle<?> circle() {
      CIRCLE.reset(filled());
      return CIRCLE;
   }

   public FilledEllipse<?> ellipse() {
      ELLIPSE.reset(filled());
      return ELLIPSE;
   }

   public FilledRectangle<?> rectangle() {
      RECTANGLE.reset(filled());
      return RECTANGLE;
   }

   public FilledTriangle<?> triangle() {
      TRIANGLE.reset(filled());
      return TRIANGLE;
   }

}
