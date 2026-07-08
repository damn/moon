(ns clojure.gdx.math.circle
  (:refer-clojure :exclude [new])
  (:import (com.badlogic.gdx.math Circle)))

(defn new [{:keys [position radius]}]
  (Circle. (float (position 0))
           (float (position 1))
           radius))
