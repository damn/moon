(ns gdx.viewport
  (:require [com.badlogic.gdx.math.vector2 :as vector2]
            [com.badlogic.gdx.utils.viewport.viewport :as viewport]))

(defn update! [viewport screen-width screen-height center-camera?]
  (viewport/update! viewport screen-width screen-height center-camera?))

(defn unproject [viewport position]
  (-> viewport
      (viewport/unproject (vector2/->java position))
      vector2/->clj))
