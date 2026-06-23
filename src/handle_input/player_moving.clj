(ns handle-input.player-moving
  (:require [ctx.player-movement-vector :refer [player-movement-vector]]
            [moon.creature.speed :as speed]))

(defn f
  [eid ctx]
  (if-let [movement-vector (player-movement-vector ctx)]
    [[:tx/assoc eid :entity/movement {:direction movement-vector
                                      :speed (speed/f @eid)}]]
    [[:tx/event eid :no-movement-input]]))
