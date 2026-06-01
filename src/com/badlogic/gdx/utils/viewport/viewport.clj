(ns com.badlogic.gdx.utils.viewport.viewport
  (:import (com.badlogic.gdx.utils.viewport Viewport)
           (com.badlogic.gdx.math Vector2)))

(defn update! [^Viewport viewport screen-width screen-height center-camera?]
  (.update viewport screen-width screen-height center-camera?))

(defn unproject [^Viewport viewport ^Vector2 vector2]
  (.unproject viewport vector2))
