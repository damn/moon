(ns clojure.usable-state
  (:require [clojure.is-applicable :as applicable?]
            [clojure.not-enough-mana :as not-enough-mana?]))

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
