(ns clojure.gdx.shape-drawer.line
  (:import (space.earlygrey.shapedrawer ShapeDrawer)))

(defn f [^ShapeDrawer shape-drawer sx sy ex ey]
  (ShapeDrawer/.line shape-drawer (float sx) (float sy) (float ex) (float ey)))
