(ns draw.ellipse
  (:import (space.earlygrey.shapedrawer ShapeDrawer)))

(defn f
  [{:keys [ctx/shape-drawer]} [x y] radius-x radius-y color-float-bits]
  (ShapeDrawer/.setColor shape-drawer (float color-float-bits))
  (ShapeDrawer/.ellipse shape-drawer x y radius-x radius-y))
