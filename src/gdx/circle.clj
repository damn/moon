(ns gdx.circle
  (:refer-clojure :exclude [new])
  (:require [com.badlogic.gdx.math.circle :as circle]
            [com.badlogic.gdx.math.intersector :as intersector]))

(defn new [x y radius]
  (circle/new x y radius))

(defn overlaps [circle rectangle]
  (intersector/overlaps circle rectangle))
