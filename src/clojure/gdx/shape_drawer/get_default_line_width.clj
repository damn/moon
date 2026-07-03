(ns clojure.gdx.shape-drawer.get-default-line-width
  (:import (space.earlygrey.shapedrawer ShapeDrawer)))

(defn f [^ShapeDrawer shape-drawer]
  (ShapeDrawer/.getDefaultLineWidth shape-drawer))
