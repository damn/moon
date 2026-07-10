(ns com.badlogic.gdx.math.circle
  (:refer-clojure :exclude [new])
  (:import (com.badlogic.gdx.math Circle)))

(defn new [x y radius]
  (Circle. (float x) (float y) (float radius)))
