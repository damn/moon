(ns entity.tick.alert-friendlies-after-duration
  (:require [game.entity :as entity]
            [moon.timer :as timer]
            [moon.grid :as grid]))

(defmethod entity/tick :entity/alert-friendlies-after-duration
  [[_k {:keys [counter faction]}]
   eid
   {:keys [ctx/elapsed-time
           ctx/grid]}]
  (when (timer/stopped? elapsed-time counter)
    (cons [:tx/mark-destroyed eid]
          (for [friendly-eid (->> {:position (:body/position (:entity/body @eid))
                                   :radius 4}
                                  (grid/circle->entities grid)
                                  (filter #(= (:entity/faction @%) faction)))]
            [:tx/event friendly-eid :alert]))))
