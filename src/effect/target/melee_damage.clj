(ns effect.target.melee-damage
  (:require [game.effect :as effect]
            [moon.stats :as stats]))

(defn- entity->melee-damage [{:keys [entity/stats]}]
  (let [strength (or (stats/get-stat-value stats :stats/strength) 0)]
    {:damage/min-max [strength strength]}))

(defn- melee-damage-effect [entity]
  [:effects.target/damage (entity->melee-damage entity)])

(defmethod effect/applicable? :effects.target/melee-damage
  [_ {:keys [effect/source] :as effect-ctx}]
  (effect/applicable? (melee-damage-effect @source) effect-ctx))

(defmethod effect/handle :effects.target/melee-damage
  [_ {:keys [effect/source] :as effect-ctx} ctx]
  (effect/handle (melee-damage-effect @source) effect-ctx ctx))
