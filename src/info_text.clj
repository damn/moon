(ns info-text
  (:require [clojure.string :as str]
            [moon.coll :as coll]
            [moon.mods :as mods]
            [moon.number :as number]
            [moon.stats :as stats]
            [moon.string :as string]
            [moon.timer :as timer]))

(def info
  {:k->fn {:creature/level (fn [v _elapsed-time]
                              (str "Level: " v))
           :entity/stats (fn [entity-stats _elapsed-time]
                           (stats/format-text entity-stats))
           :effects.target/convert (fn [_ _elapsed-time]
                                    "Converts target to your side.")
           :effects.target/damage (fn [{[min max] :damage/min-max} _elapsed-time]
                                    (str min "-" max " damage"))
           :effects.target/kill (fn [_ _elapsed-time]
                                  "Kills target")
           :effects.target/melee-damage (fn [_ _elapsed-time]
                                          "Damage based on entity strength.")
           :effects.target/spiderweb (fn [_ _elapsed-time]
                                       "Spiderweb slows 50% for 5 seconds.")
           :effects.target/stun (fn [duration _elapsed-time]
                                  (str "Stuns for " (number/readable duration) " seconds"))
           :effects/spawn (fn [{:keys [property/pretty-name]} _elapsed-time]
                            (str "Spawns a " pretty-name))
           :effects/target-all (fn [_ _elapsed-time]
                                 "All visible targets")
           :entity/delete-after-duration (fn [counter elapsed-time]
                                             (str "Remaining: " (number/readable (timer/ratio elapsed-time counter)) "/1"))
           :entity/faction (fn [faction _elapsed-time]
                             (str "Faction: " (name faction)))
           :entity/fsm (fn [fsm _elapsed-time]
                          (str "State: " (name (:state fsm))))
           :stats/modifiers (fn [mods _elapsed-time]
                              (mods/format-text mods))
           :entity/skills (fn [skills _elapsed-time]
                            (when (seq skills)
                              (str "Skills: " (str/join "," (map name (keys skills))))))
           :entity/species (fn [species _elapsed-time]
                             (str "Creature - " (str/capitalize (name species))))
           :entity/temp-modifier (fn [{:keys [counter]} elapsed-time]
                                    (str "Spiderweb - remaining: " (number/readable (timer/ratio elapsed-time counter)) "/1"))
           :projectile/piercing? (fn [_ _elapsed-time]
                                   "Piercing")
           :property/pretty-name (fn [v _elapsed-time]
                                    v)
           :skill/cooling-down? (fn [counter elapsed-time]
                                   (str "Cooldown: " (number/readable (timer/ratio elapsed-time counter)) "/1"))
           :skill/action-time (fn [v _elapsed-time]
                                 (str "Action-Time: " (number/readable v) " seconds"))
           :skill/action-time-modifier-key (fn [v _elapsed-time]
                                              (case v
                                                :stats/cast-speed "Spell"
                                                :stats/attack-speed "Attack"))
           :skill/cooldown (fn [v _elapsed-time]
                             (str "Cooldown: " (number/readable v) " seconds"))
           :skill/cost (fn [v _elapsed-time]
                          (str "Cost: " v " Mana"))
           :maxrange (fn [v _elapsed-time]
                       (str "Range: " v " Meters."))}
   :k-order [:property/pretty-name
             :skill/action-time-modifier-key
             :skill/action-time
             :skill/cooldown
             :skill/cost
             :skill/effects
             :entity/species
             :creature/level
             :entity/stats
             :entity/delete-after-duration
             :projectile/piercing?
             :entity/projectile-collision
             :maxrange
             :entity-effects]
   :k->colors {:property/pretty-name "PRETTY_NAME"
               :stats/modifiers "CYAN"
               :maxrange "LIGHT_GRAY"
               :creature/level "GRAY"
               :projectile/piercing? "LIME"
               :skill/action-time-modifier-key "VIOLET"
               :skill/action-time "GOLD"
               :skill/cooldown "SKY"
               :skill/cost "CYAN"
               :entity/delete-after-duration "LIGHT_GRAY"
               :entity/faction "SLATE"
               :entity/fsm "YELLOW"
               :entity/species "LIGHT_GRAY"
               :entity/temp-modifier "LIGHT_GRAY"}})

(defn info-text
  [entity elapsed-time]
  (let [{:keys [k->fn
                k-order
                k->colors]} info
        component-info (fn [[k v]]
                         (let [s (if-let [info-fn (k->fn k)]
                                   (str (info-fn v elapsed-time)))]
                           (if-let [color (k->colors k)]
                             (str "[" color "]" s "[]")
                             s)))]
    (->> entity
         (coll/sort-by-k-order k-order)
         (keep (fn [{k 0 v 1 :as component}]
                 (str (try (component-info component)
                           (catch Throwable _t
                             (str "*info-error* " k)))
                      (when (map? v)
                        (str "\n" (info-text v elapsed-time))))))
         (str/join "\n")
         string/remove-newlines)))
