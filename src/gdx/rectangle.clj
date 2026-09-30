(ns gdx.rectangle
  (:import (com.badlogic.gdx.math Rectangle)))

(defn create [x y width height]
  (Rectangle. (float x) (float y) (float width) (float height)))

(defn overlaps [a b]
  (.overlaps ^Rectangle a ^Rectangle b))

(defn contains [rectangle x y]
  (.contains ^Rectangle rectangle (float x) (float y)))
