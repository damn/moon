(ns clojure.item-place-position
  (:require [moon.v2 :as v2]))

; It is possible to put items out of sight, losing them.
; Because line of sight checks center of entity only, not corners
; this is okay, you have thrown the item over a hill, thats possible.
(defn item-place-position [{:keys [ctx/world-mouse-position]} player-entity]
  (assert world-mouse-position)
  (let [player-position (:body/position (:entity/body player-entity))
        ; so you cannot put it out of your own reach
        maxrange (- (:entity/click-distance-tiles player-entity) 0.1)]
    (v2/add player-position
           (v2/scale (v2/direction player-position world-mouse-position)
                     (min maxrange
                          (v2/distance player-position world-mouse-position))))))
