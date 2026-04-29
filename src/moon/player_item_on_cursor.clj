(ns moon.player-item-on-cursor
  (:require [clojure.math.vector2 :as v]))

; It is possible to put items out of sight, losing them.
; Because line of sight checks center of entity only, not corners
; this is okay, you have thrown the item over a hill, thats possible.
(defn- item-placement-point* [player target maxrange]
  (v/add player
         (v/scale (v/direction player target)
                  (min maxrange
                       (v/distance player target)))))

(defn item-place-position [world-mouse-position entity]
  (item-placement-point* (:body/position (:entity/body entity))
                         world-mouse-position
                         ; so you cannot put it out of your own reach
                         (- (:entity/click-distance-tiles entity) 0.1)))
