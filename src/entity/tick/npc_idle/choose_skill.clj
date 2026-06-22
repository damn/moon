(ns entity.tick.npc-idle.choose-skill
  (:require [moon.effect.is-useful :as useful?]
            [game.skill :as skill]
            [moon.effect.is-applicable :as applicable?]))

(defn f [ctx entity effect-ctx]
  (->> entity
       :entity/skills
       vals
       (sort-by :skill/cost)
       reverse
       (filter #(and (= :usable (skill/usable-state % entity effect-ctx))
                     (->> (:skill/effects %)
                          (filter (fn [e] (applicable?/f e effect-ctx)))
                          (some (fn [e] (useful?/f e effect-ctx ctx))))))
       first))
