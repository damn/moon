(ns clojure.gdx.shape-drawer.rectangle
  (:import (space.earlygrey.shapedrawer ShapeDrawer)))

(defn f [^ShapeDrawer shape-drawer x y w h]
  (ShapeDrawer/.rectangle shape-drawer x y w h))
