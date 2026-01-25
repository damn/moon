(ns moon.draw.ellipse
  (:import (space.earlygrey.shapedrawer ShapeDrawer)))

(defn do!
  [{:keys [^ShapeDrawer ctx/shape-drawer]}
   [x y] radius-x radius-y color-float-bits]
  (.setColor shape-drawer (float color-float-bits))
  (.ellipse shape-drawer x y radius-x radius-y))
