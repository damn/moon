(ns clojure.math.circle.overlaps
  (:require [gdx.math.circle :as circle]
            [gdx.math.intersector.overlaps :as intersector]))

(defn overlaps?  [circle rectangle]
  (intersector/overlaps? (circle/create circle)
                         rectangle))
