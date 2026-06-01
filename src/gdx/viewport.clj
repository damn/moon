(ns gdx.viewport
  (:require [clojure.gdx.math.vector2 :as vector2]
            [com.badlogic.gdx.utils.viewport.viewport :as viewport]))

(defn update! [viewport screen-width screen-height center-camera?]
  (viewport/update! viewport screen-width screen-height center-camera?))

(defn unproject [viewport position]
  (-> viewport
      (viewport/unproject (vector2/create position))
      vector2/->clj))
