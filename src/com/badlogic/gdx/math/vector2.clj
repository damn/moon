(ns com.badlogic.gdx.math.vector2
  (:refer-clojure :exclude [new])
  (:import (com.badlogic.gdx.math Vector2)))

(defn new [[x y]]
  (Vector2. x y))

(defn x [^Vector2 v]
  (.x v))

(defn y [^Vector2 v]
  (.y v))

(defn clojurize [v2] ; TODO REMOVE!
  [(x v2)
   (y v2)])
