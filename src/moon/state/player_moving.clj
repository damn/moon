(ns moon.state.player-moving
  (:require [moon.stats :as stats]
            [moon.input :as input]))

(defn- speed [{:keys [entity/stats]}]
  (or (stats/get-stat-value stats :stats/movement-speed)
      0))

(defn create
  [[_k movement-vector] eid _ctx]
  {:movement-vector movement-vector})

(defn enter
  [[_k {:keys [movement-vector]}] eid]
  [[:tx/assoc eid :entity/movement {:direction movement-vector
                                    :speed (or (stats/get-stat-value (:entity/stats @eid) :stats/movement-speed)
                                               0)}]])

(defn exit
  [_ eid _ctx]
  [[:tx/dissoc eid :entity/movement]])

(defn cursor
  [_ _eid _ctx]
  :cursors/walking)

(defn pause-game?
  [_]
  false)

(defn handle-input
  [_ eid {:keys [ctx/input]}]
  (if-let [movement-vector (input/player-movement-vector input)]
    [[:tx/assoc eid :entity/movement {:direction movement-vector
                                      :speed (speed @eid)}]]
    [[:tx/event eid :no-movement-input]]))
