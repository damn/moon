(ns moon.entity.skills
  (:require [moon.entity :as entity]
            [moon.timer :as timer]))

(defmethod entity/after-create :entity/skills
  [[_k skills] eid _ctx]
  (cons [:tx/assoc eid :entity/skills nil]
        (for [skill skills]
          [:tx/add-skill eid skill])))

(defmethod entity/tick :entity/skills
  [[_k skills] eid {:keys [ctx/world]}]
  (for [{:keys [skill/cooling-down?] :as skill} (vals skills)
        :when (and cooling-down?
                   (timer/stopped? (:world/elapsed-time world) cooling-down?))]
    [:tx/assoc-in eid [:entity/skills (:property/id skill) :skill/cooling-down?] false]))
