(ns viewport.update
  (:import (com.badlogic.gdx.utils.viewport Viewport)))

(defn f [^Viewport viewport screen-width screen-height center-camera?]
  (.update viewport screen-width screen-height center-camera?))
