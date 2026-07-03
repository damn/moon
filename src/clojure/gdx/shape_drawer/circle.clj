(ns clojure.gdx.shape-drawer.circle
  (:import (space.earlygrey.shapedrawer ShapeDrawer)))

(defn f [^ShapeDrawer shape-drawer x y radius]
  (ShapeDrawer/.circle shape-drawer x y radius))
