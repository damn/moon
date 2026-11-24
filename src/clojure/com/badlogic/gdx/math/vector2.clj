(ns clojure.com.badlogic.gdx.math.vector2
  "Encapsulates a 2D vector. Allows chaining methods by returning a reference to itself"
  (:import (com.badlogic.gdx.math Vector2)))

(defn create
  "Constructs a vector with the given components
  
  - `x`: The x-component
  
  - `y`: The y-component"
  [x y]
  (Vector2. x y))

(defn x
  "The x-component of this vector"
  [^Vector2 v]
  (.x v))

(defn y
  "The y-component of this vector"
  [^Vector2 v]
  (.y v))
