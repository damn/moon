(ns moon.viewport
  (:require [clj.api.com.badlogic.gdx.math.vector2 :as vector2]
            [clj.api.com.badlogic.gdx.utils.viewport :as viewport]))

(defn unproject [viewport position]
  (-> viewport
      (viewport/unproject (vector2/->java position))
      vector2/->clj))
