(ns space.earlygrey.shape-drawer.sector
  (:import (space.earlygrey.shapedrawer ShapeDrawer)))

(defn sector! [^ShapeDrawer this center-x center-y radius start-radians radians]
  (.sector this center-x center-y radius start-radians radians))
