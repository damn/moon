(ns com.badlogic.gdx.math.rectangle
  (:refer-clojure :exclude [contains?])
  (:import (com.badlogic.gdx.math Rectangle)))

(defn create [x y width height]
  (Rectangle. x y width height))

(defn overlaps? [rectangle-a rectangle-b]
  (.overlaps ^Rectangle rectangle-a
             ^Rectangle rectangle-b))

(defn contains? [^Rectangle rectangle [x y]]
  (.contains rectangle x y))
