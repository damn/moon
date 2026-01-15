(ns moon.draw.sector
  (:require [clj.api.com.badlogic.gdx.graphics.color :as color]
            [clj.api.space.earlygrey.shape-drawer :as sd]
            [clojure.math :as math]))

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
