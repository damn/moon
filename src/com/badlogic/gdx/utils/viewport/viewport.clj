(ns com.badlogic.gdx.utils.viewport.viewport
  (:import (com.badlogic.gdx.math Vector2)
           (com.badlogic.gdx.utils.viewport Viewport)))

(defn getCamera [viewport]
  (.getCamera ^Viewport viewport))

(defn getWorldWidth [viewport]
  (.getWorldWidth ^Viewport viewport))

(defn getWorldHeight [viewport]
  (.getWorldHeight ^Viewport viewport))

(defn update [viewport width height center-camera?]
  (.update ^Viewport viewport width height center-camera?))

(defn unproject [viewport vector2]
  (.unproject ^Viewport viewport ^Vector2 vector2))
