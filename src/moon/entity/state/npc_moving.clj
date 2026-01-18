(ns moon.entity.state.npc-moving
  (:require [moon.entity.stats :as stats]
            [moon.timer :as timer]))

(def reaction-time-multiplier 0.016)

(defn create
  [[_k movement-vector] eid {:keys [ctx/elapsed-time]}]
  {:movement-vector movement-vector
   :timer (timer/create elapsed-time
                        (* (stats/get-stat-value (:entity/stats @eid) :stats/reaction-time)
                           reaction-time-multiplier))})

(defn tick
  [[_k {:keys [timer]}] eid {:keys [ctx/elapsed-time]}]
  (when (timer/stopped? elapsed-time timer)
    [[:tx/event eid :timer-finished]]))

(defn enter
  [[_k {:keys [movement-vector]}] eid]
  [[:tx/assoc eid :entity/movement {:direction movement-vector
                                    :speed (or (stats/get-stat-value (:entity/stats @eid) :stats/movement-speed)
                                               0)}]])

(defn exit
  [_ eid _ctx]
  [[:tx/dissoc eid :entity/movement]])
