(ns space.earlygrey.shape-drawer.ellipse
  (:import (space.earlygrey.shapedrawer ShapeDrawer)))

(defn ellipse! [^ShapeDrawer this x y radius-x radius-y]
  (.ellipse this x y radius-x radius-y))
