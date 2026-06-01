(ns clojure.gdx.math.circle
  (:import (com.badlogic.gdx.math Circle)))

(defn create [x y radius]
  (Circle. x y radius))
