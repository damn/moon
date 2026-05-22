(ns game.entity.skills
  (:require [moon.effect :as effect]
            [moon.entity :as entity]
            [moon.skill]
            [moon.stats :as stats]
            [moon.timer :as timer]))

(defmethod entity/tick :entity/skills
  [[_k skills] eid {:keys [ctx/elapsed-time]}]
  (for [{:keys [skill/cooling-down?] :as skill} (vals skills)
        :when (and cooling-down?
                   (timer/stopped? elapsed-time cooling-down?))]
    [:tx/assoc-in eid [:entity/skills (:property/id skill) :skill/cooling-down?] false]))

(defmethod entity/after-create :entity/skills ; TODO same like inventory ?
  [[_k skills] eid _ctx]
  (cons [:tx/assoc eid :entity/skills nil]
        (for [skill skills]
          [:tx/add-skill eid skill])))

(.bindRoot #'moon.skill/usable-state
           (fn [{:keys [skill/cooling-down? skill/effects] :as skill}
                entity
                effect-ctx]
             (cond
              cooling-down?
              :cooldown

              (stats/not-enough-mana? (:entity/stats entity) skill)
              :not-enough-mana

              (not (seq (filter #(effect/applicable? % effect-ctx) effects)))
              :invalid-params

              :else
              :usable)))
