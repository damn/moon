(ns moon.entity.string-effect
  (:require [moon.timer :as timer]))

(defn tick
  [[_k {:keys [counter]}]
   eid
   {:keys [ctx/elapsed-time]}]
  (when (timer/stopped? elapsed-time counter)
    [[:tx/dissoc eid :entity/string-effect]]))

(defn render
  [[_k {:keys [text]}] entity {:keys [ctx/world-unit-scale]}]
  (let [[x y] (:body/position (:entity/body entity))]
    [[:draw/text {:text text
                  :x x
                  :y (+ y
                        (/ (:body/height (:entity/body entity)) 2)
                        (* 5 world-unit-scale))
                  :scale 2
                  :up? true}]]))
