(ns moon.viewport
  (:import (com.badlogic.gdx.math Vector2)
           (com.badlogic.gdx.utils.viewport Viewport)))

(defn get-world-width [viewport]
  (.getWorldWidth ^Viewport viewport))

(defn get-world-height [viewport]
  (.getWorldHeight ^Viewport viewport))

(defn get-camera [viewport]
  (.getCamera ^Viewport viewport))

(defn update! [viewport screen-width screen-height center-camera?]
  (.update ^Viewport viewport screen-width screen-height center-camera?))

(defn unproject [viewport [x y]]
  (let [v2 (.unproject ^Viewport viewport (Vector2. (float x) (float y)))]
    [(.x ^Vector2 v2) (.y ^Vector2 v2)]))
