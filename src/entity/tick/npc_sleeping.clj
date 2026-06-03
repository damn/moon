(ns entity.tick.npc-sleeping
  (:require [moon.grid :as grid]
            [moon.stats.get-stat-value :refer [get-stat-value]]))

(defn f
  [_ eid {:keys [ctx/grid]}]
  (let [entity @eid]
    (when-let [distance (grid/nearest-enemy-distance grid entity)]
      (when (<= distance (get-stat-value (:entity/stats entity) :stats/aggro-range))
        [[:tx/event eid :alert]]))))
