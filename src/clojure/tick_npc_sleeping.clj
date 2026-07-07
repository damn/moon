(ns clojure.tick-npc-sleeping
  (:require [clojure.nearest-enemy-distance :refer [nearest-enemy-distance]]
            [clojure.get-stat-value :refer [get-stat-value]]))

(defn f
  [_ eid {:keys [ctx/grid]}]
  (let [entity @eid]
    (when-let [distance (nearest-enemy-distance grid entity)]
      (when (<= distance (get-stat-value (:entity/stats entity) :stats/aggro-range))
        [[:tx/event eid :alert]]))))
