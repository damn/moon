(ns moon.entity.string-effect
  (:require [moon.entity :as entity]
            [moon.timer :as timer]))

(defmethod entity/tick :entity/string-effect
  [[_k {:keys [counter]}]
   eid
   {:keys [world/elapsed-time]}]
  (when (timer/stopped? elapsed-time counter)
    [[:tx/dissoc eid :entity/string-effect]]))

(defmethod entity/render :entity/string-effect
  [[_k {:keys [text]}] entity {:keys [ctx/graphics]}]
  (let [[x y] (:body/position (:entity/body entity))]
    [[:draw/text {:text text
                  :x x
                  :y (+ y
                        (/ (:body/height (:entity/body entity)) 2)
                        (* 5 (:graphics/world-unit-scale graphics)))
                  :scale 2
                  :up? true}]]))
