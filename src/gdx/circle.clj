(ns gdx.circle
  (:refer-clojure :exclude [new])
  (:import (com.badlogic.gdx.math Circle
                                  Intersector
                                  Rectangle)))

(defn new [x y radius]
  (Circle. (float x) (float y) (float radius)))

(defn overlaps [circle rectangle]
  (Intersector/overlaps ^Circle circle ^Rectangle rectangle))
