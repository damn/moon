(ns info.entity
  (:require [clojure.sort-by-k-order :refer [sort-by-k-order]]
            [clojure.string :as str]
            [clojure.string.remove-newlines :refer [remove-newlines]]
            [game.info :as info]
            [game.constants :refer [k->colors k-order]]
            [moon.number :as number]
            [moon.timer :as timer]
            info.entity.stats
            info.stats.modifiers
            info.effects.target.convert
            info.effects.target.damage
            info.effects.target.kill
            info.effects.target.melee-damage
            info.effects.target.spiderweb
            info.effects.target.stun
            info.creature.level))

(def info-fns-mapping
  {
   :creature/level info.creature.level/f
   :entity/stats info.entity.stats/f
   :effects.target/convert info.effects.target.convert/f
   :effects.target/damage info.effects.target.damage/f
   :effects.target/kill info.effects.target.kill/f
   :effects.target/melee-damage info.effects.target.melee-damage/f
   :effects.target/spiderweb info.effects.target.spiderweb/f
   :effects.target/stun info.effects.target.stun/f
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

(defmethod info/text :info/entity [entity ctx]
  (let [component-info (fn [[k v]]
                         (let [s (if-let [info-fn (info-fns-mapping k)]
                                   (do
                                    (str #_k #_" - " (info-fn v ctx))))]
                           (if-let [color (k->colors k)]
                             (str "[" color "]" s "[]")
                             s)))]
    (->> entity
         (sort-by-k-order k-order)
         (keep (fn [{k 0 v 1 :as component}]
                 (str (try (component-info component)
                           (catch Throwable t
                             (str "*info-error* " k))) ; TODO this try/catch FIXME design error
                      (when (map? v)
                        (str "\n" (info/text v ctx))))))
         (str/join "\n")
         remove-newlines)))
