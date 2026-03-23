(ns moon.shape-drawer)

(defprotocol ShapeDrawer
  (line! [_ [sx sy] [ex ey] color-float-bits]))
