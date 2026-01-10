(ns moon.viewport
  (:import (com.badlogic.gdx.math Vector2)
           (com.badlogic.gdx.utils.viewport FitViewport)))

(defn create [world-width world-height camera]
  (FitViewport. world-width world-height camera))

(defn camera [^FitViewport viewport]
  (.getCamera viewport))

(defn world-width [^FitViewport viewport]
  (.getWorldWidth viewport))

(defn world-height [^FitViewport viewport]
  (.getWorldHeight viewport))

(defn update! [^FitViewport viewport width height {:keys [center?]}]
  (.update viewport width height (boolean center?)))

(defn unproject [^FitViewport viewport [x y]]
  (let [v2 (.unproject viewport (Vector2. x y))]
    [(.x v2)
     (.y v2)]))
