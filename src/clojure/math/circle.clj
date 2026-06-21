(ns clojure.math.circle
  (:import (com.badlogic.gdx.math Circle)))

(defn create [{:keys [position radius]}]
  (Circle. (position 0)
           (position 1)
           radius))
