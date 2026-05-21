(ns clojure.graphics.shape-drawer)

(defprotocol ShapeDrawer
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
