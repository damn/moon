(ns moon.skill.usable-state
  (:require [moon.effect.is-applicable :as applicable?]
            [moon.stats.not-enough-mana :as not-enough-mana?]))

(defn f
  [{:keys [skill/cooling-down? skill/effects] :as skill}
   entity
   effect-ctx]
  (cond
   cooling-down?
   :cooldown

   (not-enough-mana?/f (:entity/stats entity) skill)
   :not-enough-mana

   (not (seq (filter #(applicable?/f % effect-ctx) effects)))
   :invalid-params

   :else
   :usable))
