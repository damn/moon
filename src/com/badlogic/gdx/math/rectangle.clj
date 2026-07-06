(ns com.badlogic.gdx.math.rectangle
  (:refer-clojure :exclude [new contains])
  (:import (com.badlogic.gdx.math Rectangle)))

(defn new [x y width height]
  (Rectangle. (float x) (float y) (float width) (float height)))

(defn overlaps [^Rectangle a ^Rectangle b]
  (.overlaps a b))

(defn contains [^Rectangle rectangle [x y]]
  (.contains rectangle (float x) (float y)))
