(ns gdl.set-default-line-width
  (:import (space.earlygrey.shapedrawer ShapeDrawer)))

(defn set-default-line-width! [^ShapeDrawer this width]
  (.setDefaultLineWidth this width))
