(ns gdl.shape-drawer.filled-circle
  (:import (space.earlygrey.shapedrawer ShapeDrawer)))

(defn filled-circle! [^ShapeDrawer this x y radius]
  (.filledCircle this (float x) (float y) (float radius)))
