(ns handle-input.player-moving
  (:require [game.ctx :as ctx]
            [game.state :as state]
            [moon.stats :as stats]))

(defn- creature-speed [{:keys [entity/stats]}]
  (or (stats/get-stat-value stats :stats/movement-speed)
      0))

(defmethod state/handle-input :player-moving
  [_ eid ctx]
  (if-let [movement-vector (ctx/player-movement-vector ctx)]
    [[:tx/assoc eid :entity/movement {:direction movement-vector
                                      :speed (creature-speed @eid)}]]
    [[:tx/event eid :no-movement-input]]))
