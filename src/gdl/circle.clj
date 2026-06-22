(ns gdl.circle
  (:import (space.earlygrey.shapedrawer ShapeDrawer)))

(defn circle! [^ShapeDrawer this x y radius]
  (.circle this x y radius))
