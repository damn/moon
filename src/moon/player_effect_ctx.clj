(ns moon.player-effect-ctx
  (:require [clojure.math.vector2.direction :as direction]))

(defn f [mouseover-eid world-mouse-position player-eid]
  (let [target-position (or (and mouseover-eid
                                 (:body/position (:entity/body @mouseover-eid)))
                            world-mouse-position)]
    {:effect/source player-eid
     :effect/target mouseover-eid
     :effect/target-position target-position
     :effect/target-direction (direction/f (:body/position (:entity/body @player-eid))
                                           target-position)}))
