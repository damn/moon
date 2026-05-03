(ns com.badlogic.gdx.utils.viewport.fit-viewport
  (:import (com.badlogic.gdx.math Vector2)
           (com.badlogic.gdx.utils.viewport FitViewport)))

(defn create [width height camera]
  (FitViewport. width height camera))

(defn camera [^FitViewport viewport]
  (.getCamera viewport))

(defn world-width [^FitViewport viewport]
  (.getWorldWidth viewport))

(defn world-height [^FitViewport viewport]
  (.getWorldHeight viewport))

(defn update! [^FitViewport viewport screen-width screen-height center-camera?]
  (.update viewport screen-width screen-height center-camera?))

(defn unproject [^FitViewport viewport ^Vector2 vector2]
  (.unproject viewport vector2))
