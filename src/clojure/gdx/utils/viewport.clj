(ns clojure.gdx.utils.viewport
  (:require [badlogic.math.vector2 :as vector2]
            [badlogic.utils.viewport :as viewport]))

(def camera viewport/camera)

(def world-width viewport/world-width)

(def world-height viewport/world-height)

(def update! viewport/update!)

(defn unproject [viewport position]
  (-> viewport
      (viewport/unproject (vector2/->java position))
      vector2/->clj))
