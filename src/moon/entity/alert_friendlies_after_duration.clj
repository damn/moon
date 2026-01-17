(ns moon.entity.alert-friendlies-after-duration
  (:require [moon.entity :as entity]
            [moon.timer :as timer]
            [moon.world.grid :as grid]))

(defmethod entity/tick :entity/alert-friendlies-after-duration
  [[_k {:keys [counter faction]}]
   eid
   {:keys [ctx/world]}]
  (let [{:keys [world/elapsed-time
                world/grid]} world]
    (when (timer/stopped? elapsed-time counter)
      (cons [:tx/mark-destroyed eid]
            (for [friendly-eid (->> {:position (:body/position (:entity/body @eid))
                                     :radius 4}
                                    (grid/circle->entities grid)
                                    (filter #(= (:entity/faction @%) faction)))]
              [:tx/event friendly-eid :alert])))))
