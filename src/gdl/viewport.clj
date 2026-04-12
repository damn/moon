(ns gdl.viewport
  (:require [clj.api.com.badlogic.gdx.math.vector2 :as vector2]
            [clj.api.com.badlogic.gdx.utils.viewport :as viewport]))

(defn camera [viewport]
  (viewport/camera viewport))

(defn world-width [viewport]
  (viewport/world-width viewport))

(defn world-height [viewport]
  (viewport/world-height viewport))

(defn update! [viewport screen-width screen-height center-camera?]
  (viewport/update! viewport screen-width screen-height center-camera?))

(defn unproject [viewport position]
  (-> viewport
      (viewport/unproject (vector2/->java position))
      vector2/->clj))
