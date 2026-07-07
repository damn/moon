(ns clojure.k-state-enter.npc-moving
  (:require [clojure.get-stat-value :refer [get-stat-value]]))

(defn f
  [{:keys [movement-vector]} eid]
  [[:tx/assoc eid :entity/movement {:direction movement-vector
                                    :speed (or (get-stat-value (:entity/stats @eid) :stats/movement-speed)
                                               0)}]])
