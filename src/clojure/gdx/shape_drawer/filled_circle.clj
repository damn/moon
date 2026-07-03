(ns clojure.gdx.shape-drawer.filled-circle
  (:import (space.earlygrey.shapedrawer ShapeDrawer)))

(defn f [^ShapeDrawer shape-drawer x y radius]
  (ShapeDrawer/.filledCircle shape-drawer (float x) (float y) (float radius)))
