(ns clojure.stats.calc-damage
  (:require [clojure.val-max.apply-max :as apply-max]
            [clojure.val-max.apply-min :as apply-min]))

(defn f
  ([source target damage]
   (update (f source damage)
           :damage/min-max
           apply-max/f
           (:stats/modifiers target)
           :modifier/damage-receive-max))
  ([source damage]
   (update damage
           :damage/min-max
           #(-> %
                (apply-min/f (:stats/modifiers source) :modifier/damage-deal-min)
                (apply-max/f (:stats/modifiers source) :modifier/damage-deal-max)))))
