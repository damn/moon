(ns clojure.info
  (:require [clojure.get-hitpoints :as get-hitpoints]
            [clojure.get-mana :as get-mana]
            [clojure.get-stat-value :refer [get-stat-value]]
            [clojure.math :as math]
            [clojure.ratio :as ratio]
            [clojure.readable :as readable]
            [clojure.remove-newlines :refer [remove-newlines]]
            [clojure.sort :as sort]
            [clojure.sort-by-k-order :refer [sort-by-k-order]]
            [clojure.string :as str]))

(def ^:private non-val-max-stat-ks
  [:stats/movement-speed
   :stats/aggro-range
   :stats/reaction-time
   :stats/strength
   :stats/cast-speed
   :stats/attack-speed
   :stats/armor-save
   :stats/armor-pierce])

(def info
  {:k->fn {:creature/level (fn [v _ctx]
                              (str "Level: " v))
           :entity/stats (fn [stats _ctx]
                           (str/join "\n" (concat
                                           ["*STATS*"
                                            (str "Mana: " (get-mana/f stats))
                                            (str "Hitpoints: " (get-hitpoints/f stats))]
                                           (for [stat-k non-val-max-stat-ks]
                                             (str (str/capitalize (name stat-k)) ": "
                                                  (get-stat-value stats stat-k))))))
           :effects.target/convert (fn [_ _ctx]
                                    "Converts target to your side.")
           :effects.target/damage (fn [{[min max] :damage/min-max} _ctx]
                                    (str min "-" max " damage"))
           :effects.target/kill (fn [_ _ctx]
                                  "Kills target")
           :effects.target/melee-damage (fn [_ _ctx]
                                          "Damage based on entity strength.")
           :effects.target/spiderweb (fn [_ _ctx]
                                       "Spiderweb slows 50% for 5 seconds.")
           :effects.target/stun (fn [duration _ctx]
                                  (str "Stuns for " (readable/f duration) " seconds"))
           :effects/spawn (fn [{:keys [property/pretty-name]} _ctx]
                            (str "Spawns a " pretty-name))
           :effects/target-all (fn [_ _ctx]
                                 "All visible targets")
           :entity/delete-after-duration (fn [counter {:keys [ctx/elapsed-time]}]
                                             (str "Remaining: " (readable/f (ratio/f elapsed-time counter)) "/1"))
           :entity/faction (fn [faction _ctx]
                             (str "Faction: " (name faction)))
           :entity/fsm (fn [fsm _ctx]
                          (str "State: " (name (:state fsm))))
           :stats/modifiers (fn [mods _ctx]
                              (when (seq mods)
                                (str/join "\n"
                                          (keep (fn [[k ops]]
                                                  (str/join "\n"
                                                            (keep (fn [[op-k v]]
                                                                    (when-not (zero? v)
                                                                      (str (case (math/signum v)
                                                                             0.0 ""
                                                                             1.0 "+"
                                                                             -1.0 "")
                                                                           (case op-k
                                                                             :op/inc  (str v)
                                                                             :op/mult (str v "%"))
                                                                           " "
                                                                           (str/capitalize (name k)))))
                                                                  (sort/f ops))))
                                                mods))))
           :entity/skills (fn [skills _ctx]
                            ; => recursive info-text leads to endless text wall
                            (when (seq skills)
                              (str "Skills: " (str/join "," (map name (keys skills))))))
           :entity/species (fn [species _ctx]
                             (str "Creature - " (str/capitalize (name species))))
           :entity/temp-modifier (fn [{:keys [counter]} {:keys [ctx/elapsed-time]}]
                                    (str "Spiderweb - remaining: " (readable/f (ratio/f elapsed-time counter)) "/1"))
           :projectile/piercing? (fn [_ _ctx]
                                   "Piercing")
           :property/pretty-name (fn [v _ctx]
                                    v)
           :skill/cooling-down? (fn [counter {:keys [ctx/elapsed-time]}]
                                   (str "Cooldown: " (readable/f (ratio/f elapsed-time counter)) "/1"))
           :skill/action-time (fn [v _ctx]
                                 (str "Action-Time: " (readable/f v) " seconds"))
           :skill/action-time-modifier-key (fn [v _ctx]
                                              (case v
                                                :stats/cast-speed "Spell"
                                                :stats/attack-speed "Attack"))
           :skill/cooldown (fn [v _ctx]
                             (str "Cooldown: " (readable/f v) " seconds"))
           :skill/cost (fn [v _ctx]
                          (str "Cost: " v " Mana"))
           :maxrange (fn [v _ctx]
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
  [entity ctx]
  (let [{:keys [k->fn
                k-order
                k->colors]} info
        component-info (fn [[k v]]
                         (let [s (if-let [info-fn (k->fn k)]
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
                        (str "\n" (info-text v ctx))))))
         (str/join "\n")
         remove-newlines)))
