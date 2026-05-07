(ns com.badlogic.gdx.math.rectangle
  (:refer-clojure :exclude [contains?])
  (:import (com.badlogic.gdx.math Rectangle)))

(defn create [x y width height]
  (Rectangle. x y width height))

(defn overlaps? [^Rectangle rect ^Rectangle other-rect]
  (.overlaps rect other-rect))

(defn contains? [^Rectangle rect [x y]]
  (.contains rect x y))
