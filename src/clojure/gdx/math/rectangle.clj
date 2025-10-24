(ns clojure.gdx.math.rectangle
  (:refer-clojure :exclude [contains?])
  (:import (com.badlogic.gdx.math Rectangle)))

(defn create [x y width height]
  (Rectangle. x y width height))

(defn contains?
  [rectangle [x y]]
  (Rectangle/.contains rectangle x y))
