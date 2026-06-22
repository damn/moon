(ns gdl.default-line-width
  (:import (space.earlygrey.shapedrawer ShapeDrawer)))

(defn default-line-width [^ShapeDrawer this]
  (.getDefaultLineWidth this))
