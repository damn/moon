(ns clojure.gdx.shape-drawer.ellipse
  (:import (space.earlygrey.shapedrawer ShapeDrawer)))

(defn f [^ShapeDrawer shape-drawer x y radius-x radius-y]
  (ShapeDrawer/.ellipse shape-drawer x y radius-x radius-y))
