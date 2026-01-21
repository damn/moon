(ns moon.draw.sector
  (:require [clj.api.com.badlogic.gdx.graphics.color :as color]
            [clojure.math :as math])
  (:import (space.earlygrey.shapedrawer ShapeDrawer)))

(defn do!
  [{:keys [^ShapeDrawer ctx/shape-drawer]}
   [center-x center-y] radius start-angle degree color]
  (.setColor shape-drawer (color/float-bits color))
  (.sector shape-drawer
           center-x
           center-y
           radius
           (math/to-radians start-angle)
           (math/to-radians degree)))
