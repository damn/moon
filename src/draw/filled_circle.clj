(ns draw.filled-circle
  (:import (space.earlygrey.shapedrawer ShapeDrawer)))

(defn f
  [{:keys [ctx/shape-drawer]} [x y] radius color-float-bits]
  (ShapeDrawer/.setColor shape-drawer (float color-float-bits))
  (ShapeDrawer/.filledCircle shape-drawer (float x) (float y) (float radius)))
