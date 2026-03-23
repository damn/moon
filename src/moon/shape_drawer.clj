(ns moon.shape-drawer)

(defprotocol ShapeDrawer
  (circle! [_ [x y] radius color-float-bits])
  (ellipse! [_ [x y] radius-x radius-y color-float-bits])
  (line! [_ [sx sy] [ex ey] color-float-bits]))
