(ns space.earlygrey.shape-drawer.set-default-line-width
  (:import (space.earlygrey.shapedrawer ShapeDrawer)))

(defn set-default-line-width! [^ShapeDrawer this width]
  (.setDefaultLineWidth this width))
