(ns handle-input.player-moving
  (:require [game.ctx.player-movement-vector :refer [player-movement-vector]]
            [moon.stats.get-stat-value :refer [get-stat-value]]))

(defn- creature-speed [{:keys [entity/stats]}]
  (or (get-stat-value stats :stats/movement-speed)
      0))

(defn f
  [eid ctx]
  (if-let [movement-vector (player-movement-vector ctx)]
    [[:tx/assoc eid :entity/movement {:direction movement-vector
                                      :speed (creature-speed @eid)}]]
    [[:tx/event eid :no-movement-input]]))
