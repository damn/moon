(ns moon.draw.filled-rectangle
  (:import (space.earlygrey.shapedrawer ShapeDrawer)))

(defn do!
  [{:keys [^ShapeDrawer ctx/shape-drawer]}
   x y w h color-float-bits]
  (.setColor shape-drawer (float color-float-bits))
  (.filledRectangle shape-drawer (float x) (float y) (float w) (float h)))
