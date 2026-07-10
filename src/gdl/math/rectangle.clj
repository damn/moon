(ns gdl.math.rectangle
  (:refer-clojure :exclude [contains?])
  (:require [com.badlogic.gdx.math.rectangle :as rectangle]))

(defn contains? [rectangle [x y]]
  (rectangle/contains rectangle x y))
