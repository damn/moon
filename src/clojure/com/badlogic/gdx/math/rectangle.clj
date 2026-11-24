(ns clojure.com.badlogic.gdx.math.rectangle
  "Encapsulates a 2D rectangle defined by its corner point in the bottom left and its extents in x (width) and y (height)."
  (:import (com.badlogic.gdx.math Rectangle)))

(defn create
  "Constructs a new rectangle with the given corner point in the bottom left and dimensions.
  
  - `x`: The corner point x-coordinate
  
  - `y`: The corner point y-coordinate
  
  - `width`: The width
  
  - `height`: The height"
  [x y width height]
  (Rectangle. (float x) (float y) (float width) (float height)))

(defn overlaps?
  "Returns whether this rectangle overlaps the other rectangle."
  [^Rectangle a ^Rectangle b]
  (.overlaps a b))

(defn contains?
  "- `x`: point x coordinate
  
  - `y`: point y coordinate
  
  Returns whether the point is contained in the rectangle"
  [^Rectangle rectangle x y]
  (.contains rectangle (float x) (float y)))
