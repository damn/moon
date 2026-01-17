(ns moon.entity.string-effect
  (:require [moon.entity :as entity]
            [moon.timer :as timer]))

(defmethod entity/tick :entity/string-effect
  [[_k {:keys [counter]}]
   eid
   {:keys [ctx/world]}]
  (when (timer/stopped? (:world/elapsed-time world) counter)
    [[:tx/dissoc eid :entity/string-effect]]))

(defmethod entity/render :entity/string-effect
  [[_k {:keys [text]}] entity {:keys [ctx/world-unit-scale]}]
  (let [[x y] (:body/position (:entity/body entity))]
    [[:draw/text {:text text
                  :x x
                  :y (+ y
                        (/ (:body/height (:entity/body entity)) 2)
                        (* 5 world-unit-scale))
                  :scale 2
                  :up? true}]]))
