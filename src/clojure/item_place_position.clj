(ns clojure.item-place-position
  (:require [clojure.v2.add :as add]
            [clojure.v2.direction :as direction]
            [clojure.v2.distance :as distance]
            [clojure.v2.scale :as scale]))

; It is possible to put items out of sight, losing them.
; Because line of sight checks center of entity only, not corners
; this is okay, you have thrown the item over a hill, thats possible.
(defn item-place-position [{:keys [ctx/world-mouse-position]} player-entity]
  (assert world-mouse-position)
  (let [player-position (:body/position (:entity/body player-entity))
        ; so you cannot put it out of your own reach
        maxrange (- (:entity/click-distance-tiles player-entity) 0.1)]
    (add/f player-position
           (scale/f (direction/f player-position world-mouse-position)
                    (min maxrange
                         (distance/f player-position world-mouse-position))))))
