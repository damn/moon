(ns entity.tick.skills
  (:require [game.entity :as entity]
            [moon.timer :as timer]))

(defmethod entity/tick :entity/skills
  [[_k skills] eid {:keys [ctx/elapsed-time]}]
  (for [{:keys [skill/cooling-down?] :as skill} (vals skills)
        :when (and cooling-down?
                   (timer/stopped? elapsed-time cooling-down?))]
    [:tx/assoc-in eid [:entity/skills (:property/id skill) :skill/cooling-down?] false]))
