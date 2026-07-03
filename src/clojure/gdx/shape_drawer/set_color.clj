(ns clojure.gdx.shape-drawer.set-color
  (:import (space.earlygrey.shapedrawer ShapeDrawer)))

(defn f [^ShapeDrawer shape-drawer color-float-bits]
  (ShapeDrawer/.setColor shape-drawer (float color-float-bits)))
