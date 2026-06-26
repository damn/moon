(ns com.badlogic.gdx.math.vector2
  (:import (com.badlogic.gdx.math Vector2)))

(defn create [x y]
  (Vector2. x y))

(defn x [^Vector2 v]
  (.x v))

(defn y [^Vector2 v]
  (.y v))
