(ns space.earlygrey.shape-drawer
  (:import (space.earlygrey.shapedrawer ShapeDrawer)))

(defn create [batch texture-region]
  (ShapeDrawer. batch texture-region))

(defprotocol PShapeDrawer
  (set-color! [_ color-float-bits])
  (circle! [_ x y radius])
  (ellipse! [_ x y radius-x radius-y])
  (filled-circle! [_ x y radius])
  (filled-rectangle! [_ x y w h])
  (line! [_ sx sy ex ey])
  (rectangle! [_ x y w h])
  (sector! [_ center-x center-y radius start-radians radians])
  (default-line-width [_])
  (set-default-line-width! [_ width]))

(extend-type ShapeDrawer
  PShapeDrawer
  (set-color! [this color-float-bits]
    (.setColor this (float color-float-bits)))

  (circle! [this x y radius]
    (.circle this x y radius))

  (ellipse! [this x y radius-x radius-y]
    (.ellipse this x y radius-x radius-y))

  (filled-circle! [this x y radius]
    (.filledCircle this (float x) (float y) (float radius)))

  (filled-rectangle! [this x y w h]
    (.filledRectangle this (float x) (float y) (float w) (float h)))

  (line! [this sx sy ex ey]
    (.line this (float sx) (float sy) (float ex) (float ey)))

  (rectangle! [this x y w h]
    (.rectangle this x y w h))

  (sector! [this center-x center-y radius start-radians radians]
    (.sector this center-x center-y radius start-radians radians))

  (default-line-width [this]
    (.getDefaultLineWidth this))

  (set-default-line-width! [this width]
    (.setDefaultLineWidth this width)))
