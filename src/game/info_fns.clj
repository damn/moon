(ns game.info-fns
  (:require [clojure.math :as math]
            [clojure.string :as str]
            [moon.number :as number]
            [moon.ops :as ops]
            [moon.stats :as stats]
            [moon.timer :as timer]))

(defn- ops-info [ops modifier-k]
  (str/join "\n"
            (keep
             (fn [[k v]]
               (when-not (zero? v)
                 (str (case (math/signum v)
                        0.0 ""
                        1.0 "+"
                        -1.0 "")
                      (case k
                        :op/inc  (str v)
                        :op/mult (str v "%"))
                      " "
                      (str/capitalize (name modifier-k)))))
             (ops/sort ops))))

(comment
 (deftest info-texts
   (is (= (ops/info-text {:op/inc -4
                          :op/mult 24}
                         "Strength")
          "-4 Strength\n+24% Strength"))

   (is (= (ops/info-text {:op/inc -4
                          :op/mult 0}
                         "Strength")
          "-4 Strength"))

   (is (= (ops/info-text {:op/mult 35}
                         "Hitpoints")
          "+35% Hitpoints"))

   (is (= (ops/info-text {:op/inc -30
                          :op/mult 5}
                         "Hitpoints")
          "-30 Hitpoints\n+5% Hitpoints")))
 )

(defn stats-modifiers-info [mods]
  (when (seq mods)
    (str/join "\n" (keep (fn [[k ops]]
                           (ops-info ops k)) mods))))


(def ^:private non-val-max-stat-ks
  [:stats/movement-speed
   :stats/aggro-range
   :stats/reaction-time
   :stats/strength
   :stats/cast-speed
   :stats/attack-speed
   :stats/armor-save
   :stats/armor-pierce])

(def mapping
  {:creature/level (fn [v _ctx]
                     (str "Level: " v))

   :entity/stats (fn [stats _ctx]
                   (str/join "\n" (concat
                                   ["*STATS*"
                                    (str "Mana: " (stats/get-mana stats))
                                    (str "Hitpoints: " (stats/get-hitpoints stats))]
                                   (for [stat-k non-val-max-stat-ks]
                                     (str (str/capitalize (name stat-k)) ": "
                                          (stats/get-stat-value stats stat-k))))))

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

   :stats/modifiers (fn [mods _ctx]
                      (stats-modifiers-info mods))

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
               (str "Range: " v " Meters."))})
