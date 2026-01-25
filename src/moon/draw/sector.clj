(ns moon.draw.sector
  (:import (space.earlygrey.shapedrawer ShapeDrawer)))

(defn do!
  [{:keys [^ShapeDrawer ctx/shape-drawer]}
   [center-x center-y] radius start-radians radians color-float-bits]
  (.setColor shape-drawer (float color-float-bits))
  (.sector shape-drawer
           center-x
           center-y
           radius
           start-radians
           radians))
