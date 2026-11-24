(ns clojure.com.badlogic.gdx.math.circle
  "A convenient 2D circle class."
  (:import (com.badlogic.gdx.math Circle)))

(defn create
  "Constructs a new circle with the given X and Y coordinates and the given radius.
  
  - `x`: X coordinate of the center of the circle
  
  - `y`: Y coordinate of the center of the circle
  
  - `radius`: The radius of the circle"
  [x y radius]
  (Circle. (float x) (float y) (float radius)))
