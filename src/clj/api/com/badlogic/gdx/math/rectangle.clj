(ns clj.api.com.badlogic.gdx.math.rectangle
  (:refer-clojure :exclude [contains?])
  (:import (com.badlogic.gdx.math Rectangle)))

(defn create [x y width height]
  (Rectangle. x y width height))

(defn overlaps? [^Rectangle rectangle ^Rectangle other-rectangle]
  (.overlaps rectangle other-rectangle))

(defn contains? [^Rectangle rectangle [x y]]
  (.contains rectangle x y))
