(ns gdl.shape-drawer.rectangle
  (:import (space.earlygrey.shapedrawer ShapeDrawer)))

(defn rectangle! [^ShapeDrawer this x y w h]
  (.rectangle this x y w h))
