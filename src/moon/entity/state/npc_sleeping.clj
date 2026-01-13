(ns moon.entity.state.npc-sleeping
  (:require [moon.entity :as entity]
            [moon.entity.stats :as stats]
            [moon.world.grid :as grid]))

(defmethod entity/tick :npc-sleeping
  [_ eid {:keys [world/grid]}]
  (let [entity @eid]
    (when-let [distance (grid/nearest-enemy-distance grid entity)]
      (when (<= distance (stats/get-stat-value (:entity/stats entity) :stats/aggro-range))
        [[:tx/event eid :alert]]))))

(defmethod entity/render :npc-sleeping
  [_ {:keys [entity/body]} _ctx]
  (let [[x y] (:body/position body)]
    [[:draw/text {:text "zzz"
                  :x x
                  :y (+ y (/ (:body/height body) 2))
                  :up? true}]]))
