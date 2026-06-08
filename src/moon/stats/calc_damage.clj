(ns moon.stats.calc-damage
  (:require [moon.stats :refer [apply-min
                                apply-max]]))

(defn f
  ([source target damage]
   (update (f source damage)
           :damage/min-max
           apply-max
           (:stats/modifiers target)
           :modifier/damage-receive-max))
  ([source damage]
   (update damage
           :damage/min-max
           #(-> %
                (apply-min (:stats/modifiers source) :modifier/damage-deal-min)
                (apply-max (:stats/modifiers source) :modifier/damage-deal-max)))))
