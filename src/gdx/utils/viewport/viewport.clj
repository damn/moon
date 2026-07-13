(ns gdx.utils.viewport.viewport
  (:require [com.badlogic.gdx.utils.viewport.viewport :as viewport]
            [gdx.vector2 :as vector2]))

(defn get-world-width [viewport]
  (viewport/getWorldWidth viewport))

(defn get-world-height [viewport]
  (viewport/getWorldHeight viewport))

(defn get-camera [viewport]
  (viewport/getCamera viewport))

(defn update! [viewport screen-width screen-height center-camera?]
  (viewport/update viewport screen-width screen-height center-camera?))

(defn unproject [viewport v2]
  (-> viewport
      (viewport/unproject (vector2/new v2))
      vector2/clojurize))
