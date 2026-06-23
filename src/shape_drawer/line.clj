(ns shape-drawer.line
  (:import (space.earlygrey.shapedrawer ShapeDrawer)))

(defn line! [^ShapeDrawer this sx sy ex ey]
  (.line this (float sx) (float sy) (float ex) (float ey)))
