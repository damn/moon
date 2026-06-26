(ns com.badlogic.gdx.math.rectangle
  (:import (com.badlogic.gdx.math Rectangle))
  (:refer-clojure :exclude [contains?]))

(defn create [x y w h]
  (Rectangle. x y w h))

(defn overlaps? [^Rectangle a ^Rectangle b]
  (.overlaps a b))

(defn contains? [^Rectangle rectangle x y]
  (.contains rectangle x y))
