(ns gdx.math.rectangle
  (:require [com.badlogic.gdx.math.rectangle :as rectangle]))

(defn create [x y width height]
  (rectangle/new x y width height))

(defn overlaps [a b]
  (rectangle/overlaps a b))

(defn contains [rectangle x y]
  (rectangle/contains rectangle x y))
