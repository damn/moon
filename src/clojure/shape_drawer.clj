(ns clojure.shape-drawer
  (:import (space.earlygrey.shapedrawer ShapeDrawer)))

(defn shape-drawer [batch texture-region]
  (ShapeDrawer. batch texture-region))
