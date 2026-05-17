(ns com.badlogic.gdx.math.rectangle
  (:import (com.badlogic.gdx.math Rectangle)))

(defn create [x y width height]
  (Rectangle. x y width height))

(defn overlaps? [rectangle-a rectangle-b]
  (.overlaps ^Rectangle rectangle-a
             ^Rectangle rectangle-b))
