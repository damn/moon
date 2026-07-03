(ns clojure.gdx.shape-drawer.set-default-line-width
  (:import (space.earlygrey.shapedrawer ShapeDrawer)))

(defn f [^ShapeDrawer shape-drawer line-width]
  (ShapeDrawer/.setDefaultLineWidth shape-drawer line-width))
