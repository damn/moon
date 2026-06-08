(ns game.skill
  (:require [game.effect :as effect]
            [moon.stats.not-enough-mana :as not-enough-mana?]))

(defn valid? [skill]
  (= #{:property/id
       :property/pretty-name
       :entity/image
       :skill/action-time-modifier-key
       :skill/action-time
       :skill/start-action-sound
       :skill/effects
       :skill/cooldown
       :skill/cost}
     (set (keys skill))))

(defn usable-state
  [{:keys [skill/cooling-down? skill/effects] :as skill}
   entity
   effect-ctx]
  (cond
   cooling-down?
   :cooldown

   (not-enough-mana?/f (:entity/stats entity) skill)
   :not-enough-mana

   (not (seq (filter #(effect/applicable? % effect-ctx) effects)))
   :invalid-params

   :else
   :usable))
