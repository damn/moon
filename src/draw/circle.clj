(ns draw.circle
  (:import (space.earlygrey.shapedrawer ShapeDrawer)))

(defn f
  [{:keys [ctx/shape-drawer]} [x y] radius color-float-bits]
  (ShapeDrawer/.setColor shape-drawer (float color-float-bits))
  (ShapeDrawer/.circle shape-drawer x y radius))
