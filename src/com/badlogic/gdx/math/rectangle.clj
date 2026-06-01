(ns com.badlogic.gdx.math.rectangle
  (:refer-clojure :exclude [contains?])
  (:import (com.badlogic.gdx.math Rectangle)))

(defn overlaps? [a b]
  (.overlaps ^Rectangle a
             ^Rectangle b))

(defn create [[x y width height]]
  (Rectangle. x y width height))

(defn contains? [^Rectangle rectangle [x y]]
  (.contains rectangle x y))
