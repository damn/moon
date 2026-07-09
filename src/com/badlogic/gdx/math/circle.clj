(ns com.badlogic.gdx.math.circle
  (:refer-clojure :exclude [new])
  (:import (com.badlogic.gdx.math Circle)))

(defn new [{:keys [position radius]}] ; TODO ??? no logic?
  (Circle. (float (position 0))
           (float (position 1))
           radius))
