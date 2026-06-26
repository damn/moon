(ns com.badlogic.gdx.utils.viewport.viewport
  (:import (com.badlogic.gdx.utils.viewport Viewport)))

(defn unproject [^Viewport viewport vector2]
  (.unproject viewport vector2))

(defn update! [^Viewport viewport screen-width screen-height center-camera?]
  (.update viewport screen-width screen-height center-camera?))
