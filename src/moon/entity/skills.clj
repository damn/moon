(ns moon.entity.skills
  (:require [moon.entity :as entity]
            [moon.timer :as timer]))

(defmethod entity/after-create :entity/skills
  [[_k skills] eid _world]
  (cons [:tx/assoc eid :entity/skills nil]
        (for [skill skills]
          [:tx/add-skill eid skill])))

(defn set-cooldown [skills skill elapsed-time]
  (assoc-in skills
            [(:property/id skill) :skill/cooling-down?]
            (timer/create elapsed-time (:skill/cooldown skill))))
