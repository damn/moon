(ns clojure.gdx.shape-drawer.filled-rectangle
  (:import (space.earlygrey.shapedrawer ShapeDrawer)))

(defn f [^ShapeDrawer shape-drawer x y w h]
  (ShapeDrawer/.filledRectangle shape-drawer (float x) (float y) (float w) (float h)))
