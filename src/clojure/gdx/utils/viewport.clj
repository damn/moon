(ns clojure.gdx.utils.viewport
  (:require [com.badlogic.gdx.math.vector2 :as vector2]
            [com.badlogic.gdx.utils.viewport.fit-viewport :as viewport]))

(def camera viewport/camera)

(def world-width viewport/world-width)

(def world-height viewport/world-height)

(def update! viewport/update!)

(defn unproject [viewport position]
  (-> viewport
      (viewport/unproject (vector2/->java position))
      vector2/->clj))
