(ns gdl.shape-drawer.filled-rectangle
  (:import (space.earlygrey.shapedrawer ShapeDrawer)))

(defn filled-rectangle! [^ShapeDrawer this x y w h]
  (.filledRectangle this (float x) (float y) (float w) (float h)))
