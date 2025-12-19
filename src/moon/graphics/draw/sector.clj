(ns moon.graphics.draw.sector
  (:require [clojure.math :as math]
            [gdl.graphics.color :as color]
            [gdl.graphics.shape-drawer :as sd]))

(defn do!
  [{:keys [graphics/shape-drawer]}
   [center-x center-y] radius start-angle degree color]
  (sd/set-color! shape-drawer (color/float-bits color))
  (sd/sector! shape-drawer
              center-x
              center-y
              radius
              (math/to-radians start-angle)
              (math/to-radians degree)))
