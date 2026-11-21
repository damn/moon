(ns clojure.com.badlogic.gdx.utils.viewport.viewport
  (:import (com.badlogic.gdx.math Vector2)
           (com.badlogic.gdx.utils.viewport Viewport)))

(defn world-width [^Viewport viewport]
  (.getWorldWidth viewport))

(defn world-height [^Viewport viewport]
  (.getWorldHeight viewport))

(defn camera [^Viewport viewport]
  (.getCamera viewport))

(defn unproject! [^Viewport viewport ^Vector2 v2]
  (.unproject viewport v2))

(defn update! [^Viewport viewport width height center-camera?]
  (.update viewport width height center-camera?))
