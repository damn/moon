(ns clojure.gdx.shape-drawer.sector
  (:import (space.earlygrey.shapedrawer ShapeDrawer)))

(defn f [^ShapeDrawer shape-drawer center-x center-y radius start-radians radians]
  (ShapeDrawer/.sector shape-drawer center-x center-y radius start-radians radians))
