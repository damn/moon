(ns moon.entity.state.player-moving
  (:require [moon.entity.state :as state]
            [moon.entity.stats :as stats]))

(defmethod state/create :player-moving
  [[_k movement-vector] eid _world]
  {:movement-vector movement-vector})

(defmethod state/enter :player-moving
  [[_k {:keys [movement-vector]}] eid]
  [[:tx/assoc eid :entity/movement {:direction movement-vector
                                    :speed (or (stats/get-stat-value (:entity/stats @eid) :stats/movement-speed)
                                               0)}]])

(defmethod state/exit :player-moving
  [_ eid _ctx]
  [[:tx/dissoc eid :entity/movement]])

(defmethod state/cursor :player-moving
  [_ _eid _ctx]
  :cursors/walking)
