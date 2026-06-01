(ns entity.tick.npc-sleeping
  (:require [moon.grid :as grid]
            [moon.stats :as stats]))

(defn f
  [_ eid {:keys [ctx/grid]}]
  (let [entity @eid]
    (when-let [distance (grid/nearest-enemy-distance grid entity)]
      (when (<= distance (stats/get-stat-value (:entity/stats entity) :stats/aggro-range))
        [[:tx/event eid :alert]]))))
