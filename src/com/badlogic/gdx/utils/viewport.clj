(ns com.badlogic.gdx.utils.viewport
  (:import (com.badlogic.gdx.math Vector2)
           (com.badlogic.gdx.utils.viewport Viewport)))

(defn update! [viewport width height center-camera?]
  (Viewport/.update viewport width height center-camera?))

(defn unproject [viewport vector2]
  (Viewport/.unproject viewport ^Vector2 vector2))
