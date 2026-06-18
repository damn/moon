(ns effect.target.melee-damage
  (:require [game.effect :as effect]
            [moon.stats.get-stat-value :refer [get-stat-value]]))

(defn- entity->melee-damage [{:keys [entity/stats]}]
  (let [strength (or (get-stat-value stats :stats/strength) 0)]
    {:damage/min-max [strength strength]}))

(defn- melee-damage-effect [entity]
  [:effects.target/damage (entity->melee-damage entity)])

(defn applicable?
  [_ {:keys [effect/source] :as effect-ctx}]
  (effect/applicable? (melee-damage-effect @source) effect-ctx))

(defn handle
  [_ {:keys [effect/source] :as effect-ctx} ctx]
  (effect/handle (melee-damage-effect @source) effect-ctx ctx))
