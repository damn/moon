(ns skill.usable-state
  (:require [effect.applicable :refer [applicable?]]
            [moon.stats :as stats]))

(defn usable-state
  [{:keys [skill/cooling-down? skill/effects] :as skill}
   entity
   effect-ctx]
  (cond
   cooling-down?
   :cooldown

   (stats/not-enough-mana? (:entity/stats entity) skill)
   :not-enough-mana

   (not (seq (filter #(applicable? % effect-ctx) effects)))
   :invalid-params

   :else
   :usable))
