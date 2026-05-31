(ns entity.state.enter.player-moving
  (:require [game.state :as state]
            [moon.stats :as stats]))

(defmethod state/enter :player-moving
  [[_k {:keys [movement-vector]}] eid]
  [[:tx/assoc eid :entity/movement {:direction movement-vector
                                    :speed (or (stats/get-stat-value (:entity/stats @eid) :stats/movement-speed)
                                               0)}]])
