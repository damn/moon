(ns clojure.gdx.math.circle
  (:refer-clojure :exclude [new])
  (:require [com.badlogic.gdx.math.circle :as circle]))

(defn new [x y radius]
  (circle/new x y radius))
