(ns shape-drawer.set-color
  (:import (space.earlygrey.shapedrawer ShapeDrawer)))

(defn set-color! [^ShapeDrawer this float-bits]
  (.setColor this (float float-bits)))
