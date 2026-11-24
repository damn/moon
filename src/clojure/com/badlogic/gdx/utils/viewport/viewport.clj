(ns clojure.com.badlogic.gdx.utils.viewport.viewport
  "Manages a camera and determines how world coordinates are mapped to and from the screen."
  (:import (com.badlogic.gdx.math Vector2)
           (com.badlogic.gdx.utils.viewport Viewport)))

(defn world-width [^Viewport viewport]
  (.getWorldWidth viewport))

(defn world-height [^Viewport viewport]
  (.getWorldHeight viewport))

(defn camera [^Viewport viewport]
  (.getCamera viewport))

(defn unproject!
  "Transforms the specified touch coordinate to world coordinates. The x- and y-coordinate of vec are assumed to be in touch
  coordinates (origin is the top left corner, y pointing down, x pointing to the right).
  
  Returns the vector that was passed in, transformed to world coordinates."
  [^Viewport viewport ^Vector2 v2]
  (.unproject viewport v2))

(defn update!
  "Configures this viewport's screen bounds using the specified screen size and applies the viewport.
  Typically called from application resize."
  [^Viewport viewport width height center-camera?]
  (.update viewport width height center-camera?))
