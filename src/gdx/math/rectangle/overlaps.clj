(ns gdx.math.rectangle.overlaps
  (:require [com.badlogic.gdx.math.rectangle :as rectangle]))

(defn overlaps? [a b]
  (rectangle/overlaps? a b))
