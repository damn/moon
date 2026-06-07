(ns clojure.math.circle.overlaps
  (:require [com.badlogic.gdx.math.circle :as circle]
            [com.badlogic.gdx.math.intersector.overlaps :as intersector]))

(defn overlaps?  [circle rectangle]
  (intersector/overlaps? (circle/create circle)
                         rectangle))
