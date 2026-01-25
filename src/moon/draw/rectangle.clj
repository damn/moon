(ns moon.draw.rectangle
  (:import (space.earlygrey.shapedrawer ShapeDrawer)))

(defn do!
  [{:keys [^ShapeDrawer ctx/shape-drawer]}
   x y w h color-float-bits]
  (.setColor shape-drawer (float color-float-bits))
  (.rectangle shape-drawer x y w h))
