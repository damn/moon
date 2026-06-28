(ns draw.filled-rectangle
  (:import (space.earlygrey.shapedrawer ShapeDrawer)))

(defn f
  [{:keys [ctx/shape-drawer]} x y w h color-float-bits]
  (ShapeDrawer/.setColor shape-drawer (float color-float-bits))
  (ShapeDrawer/.filledRectangle shape-drawer (float x) (float y) (float w) (float h)))
