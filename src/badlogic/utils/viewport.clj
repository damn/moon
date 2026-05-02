(ns badlogic.utils.viewport
  (:import (com.badlogic.gdx.utils.viewport Viewport)))

(defn camera [^Viewport viewport]
  (.getCamera viewport))

(defn world-width [^Viewport viewport]
  (.getWorldWidth viewport))

(defn world-height [^Viewport viewport]
  (.getWorldHeight viewport))

(defn update! [^Viewport viewport screen-width screen-height center-camera?]
  (.update viewport screen-width screen-height center-camera?))

(defn unproject [^Viewport viewport vector2]
  (.unproject viewport vector2))
