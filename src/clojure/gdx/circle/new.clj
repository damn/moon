(ns clojure.gdx.circle.new
  (:import (com.badlogic.gdx.math Circle)))

(defn f [x y radius]
  (Circle. (float x) (float y) radius))
