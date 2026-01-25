(ns moon.draw.line
  (:import (space.earlygrey.shapedrawer ShapeDrawer)))

(defn do!
  [{:keys [^ShapeDrawer ctx/shape-drawer]}
   [sx sy] [ex ey] color-float-bits]
  (.setColor shape-drawer (float color-float-bits))
  (.line shape-drawer (float sx) (float sy) (float ex) (float ey)))
