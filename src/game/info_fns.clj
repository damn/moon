(ns game.info-fns
  (:require [clojure.string :as str]
            [moon.number :as number]
            [moon.timer :as timer]
            info.entity.stats
            info.stats.modifiers))

(def mapping
  {
   :creature/level (fn [v _ctx]
                     (str "Level: " v))
   :entity/stats info.entity.stats/f
   :effects.target/convert (fn [_ _ctx]
                             "Converts target to your side.")
   :effects.target/damage (fn [{[min max] :damage/min-max} _ctx]
                            (str min "-" max " damage")
                            #_(if source
                                (let [modified (stats/damage @source damage)]
                                  (if (= damage modified)
                                    (damage/info-text damage)
                                    (str (damage/info-text damage) "\nModified: " (damage/info modified))))
                                (damage/info-text damage)) )
   :effects.target/kill (fn [_ _ctx]
                          "Kills target")
   :effects.target/melee-damage (fn [_ _ctx]
                                  (str "Damage based on entity strength."
                                       #_(when source
                                           (str "\n" (damage-info (entity->melee-damage @source))))))
   :effects.target/spiderweb (fn [_ _ctx]
                               "Spiderweb slows 50% for 5 seconds.")
   :effects.target/stun (fn [duration _ctx]
                          (str "Stuns for " (number/readable duration) " seconds"))
   :effects/spawn (fn [{:keys [property/pretty-name]} _ctx]
                    (str "Spawns a " pretty-name))
   :effects/target-all (fn [_ _ctx]
                         "All visible targets")
   :entity/delete-after-duration (fn [counter {:keys [ctx/elapsed-time]}]
                                   (str "Remaining: " (number/readable (timer/ratio elapsed-time counter)) "/1"))
   :entity/faction (fn [faction _ctx]
                     (str "Faction: " (name faction)))
   :entity/fsm (fn [fsm _ctx]
                 (str "State: " (name (:state fsm))))
   :stats/modifiers info.stats.modifiers/info
   :entity/skills (fn [skills _ctx]
                    ; => recursive info-text leads to endless text wall
                    (when (seq skills)
                      (str "Skills: " (str/join "," (map name (keys skills))))))
   :entity/species (fn [species _ctx]
                     (str "Creature - " (str/capitalize (name species))))
   :entity/temp-modifier (fn [{:keys [counter]} {:keys [ctx/elapsed-time]}]
                           (str "Spiderweb - remaining: " (number/readable (timer/ratio elapsed-time counter)) "/1"))
   :projectile/piercing? (fn [_ _ctx]
                           "Piercing")
   :property/pretty-name (fn [v _ctx]
                           v)
   :skill/cooling-down? (fn [counter {:keys [ctx/elapsed-time]}]
                          (str "Cooldown: " (number/readable (timer/ratio elapsed-time counter)) "/1"))
   :skill/action-time (fn [v _ctx]
                        (str "Action-Time: " (number/readable v) " seconds"))
   :skill/action-time-modifier-key (fn [v _ctx]
                                     (case v
                                       :stats/cast-speed "Spell"
                                       :stats/attack-speed "Attack"))
   :skill/cooldown (fn [v _ctx]
                     (str "Cooldown: " (number/readable v) " seconds"))
   :skill/cost (fn [v _ctx]
                 (str "Cost: " v " Mana"))
   :maxrange (fn [v _ctx]
               (str "Range: " v " Meters."))
   })
