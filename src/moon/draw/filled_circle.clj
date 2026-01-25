(ns moon.draw.filled-circle
  (:import (space.earlygrey.shapedrawer ShapeDrawer)))

(defn do!
  [{:keys [^ShapeDrawer ctx/shape-drawer]}
   [x y] radius color-float-bits]
  (.setColor shape-drawer (float color-float-bits))
  (.filledCircle shape-drawer (float x) (float y) (float radius)))
