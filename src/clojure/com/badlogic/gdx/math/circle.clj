(ns clojure.com.badlogic.gdx.math.circle
  (:import (com.badlogic.gdx.math Circle)))

(defn create [x y radius]
  (Circle. (float x) (float y) (float radius)))
