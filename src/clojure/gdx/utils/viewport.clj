(ns clojure.gdx.utils.viewport
  (:import (com.badlogic.gdx.math Vector2)
           (com.badlogic.gdx.utils.viewport Viewport)))

(defn camera [^Viewport viewport]
  (.getCamera viewport))

(defn world-width [^Viewport viewport]
  (.getWorldWidth viewport))

(defn world-height [^Viewport viewport]
  (.getWorldHeight viewport))

(defn update! [^Viewport viewport width height {:keys [center?]}]
  (.update viewport width height (boolean center?)))

(defn unproject [^Viewport viewport ^Vector2 vector2]
  (.unproject viewport vector2))
